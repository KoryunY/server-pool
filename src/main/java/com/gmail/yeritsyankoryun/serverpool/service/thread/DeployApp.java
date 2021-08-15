package com.gmail.yeritsyankoryun.serverpool.service.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class DeployApp implements Callable<Void> {
    private final ApplicationModel applicationModel;
    private ServerModel serverModel;
    private final ServerRepository serverRepository;
    private final ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public DeployApp(ApplicationModel applicationModel, ServerModel serverModels,
                     ServerRepository serverRepository, ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers) {
        this.applicationModel = applicationModel;
        this.serverModel = serverModels;
        this.serverRepository = serverRepository;
        this.spinningServers = spinningServers;
    }

    @Override
    public Void call() {
        boolean isTrue = true;
        while (isTrue) {
            try {
                Future<ServerModel> serverModelFuture = spinningServers.get(serverModel.getServerId());
                if (serverModelFuture.isDone()) {
                    synchronized (serverModelFuture) {
                        Future<ServerModel> futureServer = executor.submit(() -> addTo(serverModelFuture));
                        isTrue = false;
                        if (serverModel.getReservedMemory() == 0)
                            spinningServers.remove(serverModel.getServerId());
                        else {
                            spinningServers.replace(serverModel.getServerId(), serverModelFuture, futureServer);
                        }
                    }
                }
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public ServerModel addTo(Future<ServerModel> future) throws ExecutionException, InterruptedException {
        serverModel = future.get();
        serverModel.getApplicationModels().add(applicationModel);
        serverModel.setAllocatedMemory(serverModel.getAllocatedMemory() + applicationModel.getSize());
        serverModel.setReservedMemory(serverModel.getReservedMemory() - applicationModel.getSize());
        serverRepository.save(serverModel);
        return serverModel;
    }
}
