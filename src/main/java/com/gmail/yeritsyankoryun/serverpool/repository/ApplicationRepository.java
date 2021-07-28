package com.gmail.yeritsyankoryun.serverpool.repository;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationId;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationModel, ApplicationId> {
}
