package com.timeanddate.services.common;

import com.timeanddate.services.dataTypes.time.TADDateTime;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class DateTimeUtils {
	public static int ToMinuteCompare(TADDateTime t1, TADDateTime t2) {
		long t1ticks = t1.getTimeInTicks();
		long t2ticks = t2.getTimeInTicks();

		if (t1ticks > t2ticks)
			return 1;
		if (t1ticks < t2ticks)
			return -1;

		return 0;
	}
}
