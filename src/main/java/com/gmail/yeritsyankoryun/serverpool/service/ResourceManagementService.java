package com.gmail.yeritsyankoryun.serverpool.service;

import com.gmail.yeritsyankoryun.serverpool.dto.ServerDto;
import com.gmail.yeritsyankoryun.serverpool.model.ServerPool;
import com.gmail.yeritsyankoryun.serverpool.model.Server;
import com.gmail.yeritsyankoryun.serverpool.repository.Cloud;
import com.gmail.yeritsyankoryun.serverpool.service.converter.ServerConverter;
import com.gmail.yeritsyankoryun.serverpool.thread.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ResourceManagementService {
    private final Cloud cloud;
    private final ServerConverter converter;

    @Autowired
    public ResourceManagementService(Cloud cloud, ServerConverter converter) {
        this.cloud = cloud;
        this.converter = converter;
    }

    public List<ServerPool> getAllServers() {
        return cloud.findAll();
    }

    public synchronized void addServer(ServerDto serverDto) {
        Server server = converter.convertToServer(serverDto);
        if (cloud.findAll().stream().anyMatch(serverPool -> 100 - serverPool.getAllocatedSize() >= serverDto.getSize() && serverPool.isActive())) {
            ServerPool pool = cloud.findAll().stream()
                    .filter(serverPool -> 100 - serverPool.getAllocatedSize() >= serverDto.getSize()).findFirst().get();
            if (pool.getServers().add(server))
                pool.setAllocatedSize(pool.getAllocatedSize() + server.getSize());
            else
                throw new IllegalArgumentException("ERROR:Server with hostname:" + server.getHostName() + " already exist!");
            cloud.save(pool);
        } else {
            Spin thread = new Spin(server, cloud);
            thread.run();
        }
    }

    public void delete(String hostname, Integer id) {
        if (hostname == null && id == null)
            cloud.deleteAll();
        else if (hostname == null)
            cloud.deleteById(id);
        else if (id == null) {
            for (ServerPool serverPool : cloud.findAll()) {
                Set<Server> servers = serverPool.getServers();
                Optional<Server> serv = servers.stream().filter(server -> server.getHostName().equals(hostname)).findFirst();
                if (serv.isPresent()) {
                    serverPool.setAllocatedSize(serverPool.getAllocatedSize() - serv.get().getSize());
                    servers.remove(serv.get());
                    serverPool.setServers(servers);
                    cloud.save(serverPool);
                }
            }
        }
    }
}
