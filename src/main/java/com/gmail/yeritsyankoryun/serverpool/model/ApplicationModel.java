package com.gmail.yeritsyankoryun.serverpool.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.*;
import java.util.Objects;

@Entity
@IdClass(ApplicationId.class)
public class ApplicationModel {
    @Id
    @NotBlank
    private String name;
    @Id
    @Nullable
    private int serverId;
    @NotNull
    @Min(1)
    @Max(100)
    private int size;//Gb
    @NotNull
    private Db_Type type;

    @Autowired
    public ApplicationModel() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationModel applicationModel = (ApplicationModel) o;
        return name.equals(applicationModel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
