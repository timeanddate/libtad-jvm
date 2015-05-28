/*
 * 
 * Copyright (c) Microsoft Corporation
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.timeanddate.services.common;

public class TimeSpan {
	public final static long TicksPerMillisecond = 10000;
	private final double MillisecondsPerTick = 1.0 / TicksPerMillisecond;

	public final static long TicksPerSecond = TicksPerMillisecond * 1000; // 10,000,000
	private final double SecondsPerTick = 1.0 / TicksPerSecond; // 0.0001

	public final long TicksPerMinute = TicksPerSecond * 60; // 600,000,000
	private final double MinutesPerTick = 1.0 / TicksPerMinute; // 1.6666666666667e-9

	public final long TicksPerHour = TicksPerMinute * 60; // 36,000,000,000
	private final double HoursPerTick = 1.0 / TicksPerHour; // 2.77777777777777778e-11

	public final long TicksPerDay = TicksPerHour * 24; // 864,000,000,000
	private final double DaysPerTick = 1.0 / TicksPerDay; // 1.1574074074074074074e-12

	private final static int MillisPerSecond = 1000;
	private final static int MillisPerMinute = MillisPerSecond * 60; // 60,000
	private final static int MillisPerHour = MillisPerMinute * 60; // 3,600,000
	private final static int MillisPerDay = MillisPerHour * 24; // 86,400,000

	private final static long MaxSeconds = Long.MAX_VALUE / TicksPerSecond;
	private final static long MinSeconds = Long.MIN_VALUE / TicksPerSecond;

	private final static long MaxMilliSeconds = Long.MAX_VALUE
			/ TicksPerMillisecond;
	private final static long MinMilliSeconds = Long.MAX_VALUE
			/ TicksPerMillisecond;

	public static TimeSpan Zero = new TimeSpan(0);

	public static TimeSpan MaxValue = new TimeSpan(Long.MAX_VALUE);
	public static TimeSpan MinValue = new TimeSpan(Long.MIN_VALUE);

	// internal so that DateTime doesn't have to call an extra get
	// method for some arithmetic operations.
	private long _ticks;

	// public TimeSpan() {
	// _ticks = 0;
	// }

	public TimeSpan(long ticks) {
		this._ticks = ticks;
	}

	public TimeSpan(int hours, int minutes, int seconds) {
		_ticks = TimeToTicks(hours, minutes, seconds);
	}

	public TimeSpan(int days, int hours, int minutes, int seconds) {
		this(days, hours, minutes, seconds, 0);
	}

	public TimeSpan(int days, int hours, int minutes, int seconds,
			int milliseconds) {
		long totalMilliSeconds = ((long) days * 3600 * 24 + (long) hours * 3600
				+ (long) minutes * 60 + seconds)
				* 1000 + milliseconds;
		if (totalMilliSeconds > MaxMilliSeconds
				|| totalMilliSeconds < MinMilliSeconds)
			throw new IllegalArgumentException("TimeSpan too long");
		_ticks = (long) totalMilliSeconds * TicksPerMillisecond;
	}

	public long getTicks() {
		return _ticks;
	}

	public int getDays() {
		return (int) (_ticks / TicksPerDay);
	}

	public int getHours() {
		return (int) ((_ticks / TicksPerHour) % 24);
	}

	public int getMilliseconds() {
		return (int) ((_ticks / TicksPerMillisecond) % 1000);
	}

	public int getMinutes() {
		return (int) ((_ticks / TicksPerMinute) % 60);
	}

	public int getSeconds() {
		return (int) ((_ticks / TicksPerSecond) % 60);
	}

	public double getTotalDays() {
		return ((double) _ticks) * DaysPerTick;
	}

	public double getTotalHours() {
		return (double) _ticks * HoursPerTick;
	}

	public double TotalMilliseconds() {
		double temp = (double) _ticks * MillisecondsPerTick;
		if (temp > MaxMilliSeconds)
			return (double) MaxMilliSeconds;

		if (temp < MinMilliSeconds)
			return (double) MinMilliSeconds;

		return temp;
	}

	public double TotalMinutes() {
		return (double) _ticks * MinutesPerTick;
	}

	public double TotalSeconds() {
		return (double) _ticks * SecondsPerTick;
	}

	public TimeSpan Add(TimeSpan ts) {
		long result = _ticks + ts._ticks;
		// Overflow if signs of operands was identical and result's
		// sign was opposite.
		// >> 63 gives the sign bit (either 64 1's or 64 0's).
		if ((_ticks >> 63 == ts._ticks >> 63) && (_ticks >> 63 != result >> 63))
			throw new RuntimeException("TimeSpan too long");
		return new TimeSpan(result);
	}

	// Compares two TimeSpan values, returning an integer that indicates their
	// relationship.
	//
	public static int Compare(TimeSpan t1, TimeSpan t2) {
		if (t1._ticks > t2._ticks)
			return 1;
		if (t1._ticks < t2._ticks)
			return -1;
		return 0;
	}

	// Returns a value less than zero if this object
	public int CompareTo(Object value) {
		if (value == null)
			return 1;
		if (!(value instanceof TimeSpan))
			throw new IllegalArgumentException("Argument must be TimeSpan");
		long t = ((TimeSpan) value)._ticks;
		if (_ticks > t)
			return 1;
		if (_ticks < t)
			return -1;
		return 0;
	}

	public int CompareTo(TimeSpan value) {
		long t = value._ticks;
		if (_ticks > t)
			return 1;
		if (_ticks < t)
			return -1;
		return 0;
	}

	public static TimeSpan FromDays(double value) {
		return Interval(value, MillisPerDay);
	}

	public TimeSpan Duration() {
		if (getTicks() == TimeSpan.MinValue.getTicks())
			throw new RuntimeException("Overflow duration equals minimum ticks");

		return new TimeSpan(_ticks >= 0 ? _ticks : -_ticks);
	}

	public boolean Equals(Object value) {
		if (value instanceof TimeSpan) {
			return _ticks == ((TimeSpan) value)._ticks;
		}
		return false;
	}

	public boolean Equals(TimeSpan obj) {
		return _ticks == obj._ticks;
	}

	public static boolean Equals(TimeSpan t1, TimeSpan t2) {
		return t1._ticks == t2._ticks;
	}

	public int GetHashCode() {
		return (int) _ticks ^ (int) (_ticks >> 32);
	}

	public static TimeSpan FromHours(double value) {
		return Interval(value, MillisPerHour);
	}

	private static TimeSpan Interval(double value, int scale) {
		double tmp = value * scale;
		double millis = tmp + (value >= 0 ? 0.5 : -0.5);
		if (millis > Long.MAX_VALUE / TicksPerMillisecond)
			throw new RuntimeException("TimeSpan too long");
		return new TimeSpan((long) millis * TicksPerMillisecond);
	}

	public static TimeSpan FromMilliseconds(double value) {
		return Interval(value, 1);
	}

	public static TimeSpan FromMinutes(double value) {
		return Interval(value, MillisPerMinute);
	}

	public static TimeSpan FromSeconds(double value) {
		return Interval(value, MillisPerSecond);
	}

	public TimeSpan Subtract(TimeSpan ts) {
		long result = _ticks - ts._ticks;
		// Overflow if signs of operands was different and result's
		// sign was opposite from the first argument's sign.
		// >> 63 gives the sign bit (either 64 1's or 64 0's).
		if ((_ticks >> 63 != ts._ticks >> 63) && (_ticks >> 63 != result >> 63))
			throw new RuntimeException("TimeSpan too long");
		return new TimeSpan(result);
	}

	public static TimeSpan FromTicks(long value) {
		return new TimeSpan(value);
	}

	public static long TimeToTicks(int hour, int minute, int second) {
		// totalSeconds is bounded by 2^31 * 2^12 + 2^31 * 2^8 + 2^31,
		// which is less than 2^44, meaning we won't overflow totalSeconds.
		long totalSeconds = (long) hour * 3600 + (long) minute * 60
				+ (long) second;
		if (totalSeconds > MaxSeconds || totalSeconds < MinSeconds)
			throw new IllegalArgumentException("TimeSpan too long");
		return totalSeconds * TicksPerSecond;
	}

	public static TimeSpan minus(TimeSpan t) {
		if (t._ticks == TimeSpan.MinValue._ticks)
			throw new RuntimeException("Overflow_NegateTwosCompNum");
		return new TimeSpan(-t._ticks);
	}

	public static TimeSpan minus(TimeSpan t1, TimeSpan t2) {
		return t1.Subtract(t2);
	}

	public static TimeSpan plus(TimeSpan t) {
		return t;
	}

	public static TimeSpan plus(TimeSpan t1, TimeSpan t2) {
		return t1.Add(t2);
	}

	public static boolean NotEquals(TimeSpan t1, TimeSpan t2) {
		return t1._ticks != t2._ticks;
	}

	public static boolean LessThan(TimeSpan t1, TimeSpan t2) {
		return t1._ticks < t2._ticks;
	}

	public static boolean LessOrEqual(TimeSpan t1, TimeSpan t2) {
		return t1._ticks <= t2._ticks;
	}

	public static boolean GreaterThan(TimeSpan t1, TimeSpan t2) {
		return t1._ticks > t2._ticks;
	}

	public static boolean GreaterOrEqual(TimeSpan t1, TimeSpan t2) {
		return t1._ticks >= t2._ticks;
	}
}