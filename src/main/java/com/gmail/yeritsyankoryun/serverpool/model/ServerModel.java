package com.gmail.yeritsyankoryun.serverpool.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ServerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serverId;
    @Max(100)
    private int allocatedSize;
    @NotNull
    private Db_Type storingDbType;
    @ElementCollection
    @OneToMany
    private Set<ApplicationModel> applicationModels = new HashSet<>();
    private boolean isActive;


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

    public Db_Type getStoringDbType() {
        return storingDbType;
    }

    public void setStoringDbType(Db_Type storingDbType) {
        this.storingDbType = storingDbType;
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
