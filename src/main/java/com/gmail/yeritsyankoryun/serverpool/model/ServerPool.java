package com.gmail.yeritsyankoryun.serverpool.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Max;

import java.util.HashSet;
import java.util.Set;

@Entity
public class ServerPool {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int serverId;
    @Max(100)
    private int allocatedSize;
    @ElementCollection
    private Set<Server> servers = new HashSet<>();
    boolean isActive = false;

    @Autowired
    public ServerPool() {
    }

    public int getServerId() {
        return serverId;
    }

    public int getAllocatedSize() {
        return allocatedSize;
    }

    public void setAllocatedSize(int allocatedSize) {
        this.allocatedSize = allocatedSize;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public Set<Server> getServers() {
        return servers;
    }

    public void setServers(Set<Server> servers) {
        this.servers = servers;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
