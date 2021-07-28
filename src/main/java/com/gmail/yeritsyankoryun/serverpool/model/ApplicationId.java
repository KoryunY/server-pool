package com.gmail.yeritsyankoryun.serverpool.model;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Objects;

public class ApplicationId implements Serializable {
    private String name;
    private int serverId;

    @Autowired
    public ApplicationId() {
    }

    public ApplicationId(String name, int serverId) {
        this.name = name;
        this.serverId = serverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationId that = (ApplicationId) o;
        return serverId == that.serverId && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, serverId);
    }
}
