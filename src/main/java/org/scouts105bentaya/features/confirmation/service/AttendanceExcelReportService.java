package org.scouts105bentaya.features.confirmation.service;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.util.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//todo: clean code
//todo: change excel template and remove empty cells with width, set this width at runtime
//todo: create helper to avoid stateful variables
//todo: insignia kaa
@Service
public class AttendanceExcelReportService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceExcelReportService.class);
    private static final int FIRST_EVENT_COLUMN_INDEX = 3;
    private static final int EVENT_ROW_INDEX = 1;
    private static final int FIRST_SCOUT_ROW_INDEX = 4;
    private final EventService eventService;
    private final AuthService authService;
    private final SettingService settingService;
    private Workbook workbook;
    private Sheet sheet;
    private CellStyle idCellStyle;
    private CellStyle nameCellStyle;
    private CellStyle dateCellStyle;
    private CellStyle defaultStyle;
    private List<Scout> scoutsInAttendanceList;

    public AttendanceExcelReportService(
        EventService eventService,
        AuthService authService,
        SettingService settingService
    ) {
        this.eventService = eventService;
        this.authService = authService;
        this.settingService = settingService;
    }

    public ByteArrayOutputStream getGroupAttendanceAsExcel() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        User loggedUser = authService.getLoggedUser();
        List<Event> events = this.eventService.findAllByGroupIdAndActivatedAttendance(loggedUser.getGroupId());
        events.sort(Comparator.comparing(Event::getStartDate));

        this.scoutsInAttendanceList = events.stream()
            .map(Event::getConfirmationList)
            .flatMap(Collection::stream)
            .map(Confirmation::getScout)
            .distinct()
            .sorted(Comparator.comparing(Scout::getSurname).thenComparing(Scout::getName).thenComparing(Scout::getId))
            .toList();

        try (InputStream file = new ClassPathResource("excel/groupAttendanceTemplate.xlsx").getInputStream()) {
            this.workbook = new XSSFWorkbook(file);
            this.sheet = this.workbook.getSheetAt(0);

            this.generateCellStyles();

            Map<String, String> variables = new HashMap<>(Map.of("group", loggedUser.getGroupId().name()));
            replaceRowVariables(0, variables);
            replaceRowVariables(1, variables);
            variables.put("year", getCourseYear());
            replaceRowVariables(2, variables);

            addAllScoutsToExcel();
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                String formattedDate = event.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM"));
                addNewEventColumn(FIRST_EVENT_COLUMN_INDEX + i, formattedDate);
                markEventAttendance(event, formattedDate);
            }

            int totalEvents = events.size();
            int totalScouts = scoutsInAttendanceList.size();

            ExcelUtils.mergeRowCells(sheet, 0, totalEvents + FIRST_EVENT_COLUMN_INDEX + 1);

            createTotalAttendanceRow(totalScouts + FIRST_SCOUT_ROW_INDEX, totalEvents + FIRST_EVENT_COLUMN_INDEX);
            createMeanCells(totalScouts + FIRST_SCOUT_ROW_INDEX, totalEvents + FIRST_EVENT_COLUMN_INDEX);
            createScoutTotalAttendanceRow(totalEvents);

            Sheet barChartSheet = workbook.getSheetAt(1);
            createAttendanceBarChart(barChartSheet, totalEvents + 2, totalScouts + FIRST_SCOUT_ROW_INDEX);

            workbook.write(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return outputStream;
    }

    private void markEventAttendance(Event event, String formattedDate) {
        List<Scout> markedScouts = new ArrayList<>();
        List<Confirmation> confirmations = event.getConfirmationList();
        for (Confirmation scoutAttendance : confirmations) {
            if (scoutAttendance.getAttending() != null && scoutAttendance.getAttending()) {
                int scoutRow = getScoutRowIndex(scoutAttendance.getScout());
                markAttendance(scoutRow, formattedDate);
            }
            markedScouts.add(scoutAttendance.getScout());
        }

        scoutsInAttendanceList.stream()
            .filter(scout -> !markedScouts.contains(scout))
            .forEach(scout -> markNotInGroup(getScoutRowIndex(scout), formattedDate));
    }

    private void generateCellStyles() {
        idCellStyle = sheet.getRow(FIRST_SCOUT_ROW_INDEX).getCell(0).getCellStyle();
        nameCellStyle = sheet.getRow(FIRST_SCOUT_ROW_INDEX).getCell(1).getCellStyle();
        dateCellStyle = sheet.getRow(EVENT_ROW_INDEX).getCell(FIRST_EVENT_COLUMN_INDEX).getCellStyle();

        defaultStyle = workbook.createCellStyle();
        defaultStyle.setAlignment(HorizontalAlignment.CENTER);
        defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        defaultStyle.setFont(font);
    }

    private void addAllScoutsToExcel() {
        for (int i = 0; i < scoutsInAttendanceList.size(); i++) {
            Scout scout = scoutsInAttendanceList.get(i);
            Row row = sheet.createRow(FIRST_SCOUT_ROW_INDEX + i);

            Cell cell = row.createCell(0);
            cell.setCellValue((double) i + 1);
            cell.setCellStyle(idCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(scout.getSurname());
            cell.setCellStyle(nameCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(scout.getName());
            cell.setCellStyle(nameCellStyle);
        }
    }

    private void addNewEventColumn(int newColumn, String formattedDate) {
        Cell cell = this.sheet.getRow(EVENT_ROW_INDEX).createCell(newColumn);
        cell.setCellValue(formattedDate);
        cell.setCellStyle(dateCellStyle);
    }


    private void replaceRowVariables(int rowIndex, Map<String, String> variables) {
        Pattern pattern = Pattern.compile("\\{\\{([^}]*)}}");

        Row row = sheet.getRow(rowIndex);

        row.forEach(cell -> {
            if (cell.getCellType() == CellType.STRING) {
                String cellValue = cell.getStringCellValue();
                Matcher matcher = pattern.matcher(cellValue);
                StringBuilder sb = new StringBuilder();
                while (matcher.find()) {
                    String key = matcher.group(1);
                    if (variables.containsKey(key)) {
                        String replacement = variables.get(key);
                        matcher.appendReplacement(sb, replacement);
                        matcher.appendTail(sb);
                        cell.setCellValue(sb.toString());
                    }
                }
            }
        });
    }

    private void markAttendance(int rowIndex, String date) {
        Row row = this.sheet.getRow(rowIndex);
        int column = findCellToMarkAttendance(date);
        if (column >= FIRST_EVENT_COLUMN_INDEX) {
            Cell cell = row.createCell(column);
            cell.setCellStyle(defaultStyle);
            cell.setCellValue("X");
        }
    }

    private void markNotInGroup(int rowIndex, String date) {
        Row row = this.sheet.getRow(rowIndex);
        int column = findCellToMarkAttendance(date);
        if (column >= FIRST_EVENT_COLUMN_INDEX) {
            Cell cell = row.createCell(column);
            cell.setCellStyle(defaultStyle);
            cell.setCellValue("-");
        }
    }

    private int findCellToMarkAttendance(String date) {
        Row datesRow = this.sheet.getRow(1);
        for (int i = 2; i < datesRow.getLastCellNum(); i++) {
            Cell dateCell = datesRow.getCell(i);
            if (dateCell != null) {
                String dateCellValue = dateCell.getStringCellValue();
                if (dateCellValue.equals(date)) {
                    return dateCell.getColumnIndex();
                }
            }
        }
        return -1;
    }

    private void createTotalAttendanceRow(int rowIndex, int lastColumn) {
        Row row = sheet.getRow(rowIndex);
        Cell totalCell = row.createCell(2);
        totalCell.setCellValue("TOTAL");
        totalCell.setCellStyle(ExcelUtils.getCellStyle(3, 2, sheet));

        CellStyle formulaCellStyle = workbook.createCellStyle();
        formulaCellStyle.cloneStyleFrom(defaultStyle);
        formulaCellStyle.setBorderTop(BorderStyle.THIN);

        for (int i = 3; i < lastColumn; i++) {
            Cell currentCell = row.getCell(i);
            if (currentCell == null) {
                currentCell = row.createCell(i);
            }
            String firstCellInColumn = new CellReference(4, i).formatAsString();
            String lastCellInColumn = new CellReference(rowIndex - 1, i).formatAsString();
            String formula = "COUNTIF(%s:%s,\"X\")".formatted(firstCellInColumn, lastCellInColumn);
            currentCell.setCellFormula(formula);

            currentCell.setCellStyle(formulaCellStyle);
        }
    }

    private void createScoutTotalAttendanceRow(int totalEvents) {
        CellStyle formulaCellStyle = workbook.createCellStyle();
        formulaCellStyle.cloneStyleFrom(defaultStyle);
        formulaCellStyle.setBorderLeft(BorderStyle.THIN);

        CellStyle percentageStyle = workbook.createCellStyle();
        percentageStyle.cloneStyleFrom(defaultStyle);
        percentageStyle.setDataFormat(workbook.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(10)));


        for (int i = 0; i < scoutsInAttendanceList.size(); i++) {
            int scoutRowIndex = FIRST_SCOUT_ROW_INDEX + i;
            Row row = sheet.getRow(scoutRowIndex);
            Cell numberOfTimesCell = row.createCell(FIRST_SCOUT_ROW_INDEX + totalEvents - 1);
            Cell percentageCell = row.createCell(FIRST_SCOUT_ROW_INDEX + totalEvents);
            String firstCell = new CellReference(scoutRowIndex, FIRST_EVENT_COLUMN_INDEX).formatAsString();
            String lastCell = new CellReference(scoutRowIndex, FIRST_EVENT_COLUMN_INDEX + totalEvents - 1).formatAsString();
            numberOfTimesCell.setCellFormula("COUNTIF(%s:%s,\"X\")&\" / \"&COUNTIF(%s:%s,\"<>-\")".formatted(firstCell, lastCell, firstCell, lastCell));
            percentageCell.setCellFormula("COUNTIF(%s:%s,\"X\") / COUNTIF(%s:%s,\"<>-\")".formatted(firstCell, lastCell, firstCell, lastCell));
            numberOfTimesCell.setCellStyle(formulaCellStyle);
            percentageCell.setCellStyle(percentageStyle);
        }

        sheet.setColumnWidth(FIRST_SCOUT_ROW_INDEX + totalEvents - 1, 256 * 11);
        sheet.setColumnWidth(FIRST_SCOUT_ROW_INDEX + totalEvents, 256 * 11);
    }

    private void createMeanCells(int rowIndex, int col) {
        CellStyle meanValueCellStyle = workbook.createCellStyle();
        meanValueCellStyle.cloneStyleFrom(defaultStyle);

        CellReference firstCellInRow = new CellReference(rowIndex, 3);
        CellReference lastCellInRow = new CellReference(rowIndex, col - 1);

        Row row = sheet.getRow(rowIndex + 1);
        Cell meanCell = row.createCell(col);
        String formula = "AVERAGE(" + firstCellInRow.formatAsString() + ":" + lastCellInRow.formatAsString() + ")";
        meanCell.setCellFormula(formula);

        meanValueCellStyle.setBorderBottom(BorderStyle.THIN);
        meanValueCellStyle.setBorderLeft(BorderStyle.THIN);
        meanValueCellStyle.setBorderRight(BorderStyle.THIN);
        meanCell.setCellStyle(meanValueCellStyle);

        CellStyle meanCellStyle = workbook.createCellStyle();
        meanCellStyle.cloneStyleFrom(ExcelUtils.getCellStyle(0, 0, sheet));
        meanCellStyle.setBorderLeft(BorderStyle.THIN);
        meanCellStyle.setBorderRight(BorderStyle.THIN);
        meanCellStyle.setBorderTop(BorderStyle.THIN);

        Font meanFont = workbook.createFont();
        meanFont.setFontName("Arial");
        meanFont.setFontHeightInPoints((short) 12);
        meanFont.setColor(IndexedColors.WHITE1.getIndex());
        meanFont.setBold(true);
        meanCellStyle.setFont(meanFont);

        Row textRow = sheet.getRow(rowIndex);
        Cell textCell = textRow.createCell(col);
        textCell.setCellValue("MEDIA");
        textCell.setCellStyle(meanCellStyle);
    }

    private int getScoutRowIndex(Scout scout) {
        return scoutsInAttendanceList.indexOf(scout) + FIRST_SCOUT_ROW_INDEX;
    }

    private String getCourseYear() {
        int lastYearOfCurrentTerm = Integer.parseInt(settingService.findByName("currentYear").getValue());
        int firstYear = lastYearOfCurrentTerm - 1;
        int centuryYear = lastYearOfCurrentTerm % 100;
        return "%d/%d".formatted(firstYear, centuryYear);
    }

    private void createAttendanceBarChart(Sheet barChartSheet, int lastCol, int lastRow) {
        XSSFDrawing drawing = (XSSFDrawing) barChartSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 1, 10, 25);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("ASISTENCIA DE EDUCANDAS");
        chart.setTitleOverlay(false);

        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet, new CellRangeAddress(1, 1, 3, lastCol));
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet, new CellRangeAddress(lastRow, lastRow, 3, lastCol));

        XDDFCategoryAxis xAxis = chart.createCategoryAxis(AxisPosition.LEFT);
        XDDFValueAxis yAxis = chart.createValueAxis(AxisPosition.BOTTOM);
        yAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFChartData data = chart.createData(ChartTypes.BAR, xAxis, yAxis);
        ((XDDFBarChartData) data).setBarDirection(BarDirection.COL);
        ((XDDFBarChartData) data).setGapWidth(100);

        XDDFChartData.Series series = data.addSeries(categories, values);
        series.setTitle("Values", null);

        chart.plot(data);
    }
}
