package com.gmail.yeritsyankoryun.serverpool.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class DeployServer implements Runnable {
    private final ServerRepository serverRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationModel applicationModel;
    private final ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers;
    private final ExecutorService executor= Executors.newCachedThreadPool();

    public DeployServer(ServerRepository serverRepository, ApplicationRepository applicationRepository,
                        ApplicationModel applicationModel, ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers) {
        this.serverRepository = serverRepository;
        this.applicationRepository = applicationRepository;
        this.applicationModel = applicationModel;
        this.spinningServers = spinningServers;
    }

    @Override
    public void run() {
        ServerModel serverModel = new ServerModel();
        serverModel.setAllocatedSize(applicationModel.getSize());
        serverModel.setStoringDbType(applicationModel.getType());
        serverRepository.save(serverModel);
        applicationModel.setServerId(serverModel.getServerId());
        serverModel.getApplicationModels().add(applicationModel);
        applicationRepository.save(applicationModel);
        Future<ServerModel> future=executor.submit(()->createServer(serverModel));
        spinningServers.put(serverModel.getServerId(),future);
    }

    public ServerModel createServer(ServerModel serverModel){
        Thread spin=new Thread(new Spin());
        spin.start();
        try {
            spin.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serverModel.setActive(true);
        serverRepository.save(serverModel);
        return serverModel;
    }
}