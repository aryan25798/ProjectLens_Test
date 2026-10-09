package com.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    private String filePath;

    public ExcelReader(String filePath) {
        this.filePath = filePath;
    }

    public List<Map<String, String>> getData(String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in " + filePath);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return dataList;
            }

            int colCount = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int c = 0; c < colCount; c++) {
                Cell cell = headerRow.getCell(c);
                headers.add(cell != null ? getCellValue(cell) : "Col_" + c);
            }

            int rowCount = sheet.getLastRowNum();
            for (int r = 1; r <= rowCount; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Map<String, String> rowMap = new HashMap<>();
                boolean hasContent = false;
                for (int c = 0; c < colCount; c++) {
                    Cell cell = row.getCell(c);
                    String val = cell != null ? getCellValue(cell) : "";
                    if (!val.trim().isEmpty()) {
                        hasContent = true;
                    }
                    rowMap.put(headers.get(c), val);
                }
                if (hasContent) {
                    dataList.add(rowMap);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + filePath + " | " + e.getMessage(), e);
        }
        return dataList;
    }

    public Map<String, String> getTestCaseData(String sheetName, String testCaseId) {
        List<Map<String, String>> allData = getData(sheetName);
        for (Map<String, String> row : allData) {
            if (testCaseId.equalsIgnoreCase(row.get("TC_ID"))) {
                return row;
            }
        }
        throw new RuntimeException("Test case ID '" + testCaseId + "' not found in sheet '" + sheetName + "'");
    }

    private String getCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
