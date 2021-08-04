package com.gmail.yeritsyankoryun.serverpool.thread;

import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class DeployServer implements Callable<ServerModel> {
    private ServerModel serverModel;
    private ServerRepository serverRepository;

    public DeployServer(ServerModel serverModel, ServerRepository serverRepository) {
        this.serverModel = serverModel;
        this.serverRepository = serverRepository;
    }

    @Override
    public ServerModel call() throws Exception {
        try {
            sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serverModel.setActive(true);
        serverRepository.save(serverModel);
        return serverModel;
    }
}