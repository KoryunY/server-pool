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

    public ResponseEntity<String> addApplication(ApplicationDto applicationDto) {
        ApplicationModel applicationModel = converter.convertToApplication(applicationDto);
        ServerModel server = serverRepository.findAll().stream()
                .filter(serverModel -> 100 - serverModel.getAllocatedSize() >= applicationModel.getSize()).findFirst().orElse(null);
        if (server != null && server.getStoringDbType() == applicationDto.getType()) {
            synchronized (serverRepository) {
                while (!server.isActive()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            applicationModel.setServerId(server.getServerId());
            if (server.getApplicationModels().add(applicationModel)) {
                server.setAllocatedSize(server.getAllocatedSize() + applicationModel.getSize());
                applicationRepository.save(applicationModel);
            } else spinNewOne(applicationModel);
        } else {
            spinNewOne(applicationModel);
            return ResponseEntity.status(200).body("Spinning new Server For " + applicationModel.getName());
        }
        return ResponseEntity.status(200).body("Allocated " + applicationModel.getName() + " On Server ID:" + applicationModel.getServerId());
    }


    public void spinNewOne(ApplicationModel applicationModel) {
        Spin thread = new Spin(applicationModel, serverRepository, applicationRepository);
        thread.start();
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
