package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import com.timeanddate.services.BusinessDateService;
import com.timeanddate.services.BusinessDates;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.time.TADDateTime;
import com.timeanddate.services.dataTypes.businessdays.*;
import com.timeanddate.services.dataTypes.places.LocationId;

public class BusinessDateServiceTests {

	@Test
	public void Calling_BusinessDateService_And_Adding_Days_To_A_Date_Object()
		throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2017, 12, 1);
		TADDateTime endDate = new TADDateTime(2018, 1, 18);
		List<Integer> days = new ArrayList<Integer>();
		days.add(31);

		// Act
		BusinessDateService businessdateService = new BusinessDateService(Config.AccessKey, Config.SecretKey);
		BusinessDates result = businessdateService.addDays(startDate, days, new LocationId("usa/anchorage"));
		Period firstPeriod = result.Periods.get(0);

		// Assert
		assertEquals(startDate.getYear(), firstPeriod.getStartDate().getDateTime().getYear());
		assertEquals(startDate.getMonth(), firstPeriod.getStartDate().getDateTime().getMonth());
		assertEquals(startDate.getDayOfMonth(), firstPeriod.getStartDate().getDateTime().getDayOfMonth());

		assertEquals(endDate.getYear(), firstPeriod.getEndDate().getDateTime().getYear());
		assertEquals(endDate.getMonth(), firstPeriod.getEndDate().getDateTime().getMonth());
		assertEquals(endDate.getDayOfMonth(), firstPeriod.getEndDate().getDateTime().getDayOfMonth());

		assertEquals(31, firstPeriod.getIncludedDays());
		assertEquals(48, firstPeriod.getCalendarDays());
		assertEquals(17, firstPeriod.getSkippedDays());

		assertEquals(0, firstPeriod.getWeekdays().getMondayCount());
		assertEquals(0, firstPeriod.getWeekdays().getTuesdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getWednesdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getThursdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getFridayCount());
		assertEquals(7, firstPeriod.getWeekdays().getSaturdayCount());
		assertEquals(7, firstPeriod.getWeekdays().getSundayCount());

		assertTrue(firstPeriod.getHolidays().getCount() > 0);
		assertTrue(firstPeriod.getHolidays().getCount() == firstPeriod.getHolidays().getHolidays().size());
	}

	@Test
	public void Calling_BusinessDateService_And_Subtracting_Days_To_A_Date_Object()
		throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2018, 2, 1);
		TADDateTime endDate = new TADDateTime(2017, 12, 15);
		List<Integer> days = new ArrayList<Integer>();
		days.add(31);

		// Act
		BusinessDateService businessdateService = new BusinessDateService(Config.AccessKey, Config.SecretKey);
		BusinessDates result = businessdateService.subtractDays(startDate, days, new LocationId("usa/anchorage"));
		Period firstPeriod = result.Periods.get(0);

		// Assert
		assertEquals(startDate.getYear(), firstPeriod.getStartDate().getDateTime().getYear());
		assertEquals(startDate.getMonth(), firstPeriod.getStartDate().getDateTime().getMonth());
		assertEquals(startDate.getDayOfMonth(), firstPeriod.getStartDate().getDateTime().getDayOfMonth());

		assertEquals(endDate.getYear(), firstPeriod.getEndDate().getDateTime().getYear());
		assertEquals(endDate.getMonth(), firstPeriod.getEndDate().getDateTime().getMonth());
		assertEquals(endDate.getDayOfMonth(), firstPeriod.getEndDate().getDateTime().getDayOfMonth());

		assertEquals(31, firstPeriod.getIncludedDays());
		assertEquals(48, firstPeriod.getCalendarDays());
		assertEquals(17, firstPeriod.getSkippedDays());

		assertEquals(0, firstPeriod.getWeekdays().getMondayCount());
		assertEquals(0, firstPeriod.getWeekdays().getTuesdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getWednesdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getThursdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getFridayCount());
		assertEquals(7, firstPeriod.getWeekdays().getSaturdayCount());
		assertEquals(7, firstPeriod.getWeekdays().getSundayCount());
	}

	@Test
	public void Calling_BusinessDateService_With_Repeating_Days()
		throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2017, 12, 1);
		List<Integer> days = new ArrayList<Integer>();
		days.add(31);

		// Act
		BusinessDateService businessdateService = new BusinessDateService(Config.AccessKey, Config.SecretKey);
		businessdateService.setRepeat(3);
		BusinessDates result = businessdateService.addDays(startDate, days, new LocationId("usa/anchorage"));

		// Assert - first period
		TADDateTime endDate = new TADDateTime(2018, 1, 18);
		Period firstPeriod = result.Periods.get(0);

		assertEquals(startDate.getYear(), firstPeriod.getStartDate().getDateTime().getYear());
		assertEquals(startDate.getMonth(), firstPeriod.getStartDate().getDateTime().getMonth());
		assertEquals(startDate.getDayOfMonth(), firstPeriod.getStartDate().getDateTime().getDayOfMonth());

		assertEquals(endDate.getYear(), firstPeriod.getEndDate().getDateTime().getYear());
		assertEquals(endDate.getMonth(), firstPeriod.getEndDate().getDateTime().getMonth());
		assertEquals(endDate.getDayOfMonth(), firstPeriod.getEndDate().getDateTime().getDayOfMonth());

		// Assert - second period
		startDate = new TADDateTime(2018, 1, 18);
		endDate = new TADDateTime(2018, 3, 5);
		Period secondPeriod = result.Periods.get(1);

		assertEquals(startDate.getYear(), secondPeriod.getStartDate().getDateTime().getYear());
		assertEquals(startDate.getMonth(), secondPeriod.getStartDate().getDateTime().getMonth());
		assertEquals(startDate.getDayOfMonth(), secondPeriod.getStartDate().getDateTime().getDayOfMonth());

		assertEquals(endDate.getYear(), secondPeriod.getEndDate().getDateTime().getYear());
		assertEquals(endDate.getMonth(), secondPeriod.getEndDate().getDateTime().getMonth());
		assertEquals(endDate.getDayOfMonth(), secondPeriod.getEndDate().getDateTime().getDayOfMonth());

		// Assert - third period
		startDate = new TADDateTime(2018, 3, 5);
		endDate = new TADDateTime(2018, 4, 18);
		Period thirdPeriod = result.Periods.get(2);

		assertEquals(startDate.getYear(), thirdPeriod.getStartDate().getDateTime().getYear());
		assertEquals(startDate.getMonth(), thirdPeriod.getStartDate().getDateTime().getMonth());
		assertEquals(startDate.getDayOfMonth(), thirdPeriod.getStartDate().getDateTime().getDayOfMonth());

		assertEquals(endDate.getYear(), thirdPeriod.getEndDate().getDateTime().getYear());
		assertEquals(endDate.getMonth(), thirdPeriod.getEndDate().getDateTime().getMonth());
		assertEquals(endDate.getDayOfMonth(), thirdPeriod.getEndDate().getDateTime().getDayOfMonth());
	}

	@Test
	public void Calling_BusinessDateService_With_A_Filter()
		throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2017, 12, 1);
		EnumSet<BusinessDaysFilterType> filter = EnumSet.of(BusinessDaysFilterType.MONDAY, BusinessDaysFilterType.TUESDAY);
		LocationId locationId = new LocationId("usa/anchorage");

		// Act
		BusinessDateService businessdateService = new BusinessDateService(Config.AccessKey, Config.SecretKey);
		businessdateService.setFilter(filter);
		BusinessDates result = businessdateService.addDays(startDate, 31, locationId);
		Period firstPeriod = result.Periods.get(0);

		// Assert
		assertTrue(firstPeriod.getWeekdays().getMondayCount() > 0);
		assertTrue(firstPeriod.getWeekdays().getTuesdayCount() > 0);
		assertEquals(0, firstPeriod.getWeekdays().getWednesdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getThursdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getFridayCount());
		assertEquals(0, firstPeriod.getWeekdays().getSaturdayCount());
		assertEquals(0, firstPeriod.getWeekdays().getSundayCount());
	}
}
