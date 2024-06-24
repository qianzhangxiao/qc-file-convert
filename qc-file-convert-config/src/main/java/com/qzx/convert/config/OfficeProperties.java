package com.qzx.convert.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class OfficeProperties {

    @Value("${office.home:default}")
    private String officeHome;

    @Value("${office.ports:8100}")
    private String ports;

    @Value(("${office.target.path:100}"))
    private Integer maxTasksPerProcess;
}
