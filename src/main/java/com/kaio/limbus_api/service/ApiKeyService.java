package com.kaio.limbus_api.service;

import com.kaio.limbus_api.dto.ApiKeyCriadaResponse;
import com.kaio.limbus_api.entity.ApiKey;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
public class ApiKeyService {

    private static final String PREFIXO = "lmb_";
    private static final int TAMANHO_DO_PREFIXO_EXIBIDO = 12;

    private final ApiKeyRepository repository;
    private final byte[] chaveAdmin;
    private final SecureRandom aleatorio = new SecureRandom();

    public ApiKeyService(ApiKeyRepository repository,
                         @Value("${app.security.admin-key:limbus-admin}") String chaveAdmin) {
        this.repository = repository;
        this.chaveAdmin = chaveAdmin.getBytes(StandardCharsets.UTF_8);
    }

    public ApiKeyCriadaResponse gerar(String nome, String email) {
        byte[] bytes = new byte[32];
        aleatorio.nextBytes(bytes);
        String chave = PREFIXO + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        ApiKey salva = repository.save(new ApiKey(nome.trim(), email.trim(),
                chave.substring(0, TAMANHO_DO_PREFIXO_EXIBIDO), hash(chave)));
        return ApiKeyCriadaResponse.de(salva, chave);
    }

    public List<ApiKey> listar() {
        return repository.findAll(Sort.by("id"));
    }

    public ApiKey buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chave de API", id));
    }

    public ApiKey revogar(Long id) {
        ApiKey apiKey = buscarPorId(id);
        apiKey.setAtiva(false);
        return repository.save(apiKey);
    }

    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }

    public boolean ehChaveAdmin(String chave) {
        return MessageDigest.isEqual(chaveAdmin, chave.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<ApiKey> autenticar(String chave) {
        return repository.findByHash(hash(chave))
                .filter(ApiKey::isAtiva)
                .map(apiKey -> {
                    apiKey.setUltimoUso(LocalDateTime.now());
                    return repository.save(apiKey);
                });
    }

    private static String hash(String chave) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(chave.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
