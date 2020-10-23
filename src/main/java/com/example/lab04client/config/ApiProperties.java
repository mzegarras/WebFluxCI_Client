package com.example.lab04client.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "apis")
@Getter
@Setter
public class ApiProperties {

    private String url;

    private String productsListAll;
    private String productsById;
    private String productsCreated;
    private String productsDeleted;
    private String productsEdit;

}
