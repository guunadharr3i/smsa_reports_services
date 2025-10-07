package com.smsaSchedulers.Schedulers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication(scanBasePackages = {"com.smsaSchedulers.Schedulers"})
public class SchedulersApplication  {

    public static void main(String[] args) {
        SpringApplication.run(SchedulersApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.smsaSchedulers.Schedulers"))
                .paths(PathSelectors.any()).build().pathMapping("/");
    }
}
