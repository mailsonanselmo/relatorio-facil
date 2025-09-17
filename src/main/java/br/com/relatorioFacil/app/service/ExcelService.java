package br.com.relatorioFacil.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelService {

    @Value("${relatorio.output-dir:./output}")
    private String outputDir;

    @Value("${relatorio.timezone:America/Fortaleza}")
    private String zoneId;

    public Path gerarExcel(List<Map.Entry<String, Object>> linhas) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório");

            // === Estilos ===
            // Fonte para o cabeçalho
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            // Estilo do cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex()); // cor próxima, mas vamos usar custom
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Customizar cor exata #009b90 (RGB)
            XSSFColor customGreen = new XSSFColor(new java.awt.Color(0, 155, 144), null);
            ((XSSFCellStyle) headerStyle).setFillForegroundColor(customGreen);

            // Estilo das células normais
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // === Cabeçalho ===
            Row header = sheet.createRow(0);
            Cell h0 = header.createCell(0);
            h0.setCellValue("Título");
            h0.setCellStyle(headerStyle);

            Cell h1 = header.createCell(1);
            h1.setCellValue("Resultado");
            h1.setCellStyle(headerStyle);

            // === Dados ===
            int rowIdx = 1;
            for (Map.Entry<String, Object> e : linhas) {
                Row row = sheet.createRow(rowIdx++);

                Cell c0 = row.createCell(0);
                c0.setCellValue(e.getKey());
                c0.setCellStyle(cellStyle);

                Cell c1 = row.createCell(1);
                Object v = e.getValue();
                if (v == null) {
                    c1.setCellValue("");
                } else if (v instanceof Number num) {
                    c1.setCellValue(num.doubleValue());
                } else if (v instanceof Boolean b) {
                    c1.setCellValue(b);
                } else {
                    c1.setCellValue(String.valueOf(v));
                }
                c1.setCellStyle(cellStyle);
            }

            // Ajusta tamanho das colunas
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            // Cria diretório se não existir
            Files.createDirectories(Paths.get(outputDir));

            LocalDateTime now = LocalDateTime.now(ZoneId.of(zoneId));
            String fname = "relatorio-" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".xlsx";
            Path file = Paths.get(outputDir, fname);

            try (FileOutputStream out = new FileOutputStream(file.toFile())) {
                workbook.write(out);
            }

            log.info("Excel gerado em {}", file.toAbsolutePath());
            return file;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel: " + e.getMessage(), e);
        }
    }
}
