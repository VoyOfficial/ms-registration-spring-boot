package br.voy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ComponentScan("br.voy")
@SpringBootApplication
public class MsRegistrationSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsRegistrationSpringBootApplication.class, args);
    }
}
