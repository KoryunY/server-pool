package com.gmail.yeritsyankoryun.serverpool.controller;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import com.gmail.yeritsyankoryun.serverpool.service.ResourceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public List<ApplicationModel> getApplications() {
        return service.getAllApplications();
    }

    @GetMapping(path = "get")
    public ApplicationModel getApplication(@RequestParam(name = "id") int id, @RequestParam(name = "name") String name) {
        return service.getApplicationById(id, name);
    }

    @DeleteMapping(path = "delete")
    public void clearPool(@RequestParam(name = "name", required = false) String name,
                          @RequestParam(name = "id", required = false) Integer id) {
        service.deleteApp(id, name);
    }
}
