package com.gmail.yeritsyankoryun.serverpool.service.converter;

import com.gmail.yeritsyankoryun.serverpool.dto.ServerDto;
import com.gmail.yeritsyankoryun.serverpool.model.Server;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerConverter {
    @Autowired
    private ModelMapper modelMapper;

    public Server convertToServer(ServerDto serverDto) {
        return modelMapper.map(serverDto, Server.class);
    }
}
