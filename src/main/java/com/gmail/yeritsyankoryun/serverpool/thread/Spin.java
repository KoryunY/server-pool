package com.gmail.yeritsyankoryun.serverpool.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;

public class Spin extends Thread {
    private ApplicationModel applicationModel;
    private ServerRepository serverRepository;
    private ApplicationRepository applicationRepository;

    public Spin(ApplicationModel applicationModel, ServerRepository serverRepository, ApplicationRepository applicationRepository) {
        this.applicationModel = applicationModel;
        this.serverRepository = serverRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public void run() {
        ServerModel serverModel = new ServerModel();
        serverRepository.save(serverModel);
        serverModel.setAllocatedSize(applicationModel.getSize());
        applicationModel.setServerId(serverModel.getServerId());
        serverModel.getApplicationModels().add(applicationModel);
        try {
            sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serverModel.setActive(true);
        applicationRepository.save(applicationModel);
        serverRepository.save(serverModel);
    }
}
