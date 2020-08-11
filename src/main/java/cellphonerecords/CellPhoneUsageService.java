package cellphonerecords;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CellPhoneUsageService {
    private CellPhoneService cellPhoneService;

    public CellPhoneUsageService(CellPhoneService cellPhoneService) {
        this.cellPhoneService = cellPhoneService;
    }

    public boolean processCellPhoneUsageCSV(String csvFile, Integer year) {
        Reader fileReader = null;
        Iterable<CSVRecord> records = null;
        try {
            fileReader = new FileReader(csvFile);
            records = CSVFormat.RFC4180.withFirstRecordAsHeader().withHeader(
                    CellPhoneUsage.EMPLOYEE_ID,
                    CellPhoneUsage.DATE,
                    CellPhoneUsage.TOTAL_MINUTES,
                    CellPhoneUsage.TOTAL_DATA).parse(fileReader);
        } catch (Exception e) {
            System.out.println("Unable to read CSV file : " + e.getMessage());
        }

        int count = 0;
        for (CSVRecord record : records) {
            try {
                Date date = new SimpleDateFormat("MM/dd/yyyy").parse(record.get(CellPhoneUsage.DATE));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                if (year != null && year != cal.get(Calendar.YEAR)) {
                    continue;
                }

                CellPhoneUsage cellPhoneUsage = new CellPhoneUsage(
                        Integer.parseInt(record.get(CellPhoneUsage.EMPLOYEE_ID)),
                        new SimpleDateFormat("MM/dd/yyyy").parse(record.get(CellPhoneUsage.DATE)),
                        Integer.parseInt(record.get(CellPhoneUsage.TOTAL_MINUTES)),
                        Float.parseFloat(record.get(CellPhoneUsage.TOTAL_DATA))
                );
                cellPhoneService.add(cellPhoneUsage);
                count++;

            } catch (ParseException e) {
                System.out.println("Error parsing csv: " + e.getMessage());
                continue;
            }
        }
        return count > 0;
    }
}
