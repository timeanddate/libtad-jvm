package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import com.timeanddate.services.HolidaysService;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.holidays.Holiday;
import com.timeanddate.services.dataTypes.holidays.HolidayState;
import com.timeanddate.services.dataTypes.holidays.HolidayType;
import com.timeanddate.services.dataTypes.time.TADDateTime;

public class HolidaysServiceTests {

	@Test
	public void Calling_HolidaysService_WithCountry_And_WithYear_Should_ReturnHolidays() throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		String country = "us";
		int year = 2014;
		String expectedHoliday = "New Year's Day";
		String expectedUid = "0007d600000007de";
		URI expectedUrl = new URI(
				"http://www.timeanddate.com/holidays/us/new-year-day");
		int expectedId = 2006;

		TADDateTime expectedDate = new TADDateTime(2014, 1, 1);

		// Act
		HolidaysService holidaysService = new HolidaysService(Config.AccessKey,
				Config.SecretKey);
		List<Holiday> result = holidaysService
				.holidaysForCountry(country, year);
		Holiday firstHoliday = result.get(0);
		// Assert
		assertNotNull(firstHoliday);
		assertEquals(expectedHoliday, firstHoliday.getName());
		assertEquals(expectedUid, firstHoliday.getUid());
		assertEquals(expectedUrl.toURL(), firstHoliday.getUrl().toURL());

		assertEquals(expectedId, firstHoliday.getId());
		assertEquals(expectedDate.getYear(),
				firstHoliday.getDate().getDateTime().getYear());
		assertEquals(expectedDate.getMonth(),
				firstHoliday.getDate().getDateTime().getMonth());
		assertEquals(expectedDate.getDayOfMonth(),
				firstHoliday.getDate().getDateTime().getDayOfMonth());
	}

	@Test
	public void Calling_HolidaysService_WithCountry_And_WithYear_Should_ReturnHolidaysWithStates() throws AuthenticationException, ServerSideException {
		// Arrange
		String country = "us";
		int year = 2014;
		String expectedState = "Alabama";

		// Act
		HolidaysService holidaysService = new HolidaysService(Config.AccessKey,
				Config.SecretKey);
		List<Holiday> result = holidaysService
				.holidaysForCountry(country, year);

		List<Holiday> holidaysWithSpecificStates = new ArrayList<Holiday>();
		for (Holiday h : result) {
			if (h.getStates() != null && h.getStates().size() > 0)
				holidaysWithSpecificStates.add(h);
		}

		Holiday firstHoliday = holidaysWithSpecificStates.get(0);
		HolidayState firstState = firstHoliday.getStates().get(0);

		// Assert
		assertNotNull(firstHoliday);
		assertNotNull(firstHoliday.getStates());
		assertEquals(expectedState, firstState.getName());
	}

	@Test
	public void Calling_HolidaysService_WithSpecifiedTypes_Should_ReturnHolidaysWithCorrectTypes() throws AuthenticationException, ServerSideException {
		// Arrange
		String country = "us";
		String expectedType = "Christian";
		int year = 2014;
		int expectedCount = 21;

		// Act
		HolidaysService holidaysService = new HolidaysService(Config.AccessKey,
				Config.SecretKey);
		holidaysService.setHolidayTypes(EnumSet.of(HolidayType.CHRISTIAN,
				HolidayType.BUDDHISM));
		List<Holiday> result = holidaysService
				.holidaysForCountry(country, year);
		Holiday sample = result.get(0);

		// Assert
		assertEquals(expectedCount, result.size());
		assertTrue(sample.getDescription() != null && sample.getDescription() != "");
		assertTrue(sample.getUid() != null && sample.getDescription() != "");
		assertNotNull(sample.getUrl());
		assertTrue(sample.getUrl().isAbsolute());

		for (Holiday h : result) {
			assertTrue(h.getTypes().contains(expectedType));
			assertEquals(country, h.getCountry().getId());
		}
	}
}
