package com.gmail.yeritsyankoryun.serverpool.service;

import com.gmail.yeritsyankoryun.serverpool.dto.ApplicationDto;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationId;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;
import com.gmail.yeritsyankoryun.serverpool.service.converter.ApplicationConverter;
import com.gmail.yeritsyankoryun.serverpool.service.response.DeployResponse;
import com.gmail.yeritsyankoryun.serverpool.service.thread.DeployApp;
import com.gmail.yeritsyankoryun.serverpool.service.thread.DeployServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ResourceManagementService {
    private final ServerRepository serverRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationConverter converter;
    private final ConcurrentHashMap<Integer, Future<ServerModel>> spinningServers = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    public final static Object lock = new Object();

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
                .filter(server -> 100 - server.getAllocatedMemory() - server.getReservedMemory() >= applicationModel.getSize()
                        && server.getStoringDbType() == applicationModel.getType()
                        && !server.getApplicationModels().contains(applicationModel)).collect(Collectors.toList());
        if (!serverModels.isEmpty()) {
            for (ServerModel serverModel : serverModels) {
                if (serverModel.isActive()) {
                    applicationModel.setServerId(serverModel.getServerId());
                    deployApp(applicationModel, serverModel);
                    return DeployResponse.deployed(applicationModel);
                }
            }
            synchronized (lock) {
                for (ServerModel serverModel : serverModels) {
                    applicationModel.setServerId(serverModel.getServerId());
                    serverModel.setReservedMemory(serverModel.getReservedMemory() + applicationModel.getSize());
                    applicationRepository.save(applicationModel);
                    serverRepository.save(serverModel);
                    DeployApp deployApp = new DeployApp(applicationModel,
                            serverModel, serverRepository, spinningServers);
                    executor.submit(deployApp);
                    return DeployResponse.scheduled(applicationModel);
                }
            }
        }
        createServer(applicationModel);
        return DeployResponse.spinning(applicationModel);
    }

    public void createServer(ApplicationModel applicationModel) {
        ServerModel serverModel = new ServerModel();
        serverModel.setAllocatedMemory(applicationModel.getSize());
        serverModel.setStoringDbType(applicationModel.getType());
        serverRepository.save(serverModel);
        applicationModel.setServerId(serverModel.getServerId());
        serverModel.getApplicationModels().add(applicationModel);
        applicationRepository.save(applicationModel);
        DeployServer deployServer = new DeployServer(serverModel, serverRepository);
        spinningServers.put(serverModel.getServerId(), executor.submit(deployServer));

    }

    public void deployApp(ApplicationModel applicationModel, ServerModel serverModel) {
        applicationModel.setServerId(serverModel.getServerId());
        serverModel.getApplicationModels().add(applicationModel);
        serverModel.setAllocatedMemory(serverModel.getAllocatedMemory() + applicationModel.getSize());
        applicationRepository.save(applicationModel);
        serverRepository.save(serverModel);
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
        server.setAllocatedMemory(server.getAllocatedMemory() - application.getSize());
        server.getApplicationModels().remove(application);
        serverRepository.save(server);
    }
}
