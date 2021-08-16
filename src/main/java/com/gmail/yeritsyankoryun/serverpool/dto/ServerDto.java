package com.gmail.yeritsyankoryun.serverpool.dto;

import com.gmail.yeritsyankoryun.serverpool.model.Db_Type;

import java.util.List;

public class ServerDto {
    private int serverId;
    private int allocatedMemory;
    private int reservedMemory;
    private Db_Type storingDbType;
    private List<ApplicationDto> applicationModels;
    private boolean isActive;

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
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

    public List<ApplicationDto> getApplicationModels() {
        return applicationModels;
    }

    public void setApplicationModels(List<ApplicationDto> applicationModels) {
        this.applicationModels = applicationModels;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
