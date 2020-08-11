package cellphonerecords;

import static java.lang.System.exit;

public class CellPhoneRecords {
    public static void main(String[] args) {

        String argsError = System.getProperty("sun.java.command") + " [year] [CellPhone.csv] [CellPhoneUsageByMonth.csv]";
        String cellPhoneRecordCSV = System.getProperty("user.dir") + "/CellPhone.csv";
        String cellPhoneUsageRecordCSV = System.getProperty("user.dir") + "/CellPhoneUsageByMonth.csv";
        Integer year = null;

        int count = 0;

        if (args.length > count) {
            year = Integer.parseInt(args[count]);
            if (year <= 0) {
                System.err.println(argsError);
                exit(-1);
            }
            count++;
        }

        if (args.length > count) {
            cellPhoneRecordCSV = args[count];
            count++;
        }
        if (args.length > count) {
            cellPhoneUsageRecordCSV = args[count];
            count++;
        }

        if (args.length > count) {
            System.err.println(argsError);
            exit(-1);
        }

        CellPhoneService cellPhoneService = new CellPhoneService();
        cellPhoneService.readCellPhonesCSV(cellPhoneRecordCSV);
        CellPhoneUsageService cellPhoneUsageService = new CellPhoneUsageService(cellPhoneService);
        if (!cellPhoneUsageService.processCellPhoneUsageCSV(cellPhoneUsageRecordCSV, year)) {
            if (year == null) {
                System.err.println("Error: no records to print.");
            } else {
                System.err.printf("Error: no records to print for year %d.\n", year);
            }
            exit(-1);
        }
        cellPhoneService.printStats(46);
    }
}
