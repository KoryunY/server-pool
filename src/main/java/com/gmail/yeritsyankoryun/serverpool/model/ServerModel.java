package com.gmail.yeritsyankoryun.serverpool.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Max;

import java.util.HashSet;
import java.util.Set;

@Entity
public class ServerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serverId;
    @Max(100)
    private int allocatedSize;
    @ElementCollection
    @OneToMany
    private Set<ApplicationModel> applicationModels = new HashSet<>();
    boolean isActive = false;

    @Autowired
    public ServerModel() {
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

    public Set<ApplicationModel> getApplicationModels() {
        return applicationModels;
    }

    public void setApplicationModels(Set<ApplicationModel> applicationModels) {
        this.applicationModels = applicationModels;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
