package org.scouts105bentaya.features.confirmation.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.shared.Group;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//todo clean a little bit
@Slf4j
@Service
public class AttendanceExcelReportService {

    private static final int ATTENDING = 2;
    private static final int NOT_ATTENDING_JUSTIFIED = 1;
    private static final int NOT_ATTENDING = 0;
    private static final String NOT_IN_GROUP = "-";

    private static final CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();

    private static final CellReference GROUP_CELL = new CellReference("C2");
    private static final int FIRST_SCOUT_ROW = 5;
    private static final int FIRST_EVENT_ROW = 3;
    private static final int FIRST_EVENT_COLUMN = 2;


    private final EventService eventService;
    private final AuthService authService;

    public AttendanceExcelReportService(
        EventService eventService,
        AuthService authService
    ) {
        this.eventService = eventService;
        this.authService = authService;
    }

    private XSSFCell getCell(XSSFSheet sheet, CellReference cellReference) {
        return sheet.getRow(cellReference.getRow()).getCell(cellReference.getCol());
    }

    private List<Event> getEvents(Group group) {
        List<Event> events = this.eventService.findAllByGroupIdAndActivatedAttendance(group);
        events.sort(Comparator.comparing(Event::getStartDate));
        return events;
    }

    private List<Scout> getScoutsInAttendanceList(List<Event> events) {
        return events.stream()
            .map(Event::getConfirmationList)
            .flatMap(Collection::stream)
            .map(Confirmation::getScout)
            .distinct()
            .sorted(Comparator.comparing(Scout::getSurname).thenComparing(Scout::getName).thenComparing(Scout::getId))
            .toList();
    }

    private <T> void copyCellsInRow(XSSFSheet sheet, CellReference firstCellReference, List<T> values) {
        XSSFRow row = sheet.getRow(firstCellReference.getRow());
        XSSFCell cell = getCell(sheet, firstCellReference);
        int width = sheet.getColumnWidth(cell.getColumnIndex());
        setCellValue(cell, values.get(0));

        int col = firstCellReference.getCol();
        for (int i = 1; i < values.size(); i++) {
            cell = row.getCell(++col);
            if (cell == null) {
                cell = row.createCell(col);
            }
            cell.copyCellFrom(row.getCell(col - 1), cellCopyPolicy);
            sheet.setColumnWidth(col, width);
            setCellValue(cell, values.get(i));
        }
    }

    private <T> void setCellValue(XSSFCell cell, T value) {
        if (value instanceof String string) {
            cell.setCellValue(string);
        } else if (value instanceof Integer integer) {
            cell.setCellValue(integer);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
        }
    }


    private void addEvents(XSSFSheet sheet, List<Event> events) {
        CellReference firstEventReference = new CellReference(FIRST_EVENT_ROW, FIRST_EVENT_COLUMN);
        CellReference firstEventNameReference = new CellReference(FIRST_EVENT_ROW + 1, FIRST_EVENT_COLUMN);
        CellReference firstScoutAttendanceReference = new CellReference(FIRST_SCOUT_ROW, FIRST_EVENT_COLUMN);

        List<String> dates = events.stream()
            .map(event -> event.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")))
            .collect(Collectors.toList());
        dates.add("");

        List<String> eventNumbers = IntStream.range(1, events.size() + 1)
            .mapToObj("Reu %d"::formatted)
            .collect(Collectors.toList());
        eventNumbers.add("Control");

        List<Integer> attendanceMarking = Collections.nCopies(events.size() + 1, -1);

        copyCellsInRow(sheet, firstEventReference, dates);
        copyCellsInRow(sheet, firstEventNameReference, eventNumbers);
        copyCellsInRow(sheet, firstScoutAttendanceReference, attendanceMarking);
    }

    private void generateScoutRow(XSSFSheet sheet, Scout scout, int index, List<Event> events) {
        if (index > 0) sheet.copyRows(FIRST_SCOUT_ROW, FIRST_SCOUT_ROW, FIRST_SCOUT_ROW + index, cellCopyPolicy);
        XSSFRow row = sheet.getRow(FIRST_SCOUT_ROW + index);
        row.getCell(0).setCellValue(index + 1d);
        row.getCell(1).setCellValue("%s, %s".formatted(scout.getSurname(), scout.getName()).toUpperCase());

        List<Confirmation> confirmations = scout.getConfirmationList();

        IntStream.range(0, events.size())
            .forEach(i -> {
                Event event = events.get(i);
                addConfirmation(row, confirmations.stream().filter(c -> event.equals(c.getEvent())).findFirst(), i);
            });

        if (index < 1) {
            XSSFCell cell = row.createCell(FIRST_EVENT_COLUMN + events.size());
            XSSFCellStyle style = cell.getCellStyle().copy();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setDataFormat(10);
            cell.setCellStyle(style);
        }

        addConfirmationControl(row, events.size());
    }

    private void addConfirmationControl(XSSFRow row, int events) {
        XSSFCell cell = row.getCell(FIRST_EVENT_COLUMN + events);
        String firstCell = row.getCell(FIRST_EVENT_COLUMN).getReference();
        String lastCell = row.getCell(FIRST_EVENT_COLUMN + events - 1).getReference();
        cell.setCellFormula("COUNTIF(%1$s:%2$s,\"=\"&M2)/COUNTIF(%1$s:%2$s,\"<>\"&P2)".formatted(firstCell, lastCell));
    }

    private void addConfirmation(XSSFRow row, Optional<Confirmation> optionalConfirmation, int eventNumber) {
        XSSFCell cell = row.getCell(eventNumber + FIRST_EVENT_COLUMN);
        if (optionalConfirmation.isPresent()) {
            Confirmation confirmation = optionalConfirmation.get();
            if (Boolean.TRUE.equals(confirmation.getAttending())) {
                cell.setCellValue(ATTENDING);
            } else if (StringUtils.isBlank(confirmation.getText())) {
                cell.setCellValue(NOT_ATTENDING);
            } else {
                cell.setCellValue(NOT_ATTENDING_JUSTIFIED);
                XSSFComment comment = row.getSheet().createDrawingPatriarch().createCellComment(new XSSFClientAnchor());
                comment.setString(confirmation.getText());
                cell.setCellComment(comment);
            }
        } else {
            cell.setCellValue(NOT_IN_GROUP);
        }
    }

    private void addTotalRow(XSSFSheet sheet, int totalScouts, int eventNumber) {
        int newRow = FIRST_SCOUT_ROW + totalScouts;
        sheet.copyRows(FIRST_SCOUT_ROW, FIRST_SCOUT_ROW, newRow, cellCopyPolicy);
        XSSFRow row = sheet.getRow(newRow);

        XSSFCell totalCell = row.getCell(0);
        totalCell.setCellValue("ASISTENCIA POR REUNIÓN Y MEDIA TOTAL");
        XSSFCellStyle style = totalCell.getCellStyle();
        sheet.addMergedRegion(new CellRangeAddress(newRow, newRow, 0, 1));

        XSSFRow firstRow = sheet.getRow(FIRST_SCOUT_ROW);
        XSSFRow lastRow = sheet.getRow(FIRST_SCOUT_ROW + totalScouts - 1);
        for (int i = 0; i < eventNumber; i++) {
            XSSFCell cell = row.createCell(2 + i);
            String firstCell = firstRow.getCell(cell.getColumnIndex()).getReference();
            String lastCell = lastRow.getCell(cell.getColumnIndex()).getReference();
            cell.setCellFormula("COUNTIF(%1$s:%2$s,\"=\"&M2)".formatted(firstCell, lastCell));
            cell.setCellStyle(style);
        }

        XSSFCell cell = row.createCell(2 + eventNumber);
        String firstCell = row.getCell(FIRST_EVENT_COLUMN).getReference();
        String lastCell = lastRow.getCell(FIRST_EVENT_COLUMN + eventNumber - 1).getReference();
        cell.setCellFormula("AVERAGE(%1$s:%2$s)".formatted(firstCell, lastCell));
        cell.setCellStyle(style);
    }

    private void updateValidationsAndFormats(XSSFSheet sheet, int scouts, int events) {
        CellRangeAddress cellAddresses = new CellRangeAddress(FIRST_SCOUT_ROW, scouts + FIRST_SCOUT_ROW - 1, FIRST_EVENT_COLUMN, events + FIRST_EVENT_COLUMN - 1);
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList();
        cellRangeAddressList.addCellRangeAddress(cellAddresses);

        XSSFDataValidation validation = sheet.getDataValidations().get(0);
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validation.getValidationConstraint();
        DataValidation newValidation = helper.createValidation(constraint, cellRangeAddressList);
        sheet.getDataValidations().remove(validation);
        sheet.addValidationData(newValidation);

        sheet.getSheetConditionalFormatting().getConditionalFormattingAt(0).setFormattingRanges(new CellRangeAddress[]{cellAddresses});
        cellAddresses = new CellRangeAddress(FIRST_SCOUT_ROW, scouts + FIRST_SCOUT_ROW - 1, events + FIRST_EVENT_COLUMN, events + FIRST_EVENT_COLUMN);
        sheet.getSheetConditionalFormatting().getConditionalFormattingAt(1).setFormattingRanges(new CellRangeAddress[]{cellAddresses});
    }

    private void addAttendances(XSSFSheet sheet, List<Scout> scouts, List<Event> events) {
        IntStream.range(0, scouts.size()).forEach(i -> generateScoutRow(sheet, scouts.get(i), i, events));
        addTotalRow(sheet, scouts.size(), events.size());

        updateValidationsAndFormats(sheet, scouts.size(), events.size());

        sheet.setColumnWidth(events.size() + FIRST_EVENT_COLUMN, 256 * 13);
    }

    public ByteArrayOutputStream getGroupAttendanceAsExcel() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        User loggedUser = authService.getLoggedUser();
        List<Event> events = getEvents(loggedUser.getGroupId());
        List<Scout> scouts = getScoutsInAttendanceList(events);

        try (InputStream file = new ClassPathResource("excel/attendance_template.xlsx").getInputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFCell groupCell = getCell(sheet, GROUP_CELL);
            groupCell.setCellValue(loggedUser.getGroupId().name());

            addEvents(sheet, events);
            addAttendances(sheet, scouts, events);

            createAttendanceBarChart(workbook.getSheetAt(1), sheet, FIRST_EVENT_COLUMN + events.size() - 1, FIRST_SCOUT_ROW + scouts.size());

            workbook.write(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return outputStream;
    }

    private void createAttendanceBarChart(XSSFSheet barChartSheet, XSSFSheet dataSheet, int lastCol, int lastRow) {
        XSSFDrawing drawing = barChartSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 1, 10, 25);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("ASISTENCIA DE EDUCANDAS");
        chart.setTitleOverlay(false);

        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(dataSheet, new CellRangeAddress(FIRST_EVENT_ROW, FIRST_EVENT_ROW, FIRST_EVENT_COLUMN, lastCol));
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(dataSheet, new CellRangeAddress(lastRow, lastRow, FIRST_EVENT_COLUMN, lastCol));

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
