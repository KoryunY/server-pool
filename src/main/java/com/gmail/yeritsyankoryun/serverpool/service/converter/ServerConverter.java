package com.gmail.yeritsyankoryun.serverpool.service.converter;

import com.gmail.yeritsyankoryun.serverpool.dto.ServerDto;
import com.gmail.yeritsyankoryun.serverpool.model.ServerModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerConverter {
    @Autowired
    private ModelMapper modelMapper;

    public ServerDto convertToDto(ServerModel serverModel) {
        return modelMapper.map(serverModel, ServerDto.class);
    }
}
