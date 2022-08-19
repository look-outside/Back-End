package com.springboot.lookoutside;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.springboot.lookoutside.config.properties.AppProperties;
import com.springboot.lookoutside.config.properties.CorsProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        CorsProperties.class,
        AppProperties.class
})
public class LookOutsideApplication {

	public static void main(String[] args) {
		SpringApplication.run(LookOutsideApplication.class, args);
	}

}
