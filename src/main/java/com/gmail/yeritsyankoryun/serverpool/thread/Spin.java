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
        serverModel.setAllocatedSize(applicationModel.getSize());
        serverModel.setStoringDbType(applicationModel.getType());
        serverRepository.save(serverModel);
        applicationModel.setServerId(serverModel.getServerId());
        serverModel.getApplicationModels().add(applicationModel);
        synchronized (serverRepository) {
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
}
