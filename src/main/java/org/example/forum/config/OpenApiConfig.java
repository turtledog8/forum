package org.example.forum.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SESSION_AUTH = "sessionAuth";

    @Bean
    public OpenAPI forumOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Forum System API")
                        .description("REST API for the Forum System: users, posts, comments, likes and tags.")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(SESSION_AUTH))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(SESSION_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")));
    }
}
