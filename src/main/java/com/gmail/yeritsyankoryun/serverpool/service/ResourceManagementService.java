package com.gmail.yeritsyankoryun.serverpool.service;

import com.gmail.yeritsyankoryun.serverpool.dto.ServerDto;
import com.gmail.yeritsyankoryun.serverpool.model.ServerPool;
import com.gmail.yeritsyankoryun.serverpool.model.Server;
import com.gmail.yeritsyankoryun.serverpool.repository.Cloud;
import com.gmail.yeritsyankoryun.serverpool.service.converter.RequestConverter;
import com.gmail.yeritsyankoryun.serverpool.thread.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceManagementService {
    private final Cloud cloud;
    private final RequestConverter converter;

    @Autowired
    public ResourceManagementService(Cloud cloud, RequestConverter converter) {
        this.cloud = cloud;
        this.converter = converter;
    }

    public List<ServerPool> getAllServers() {
        return cloud.findAll();
    }

    public synchronized void addServer(ServerDto serverDto) {
        Server request = converter.convertToRequest(serverDto);
        if (cloud.findAll().stream().anyMatch(serverPool -> 100 - serverPool.getAllocatedSize() >= serverDto.getSize() && serverPool.isActive())) {
            ServerPool post = cloud.findAll().stream()
                    .filter(serverPool -> 100 - serverPool.getAllocatedSize() >= serverDto.getSize()).findFirst().get();
            post.getServerRequests().add(request);
            post.setAllocatedSize(post.getAllocatedSize() + request.getSize());
            cloud.save(post);
        } else {
            Spin thread = new Spin(request, cloud);
            thread.run();
        }
    }

    public void deleteAll() {
        cloud.deleteAll();
    }
}
