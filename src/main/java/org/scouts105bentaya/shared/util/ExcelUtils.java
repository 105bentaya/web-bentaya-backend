package org.scouts105bentaya.shared.util;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.time.LocalDate;
import java.time.Period;

public class ExcelUtils {

    private ExcelUtils() {
    }

    public static void mergeRowCells(Sheet sheet, int rowIndex, int lastCol) {
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
            CellRangeAddress cellRange = new CellRangeAddress(rowIndex, rowIndex, 0, lastCol);
            sheet.addMergedRegion(cellRange);
        }
    }

    public static CellStyle getCellStyle(int row, int column, Sheet sheet) {
        Row cellToGetStyleRow = sheet.getRow(row);
        Cell cellToCopyStyle = cellToGetStyleRow.getCell(column);

        return cellToCopyStyle.getCellStyle();
    }

    public static void autosizeSheet(XSSFSheet sheet, int colNumbers) {
        for (int i = 0; i < colNumbers; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(65280, sheet.getColumnWidth(i) + 1000));
        }
    }

    public static void createTable(XSSFSheet sheet, int rows, int cols) {
        sheet.createTable(new AreaReference(
            new CellReference(0, 0),
            new CellReference(rows, cols),
            SpreadsheetVersion.EXCEL2007
        ));
    }

    public static String getAge(LocalDate birthDate) {
        Period age = Period.between(birthDate, LocalDate.now());
        return "%d años, %d meses, %d días".formatted(age.getYears(), age.getMonths(), age.getDays());
    }
}
