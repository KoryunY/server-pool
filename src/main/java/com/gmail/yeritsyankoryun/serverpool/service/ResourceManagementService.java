package com.gmail.yeritsyankoryun.serverpool.service;

import com.gmail.yeritsyankoryun.serverpool.dto.ApplicationDto;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationId;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;
import com.gmail.yeritsyankoryun.serverpool.service.converter.ApplicationConverter;
import com.gmail.yeritsyankoryun.serverpool.service.response.DeployResponse;
import com.gmail.yeritsyankoryun.serverpool.thread.DeployApp;
import com.gmail.yeritsyankoryun.serverpool.thread.DeployServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class ResourceManagementService {
    private final ServerRepository serverRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationConverter converter;
    private final ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers = new ConcurrentHashMap<>();

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

    public DeployResponse addApplication(ApplicationDto applicationDto) {
        ApplicationModel applicationModel = converter.convertToApplication(applicationDto);
        List<ServerModel> serverModels = serverRepository.findAll().stream()
                .filter(server -> 100 - server.getAllocatedSize() >= applicationModel.getSize()).collect(Collectors.toList());
        if(!serverModels.isEmpty()){
            for(ServerModel serverModel:serverModels){
                if(serverModel.getStoringDbType() == applicationDto.getType()){
                    applicationModel.setServerId(serverModel.getServerId());
                    new Thread(new DeployApp(applicationRepository, applicationModel,
                            serverModel, serverRepository, spinningServers)).start();
                    if (!serverModel.isActive()) {
                        return DeployResponse.scheduled(applicationModel);
                    }
                    return DeployResponse.deployed(applicationModel);
                }
            }
        }
        new Thread(new DeployServer(serverRepository,applicationRepository,applicationModel,spinningServers)).start();
        return DeployResponse.spinning(applicationModel);
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
