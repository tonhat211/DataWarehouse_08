package model;

public class DateDim {
    private int dateId;             // Surrogate Key (Primary Key)
    private java.sql.Date dateValue; // Actual Date Value
    private int year;               // Year
    private int quarter;            // Quarter
    private int month;              // Month Number
    private String monthName;       // Month Name
    private int week;               // Week Number
    private int day;                // Day of the Month
    private String dayName;         // Day Name
    private boolean isWeekend;      // Indicator for Weekends

    // Constructor
    public DateDim(int dateId, java.sql.Date dateValue, int year, int quarter, int month,
                    String monthName, int week, int day, String dayName, boolean isWeekend) {
        this.dateId = dateId;
        this.dateValue = dateValue;
        this.year = year;
        this.quarter = quarter;
        this.month = month;
        this.monthName = monthName;
        this.week = week;
        this.day = day;
        this.dayName = dayName;
        this.isWeekend = isWeekend;
    }

    // Default Constructor
    public DateDim() {
    }

    // Getters and Setters
    public int getDateId() {
        return dateId;
    }

    public void setDateId(int dateId) {
        this.dateId = dateId;
    }

    public java.sql.Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(java.sql.Date dateValue) {
        this.dateValue = dateValue;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public boolean isWeekend() {
        return isWeekend;
    }

    public void setWeekend(boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    @Override
    public String toString() {
        return "DateDim{" +
                "dateId=" + dateId +
                ", dateValue=" + dateValue +
                ", year=" + year +
                ", quarter=" + quarter +
                ", month=" + month +
                ", monthName='" + monthName + '\'' +
                ", week=" + week +
                ", day=" + day +
                ", dayName='" + dayName + '\'' +
                ", isWeekend=" + isWeekend +
                '}';
    }

}
