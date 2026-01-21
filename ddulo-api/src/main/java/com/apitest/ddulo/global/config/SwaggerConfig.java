package com.apitest.ddulo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {
	
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info().title("Title")
						.description("""
	                            <p>소개글</p>
	                            <h3>뚫어뻥</h3>
	                            <ul>
		                            <li><strong>이름</strong> : <a href="#" target="_blank">Github</a></li>
	                            </ul>
	                            """)
	                    .version("v0.0.1")
	                    .contact(new Contact()
	                            .name("주소")
	                            .url("#"))
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
}
