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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ResourceManagementService {
    private final ServerRepository serverRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationConverter converter;

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

    public void addServer(ApplicationDto applicationDto) {
        ApplicationModel applicationModel = converter.convertToApplication(applicationDto);
        if (serverRepository.findAll().stream()
                .anyMatch(serverModel -> 100 - serverModel.getAllocatedSize() >= applicationModel.getSize()
                        && serverModel.isActive())) {
            ServerModel server = serverRepository.findAll().stream()
                    .filter(serverModel -> 100 - serverModel.getAllocatedSize() >= applicationModel.getSize()).findFirst().get();
            applicationModel.setServerId(server.getServerId());
            if (server.getApplicationModels().add(applicationModel)) {
                server.setAllocatedSize(server.getAllocatedSize() + applicationModel.getSize());
                applicationRepository.save(applicationModel);
            } else spinNewOne(applicationModel);
        } else {
            spinNewOne(applicationModel);
        }
    }

    public synchronized void spinNewOne(ApplicationModel applicationModel) {
        Spin thread = new Spin(applicationModel, serverRepository, applicationRepository);
        thread.run();
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
        if (id == null && name == null) {
            for (ServerModel serverModel : serverRepository.findAll()) {
                serverModel.getApplicationModels().clear();
                serverModel.setAllocatedSize(0);
                serverRepository.save(serverModel);
            }
        } else if (id == null || name == null)
            throw new IllegalArgumentException("Cant access Application " + (id == null ? "Id" : "Name") + " field");
        else {
            ServerModel server = serverRepository.getById(id);
            ApplicationModel application = applicationRepository.getById(new ApplicationId(name, id));
            server.setAllocatedSize(server.getAllocatedSize() - application.getSize());
            server.getApplicationModels().remove(application);
            serverRepository.save(server);
        }
    }
}
