package org.lukos.controller;

import org.lukos.model.instances.InstanceRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * The class that runs the Spring application.
 *
 * @since 12-02-2022
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ControllerApplication {

    /**
     * Main function to run the Spring application.
     *
     * @param args the arguments of the main function
     */
    public static void main(String[] args) {
        SpringApplication.run(ControllerApplication.class, args);
        InstanceRunner.getInstanceRunner();
    }
}
