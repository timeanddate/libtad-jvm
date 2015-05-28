package com.timeanddate.services.dataTypes.time;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.timeanddate.services.common.ISODateTimeParser;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class TADDateTime {
	protected int Year;

	protected int Month;

	protected int Day;

	protected int Hour;

	protected int Minute;

	protected int Second;

	protected long OffsetInMillis;

	protected int OffsetHours;

	protected int OffsetMinutes;

	protected String ISORepresentation = "";

	public TADDateTime(int year) {
		Year = year;
	}

	public TADDateTime(int year, int month) {
		Year = year;
		Month = month;
	}

	public TADDateTime(int year, int month, int day) {
		Year = year;
		Month = month;
		Day = day;
	}

	public TADDateTime(int year, int month, int day, int hour) {
		Year = year;
		Month = month;
		Day = day;
		Hour = hour;
	}

	public TADDateTime(int year, int month, int day, int hour, int minute) {
		Year = year;
		Month = month;
		Day = day;
		Hour = hour;
		Minute = minute;
	}

	public TADDateTime(int year, int month, int day, int hour, int minute,
			int second) {
		Year = year;
		Month = month;
		Day = day;
		Hour = hour;
		Minute = minute;
		Second = second;
	}

	public void setOffset(int hour, int minute) {
		OffsetHours = hour;
		OffsetMinutes = minute;
	}

	public void setOffset(int millis) {
		Hour = (int) TimeUnit.MILLISECONDS.toHours(millis);
		Minute = (int) TimeUnit.MILLISECONDS.toMinutes(millis);
	}

	public void setOffset(String iso) {

	}

	public int getYear() {
		return Year;
	}

	public int getMonth() {
		return Month;
	}

	public int getDayOfMonth() {
		return Day;
	}

	public int getHour() {
		return Hour;
	}

	public int getMinute() {
		return Minute;
	}

	public int getSecond() {
		return Second;
	}

	public String getISO8601Date() {
		String month = Month < 10 ? "0" + Integer.toString(Month) : Integer
				.toString(Month);

		String day = Day < 10 ? "0" + Integer.toString(Day) : Integer
				.toString(Day);

		return String.format("%d-%s-%s", Year, month, day);
	}

	// TODO: Implement the UTC offset as well
	public String getISO8601Combined() {
		if (!ISORepresentation.isEmpty())
			return ISORepresentation;
		else {
			String month = Month < 10 ? "0" + Integer.toString(Month) : Integer
					.toString(Month);

			String day = Day < 10 ? "0" + Integer.toString(Day) : Integer
					.toString(Day);

			String hour = Hour < 10 ? "0" + Integer.toString(Hour) : Integer
					.toString(Hour);

			String minute = Minute < 10 ? "0" + Integer.toString(Minute)
					: Integer.toString(Minute);

			String second = Second < 10 ? "0" + Integer.toString(Second)
					: Integer.toString(Second);

			String isostr = String.format("%d-%s-%sT%s:%s:%s", Year, month,
					day, hour, minute, second);

			return isostr;
		}
	}

	public TADDateTime(String iso) {
		ISORepresentation = iso;

		ISODateTimeParser parser = new ISODateTimeParser();
		parser.parseISODate(iso);

		Year = parser.getYear();
		Month = parser.getMonth();
		Day = parser.getDay();
		Hour = parser.getHour();
		Minute = parser.getMinute();
		Second = parser.getSecond();
	}

	public long getTimeInTicks() {
		final long TICKS_AT_EPOCH = 621355968000000000L;
		final long TICKS_PER_MILLISECOND = 10000;

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month - 1); // Months are zero index in dear
											// Java
		cal.set(Calendar.DAY_OF_MONTH, Day);
		cal.set(Calendar.HOUR_OF_DAY, Hour);
		cal.set(Calendar.MINUTE, Minute);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTimeInMillis() * TICKS_PER_MILLISECOND + TICKS_AT_EPOCH;
	}

	@Override
	public String toString() {
		return getISO8601Combined();
	}
}
