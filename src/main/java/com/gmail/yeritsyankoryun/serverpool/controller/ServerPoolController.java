package com.gmail.yeritsyankoryun.serverpool.controller;

import com.gmail.yeritsyankoryun.serverpool.dto.ServerDto;
import com.gmail.yeritsyankoryun.serverpool.model.ServerPool;
import com.gmail.yeritsyankoryun.serverpool.service.ResourceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/server")
public class ServerPoolController {
    private final ResourceManagementService service;

    @Autowired
    public ServerPoolController(ResourceManagementService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServerPool> getServerPool() {
        return service.getAllServers();
    }

    @PostMapping(path = "create")
    public void addToPool(@Valid @RequestBody ServerDto serverDto) {
        service.addServer(serverDto);
    }

    @DeleteMapping(path = "delete")
    public void clearPool(@RequestParam(name = "id", required = false) Integer id, @RequestParam(name = "hostname", required = false) String hostname) {
        service.delete(hostname, id);
    }
}
