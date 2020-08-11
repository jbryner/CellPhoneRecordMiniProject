package cellphonerecords;

import java.util.Date;

public class CellPhoneUsage {
    private Integer employeeId;
    private Date date;
    private Integer totalMinutes;
    private Float totalData;

    static final String EMPLOYEE_ID = "employeeId";
    static final String DATE = "date";
    static final String TOTAL_MINUTES = "totalMinutes";
    static final String TOTAL_DATA = "totalData";

    public CellPhoneUsage(Integer employeeId, Date date, Integer totalMinutes, Float totalData) {
        this.employeeId = employeeId;
        this.date = date;
        this.totalMinutes = totalMinutes;
        this.totalData = totalData;
    }

    public void add(CellPhoneUsage cellPhoneUsage) {
        this.totalMinutes += cellPhoneUsage.totalMinutes;
        this.totalData += cellPhoneUsage.totalData;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public Date getDate() {
        return date;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public Float getTotalData() {
        return totalData;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public void setTotalData(Float totalData) {
        this.totalData = totalData;
    }
}
