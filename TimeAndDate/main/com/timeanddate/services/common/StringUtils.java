package com.timeanddate.services.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.timeanddate.services.dataTypes.astro.AstronomyEventClass;
import com.timeanddate.services.dataTypes.astro.AstronomyEventCode;
import com.timeanddate.services.dataTypes.holidays.HolidayType;
import com.timeanddate.services.dataTypes.businessdays.BusinessDaysFilterType;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class StringUtils {
	public static String BoolToNum(boolean bool) {
		return bool ? "1" : "0";
	}

	public static String placeIdByCoordinates(double latitude, double longitude) {
		StringBuilder coords = new StringBuilder();
		if (latitude >= 0)
			coords.append("+");

		coords.append(Double.toString(latitude));

		if (longitude >= 0)
			coords.append("+");

		coords.append(Double.toString(longitude));

		return coords.toString();
	}

	public static String join(Collection<?> col, String delim) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> iter = col.iterator();
		if (iter.hasNext())
			sb.append(iter.next().toString());
		while (iter.hasNext()) {
			sb.append(delim);
			sb.append(iter.next().toString());
		}
		return sb.toString();
	}

	public static BusinessDaysTuple resolveBusinessDaysFilter(IPredicate<BusinessDaysFilterType> predicate) {
		List<BusinessDaysTuple> filters = Arrays.<BusinessDaysTuple> asList(
				new BusinessDaysTuple("all", BusinessDaysFilterType.ALL),
				new BusinessDaysTuple("mon", BusinessDaysFilterType.MONDAY),
				new BusinessDaysTuple("tue", BusinessDaysFilterType.TUESDAY),
				new BusinessDaysTuple("wed", BusinessDaysFilterType.WEDNESDAY),
				new BusinessDaysTuple("thu", BusinessDaysFilterType.THURSDAY),
				new BusinessDaysTuple("fri", BusinessDaysFilterType.FRIDAY),
				new BusinessDaysTuple("sat", BusinessDaysFilterType.SATURDAY),
				new BusinessDaysTuple("sun", BusinessDaysFilterType.SUNDAY),
				new BusinessDaysTuple("weekend", BusinessDaysFilterType.WEEKEND),
				new BusinessDaysTuple("holidays", BusinessDaysFilterType.HOLIDAYS),
				new BusinessDaysTuple("weekendholidays", BusinessDaysFilterType.WEEKENDHOLIDAYS),
				new BusinessDaysTuple("none", BusinessDaysFilterType.NONE)
		);

		for (BusinessDaysTuple t: filters)
			if (predicate.is(t.EnumRepresentation))
				return t;

		return null;
	}

	public static HolidayTuple resolveHolidays(IPredicate<HolidayType> predicate) {
		List<HolidayTuple> holidays = Arrays.<HolidayTuple> asList(
				new HolidayTuple("all", HolidayType.ALL), new HolidayTuple(
						"default", HolidayType.DEFAULT), new HolidayTuple(
						"countrydefault", HolidayType.DEFAULTFORCOUNTRY),
				new HolidayTuple("obs", HolidayType.OBSERVANCES),
				new HolidayTuple("federal", HolidayType.FEDERAL),
				new HolidayTuple("federallocal", HolidayType.FEDERALLOCAL),
				new HolidayTuple("local", HolidayType.LOCAL), new HolidayTuple(
						"flagday", HolidayType.FLAGDAYS), new HolidayTuple(
						"local2", HolidayType.LOCALOBSERVANCES),
				new HolidayTuple("obs1", HolidayType.IMPORTANTOBSERVANCES),
				new HolidayTuple("obs2", HolidayType.COMMONOBSERVANCES),
				new HolidayTuple("obs3", HolidayType.OTHEROBSERVANCES),
				new HolidayTuple("weekday", HolidayType.WEEKDAYS),
				new HolidayTuple("buddhism", HolidayType.BUDDHISM),
				new HolidayTuple("hebrew", HolidayType.HEBREW),
				new HolidayTuple("hinduism", HolidayType.HINDUISM),
				new HolidayTuple("muslim", HolidayType.MUSLIM),
				new HolidayTuple("orthodox", HolidayType.ORTHODOX),
				new HolidayTuple("seasons", HolidayType.SEASONS),
				new HolidayTuple("tz", HolidayType.TIMEZONEEVENTS),
				new HolidayTuple("un", HolidayType.UNITEDNATIONS),
				new HolidayTuple("world", HolidayType.WORLDWIDEOBSERVANCES),
				new HolidayTuple("christian", HolidayType.CHRISTIAN));

		for (HolidayTuple t : holidays)
			if (predicate.is(t.EnumRepresentation))
				return t;

		return null;
	}

	public static AstronomyEventTuple resolveAstronomyEventClass(
			IPredicate<AstronomyEventClass> predicate) {
		List<AstronomyEventTuple> holidays = Arrays
				.<AstronomyEventTuple> asList(new AstronomyEventTuple("all",
						AstronomyEventClass.ALL), new AstronomyEventTuple(
						"daylength", AstronomyEventClass.DAYLENGTH),
						new AstronomyEventTuple("meridian",
								AstronomyEventClass.MERIDIAN),
						new AstronomyEventTuple("phase",
								AstronomyEventClass.PHASE),
						new AstronomyEventTuple("setrise",
								AstronomyEventClass.SETRISE),
						new AstronomyEventTuple("twilight",
								AstronomyEventClass.ALLTWILIGHTS),
						new AstronomyEventTuple("twilight6",
								AstronomyEventClass.CIVILTWILIGHT),
						new AstronomyEventTuple("twilight12",
								AstronomyEventClass.NAUTICALTWILIGHT),
						new AstronomyEventTuple("twilight18",
								AstronomyEventClass.ASTRONOMICALTWILIGHT));

		for (AstronomyEventTuple t : holidays)
			if (predicate.is(t.EnumRepresentation))
				return t;

		return null;
	}

	public static AstronomyEventCode resolveAstronomyEventCode(String eventCode) throws MalformedXMLException {
		switch (eventCode) {
		case "twi18_start":
			return AstronomyEventCode.AstronomicalTwilightStarts;
		case "twi12_start":
			return AstronomyEventCode.NauticalTwilightStarts;
		case "twi6_start":
			return AstronomyEventCode.CivilTwilightStarts;
		case "rise":
			return AstronomyEventCode.Rise;
		case "meridian":
			return AstronomyEventCode.Meridian;
		case "antimeridian":
			return AstronomyEventCode.AntiMeridian;
		case "set":
			return AstronomyEventCode.Set;
		case "twi6_end":
			return AstronomyEventCode.CivilTwilightEnds;
		case "twi12_end":
			return AstronomyEventCode.NauticalTwilightEnds;
		case "twi18_end":
			return AstronomyEventCode.AstronomicalTwilightEnds;
		case "newmoon":
			return AstronomyEventCode.NewMoon;
		case "firstquarter":
			return AstronomyEventCode.FirstQuarter;
		case "fullmoon":
			return AstronomyEventCode.FullMoon;
		case "thirdquarter":
			return AstronomyEventCode.ThirdQuarter;
		default:
			throw new MalformedXMLException(
					"EventCode does not conform to enum AstronomyEventCode");
		}
	}
}
