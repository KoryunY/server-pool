package com.gmail.yeritsyankoryun.serverpool.controller;

import com.gmail.yeritsyankoryun.serverpool.dto.ApplicationDto;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.service.ResourceManagementService;
import com.gmail.yeritsyankoryun.serverpool.service.response.DeployResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/app")
public class ApplicationController {
    private final ResourceManagementService service;

    @Autowired
    public ApplicationController(ResourceManagementService service) {
        this.service = service;
    }

    @GetMapping
    public List<ApplicationDto> getApplications() {
        return service.getAllApplications();
    }

    @GetMapping(path = "get")
    public ApplicationDto getApplication(@RequestParam(name = "id") int id, @RequestParam(name = "name") String name) {
        return service.getApplicationById(id, name);
    }

    @PostMapping(path = "create")
    public DeployResponse addToServer(@Valid @RequestBody ApplicationDto applicationDto){
        return service.addApplication(applicationDto);
    }

    @DeleteMapping(path = "delete")
    public void deleteApplication(@RequestParam(name = "name") String name,
                          @RequestParam(name = "id") Integer id) {
        service.deleteApp(id, name);
    }
}
