
package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import com.timeanddate.services.BusinessDurationService;
import com.timeanddate.services.BusinessDuration;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.time.TADDateTime;
import com.timeanddate.services.dataTypes.businessdays.*;
import com.timeanddate.services.dataTypes.places.LocationId;

public class BusinessDurationServiceTests {

	@Test
	public void Calling_BusinessDateService_And_Adding_Days_To_A_Date_Object()
		throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2017, 12, 1);
		TADDateTime endDate = new TADDateTime(2018, 1, 31);
		LocationId placeId = new LocationId("usa/anchorage");

		// Act
		BusinessDurationService service = new BusinessDurationService(Config.AccessKey, Config.SecretKey);
		BusinessDuration result = service.getDuration(startDate, endDate, placeId);
		// Assert
		assertEquals("Anchorage", result.Geography.getName());
		assertEquals(61, result.Period.getCalendarDays());
		assertEquals(21, result.Period.getSkippedDays());
		assertEquals(40, result.Period.getIncludedDays());
		assertEquals(9, result.Period.getWeekdays().getSaturdayCount());
		assertEquals(9, result.Period.getWeekdays().getSundayCount());
		assertEquals(3, result.Period.getHolidays().getCount());
	}

	@Test
	public void Calling_BusinessDurationService_With_IncludeDays()
		throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2017, 12, 1);
		TADDateTime endDate = new TADDateTime(2018, 1, 31);
		LocationId placeId = new LocationId("usa/anchorage");

		// Act
		BusinessDurationService service = new BusinessDurationService(Config.AccessKey, Config.SecretKey);
		service.setIncludeDays(true);
		BusinessDuration result = service.getDuration(startDate, endDate, placeId);
		// Assert
		assertEquals("Anchorage", result.Geography.getName());
		assertEquals(61, result.Period.getCalendarDays());
		assertEquals(40, result.Period.getSkippedDays());
		assertEquals(21, result.Period.getIncludedDays());
		assertEquals(9, result.Period.getWeekdays().getSaturdayCount());
		assertEquals(9, result.Period.getWeekdays().getSundayCount());
		assertEquals(3, result.Period.getHolidays().getCount());
	}

	@Test
	public void Calling_BusinessDurationService_With_IncludeLastDate()
		throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2017, 12, 1);
		TADDateTime endDate = new TADDateTime(2018, 1, 31);
		LocationId placeId = new LocationId("usa/anchorage");

		// Act
		BusinessDurationService service = new BusinessDurationService(Config.AccessKey, Config.SecretKey);
		service.setIncludeLastDate(true);
		BusinessDuration result = service.getDuration(startDate, endDate, placeId);
		// Assert
		assertEquals("Anchorage", result.Geography.getName());
		assertEquals(62, result.Period.getCalendarDays());
		assertEquals(21, result.Period.getSkippedDays());
		assertEquals(41, result.Period.getIncludedDays());
	}

	@Test
	public void Calling_BusinessDurationService_With_Filter()
		throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2017, 12, 1);
		TADDateTime endDate = new TADDateTime(2018, 1, 31);
		LocationId placeId = new LocationId("usa/anchorage");
		EnumSet<BusinessDaysFilterType> filter = EnumSet.of(BusinessDaysFilterType.MONDAY, BusinessDaysFilterType.TUESDAY);

		// Act
		BusinessDurationService service = new BusinessDurationService(Config.AccessKey, Config.SecretKey);
		service.setFilter(filter);
		BusinessDuration result = service.getDuration(startDate, endDate, placeId);
		// Assert
		assertEquals("Anchorage", result.Geography.getName());
		assertEquals(61, result.Period.getCalendarDays());
		assertEquals(18, result.Period.getSkippedDays());
		assertEquals(43, result.Period.getIncludedDays());
		assertEquals(9, result.Period.getWeekdays().getMondayCount());
		assertEquals(9, result.Period.getWeekdays().getTuesdayCount());
		assertEquals(0, result.Period.getWeekdays().getWednesdayCount());
		assertEquals(0, result.Period.getWeekdays().getThursdayCount());
		assertEquals(0, result.Period.getWeekdays().getFridayCount());
		assertEquals(0, result.Period.getWeekdays().getSaturdayCount());
		assertEquals(0, result.Period.getWeekdays().getSundayCount());

	}
}
