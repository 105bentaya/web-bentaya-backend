package org.scouts105bentaya.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtils {

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
}
