package com.gmail.yeritsyankoryun.serverpool.controller;

import com.gmail.yeritsyankoryun.serverpool.dto.ApplicationDto;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import com.gmail.yeritsyankoryun.serverpool.service.ResourceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/server")
public class ServerController {
    private final ResourceManagementService service;

    @Autowired
    public ServerController(ResourceManagementService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServerModel> getServers() {
        return service.getAllServers();
    }

    @GetMapping(path = "get")
    public ServerModel getServer(@RequestParam(name = "id") int id) {
        return service.getServerById(id);
    }

    @DeleteMapping(path = "delete")
    public void clearServers(@RequestParam(name = "id", required = false) Integer id) {
        service.deleteServer(id);
    }
}
