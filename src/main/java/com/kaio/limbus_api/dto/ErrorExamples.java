package com.kaio.limbus_api.dto;

public final class ErrorExamples {
    private ErrorExamples() {}

    public static final String BAD_REQUEST_VALIDATION = """
            {"status": 400, "erro": "Bad Request", "mensagem": "nome: não deve estar em branco", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String BAD_REQUEST_ID = """
            {"status": 400, "erro": "Bad Request", "mensagem": "Parâmetro 'id' com valor inválido: abc", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String BAD_REQUEST_PARAM_NOME = """
            {"status": 400, "erro": "Bad Request", "mensagem": "Parâmetro obrigatório ausente: nome", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String BAD_REQUEST_PARAM_TAG = """
            {"status": 400, "erro": "Bad Request", "mensagem": "Parâmetro obrigatório ausente: tag", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_SINNER = """
            {"status": 404, "erro": "Not Found", "mensagem": "Sinner não encontrado(a) com id: 99", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_TAG = """
            {"status": 404, "erro": "Not Found", "mensagem": "Tag não encontrado(a) com id: 99", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_IDENTITY = """
            {"status": 404, "erro": "Not Found", "mensagem": "Identity não encontrado(a) com id: 99", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String CONFLICT = """
            {"status": 409, "erro": "Conflict", "mensagem": "Operação viola uma regra de integridade (valor duplicado ou registro em uso)", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_STATS = """
            {"status": 404, "erro": "Not Found", "mensagem": "IdentityStats não encontrado(a) com id: 99", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_STATS_IDENTITY = """
            {"status": 404, "erro": "Not Found", "mensagem": "IdentityStats da Identity não encontrado(a) com id: 5", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_SANITY = """
            {"status": 404, "erro": "Not Found", "mensagem": "Sanity não encontrado(a) com id: 99", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_SANITY_IDENTITY = """
            {"status": 404, "erro": "Not Found", "mensagem": "Sanity da Identity não encontrado(a) com id: 5", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_SKILL = """
            {"status": 404, "erro": "Not Found", "mensagem": "Skill não encontrado(a) com id: 99", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String NOT_FOUND_PASSIVE = """
            {"status": 404, "erro": "Not Found", "mensagem": "Passive não encontrado(a) com id: 99", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String BAD_REQUEST_SIN = """
            {"status": 400, "erro": "Bad Request", "mensagem": "Parâmetro 'sin' com valor inválido: RAGE", "timestamp": "2026-10-08T00:54:40"}""";

    public static final String BAD_REQUEST_TIPO = """
            {"status": 400, "erro": "Bad Request", "mensagem": "Parâmetro 'tipo' com valor inválido: ATTACK", "timestamp": "2026-10-08T00:54:40"}""";
}
