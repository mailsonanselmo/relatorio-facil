package br.com.relatorioFacil.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final DataSource dataSource;

    /**
     * Verifica a conexão antes de executar qualquer SQL e loga usuário/db configurados.
     */
    private void verificarConexao() {

        // Se for HikariDataSource, conseguimos extrair config antes mesmo de abrir conexão
        //if (dataSource instanceof HikariDataSource hikari) {
            //log.info("Tentando conectar: user='{}', jdbcUrl='{}'",
                    //hikari.getUsername(), hikari.getJdbcUrl());
        //}

        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(5)) {
                throw new IllegalStateException("Conexão com o banco inválida ou expirou.");
            }
            //log.info("Conexão com o banco validada com sucesso.");
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao validar conexão com o banco: " + e.getMessage(), e);
        }
    }

    /**
     * Executa uma consulta esperando resultado escalar (primeira coluna da primeira linha).
     * Se retornar mais de uma coluna, o resultado será serializado como JSON de uma única linha.
     */
    public Object executarSqlPorLinha(String sql) {
        
        verificarConexao();

        String trimmed = sql.trim();
        if (!trimmed.toLowerCase().startsWith("select")) {
            throw new IllegalArgumentException("Somente instruções SELECT são permitidas por segurança.");
        }

        List<Map<String, Object>> linhas = jdbcTemplate.queryForList(sql);
        if (linhas.isEmpty()) return null;

        Map<String, Object> primeira = linhas.get(0);
        if (primeira.size() == 1) {
            return primeira.values().iterator().next();
        } else {
            try {
                return objectMapper.writeValueAsString(primeira);
            } catch (Exception e) {
                log.warn("Falha ao serializar linha como JSON: {}", e.getMessage());
                return primeira.toString();
            }
        }
    }
}
