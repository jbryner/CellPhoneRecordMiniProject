package cellphonerecords;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CellPhoneService implements Printable {
    private Map<Integer, CellPhone> cellPhoneMap = new HashMap<Integer, CellPhone>();
    private ArrayList<String> printLines = new ArrayList<String>();
    private Integer linesPerPage;

    public void readCellPhonesCSV(String csvFile) {
        Reader fileReader = null;
        Iterable<CSVRecord> records = null;
        try {
            fileReader = new FileReader(csvFile);
            records = CSVFormat.RFC4180.withFirstRecordAsHeader().withHeader(
                    CellPhone.EMPLOYEE_ID,
                    CellPhone.EMPLOYEE_NAME,
                    CellPhone.PURCHASE_DATE,
                    CellPhone.MODEL).parse(fileReader);
        } catch (Exception e) {
            System.out.println("Unable to read CSV file : " + e.getMessage());
        }

        for (CSVRecord record : records) {
            try {
                CellPhone cellPhone = new CellPhone(
                        Integer.parseInt(record.get(CellPhone.EMPLOYEE_ID)),
                        record.get(CellPhone.EMPLOYEE_NAME),
                        new SimpleDateFormat("yyyyMMdd").parse(record.get(CellPhone.PURCHASE_DATE)),
                        record.get(CellPhone.MODEL)
                );
                cellPhoneMap.put(cellPhone.getEmployeeId(), cellPhone);
            } catch (ParseException e) {
                System.out.println("Error parsing csv: " + e.getMessage());
            }
        }

        System.out.println(cellPhoneMap.toString());
    }

    public void add(CellPhoneUsage cellPhoneUsage) {
        CellPhone cellPhone = cellPhoneMap.get(cellPhoneUsage.getEmployeeId());
        if (cellPhone == null) {
            System.out.println("Error: No record for employee id " + cellPhone.getEmployeeId());
            return;
        }
        cellPhone.addCellPhoneUsageInfo(cellPhoneUsage);
    }

    private String getDateString(Date d) {
        LocalDate localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return String.format("%04d-%02d-%02d", localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    private List<String> getSummary() {
        LocalDate runDate = java.time.LocalDate.now();
        Integer numberOfPhones = cellPhoneMap.size();
        Long totalMinutes = 0L;
        Double totalData = 0.0;
        Long totalMonths = 0L;
        for (CellPhone cellPhone : cellPhoneMap.values()) {
            totalMinutes += cellPhone.getTotalMinutesFromPhone();
            totalData += cellPhone.getTotalDataFromPhone();
            totalMonths += cellPhone.getMonthCount();
        }
        Double averageMinutes = (double)(totalMinutes) / (double)(totalMonths); // Per phone per month
        Double averageData = totalData / (double)(totalMonths);
        List<String> stringArray = new ArrayList<String>();

        stringArray.add("============== Cell Phone Logs ===============");
        stringArray.add("Run Date        : " + runDate);
        stringArray.add("Number of Phones: " + String.format("%6d", numberOfPhones));
        stringArray.add("Total Minutes   : " + String.format("%6d", totalMinutes));
        stringArray.add("Total Data      : " + String.format("%10.3f", totalData));
        stringArray.add("Total Months    : " + String.format("%6d", totalMonths));
        stringArray.add("Average Minutes : " + String.format("%8.1f", averageMinutes));
        stringArray.add("Average Data    : " + String.format("%10.3f", averageData));
        stringArray.add("===============================================");
        stringArray.add("");
        return stringArray;
    }

    String[] printHeader = {
            "Employee Id    Employee Name          Model                 Purch Date     Bill Date  Minutes Usage   Data Usage",
            "----------------------------------------------------------------------------------------------------------------"
    };

    final Integer HEADER_LINE_WIDTH = printHeader[0].length();

    PrinterJob printerJob;

    public void printStats(Integer linesPerPage) {
        this.linesPerPage = linesPerPage;
        this.printerJob = PrinterJob.getPrinterJob();
        this.printerJob.setPrintable(this);
        boolean doPrint = printerJob.printDialog();
        if (!doPrint) {
            return;
        }

        for (CellPhone cellPhone : cellPhoneMap.values()) {
            Integer employeeId = cellPhone.getEmployeeId();
            String employeeName = cellPhone.getEmployeeName();
            String model = cellPhone.getModel();
            String date = getDateString(cellPhone.getPurchaseDate());
            boolean printCellPhoneInfo = true;
            for (CellPhoneUsage cellPhoneUsage : cellPhone.getCellPhoneUsageMap().values()) {
                StringBuilder strbuf = new StringBuilder(HEADER_LINE_WIDTH);
                strbuf.append(String.format("%11s    ",  printCellPhoneInfo ? employeeId.toString() : ""));
                strbuf.append(String.format("%-21s  ",   printCellPhoneInfo ? employeeName : ""));
                strbuf.append(String.format("%-16s  ",   printCellPhoneInfo ? model : ""));
                strbuf.append(String.format("%14s  ",    printCellPhoneInfo ? date : ""));
                strbuf.append(String.format("%12s  ",    getDateString(cellPhoneUsage.getDate())));
                strbuf.append(String.format("%13d  ",    cellPhoneUsage.getTotalMinutes()));
                strbuf.append(String.format("%11.3f",    cellPhoneUsage.getTotalData()));
                printLines.add(strbuf.toString());
                printCellPhoneInfo = false;
            }
            printLines.add("");
        }
        try {
            this.printerJob.print();
        } catch (PrinterException ex) {
            System.out.println("Printer exception: " + ex.getMessage());
        }

    }

    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        Font font = new Font("Courier", Font.PLAIN, 8);
        FontMetrics metrics = g.getFontMetrics(font);
        int lineHeight = metrics.getHeight() + 6;
        double pageHeight = pf.getImageableHeight();

        int linesPerPage = ((int)pageHeight)/lineHeight;
        int numBreaks = (printLines.size()-1)/linesPerPage;
        int[] pageBreaks = new int[numBreaks];
        for (int b=0; b < numBreaks; b++) {
            pageBreaks[b] = (b+1)*linesPerPage;
        }

        if (this.linesPerPage * page > printLines.size()) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D)g;
        g2d.setFont(font);
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        Integer lineYoffset = 10;
        Integer lineXoffset = 10;
        Integer summaryLines = 0;
        if (page == 0) {
            List<String> summary = getSummary();
            summaryLines = summary.size();
            for (String s : summary) {
                g.drawString(s, lineXoffset, lineYoffset);
                lineYoffset += lineHeight;
            }
            lineYoffset += lineHeight;
        }

        lineYoffset += lineHeight;
        for (Integer i = 0; i < printHeader.length; i++, lineYoffset += lineHeight) {
            g.drawString(printHeader[i], lineXoffset, lineYoffset);
        }

        for (Integer line = 0; line < this.linesPerPage - summaryLines; line++) {
            Integer actualLine = this.linesPerPage * page + line;
            String s = printLines.get(actualLine);
            if (s == null) {
                printPageNumber(g, page, lineXoffset, lineYoffset + (this.linesPerPage-summaryLines+1) * lineHeight);
                return PAGE_EXISTS;
            }
            g.drawString(printLines.get(actualLine), lineXoffset, lineYoffset + line * (lineHeight));
        }
        printPageNumber(g, page, lineXoffset, lineYoffset + (this.linesPerPage-summaryLines+1) * lineHeight);
        return PAGE_EXISTS;
    }

    private void printPageNumber(Graphics g, Integer page, Integer x, Integer y) {
        g.drawString(String.format("%" + this.HEADER_LINE_WIDTH + "s", "page " + Integer.toString(page+1)), x, y);
    }
}
