package com.example.excelimport.export;

import com.example.excelimport.model.BesetzungsEintrag;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class ExcelExportService {

    public static InputStream generateExcel(List<BesetzungsEintrag> daten) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Besetzungsanalyse");

            // Header-Zeile
            Row header = sheet.createRow(0);
            String[] headers = {"Org.-Einheit", "Beschreibung", "IST", "SOLL", "Differenz", "Status"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            // Datenzeilen
            int rowIdx = 1;
            for (BesetzungsEintrag e : daten) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getOrgEinheit());
                row.createCell(1).setCellValue(e.getBeschreibung());
                row.createCell(2).setCellValue(e.getIst());
                row.createCell(3).setCellValue(e.getSoll());
                row.createCell(4).setCellValue(e.getDifferenz());
                row.createCell(5).setCellValue(e.getStatus());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
            throw new RuntimeException("Excel-Export fehlgeschlagen", ex);
        }
    }
}
