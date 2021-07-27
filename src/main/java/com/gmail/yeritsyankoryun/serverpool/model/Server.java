package com.gmail.yeritsyankoryun.serverpool.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Embeddable
public class Server {
    @NotNull
    @Min(1)
    @Max(100)
    private int size;//Gb
    @NotNull
    private Db_Type type;
    @NotBlank
    private String hostName;

    @Autowired
    public Server() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return hostName.equals(server.hostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostName);
    }
}
