package com.kaio.limbus_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI limbusOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Limbus API")
                .version("1.0.0")
                .description("API REST estilo wiki da Limbus Company: Sinners, Identities, Tags, Skills, Passivas, Stats e Sanity. "
                        + "Todas as listagens são paginadas (page, size, sort), cada recurso traz links HATEOAS (_links com self, update e delete) e os erros seguem o formato padrão ErrorResponse.")
                .contact(new Contact().name("Kaio Alves").email("kaioalvesesousa@gmail.com")))
                .tags(List.of(
                        new Tag().name("Sinners").description("Gerenciamento dos 12 Sinners da Limbus Company, os personagens jogáveis aos quais as Identities pertencem"),
                        new Tag().name("Tags").description("Gerenciamento das Tags temáticas das Identities, como 'The House of Spiders' ou 'The Pinky'"),
                        new Tag().name("Identities").description("Gerenciamento das Identities, as versões jogáveis de cada Sinner, com raridade, uptie e Tags"),
                        new Tag().name("Skills").description("Skills das Identities: três ataques e uma defesa, cada uma com pecado, moedas e variantes"),
                        new Tag().name("Passives").description("Passivas das Identities, de combate (BATTLE) ou de suporte (SUPPORT)"),
                        new Tag().name("Sanity").description("Informações de Sanity de cada Identity: descrição do Panic e o que aumenta ou diminui a Sanity. Cada Identity tem no máximo uma"),
                        new Tag().name("Identity Stats").description("Atributos de combate de cada Identity (HP, velocidade, defesa, limiar de Stagger e resistências). Cada Identity tem no máximo um registro")
                ));
    }

    private static final List<String> ORDEM_TAGS = List.of(
            "Sinners", "Tags", "Identities", "Skills", "Passives", "Sanity", "Identity Stats");

    @Bean
    public OpenApiCustomizer ordenarTags() {
        return openApi -> {
            if (openApi.getTags() == null) return;
            List<Tag> ordenadas = new ArrayList<>(openApi.getTags());
            ordenadas.sort(Comparator.comparingInt(t -> {
                int i = ORDEM_TAGS.indexOf(t.getName());
                return i < 0 ? Integer.MAX_VALUE : i;
            }));
            openApi.setTags(ordenadas);
        };
    }

    @Bean
    public SwaggerIndexTransformer swaggerIndexTransformer(
            SwaggerUiConfigProperties swaggerUiConfig,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider) {

        return new SwaggerCssInjector(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon, objectMapperProvider);
    }
}
