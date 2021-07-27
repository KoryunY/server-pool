package com.gmail.yeritsyankoryun.serverpool.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ServerPool {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long serverId;
    @Max(100)
    private int allocatedSize;
    @ElementCollection
    private final List<Server> servers = new ArrayList<>();
    boolean isActive = false;

    @Autowired
    public ServerPool() {
    }

    public long getServerId() {
        return serverId;
    }

    public int getAllocatedSize() {
        return allocatedSize;
    }

    public void setAllocatedSize(int allocatedSize) {
        this.allocatedSize = allocatedSize;
    }

    public List<Server> getServerRequests() {
        return servers;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
