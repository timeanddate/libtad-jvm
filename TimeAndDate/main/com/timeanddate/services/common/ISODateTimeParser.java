package com.timeanddate.services.common;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public final class ISODateTimeParser {
	private int _year;
	private int _month;
	private int _day;
	private int _hour;
	private int _minute;
	private int _second;
	private long _offset;

	public void parseISODate(String iso) {
		String isotxt = iso.toUpperCase();
		String[] datepart = isotxt.split("-");
		String[] timepart = isotxt.split("T");

		if (timepart.length > 1) {
			String[] negativeOffset = timepart[1].split("\\-");
			String[] positiveOffset = timepart[1].split("\\+");

			boolean hasNegativeOffset = negativeOffset.length > 1;
			boolean hasOffset = hasNegativeOffset && positiveOffset.length > 1;

			String[] timeparts = hasNegativeOffset ? negativeOffset[0]
					.split(":") : positiveOffset[0].split(":");

			if (timeparts.length >= 1) {
				String hour = timeparts[0];
				_hour = Integer.parseInt(hour);
			}

			if (timeparts.length >= 2) {
				String minute = timeparts[1];
				_minute = Integer.parseInt(minute);
			}

			if (timeparts.length >= 3) {
				String second = timeparts[2];
				_second = Integer.parseInt(second);
			}

			if (hasOffset) {
				String[] offsetparts = hasNegativeOffset ? negativeOffset[1]
						.split(":") : positiveOffset[1].split(":");

				String hours = offsetparts[0];
				String minutes = offsetparts[1];

				if (minutes.contains("Z"))
					minutes = minutes.split("Z")[0];

				int hoursInMin = Integer.parseInt(hours) * 60;
				int totalMins = hoursInMin + Integer.parseInt(minutes);
				int offset = hasNegativeOffset ? -totalMins : totalMins;

				_offset = offset * 1000;
			}

			String[] dates = timepart[0].split("-");
			_year = Integer.parseInt(dates[0]);
			_month = Integer.parseInt(dates[1]);
			_day = Integer.parseInt(dates[2]);

		} else {
			_year = Integer.parseInt(datepart[0]);
			_month = Integer.parseInt(datepart[1]);
			_day = Integer.parseInt(datepart[2]);
		}
	}

	public int getYear() {
		return _year;
	}

	public int getMonth() {
		return _month;
	}

	public int getDay() {
		return _day;
	}

	public int getHour() {
		return _hour;
	}

	public int getMinute() {
		return _minute;
	}

	public int getSecond() {
		return _second;
	}

	public long getOffset() {
		return _offset;
	}
}
