package com.wolox.training.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiEndPoinstInfo())
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiEndPoinstInfo() { 
        return new ApiInfoBuilder()
                .title("Nlpe Training Java REST API")
                .description("REST API for java training Nestor Perez")
                .contact(new Contact("Nestor Perez", "https://github.com/nlperez11", "nestor.perez@wolox.co"))
                .license("Apache 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.txt")
                .version("1.0.0")
                .build();
    }
}
