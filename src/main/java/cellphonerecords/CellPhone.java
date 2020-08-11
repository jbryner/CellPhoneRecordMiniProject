package cellphonerecords;

import java.util.*;

public class CellPhone {

    private Integer employeeId;
    private String employeeName;
    private Date purchaseDate;
    private String model;
    private Map<Date, CellPhoneUsage> cellPhoneUsageMap = new TreeMap<>();

    static final String EMPLOYEE_ID = "employeeId";
    static final String EMPLOYEE_NAME = "employeeName";
    static final String PURCHASE_DATE = "purchaseDate";
    static final String MODEL = "model";

    public CellPhone(Integer employeeId, String employeeName, Date purchaseDate, String model) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.purchaseDate = purchaseDate;
        this.model = model;
    }

    public void addCellPhoneUsageInfo(CellPhoneUsage cellPhoneUsage) {
        Date date = cellPhoneUsage.getDate();
        CellPhoneUsage currentUsage = cellPhoneUsageMap.get(date);
        if (currentUsage == null) {
            cellPhoneUsageMap.put(date, cellPhoneUsage);
        } else {
            currentUsage.add(cellPhoneUsage);
        }
    }

    Map<Date, CellPhoneUsage> getCellPhoneUsageMap() {
        return cellPhoneUsageMap;
    }

    public Integer getTotalMinutesFromPhone() {
        return cellPhoneUsageMap.values().stream().mapToInt(o-> o.getTotalMinutes()).sum();
    }

    public Double getTotalDataFromPhone() {
        return cellPhoneUsageMap.values().stream().mapToDouble(o->o.getTotalData()).sum();
    }

    public Integer getMonthCount() {
        return cellPhoneUsageMap.size();
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public String getModel() {
        return model;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setModel(String model) {
        this.model = model;
    }

}
