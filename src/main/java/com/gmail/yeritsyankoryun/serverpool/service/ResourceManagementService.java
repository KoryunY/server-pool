package com.gmail.yeritsyankoryun.serverpool.service;

import com.gmail.yeritsyankoryun.serverpool.dto.ApplicationDto;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationId;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;
import com.gmail.yeritsyankoryun.serverpool.service.converter.ApplicationConverter;
import com.gmail.yeritsyankoryun.serverpool.thread.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ResourceManagementService {
    private final ServerRepository serverRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationConverter converter;
    private final ExecutorService threadpool = Executors.newCachedThreadPool();
    private Future<ApplicationModel> futureTask;
    @Autowired
    public ResourceManagementService(ServerRepository serverRepository, ApplicationRepository applicationRepository, ApplicationConverter converter) {
        this.serverRepository = serverRepository;
        this.applicationRepository = applicationRepository;
        this.converter = converter;
    }

    public List<ServerModel> getAllServers() {
        return serverRepository.findAll();
    }

    public List<ApplicationModel> getAllApplications() {
        return applicationRepository.findAll();
    }

    public ResponseEntity<String> addApplication(ApplicationDto applicationDto) {
        ApplicationModel applicationModel = converter.convertToApplication(applicationDto);
        ServerModel server = serverRepository.findAll().stream()
                .filter(serverModel -> 100 - serverModel.getAllocatedSize() >= applicationModel.getSize()).findFirst().orElse(null);
        if (server != null && server.getStoringDbType() == applicationDto.getType()) {
            if (!server.isActive()) {
                while (!futureTask.isDone()){
                    //do Nothing
                }
            }
            addToServer(applicationModel, server);
        } else {
            futureTask = threadpool.submit(() -> createServer(applicationModel));
            return ResponseEntity.status(200).body("Spinning new Server For " + applicationModel.getName());
        }
        return ResponseEntity.status(200).body("Allocated " + applicationModel.getName() + " On Server ID:" + applicationModel.getServerId());
    }

    public ApplicationModel createServer(ApplicationModel applicationModel) {
        ServerModel serverModel = new ServerModel();
        serverModel.setAllocatedSize(applicationModel.getSize());
        serverModel.setStoringDbType(applicationModel.getType());
        serverRepository.save(serverModel);
        applicationModel.setServerId(serverModel.getServerId());
        serverModel.getApplicationModels().add(applicationModel);
        spinNewOne();
        applicationRepository.save(applicationModel);
        serverModel.setActive(true);
        serverRepository.save(serverModel);
        return applicationModel;
    }

    public void addToServer(ApplicationModel applicationModel, ServerModel serverModel) {
        applicationModel.setServerId(serverModel.getServerId());
        if (serverModel.getApplicationModels().add(applicationModel)) {
            serverModel.setAllocatedSize(serverModel.getAllocatedSize() + applicationModel.getSize());
            serverModel.setActive(true);
            applicationRepository.save(applicationModel);
        } else createServer(applicationModel);
    }

    public void waitToActivate() {
        synchronized (serverRepository) {
            try {
                serverRepository.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void spinNewOne() {
        Spin spinning = new Spin();
        Thread thread = new Thread(spinning);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ServerModel getServerById(int id) {
        return serverRepository.getById(id);
    }

    public ApplicationModel getApplicationById(int id, String name) {
        return applicationRepository.getById(new ApplicationId(name, id));
    }

    public void deleteServer(Integer id) {
        if (id == null)
            serverRepository.deleteAll();
        else
            serverRepository.deleteById(id);
    }

    public void deleteApp(Integer id, String name) {
        ServerModel server = serverRepository.getById(id);
        ApplicationModel application = applicationRepository.getById(new ApplicationId(name, id));
        server.setAllocatedSize(server.getAllocatedSize() - application.getSize());
        server.getApplicationModels().remove(application);
        serverRepository.save(server);
    }
}
