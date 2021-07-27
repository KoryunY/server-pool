package com.gmail.yeritsyankoryun.serverpool.repository;

import com.gmail.yeritsyankoryun.serverpool.model.ServerPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Cloud extends JpaRepository<ServerPool, Integer> {
}
