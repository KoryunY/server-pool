package com.gmail.yeritsyankoryun.serverpool.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
public class ServerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serverId;
    @Max(100)
    private int allocatedMemory;
    private int reservedMemory;
    @NotNull
    private Db_Type storingDbType;
    @ElementCollection
    @OneToMany(fetch = FetchType.EAGER)
    private final Set<ApplicationModel> applicationModels = new HashSet<>();
    private boolean isActive;


    @Autowired
    public ServerModel() {
    }

    public int getServerId() {
        return serverId;
    }

    public int getAllocatedMemory() {
        return allocatedMemory;
    }

    public void setAllocatedMemory(int allocatedMemory) {
        this.allocatedMemory = allocatedMemory;
    }

    public int getReservedMemory() {
        return reservedMemory;
    }

    public void setReservedMemory(int reservedMemory) {
        this.reservedMemory = reservedMemory;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
