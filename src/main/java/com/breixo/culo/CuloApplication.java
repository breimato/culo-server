package com.breixo.culo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The Class CuloApplication.
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class CuloApplication {

    /**
	 * The main method.
	 *
	 * @param args the arguments
	 */
    public static void main(String[] args) {
        SpringApplication.run(CuloApplication.class, args);
    }
}
