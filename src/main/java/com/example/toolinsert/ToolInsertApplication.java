package com.example.toolinsert;

import com.example.toolinsert.config.ImportProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ImportProperties.class)
public class ToolInsertApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolInsertApplication.class, args);
    }
}

