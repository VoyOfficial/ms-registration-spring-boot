package br.voy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("br.voy")
@SpringBootApplication
public class MsRegistrationSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsRegistrationSpringBootApplication.class, args);
    }

}
