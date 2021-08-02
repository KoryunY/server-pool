package com.gmail.yeritsyankoryun.serverpool.service.response;

import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;

public class DeployResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public static DeployResponse deployed(ApplicationModel app) {
        DeployResponse response = new DeployResponse();
        response.message = "Deployed application:" + app.getName() + " on server id:" + app.getServerId();
        return response;
    }

    public static DeployResponse scheduled(ApplicationModel app) {
        DeployResponse response = new DeployResponse();
        response.message = "Scheduled deployment of application:" + app.getName() + " on server id:" + app.getServerId();
        return response;
    }

    public static DeployResponse spinning(ApplicationModel app) {
        DeployResponse response = new DeployResponse();
        response.message = "Spinning a new Server for application:" + app.getName() + " on server id:" + app.getServerId();
        return response;
    }
}
