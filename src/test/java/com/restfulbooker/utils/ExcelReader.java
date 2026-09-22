package com.restfulbooker.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.restfulbooker.config.ConfigManager;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.CellType;

public final class ExcelReader {

    private static final DateTimeFormatter API_DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;
   // private static final String DEFAULT_EXCEL_FILE_PATH =ConfigManager.getProperty("excel.file.path");

    private ExcelReader() {
        // Prevent object creation
    }

//    public static Map<String, String> getRowData(
//            String sheetName,
//            String testCaseId
//    ) {
//
//        return getRowData(
//                DEFAULT_EXCEL_FILE_PATH,
//                sheetName,
//                testCaseId
//        );
  //  }

    public static Map<String, String> getRowData(
            String filePath,
            String sheetName,
            String testCaseId
    ) {

        InputStream inputStream = ExcelReader.class
                .getClassLoader()
                .getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "Excel file was not found: " + filePath
            );
        }

        try (
                inputStream;
                Workbook workbook =
                        WorkbookFactory.create(inputStream)
        ) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new IllegalArgumentException(
                        "Excel sheet was not found: " + sheetName
                );
            }

            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new IllegalStateException(
                        "Header row is missing in sheet: "
                                + sheetName
                );
            }

            DataFormatter dataFormatter =
                    new DataFormatter();

            FormulaEvaluator formulaEvaluator = workbook
                    .getCreationHelper()
                    .createFormulaEvaluator();

            int testCaseIdColumn = findColumnIndex(
                    headerRow,
                    "TestCaseId",
                    dataFormatter,
                    formulaEvaluator
            );

            for (
                    int rowIndex = 1;
                    rowIndex <= sheet.getLastRowNum();
                    rowIndex++
            ) {

                Row currentRow = sheet.getRow(rowIndex);

                if (currentRow == null) {
                    continue;
                }

                String currentTestCaseId = formatCellValue(
                        currentRow,
                        testCaseIdColumn,
                        dataFormatter,
                        formulaEvaluator
                );

                if (testCaseId.equalsIgnoreCase(
                        currentTestCaseId
                )) {
                    return convertRowToMap(
                            headerRow,
                            currentRow,
                            dataFormatter,
                            formulaEvaluator
                    );
                }
            }

            throw new IllegalArgumentException(
                    "Test case ID was not found in Excel: "
                            + testCaseId
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to read Excel file: " + filePath,
                    exception
            );
        }
    }

    private static int findColumnIndex(
            Row headerRow,
            String columnName,
            DataFormatter dataFormatter,
            FormulaEvaluator formulaEvaluator
    ) {

        for (
                int columnIndex = 0;
                columnIndex < headerRow.getLastCellNum();
                columnIndex++
        ) {

            String headerValue = formatCellValue(
                    headerRow,
                    columnIndex,
                    dataFormatter,
                    formulaEvaluator
            );

            if (columnName.equalsIgnoreCase(headerValue)) {
                return columnIndex;
            }
        }

        throw new IllegalArgumentException(
                "Column was not found in Excel: " + columnName
        );
    }

    private static Map<String, String> convertRowToMap(
            Row headerRow,
            Row dataRow,
            DataFormatter dataFormatter,
            FormulaEvaluator formulaEvaluator
    ) {

        Map<String, String> rowData =
                new LinkedHashMap<>();

        for (
                int columnIndex = 0;
                columnIndex < headerRow.getLastCellNum();
                columnIndex++
        ) {

            String columnName = formatCellValue(
                    headerRow,
                    columnIndex,
                    dataFormatter,
                    formulaEvaluator
            );

            String cellValue = formatCellValue(
                    dataRow,
                    columnIndex,
                    dataFormatter,
                    formulaEvaluator
            );

            rowData.put(columnName, cellValue);
        }

        return rowData;
    }

    private static String formatCellValue(
        Row row,
        int columnIndex,
        DataFormatter dataFormatter,
        FormulaEvaluator formulaEvaluator
) {

    Cell cell = row.getCell(columnIndex);

    if (cell == null) {
        return "";
    }

    if (
            cell.getCellType() == CellType.NUMERIC
            && DateUtil.isCellDateFormatted(cell)
    ) {

        LocalDate date = cell
                .getLocalDateTimeCellValue()
                .toLocalDate();

        return date.format(API_DATE_FORMAT);
    }

    return dataFormatter
            .formatCellValue(
                    cell,
                    formulaEvaluator
            )
            .trim();
}
}