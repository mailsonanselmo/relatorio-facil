package br.com.relatorioFacil.app.service;

import br.com.relatorioFacil.app.model.ConsultaSql;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ScriptLoaderService scriptLoaderService;
    private final DatabaseService databaseService;
    private final ExcelService excelService;
    private final EmailService emailService;

    public Path gerarEnviarRelatorio() {
        List<ConsultaSql> consultas = scriptLoaderService.carregarConsultas();
        if (consultas.isEmpty()) {
            log.warn("Nenhuma consulta encontrada.");
        }

        List<Map.Entry<String, Object>> linhas = new ArrayList<>();
        for (ConsultaSql c : consultas) {
            Object valor = databaseService.executarSqlPorLinha(c.getSql());
            linhas.add(new AbstractMap.SimpleEntry<>(c.getTitulo(), valor));
        }

        Path arquivo = excelService.gerarExcel(linhas);
        emailService.enviarComAnexo(arquivo.toFile());
        return arquivo;
    }
}
