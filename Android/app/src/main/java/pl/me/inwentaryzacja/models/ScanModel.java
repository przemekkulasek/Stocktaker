package pl.me.inwentaryzacja.models;

import java.io.Serializable;
import java.util.Date;

public class ScanModel implements Serializable {

    private int id;
    private String name;
    private String date;
    private String employeeCode;

    public ScanModel(int id, String name, String date, String employeeCode) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.employeeCode = employeeCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() { return date; }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employee_code) {
        this.employeeCode = employee_code;
    }

    @Override
    public String toString() {
        return "ScanModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", employeeCode='" + employeeCode + '\'' +
                '}';
    }
}
