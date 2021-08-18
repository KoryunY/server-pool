package com.gmail.yeritsyankoryun.serverpool.service;

import com.gmail.yeritsyankoryun.serverpool.dto.ApplicationDto;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.model.Db_Type;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.repository.ApplicationRepository;
import com.gmail.yeritsyankoryun.serverpool.repository.ServerRepository;
import com.gmail.yeritsyankoryun.serverpool.service.converter.ApplicationConverter;
import com.gmail.yeritsyankoryun.serverpool.service.converter.ServerConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.gmail.yeritsyankoryun.serverpool.service.response.DeployResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

class ResourceManagementServiceTest {
    ServerRepository serverRepository = Mockito.mock(ServerRepository.class);
    ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);
    ServerConverter serverConverter = new ServerConverter();
    ApplicationConverter applicationConverter = Mockito.mock(ApplicationConverter.class);
    ResourceManagementService service = new ResourceManagementService(serverRepository, applicationRepository,
            applicationConverter, serverConverter);
    static List<ServerModel> servers = new ArrayList<>();
    static List<ApplicationModel> applications = new ArrayList<>();

    @BeforeAll
    static void fill() {
        ApplicationModel app1 = new ApplicationModel("app1", 1, 50, Db_Type.MY_SQL);
        applications.add(app1);
        ApplicationModel app5 = new ApplicationModel("app5", 1, 10, Db_Type.MY_SQL);
        applications.add(app5);

        ServerModel server1 = new ServerModel(1, 60, 0, Db_Type.MY_SQL, true);
        server1.getApplicationModels().add(app1);
        server1.getApplicationModels().add(app5);
        servers.add(server1);
        servers.add(server1);

    }


    @DisplayName("addApplication() function test")
    @Test
    void addApplication() {
        ApplicationDto app1 = new ApplicationDto(50, Db_Type.MY_SQL, "app8");
        when(serverRepository.findAll()).thenReturn(servers);
        when(applicationConverter.convertToApplication(app1)).thenReturn(new ApplicationModel("app8",0,50,Db_Type.MY_SQL));
        DeployResponse dp1 = service.addApplication(app1);
        assertEquals("Spinning a new Server for application:app8", dp1.getMessage());

        ApplicationDto app2 = new ApplicationDto(40, Db_Type.MY_SQL, "app9");
        when(applicationConverter.convertToApplication(app2)).thenReturn(new ApplicationModel("app9",0,40,Db_Type.MY_SQL));
        DeployResponse dp2 = service.addApplication(app2);
        assertEquals("Deployed application:app9 on server id:1", dp2.getMessage());
        assertEquals(100,servers.get(0).getAllocatedMemory());

        ServerModel server2=new ServerModel(2,0,0,Db_Type.MARIA_DB,false);
        servers.add(server2);
        ApplicationDto app3 = new ApplicationDto(40, Db_Type.MARIA_DB, "app10");
        when(applicationConverter.convertToApplication(app3)).thenReturn(new ApplicationModel("app10",0,40,Db_Type.MARIA_DB));
        DeployResponse dp3 = service.addApplication(app3);
        assertEquals("Scheduled deployment of application:app10 on server id:2", dp3.getMessage());
        assertEquals(40,server2.getReservedMemory());
    }
}

