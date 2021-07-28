package com.gmail.yeritsyankoryun.serverpool.repository;

import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<ServerModel, Integer> {
}
