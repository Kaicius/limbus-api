package com.kaio.limbus_api.filter;

import com.kaio.limbus_api.entity.ApiKey;
import com.kaio.limbus_api.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 15)
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-API-Key";
    public static final String ROTA_DE_CHAVES = "/api-keys";
    public static final String ROTA_DA_PROPRIA_CHAVE = ROTA_DE_CHAVES + "/me";
    public static final String ATRIBUTO_ID_DA_CHAVE = "apiKeyId";
    private static final Set<String> METODOS_DE_ESCRITA = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final ApiKeyService apiKeyService;

    public ApiKeyFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String metodo = request.getMethod().toUpperCase();
        String caminho = request.getRequestURI();
        if (metodo.equals("OPTIONS")) {
            return true;
        }
        if (metodo.equals("POST") && caminho.equals(ROTA_DE_CHAVES)) {
            return true;
        }
        return !ehRotaDeChaves(caminho) && !METODOS_DE_ESCRITA.contains(metodo);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String caminho = request.getRequestURI();
        boolean propriaChave = caminho.equals(ROTA_DA_PROPRIA_CHAVE);
        boolean administracao = ehRotaDeChaves(caminho) && !propriaChave;

        String chave = request.getHeader(HEADER);
        if (chave == null || chave.isBlank()) {
            FilterErrorWriter.escrever(response, 401, "Unauthorized",
                    "Chave de API ausente: envie uma chave válida no cabeçalho " + HEADER);
            return;
        }
        chave = chave.trim();

        if (apiKeyService.ehChaveAdmin(chave)) {
            if (propriaChave) {
                FilterErrorWriter.escrever(response, 403, "Forbidden",
                        "A chave de administrador não tem cadastro próprio: use uma chave de usuário nesta rota");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        Optional<ApiKey> apiKey = apiKeyService.autenticar(chave);
        if (apiKey.isEmpty()) {
            FilterErrorWriter.escrever(response, 401, "Unauthorized", "Chave de API inválida ou revogada");
            return;
        }
        if (administracao) {
            FilterErrorWriter.escrever(response, 403, "Forbidden",
                    "Somente a chave de administrador pode gerenciar as chaves de outros usuários");
            return;
        }
        request.setAttribute(ATRIBUTO_ID_DA_CHAVE, apiKey.get().getId());
        chain.doFilter(request, response);
    }

    private static boolean ehRotaDeChaves(String caminho) {
        return caminho.equals(ROTA_DE_CHAVES) || caminho.startsWith(ROTA_DE_CHAVES + "/");
    }
}
