package com.gmail.yeritsyankoryun.serverpool.service.converter;

import com.gmail.yeritsyankoryun.serverpool.dto.ApplicationDto;
import com.gmail.yeritsyankoryun.serverpool.model.ApplicationModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConverter {
    @Autowired
    private ModelMapper modelMapper;

    public ApplicationModel convertToApplication(ApplicationDto applicationDto) {
        return modelMapper.map(applicationDto, ApplicationModel.class);
    }
}
