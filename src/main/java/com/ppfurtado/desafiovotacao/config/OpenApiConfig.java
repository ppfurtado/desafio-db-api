package com.ppfurtado.desafiovotacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Desafio Votação - API REST")
                        .version("v1")
                        .description("""
                                API REST para gerenciamento de pautas, abertura de sessões e recepção de votos em assembleias cooperativas.
                                """)
                        .contact(new Contact()
                                .name("Equipe Desafio Votação")
                                .email("suporte@somosdb.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
