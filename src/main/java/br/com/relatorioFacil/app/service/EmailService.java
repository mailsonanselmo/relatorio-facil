package br.com.relatorioFacil.app.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${relatorio.mail.to:}")
    private String toList;

    @Value("${relatorio.mail.subject:Relatório SQL}")
    private String subject;

    @Value("${relatorio.mail.body:Segue em anexo o relatório gerado automaticamente.}")
    private String body;

    @Value("${relatorio.timezone:America/Fortaleza}")
    private String timezone;

    public void enviarComAnexo(File arquivo) {
        try {
            List<String> destinatarios = Arrays.stream(toList.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            if (destinatarios.isEmpty()) {
                log.warn("Nenhum destinatário configurado em relatorio.mail.to");
                return;
            }

            // pega data da execução
            String dataHoje = LocalDate.now(ZoneId.of(timezone))
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // substitui {data} no subject
            String subjectFinal = subject.replace("{data}", dataHoje);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(destinatarios.toArray(new String[0]));
            helper.setSubject(subjectFinal);
            helper.setText(body, false);

            FileSystemResource file = new FileSystemResource(arquivo);
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
            //log.info("E-mail enviado para {} com assunto '{}'", destinatarios, subjectFinal);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
        }
    }
}