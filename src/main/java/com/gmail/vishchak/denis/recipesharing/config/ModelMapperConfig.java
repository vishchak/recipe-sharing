package com.gmail.vishchak.denis.recipesharing.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Set the matching strategy
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return new ModelMapper();
    }
}

