package com.smsaSchedulers.Schedulers.Utils;

import com.aspose.cells.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ExcelExportUtilAspose {

    /**
     * Generate an export file (XLSB or pipe-delimited CSV) from
     * List<Map<String, String>>.
     *
     * @param dataList List of data maps (each map = one row)
     * @param sheetName Sheet name (used only for XLSB)
     * @param exportType "XLSB" or "CSV"
     * @return byte[] representing the generated file
     */
    public static byte[] exportData(List<Map<String, String>> dataList, String sheetName, String exportType) throws Exception {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("No data to export");
        }
        if (exportType == null || exportType.isEmpty()) {
            throw new IllegalArgumentException("Export type must be specified (XLSB or CSV)");
        }

        if (exportType.equalsIgnoreCase("XLSB")) {
            return exportToXlsb(dataList, sheetName);
        } else if (exportType.equalsIgnoreCase("CSV")) {
            return exportToPipeDelimitedCsv(dataList);
        } else {
            throw new IllegalArgumentException("Unsupported export type: " + exportType);
        }
    }

    // ==============================================================
    // ✅ XLSB Export (Aspose.Cells)
    // ==============================================================
    private static byte[] exportToXlsb(List<Map<String, String>> dataList, String sheetName) throws Exception {
        Workbook workbook = new Workbook(FileFormatType.XLSB);
        Worksheet sheet = workbook.getWorksheets().get(0);
        sheet.setName(sheetName != null ? sheetName : "Sheet1");

        Cells cells = sheet.getCells();

        // === HEADER ROW ===
        Map<String, String> firstRow = dataList.get(0);
        int headerCol = 0;
        for (String key : firstRow.keySet()) {
            Cell cell = cells.get(0, headerCol);
            cell.putValue(key);

            // Header style
            Style style = cell.getStyle();
            Font font = style.getFont();
            font.setBold(true);
            style.setPattern(BackgroundType.SOLID);
            style.setForegroundColor(Color.getLightGray());
            cell.setStyle(style);

            headerCol++;
        }

        // === DATA ROWS ===
        int rowIndex = 1;
        for (Map<String, String> rowData : dataList) {
            int colIndex = 0;
            for (String value : rowData.values()) {
                cells.get(rowIndex, colIndex++).putValue(value != null ? value : "");
            }
            rowIndex++;
        }

        sheet.autoFitColumns();

        // Write to byte array
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.save(out, SaveFormat.XLSB);
            return out.toByteArray();
        }
    }

    // ==============================================================
    // ✅ CSV Export (Plain Java, Pipe-Delimited)
    // ==============================================================
    private static byte[] exportToPipeDelimitedCsv(List<Map<String, String>> dataList) throws Exception {
        StringBuilder sb = new StringBuilder();

        // === HEADER ROW ===
        Map<String, String> firstRow = dataList.get(0);
        int headerIndex = 0;
        for (String key : firstRow.keySet()) {
            if (headerIndex++ > 0) {
                sb.append('|');
            }
            sb.append('"').append(escapeCsvValue(key)).append('"');
        }
        sb.append("\n");

        // === DATA ROWS ===
        for (Map<String, String> rowData : dataList) {
            int colIndex = 0;
            for (String value : rowData.values()) {
                if (colIndex++ > 0) {
                    sb.append('|');
                }
                sb.append('"').append(escapeCsvValue(value)).append('"');
            }
            sb.append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    // Helper for escaping double quotes inside values
    private static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }
}
