package com.gmail.yeritsyankoryun.serverpool.service.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class DeployApp implements Runnable {
    private final ApplicationModel applicationModel;
    private ServerModel serverModel;
    private final ServerRepository serverRepository;
    private final ConcurrentHashMap<Integer, Future<Void>> spinningServers;

    public DeployApp(ApplicationModel applicationModel, ServerModel serverModels,
                     ServerRepository serverRepository, ConcurrentHashMap<Integer, Future<Void>> spinningServers) {
        this.applicationModel = applicationModel;
        this.serverModel = serverModels;
        this.serverRepository = serverRepository;
        this.spinningServers = spinningServers;
    }

    @Override
    public void run() {
        Future<Void> serverModelFuture = spinningServers.get(serverModel.getServerId());
        while (!serverModelFuture.isDone()) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            synchronized (spinningServers) {
                serverModel = serverRepository.findById(serverModel.getServerId()).get();
                serverModel.getApplicationModels().add(applicationModel);
                serverModel.setAllocatedMemory(serverModel.getAllocatedMemory() + applicationModel.getSize());
                serverModel.setReservedMemory(serverModel.getReservedMemory() - applicationModel.getSize());
            }
        } catch (Exception e) {
            serverModel.setAllocatedMemory(serverModel.getAllocatedMemory() + applicationModel.getSize());
            e.printStackTrace();
        } finally {
            serverRepository.save(serverModel);
        }
    }
}

