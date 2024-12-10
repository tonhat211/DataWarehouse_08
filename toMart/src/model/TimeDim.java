package model;

public class TimeDim {
	public String timeID;
	public int year, quarter, month, week, day;
	public String monthName, dayName;
	public Boolean isWeekend;

	public TimeDim(String timeID, int year, int quarter, int month, int week, int day, String monthName, String dayName,
			Boolean isWeekend) {
		super();
		this.timeID = timeID;
		this.year = year;
		this.quarter = quarter;
		this.month = month;
		this.week = week;
		this.day = day;
		this.monthName = monthName;
		this.dayName = dayName;
		this.isWeekend = isWeekend;
	}

	public TimeDim(int year, int quarter, int month, int week, int day, String monthName, String dayName,
			Boolean isWeekend) {
		super();
		this.year = year;
		this.quarter = quarter;
		this.month = month;
		this.week = week;
		this.day = day;
		this.monthName = monthName;
		this.dayName = dayName;
		this.isWeekend = isWeekend;
	}

}
