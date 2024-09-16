package com.keteso;

import com.keteso.properties.APIStatusProperties;
import com.keteso.properties.EntityStatusProperties;
import com.keteso.properties.UserServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties({UserServiceProperties.class, APIStatusProperties.class, EntityStatusProperties.class})

public class CustomerProfileApplication {

	public static void main(String[] args) {

		SpringApplication.run(CustomerProfileApplication.class, args);

	}

}
