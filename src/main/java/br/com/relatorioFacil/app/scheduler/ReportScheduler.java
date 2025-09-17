package br.com.relatorioFacil.app.scheduler;

import br.com.relatorioFacil.app.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportScheduler {

    private final ReportService reportService;

    // Padrão: 06:00 nos dias 1 e 16. Pode ser sobrescrito por propriedade.
    @Scheduled(cron = "${relatorio.schedule-cron:0 0 6 1,16 * *}", zone = "${relatorio.schedule-zone:America/Fortaleza}")
    public void executarAgendado() {
        log.info("Iniciando geração agendada de relatório...");
        try {
            reportService.gerarEnviarRelatorio();
            log.info("Geração e envio concluídos com sucesso.");
        } catch (Exception e) {
            log.error("Falha ao gerar/enviar relatório: {}", e.getMessage(), e);
        }
    }
}
