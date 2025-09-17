package br.com.relatorioFacil.app.web;

import br.com.relatorioFacil.app.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class ManualController {

    private final ReportService reportService;

    @PostMapping("/gerar")
    public ResponseEntity<String> gerarAgora() {
        Path path = reportService.gerarEnviarRelatorio();
        return ResponseEntity.ok("Gerado: " + path.toAbsolutePath());
    }
}
