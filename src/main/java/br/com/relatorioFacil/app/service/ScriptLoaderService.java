package br.com.relatorioFacil.app.service;

import br.com.relatorioFacil.app.model.ConsultaSql;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScriptLoaderService {

    private final ObjectMapper objectMapper;

    @Value("${relatorio.scripts-dir:./scripts}")
    private String scriptsDir;

    public List<ConsultaSql> carregarConsultas() {
        List<ConsultaSql> consultas = new ArrayList<>();
        Path dir = Paths.get(scriptsDir);

        if (!Files.exists(dir)) {
            log.warn("Diretório de scripts não existe: {}", dir.toAbsolutePath());
            return consultas;
        }

        try (Stream<Path> files = Files.list(dir)) {
            List<Path> txtFiles = files
                    .filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt"))
                    .sorted()
                    .collect(Collectors.toList());

            for (Path file : txtFiles) {
                String content = Files.readString(file, StandardCharsets.UTF_8).trim();
                if (StringUtils.isBlank(content)) {
                    log.warn("Arquivo vazio ignorado: {}", file.getFileName());
                    continue;
                }

                try {
                    if (content.startsWith("[")) {
                       
                        List<ConsultaSql> lista = objectMapper.readValue(content, new TypeReference<List<ConsultaSql>>() {});

                        consultas.addAll(lista);
                    } else {
                        
                        List<ConsultaSql> lista = new ArrayList<>();

                        for (String line : content.split("\\\\R")) {
                            if (StringUtils.isBlank(line)) continue;
                            lista.add(objectMapper.readValue(line, ConsultaSql.class));
                        }

                        consultas.addAll(lista);
                    }

                    log.info("Carregado(s) {} item(ns) de {}", consultas.size(), file.getFileName());

                } catch (Exception ex) {
                    log.error("Falha ao parsear {}: {}", file.getFileName(), ex.getMessage());
                    throw ex;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler diretório de scripts: " + e.getMessage(), e);
        }

        // Validação
        consultas.removeIf(c -> c.getTitulo() == null || c.getSql() == null);
        return consultas;
    }
}
