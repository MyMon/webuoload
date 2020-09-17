package com.webupload.demo;

import com.webupload.demo.common.FileUploadConfig;
import com.webupload.demo.common.util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebuploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebuploadApplication.class, args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1);
    }


    @Bean
    public FileUploadConfig fileUploadConfig(){
        return new FileUploadConfig();
    }

}
