package com;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 安逸i
 * @version 1.0
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@MapperScan(basePackages = "com.anyi.reggie.mapper")
public class ReggieApplication {
    public static void main(String[] args) {

        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功...");
    }
}
