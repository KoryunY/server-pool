package com.gmail.yeritsyankoryun.serverpool.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;
import com.gmail.yeritsyankoryun.serverpool.service.response.DeployResponse;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DeployApp implements Runnable {
    private final ApplicationRepository applicationRepository;
    private final ApplicationModel applicationModel;
    private List<ServerModel> serverModels;
    private final ServerRepository serverRepository;
    private final ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public DeployApp(ApplicationRepository applicationRepository,
                     ApplicationModel applicationModel, List<ServerModel> serverModels,
                     ServerRepository serverRepository, ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers) {
        this.applicationRepository = applicationRepository;
        this.applicationModel = applicationModel;
        this.serverModels = serverModels;
        this.serverRepository = serverRepository;
        this.spinningServers = spinningServers;
    }

    @Override
    public void run() {
        boolean isTrue = true;
        while (isTrue)
            if (tryToAdd()) {
                isTrue = false;
            } else synchronized (serverRepository) {
                if (!update()) {
                    create();
                    isTrue = false;
                }
            }
    }

    public boolean tryToAdd() {
        ServerModel server = null;
        while (true) {
            int count = 0;
            for (ServerModel serverModel : serverModels) {
                synchronized (spinningServers) {
                    Future<ServerModel> future = spinningServers.get(serverModel.getServerId());
                    if (future.isDone()) {
                        try {
                            server = future.get();
                            if (applicationModel.getSize() <= 100 - server.getAllocatedSize()) {
                                ServerModel finalServer = server;
                                Future<ServerModel> futureServer = executor.submit(() -> add(finalServer));
                                server = futureServer.get();
                                if (server.getAllocatedSize() == 100) {
                                    spinningServers.remove(server.getServerId());
                                } else {
                                    spinningServers.replace(server.getServerId(), future, futureServer);
                                }
                                return true;
                            } else count++;
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (count != 0 && count == spinningServers.values().stream().filter(Future::isDone).count()) {
                return false;
            }
        }
    }

    public boolean update() {
        this.serverModels = serverRepository.findAll().stream()
                .filter(server -> 100 - server.getAllocatedSize() >= applicationModel.getSize()
                        && server.getStoringDbType() == applicationModel.getType() && !server.getApplicationModels().contains(applicationModel)).collect(Collectors.toList());
        return !serverModels.isEmpty();
    }

    public ServerModel add(ServerModel server) {
        applicationModel.setServerId(server.getServerId());
        applicationRepository.save(applicationModel);
        server.getApplicationModels().add(applicationModel);
        server.setAllocatedSize(server.getAllocatedSize() + applicationModel.getSize());
        return serverRepository.save(server);
    }

    public void create() {
        ServerModel serverModel = new ServerModel();
        serverModel.setAllocatedSize(applicationModel.getSize());
        serverModel.setStoringDbType(applicationModel.getType());
        serverRepository.save(serverModel);
        applicationModel.setServerId(serverModel.getServerId());
        serverModel.getApplicationModels().add(applicationModel);
        applicationRepository.save(applicationModel);
        DeployServer deployServer = new DeployServer(serverModel, serverRepository);
        spinningServers.put(serverModel.getServerId(), executor.submit(deployServer));
    }
}
