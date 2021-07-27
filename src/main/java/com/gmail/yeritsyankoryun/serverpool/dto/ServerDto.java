package com.gmail.yeritsyankoryun.serverpool.dto;

import com.gmail.yeritsyankoryun.serverpool.model.Db_Type;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ServerDto {
    @NotNull
    @Min(1)
    @Max(100)
    private int size;//Gb
    @NotNull
    private Db_Type type;
    @NotBlank
    private String hostName;

    @Autowired
    public ServerDto() {
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Db_Type getType() {
        return type;
    }

    public void setType(Db_Type type) {
        this.type = type;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
