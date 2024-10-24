package com.victorlamp.matrixiot.service;

import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.service.system.api.file.FileApi;
import com.victorlamp.matrixiot.service.system.api.file.dto.FileCreateReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@EnableDubbo
@EnableTransactionManagement
public class SystemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemServiceApplication.class, args);
    }

    @Bean
    public FileApi fileApi() {
        //TODO 实现FileService
        return new FileApi() {
            @Override
            public CommonResult<String> createFile(FileCreateReqDTO createReqDTO) {
                return null;
            }
        };
    }
}
