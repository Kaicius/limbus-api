package com.kaio.limbus_api.config;

import com.kaio.limbus_api.filter.ApiKeyFilter;
import com.kaio.limbus_api.filter.IdempotencyFilter;
import com.kaio.limbus_api.filter.RateLimitFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI limbusOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(ESQUEMA_DE_CHAVE, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name(ApiKeyFilter.HEADER)
                        .description("Chave de API. Obrigatória em POST, PUT e DELETE. Gere a sua em POST /api-keys (rota aberta)")))
                .info(new Info()
                        .title("Limbus API")
                        .version("2.0.0")
                        .description("API REST estilo wiki da Limbus Company: Sinners, Identities, Tags, Skills, Passivas, Stats e Sanity. "
                                + "Todas as listagens são paginadas (page, size, sort), cada recurso traz links HATEOAS (_links com self, update e delete) e os erros seguem o formato padrão ErrorResponse.\n\n"
                                + "**Recursos avançados:** todo POST exige o cabeçalho X-Idempotency-Key (idempotência); há limite de requisições por IP (token bucket, 429 com Retry-After); "
                                + "a API é versionada pelo cabeçalho X-API-Version (escolha a versão no seletor \"Select a definition\", no topo); e o CORS libera apenas as origens configuradas.\n\n"
                                + "**Autenticação:** as leituras (GET) são livres. POST, PUT e DELETE exigem uma chave de API válida no cabeçalho X-API-Key "
                                + "(clique em \"Authorize\" e informe a chave). Qualquer pessoa gera a sua chave em POST /api-keys, informando nome e e-mail; "
                                + "cada usuário consulta e revoga a própria em /api-keys/me, e só a chave de administrador lista e revoga as dos outros.")
                        .termsOfService("https://github.com/Kaicius/limbus-api#termos-de-servi%C3%A7o")
                        .contact(new Contact()
                                .name("Kaio Alves")
                                .email("kaioalvesesousa@gmail.com")
                                .url("https://github.com/Kaicius"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .tags(List.of(
                        new Tag().name("Sinners").description("Gerenciamento dos 12 Sinners da Limbus Company, os personagens jogáveis aos quais as Identities pertencem"),
                        new Tag().name("Tags").description("Gerenciamento das Tags temáticas das Identities, como 'The House of Spiders' ou 'The Pinky'"),
                        new Tag().name("Identities").description("Gerenciamento das Identities, as versões jogáveis de cada Sinner, com raridade, uptie e Tags"),
                        new Tag().name("Skills").description("Skills das Identities: três ataques e uma defesa, cada uma com pecado, moedas e variantes"),
                        new Tag().name("Passives").description("Passivas das Identities, de combate (BATTLE) ou de suporte (SUPPORT)"),
                        new Tag().name("Sanity").description("Informações de Sanity de cada Identity: descrição do Panic e o que aumenta ou diminui a Sanity. Cada Identity tem no máximo uma"),
                        new Tag().name("Identity Stats").description("Atributos de combate de cada Identity (HP, velocidade, defesa, limiar de Stagger e resistências). Cada Identity tem no máximo um registro"),
                        new Tag().name("API Keys").description("Chaves de API: qualquer pessoa gera a sua, cada usuário consulta e revoga a própria, e o administrador gerencia todas")
                ));
    }

    private static final List<String> ORDEM_TAGS = List.of(
            "Sinners", "Tags", "Identities", "Skills", "Passives", "Sanity", "Identity Stats", "API Keys");

    private static final String ESQUEMA_DE_CHAVE = "ApiKeyAuth";
    private static final Set<PathItem.HttpMethod> METODOS_DE_ESCRITA = Set.of(
            PathItem.HttpMethod.POST, PathItem.HttpMethod.PUT, PathItem.HttpMethod.PATCH, PathItem.HttpMethod.DELETE);

    @Bean
    public GroupedOpenApi apiV1() {
        return grupoDaVersao("1");
    }

    @Bean
    public GroupedOpenApi apiV2() {
        return grupoDaVersao("2");
    }

    private GroupedOpenApi grupoDaVersao(String versao) {
        return GroupedOpenApi.builder()
                .group("v" + versao)
                .displayName("Limbus API v" + versao)
                .addOpenApiMethodFilter(metodo -> {
                    RequestMapping mapeamento = AnnotatedElementUtils.findMergedAnnotation(metodo, RequestMapping.class);
                    return mapeamento == null || mapeamento.version().isEmpty() || mapeamento.version().equals(versao);
                })
                .addOpenApiCustomizer(ordenarTags())
                .addOpenApiCustomizer(documentarRecursosAvancados(versao))
                .build();
    }

    private OpenApiCustomizer ordenarTags() {
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

    private OpenApiCustomizer documentarRecursosAvancados(String versao) {
        return openApi -> {
            if (openApi.getPaths() == null) return;
            openApi.getPaths().forEach((rota, caminho) -> {
                boolean rotaDeChaves = rota.startsWith(ApiKeyFilter.ROTA_DE_CHAVES);
                boolean propriaChave = rota.equals(ApiKeyFilter.ROTA_DA_PROPRIA_CHAVE);
                caminho.readOperationsMap().forEach((metodo, operacao) -> {
                    documentarVersao(operacao, versao);
                    documentarRateLimit(operacao);
                    boolean cadastro = rota.equals(ApiKeyFilter.ROTA_DE_CHAVES) && metodo == PathItem.HttpMethod.POST;
                    if (!cadastro && (rotaDeChaves || METODOS_DE_ESCRITA.contains(metodo))) {
                        documentarChaveDeApi(operacao, rotaDeChaves && !propriaChave);
                    }
                });
                if (caminho.getPost() != null) {
                    documentarIdempotencia(caminho.getPost());
                }
            });
        };
    }

    private static void documentarChaveDeApi(Operation operacao, boolean somenteAdministrador) {
        if (operacao.getSecurity() == null || operacao.getSecurity().isEmpty()) {
            operacao.addSecurityItem(new SecurityRequirement().addList(ESQUEMA_DE_CHAVE));
        }
        adicionarResposta(operacao, "401",
                "Unauthorized – Cabeçalho X-API-Key ausente, ou chave inválida ou revogada",
                "Sem chave de API",
                exemploDeErro(401, "Unauthorized", "Chave de API ausente: envie uma chave válida no cabeçalho X-API-Key"));
        if (somenteAdministrador) {
            adicionarResposta(operacao, "403",
                    "Forbidden – A chave é válida, mas não é a de administrador",
                    "Chave sem permissão",
                    exemploDeErro(403, "Forbidden", "Somente a chave de administrador pode gerenciar as chaves de outros usuários"));
        }
    }

    private static void documentarVersao(Operation operacao, String versao) {
        if (operacao.getParameters() == null) {
            operacao.setParameters(new ArrayList<>());
        }
        Parameter cabecalho = operacao.getParameters().stream()
                .filter(p -> ApiVersionConfig.HEADER.equals(p.getName()))
                .findFirst().orElse(null);

        if (cabecalho == null) {
            cabecalho = new HeaderParameter()
                    .name(ApiVersionConfig.HEADER)
                    .required(false)
                    .description("Versão da API. Opcional: este endpoint responde igual nas versões 1 e 2. "
                            + "Sem o cabeçalho vale a versão 1; uma versão inexistente (ex.: 3) devolve 400");
            operacao.addParametersItem(cabecalho);
        } else {
            cabecalho.required(false)
                    .description("Versão da API. Este endpoint muda conforme a versão: 1 devolve a identity simples "
                            + "e 2 devolve a ficha completa. Sem o cabeçalho vale a versão 1; uma versão inexistente (ex.: 3) devolve 400");
        }
        cabecalho.setSchema(new StringSchema()._enum(new ArrayList<>(List.of("1", "2")))._default(versao));
    }

    private static void documentarRateLimit(Operation operacao) {
        ApiResponse limiteExcedido = adicionarResposta(operacao, "429",
                "Too Many Requests – Limite de requisições por IP excedido (token bucket). O cabeçalho Retry-After diz quantos segundos esperar",
                "Limite excedido",
                exemploDeErro(429, "Too Many Requests", "Limite de 20 requisições a cada 60s excedido. Tente novamente em 3s"));
        limiteExcedido.addHeaderObject("Retry-After", new Header()
                .description("Segundos a esperar antes de tentar de novo")
                .schema(new IntegerSchema().example(3)));

        operacao.getResponses().values().forEach(resposta -> {
            resposta.addHeaderObject(RateLimitFilter.HEADER_LIMITE, new Header()
                    .description("Capacidade do balde: máximo de requisições por IP a cada 60 segundos")
                    .schema(new IntegerSchema().example(20)));
            resposta.addHeaderObject(RateLimitFilter.HEADER_RESTANTE, new Header()
                    .description("Requisições (fichas) que este IP ainda pode fazer agora")
                    .schema(new IntegerSchema().example(19)));
        });
    }

    private static void documentarIdempotencia(Operation post) {
        boolean jaDocumentado = post.getParameters().stream()
                .anyMatch(p -> IdempotencyFilter.HEADER.equals(p.getName()));
        if (!jaDocumentado) {
            post.addParametersItem(new HeaderParameter()
                    .name(IdempotencyFilter.HEADER)
                    .required(true)
                    .description("Chave única da operação (ex.: um UUID). Repetir a mesma chave com o mesmo corpo devolve a resposta original "
                            + "(com Idempotent-Replayed: true) em vez de criar outro registro")
                    .schema(new StringSchema().maxLength(255))
                    .example("3f2b8c1e-9d4a-4c7e-b1a2-6e5f0d9c8a71"));
        }

        adicionarResposta(post, "400",
                "Bad Request – Cabeçalho X-Idempotency-Key ausente, em branco ou com mais de 255 caracteres",
                "Sem X-Idempotency-Key",
                exemploDeErro(400, "Bad Request",
                        "Cabeçalho obrigatório ausente: X-Idempotency-Key (envie uma chave única por operação, ex.: um UUID)"));
        adicionarResposta(post, "409",
                "Conflict – Uma requisição com a mesma X-Idempotency-Key ainda está sendo processada",
                "Chave em processamento",
                exemploDeErro(409, "Conflict", "Uma requisição com esta X-Idempotency-Key ainda está sendo processada"));
        adicionarResposta(post, "422",
                "Unprocessable Content – A X-Idempotency-Key já foi usada com um corpo de requisição diferente",
                "Chave reutilizada",
                exemploDeErro(422, "Unprocessable Content", "X-Idempotency-Key já foi usada com um corpo de requisição diferente"));

        ApiResponse criado = post.getResponses().get("201");
        if (criado != null) {
            criado.addHeaderObject("Location", new Header()
                    .description("URL do recurso criado")
                    .schema(new StringSchema().example("http://localhost:8080/sinners/1")));
            criado.addHeaderObject(IdempotencyFilter.HEADER_REPLAY, new Header()
                    .description("Vem como true quando a resposta é a repetição de uma requisição anterior com a mesma X-Idempotency-Key "
                            + "(nada novo foi criado). Ausente na primeira vez")
                    .schema(new BooleanSchema().example(true)));
        }
    }

    private static ApiResponse adicionarResposta(Operation operacao, String status, String descricao,
                                                 String nomeDoExemplo, Map<String, Object> exemplo) {
        if (operacao.getResponses() == null) {
            operacao.setResponses(new ApiResponses());
        }
        ApiResponse resposta = operacao.getResponses().get(status);
        if (resposta == null) {
            resposta = new ApiResponse().description(descricao);
            operacao.getResponses().addApiResponse(status, resposta);
        } else if (resposta.getDescription() == null) {
            resposta.setDescription(descricao);
        } else if (!resposta.getDescription().contains(descricao)) {
            resposta.setDescription(resposta.getDescription() + ". Também: " + descricao);
        }

        if (resposta.getContent() == null) {
            resposta.setContent(new Content());
        }
        MediaType json = resposta.getContent().get("application/json");
        if (json == null) {
            json = new MediaType().schema(new Schema<Object>().$ref("#/components/schemas/ErrorResponse"));
            resposta.getContent().addMediaType("application/json", json);
        }
        json.addExamples(nomeDoExemplo, new Example().value(exemplo));
        return resposta;
    }

    private static Map<String, Object> exemploDeErro(int status, String erro, String mensagem) {
        Map<String, Object> exemplo = new LinkedHashMap<>();
        exemplo.put("status", status);
        exemplo.put("erro", erro);
        exemplo.put("mensagem", mensagem);
        exemplo.put("timestamp", "2026-10-08T00:54:40");
        return exemplo;
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
