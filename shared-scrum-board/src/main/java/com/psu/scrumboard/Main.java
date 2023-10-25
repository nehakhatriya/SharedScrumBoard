package com.psu.scrumboard;

import com.psu.scrumboard.config.DBType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.vaadin.artur.helpers.LaunchUtil;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Main extends SpringBootServletInitializer {

  public static void main(String[] args) {
    LaunchUtil.launchBrowserInDevelopmentMode(
        new SpringApplicationBuilder(Main.class)
            .properties("vaadin.heartbeatinterval=3")
            .properties("spring.main.allow-circular-references=true")
            .profiles(DBType.H2_DRIVER)
            .run(args));
  }

}
