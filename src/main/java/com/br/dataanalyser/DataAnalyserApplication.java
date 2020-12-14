package com.br.dataanalyser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DataAnalyserApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataAnalyserApplication.class, args);
    }

}
