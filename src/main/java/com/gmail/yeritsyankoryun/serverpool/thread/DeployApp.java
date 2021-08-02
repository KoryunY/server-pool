package com.gmail.yeritsyankoryun.serverpool.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DeployApp implements Runnable {
    private final ApplicationRepository applicationRepository;
    private final ApplicationModel applicationModel;
    private ServerModel serverModel;
    private final ServerRepository serverRepository;
    private final ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers;

    public DeployApp(ApplicationRepository applicationRepository,
                     ApplicationModel applicationModel, ServerModel serverModel,
                     ServerRepository serverRepository, ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers) {
        this.applicationRepository = applicationRepository;
        this.applicationModel = applicationModel;
        this.serverModel = serverModel;
        this.serverRepository = serverRepository;
        this.spinningServers = spinningServers;
    }

    @Override
    public void run() {
        if (!serverModel.isActive()) {
            Thread waiter = new Thread(new Wait(serverModel.getServerId(), spinningServers));
            waiter.start();
            try {
                waiter.join();
                serverModel = serverRepository.findById(serverModel.getServerId()).get();
                Set<ApplicationModel> applicationModels = applicationRepository.findAll().stream()
                        .filter(applicationModel1 -> applicationModel1.getServerId() == serverModel.getServerId())
                        .collect(Collectors.toSet());
                serverModel.setApplicationModels(applicationModels);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        applicationModel.setServerId(serverModel.getServerId());
        if (serverModel.getApplicationModels().add(applicationModel)) {
            serverModel.setAllocatedSize(serverModel.getAllocatedSize() + applicationModel.getSize());
            applicationRepository.save(applicationModel);
            serverRepository.save(serverModel);
        } else
            new Thread(new DeployServer(serverRepository, applicationRepository, applicationModel, spinningServers)).start();
    }
}
