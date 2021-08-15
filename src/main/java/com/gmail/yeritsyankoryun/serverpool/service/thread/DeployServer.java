package com.gmail.yeritsyankoryun.serverpool.service.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class DeployServer implements Callable<Void> {
    private ServerModel serverModel;
    private final ServerRepository serverRepository;

    public DeployServer(ServerModel serverModel, ServerRepository serverRepository) {
        this.serverModel = serverModel;
        this.serverRepository = serverRepository;
    }

    @Override
    public Void call() throws Exception {
        try {
            sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serverModel = serverRepository.findById(serverModel.getServerId()).get();
        serverModel.setActive(true);
        serverRepository.save(serverModel);
        return null;
    }
}