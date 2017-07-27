package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.timeanddate.services.TimeService;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.LocalTimeDoesNotExistException;
import com.timeanddate.services.common.MissingTimeChangesException;
import com.timeanddate.services.common.QueriedDateOutOfRangeException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.common.TimeSpan;
import com.timeanddate.services.dataTypes.astro.Astronomy;
import com.timeanddate.services.dataTypes.astro.AstronomyEvent;
import com.timeanddate.services.dataTypes.astro.AstronomyEventType;
import com.timeanddate.services.dataTypes.astro.AstronomyObjectType;
import com.timeanddate.services.dataTypes.places.Coordinates;
import com.timeanddate.services.dataTypes.places.Location;
import com.timeanddate.services.dataTypes.places.LocationId;
import com.timeanddate.services.dataTypes.time.TADDateTime;
import com.timeanddate.services.dataTypes.time.TimeChange;

public class TimeServiceTests {
	@Test
	public void calling_TimeService_WithNumericId_Should_ReturnCorrectLocation() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		int placeId = 179;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(Integer.toString(placeId), firstLocation.getId());
	}

	@Test
	public void calling_TimeService_WithTextualId_Should_ReturnCorrectLocation() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		LocationId locationId = new LocationId("norway/oslo");
		String placeId = "187";

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(locationId);
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(placeId, firstLocation.getId());
	}

	@Test
	public void calling_TimeService_WithCoordinates_Should_ReturnCorrectLocation() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		Coordinates osloCoords = new Coordinates(59.914d, 10.752d);
		LocationId locationId = new LocationId(osloCoords);
		String expectedId = String.format("+%1$,.3f+%2$,.3f",
				osloCoords.getLatitude(), osloCoords.getLongitude());

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(locationId);
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(expectedId, firstLocation.getId());
	}

	@Test
	public void calling_TimeService_WithTextualId_Should_ReturnCorrectGeo() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		String expectedCountry = "Norway";
		String expectedPlace = "Oslo";
		String placeName = String.format("%s/%s", expectedCountry,
				expectedPlace).toLowerCase();
		String expectedCountryId = "no";
		String expectedPlaceId = "187";
		LocationId locationId = new LocationId(placeName);

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(locationId);
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(expectedCountry, firstLocation.getGeography().getCountry().getName());
		assertEquals(expectedCountryId, firstLocation.getGeography().getCountry().getId());
		assertEquals(expectedPlace, firstLocation.getGeography().getName());
		assertEquals(expectedPlaceId, firstLocation.getId());
	}

	@Test
	public void calling_TimeService_WithNumericId_Should_ReturnCorrectGeo() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		String expectedCountry = "Norway";
		String expectedPlace = "Oslo";
		String expectedCountryId = "no";
		int placeId = 187;
		LocationId locationId = new LocationId(placeId);

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(locationId);
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(expectedCountry, firstLocation.getGeography().getCountry().getName());
		assertEquals(expectedCountryId, firstLocation.getGeography().getCountry().getId());
		assertEquals(expectedPlace, firstLocation.getGeography().getName());
		assertEquals(Integer.toString(placeId), firstLocation.getId());
	}

	@Test
	public void calling_TimeService_WithNumericId_Should_ReturnCorrectTime() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		int placeId = 187;
		TADDateTime now = new TADDateTime(Calendar.getInstance().get(
				Calendar.YEAR));

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(now.getYear(), firstLocation.getTime().getDateTime().getYear(), 0);
	}

	@Test
	public void calling_TimeService_WithTextualId_Should_ReturnCorrectTime() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		String placeName = "norway/oslo";
		TADDateTime now = new TADDateTime(Calendar.getInstance().get(
				Calendar.YEAR));

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeName));
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(now.getYear(), firstLocation.getTime().getDateTime().getYear(), 0);
	}

	@Test
	public void calling_TimeService_WithCoordinates_Should_ReturnCorrectTime() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		Coordinates osloCoords = new Coordinates(59.914d, 10.752d);
		String expectedId = String.format("+%1$,.3f+%2$,.3f",
				osloCoords.getLatitude(), osloCoords.getLongitude());

		TADDateTime now = new TADDateTime(Calendar.getInstance().get(
				Calendar.YEAR));

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		timeservice.setIncludeCoordinates(true);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				osloCoords));
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(now.getYear(), firstLocation.getTime().getDateTime().getYear());
		assertEquals(expectedId, firstLocation.getId());
	}

	@Test
	public void calling_TimeService_WithNumericId_Should_ReturnCorrectTimezone() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		int placeId = 187;
		String expectedTimezoneAbbr1 = "CEST";
		String expectedTimezoneAbbr2 = "CET";
		String expectedTimezoneName = "Central European Summer Time";
		int expectedOffsetHour = 2;
		int expectedOffsetMinute = 0;
		int expectedBasicOffset = 3600;
		int expectedDstOffset = 3600;
		int expectedTotalOffset = 7200;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);
		String abbr = firstLocation.getTime().getTimezone().getAbbrevation();

		// Assert
		assertTrue (abbr.equals(expectedTimezoneAbbr1) || abbr.equals(expectedTimezoneAbbr2));
		assertEquals(expectedTimezoneName, firstLocation.getTime().getTimezone().getName());
		assertEquals(expectedOffsetHour,
				firstLocation.getTime().getTimezone().getOffset().getHours());
		assertEquals(expectedOffsetMinute,
				firstLocation.getTime().getTimezone().getOffset().getMinutes());
		assertEquals(expectedBasicOffset,
				firstLocation.getTime().getTimezone().getBasicOffset());
		assertEquals(expectedDstOffset, firstLocation.getTime().getTimezone().getDSTOffset());
		assertEquals(expectedTotalOffset,
				firstLocation.getTime().getTimezone().getTotalOffset());
	}

	@Test
	public void calling_TimeService_WithTextualId_Should_ReturnCorrectTimezone() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		String placeName = "norway/oslo";
		String expectedTimezoneAbbr = "CEST";
		String expectedTimezoneName = "Central European Summer Time";
		int expectedOffsetHour = 2;
		int expectedOffsetMinute = 0;
		int expectedBasicOffset = 3600;
		int expectedDstOffset = 3600;
		int expectedTotalOffset = 7200;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeName));
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(expectedTimezoneAbbr,
				firstLocation.getTime().getTimezone().getAbbrevation());
		assertEquals(expectedTimezoneName, firstLocation.getTime().getTimezone().getName());
		assertEquals(expectedOffsetHour,
				firstLocation.getTime().getTimezone().getOffset().getHours());
		assertEquals(expectedOffsetMinute,
				firstLocation.getTime().getTimezone().getOffset().getMinutes());
		assertEquals(expectedBasicOffset,
				firstLocation.getTime().getTimezone().getBasicOffset());
		assertEquals(expectedDstOffset, firstLocation.getTime().getTimezone().getDSTOffset());
		assertEquals(expectedTotalOffset,
				firstLocation.getTime().getTimezone().getTotalOffset());
	}

	@Test
	public void calling_TimeService_WithCoordinates_Should_ReturnCorrectTimezone() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		Coordinates osloCoords = new Coordinates(59.914d, 10.752d);
		String expectedTimezoneAbbr = "CEST";
		String expectedTimezoneName = "Central European Summer Time";
		int expectedOffsetHour = 2;
		int expectedOffsetMinute = 0;
		int expectedBasicOffset = 3600;
		int expectedDstOffset = 3600;
		int expectedTotalOffset = 7200;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				osloCoords));
		Location firstLocation = result.get(0);

		// Assert
		assertEquals(expectedTimezoneAbbr,
				firstLocation.getTime().getTimezone().getAbbrevation());
		assertEquals(expectedTimezoneName, firstLocation.getTime().getTimezone().getName());
		assertEquals(expectedOffsetHour,
				firstLocation.getTime().getTimezone().getOffset().getHours());
		assertEquals(expectedOffsetMinute,
				firstLocation.getTime().getTimezone().getOffset().getMinutes());
		assertEquals(expectedBasicOffset,
				firstLocation.getTime().getTimezone().getBasicOffset());
		assertEquals(expectedDstOffset, firstLocation.getTime().getTimezone().getDSTOffset());
		assertEquals(expectedTotalOffset,
				firstLocation.getTime().getTimezone().getTotalOffset());
	}

	@Test
	public void calling_TimeService_WithNumericId_Should_ReturnCorrectTimeChanges() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		int placeId = 187;
		int expectedFirstNewOffset = 7200;
		int expectedFirstNewDstOffset = 3600;
		int expectedSecondNewOffset = 3600;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);
		TimeChange firstChange = firstLocation.getTimeChanges().get(0);
		TimeChange secondChange = firstLocation.getTimeChanges().get(1);

		// Assert
		assertTrue(firstChange.getNewDaylightSavingTime() >= 0);
		assertEquals(expectedFirstNewDstOffset,
				firstChange.getNewDaylightSavingTime());
		assertEquals(expectedFirstNewOffset, firstChange.getNewTotalOffset());
		assertEquals(expectedSecondNewOffset, secondChange.getNewTotalOffset());
	}

	@Test
	public void calling_TimeService_WithTextualId_Should_ReturnCorrectTimeChanges() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		String placeName = "norway/oslo";
		int expectedFirstNewOffset = 7200;
		int expectedFirstNewDstOffset = 3600;
		int expectedSecondNewOffset = 3600;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeName));
		Location firstLocation = result.get(0);
		TimeChange firstChange = firstLocation.getTimeChanges().get(0);
		TimeChange secondChange = firstLocation.getTimeChanges().get(1);

		// Assert
		assertTrue(firstChange.getNewDaylightSavingTime() >= 0);
		assertEquals(expectedFirstNewDstOffset,
				firstChange.getNewDaylightSavingTime());
		assertEquals(expectedFirstNewOffset, firstChange.getNewTotalOffset());
		assertEquals(expectedSecondNewOffset, secondChange.getNewTotalOffset());
	}

	@Test
	public void calling_TimeService_WithCoordinates_Should_ReturnCorrectTimeChanges() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		Coordinates osloCoords = new Coordinates(59.914d, 10.752d);
		int expectedFirstNewOffset = 7200;
		int expectedFirstNewDstOffset = 3600;
		int expectedSecondNewOffset = 3600;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				osloCoords));
		Location firstLocation = result.get(0);
		TimeChange firstChange = firstLocation.getTimeChanges().get(0);
		TimeChange secondChange = firstLocation.getTimeChanges().get(1);

		// Assert
		assertTrue(firstChange.getNewDaylightSavingTime() >= 0);
		assertEquals(expectedFirstNewDstOffset,
				firstChange.getNewDaylightSavingTime());
		assertEquals(expectedFirstNewOffset, firstChange.getNewTotalOffset());
		assertEquals(expectedSecondNewOffset, secondChange.getNewTotalOffset());
	}

	@Test
	// This test checks on sunset, but the check criteria can have changed from
	// day to day
	// TODO to find a way around this
	public void Calling_TimeService_WithNumericId_Should_ReturnCorrectAstronomy() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		int placeId = 187;
		AstronomyObjectType expectedObjectName = AstronomyObjectType.Sun;
		AstronomyEventType expectedRise = AstronomyEventType.Rise;
		AstronomyEventType expectedSet = AstronomyEventType.Set;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);
		Astronomy firstObject = firstLocation.getAstronomy().get(0);
		AstronomyEvent rise = firstObject.getEvents().get(0);
		AstronomyEvent set = firstObject.getEvents().get(1);

		// Assert
		assertEquals(expectedObjectName, firstObject.getName());
		assertEquals(expectedRise, rise.getType());
		assertEquals(expectedSet, set.getType());

		// Sunrise in Oslo is most likely never before 3 and never after 10
		// Sunset in Oslo is most likely never before 14 and never after 22
		assertTrue(rise.getTime().getHours() >= 3 && rise.getTime().getHours() <= 10);
		assertTrue(set.getTime().getHours() >= 14 && set.getTime().getHours() <= 22);
	}

	@Test
	// This test checks on sunset, but the check criteria can have changed from
	// day to day
	// TODO to find a way around this
	public void Calling_TimeService_WithTextualId_Should_ReturnCorrectAstronomy() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		String placeName = "norway/oslo";
		AstronomyObjectType expectedObjectName = AstronomyObjectType.Sun;
		AstronomyEventType expectedRise = AstronomyEventType.Rise;
		AstronomyEventType expectedSet = AstronomyEventType.Set;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeName));
		Location firstLocation = result.get(0);
		Astronomy firstObject = firstLocation.getAstronomy().get(0);
		AstronomyEvent rise = firstObject.getEvents().get(0);
		AstronomyEvent set = firstObject.getEvents().get(1);

		// Assert
		assertEquals(expectedObjectName, firstObject.getName());
		assertEquals(expectedRise, rise.getType());
		assertEquals(expectedSet, set.getType());

		// Sunrise in Oslo is most likely never before 3 and never after 10
		// Sunset in Oslo is most likely never before 14 and never after 22
		assertTrue(rise.getTime().getHours() >= 3 && rise.getTime().getHours() <= 10);
		assertTrue(set.getTime().getHours() >= 14 && set.getTime().getHours() <= 22);
	}

	@Test
	// This test checks on sunset, but the check criteria can have changed from
	// day to day
	// TODO to find a way around this
	public void Calling_TimeService_WithCoordinates_Should_ReturnCorrectAstronomy() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		Coordinates osloCoords = new Coordinates(59.914d, 10.752d);
		AstronomyObjectType expectedObjectName = AstronomyObjectType.Sun;
		AstronomyEventType expectedRise = AstronomyEventType.Rise;
		AstronomyEventType expectedSet = AstronomyEventType.Set;

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				osloCoords));
		Location firstLocation = result.get(0);
		Astronomy firstObject = firstLocation.getAstronomy().get(0);
		AstronomyEvent rise = firstObject.getEvents().get(0);
		AstronomyEvent set = firstObject.getEvents().get(1);

		// Assert
		assertEquals(expectedObjectName, firstObject.getName());
		assertEquals(expectedRise, rise.getType());
		assertEquals(expectedSet, set.getType());

		// Sunrise in Oslo is most likely never before 3 and never after 10
		// Sunset in Oslo is most likely never before 14 and never after 22
		assertTrue(rise.getTime().getHours() >= 3 && rise.getTime().getHours() <= 10);
		assertTrue(set.getTime().getHours() >= 14 && set.getTime().getHours() <= 22);
	}

	@Test(expected = LocalTimeDoesNotExistException.class)
	public void calling_TimeService_And_GettingUTCOffset_WithNonExistingLocalTime_Should_ThrowException() 
		throws AuthenticationException, ServerSideException, MissingTimeChangesException, 
		QueriedDateOutOfRangeException, LocalTimeDoesNotExistException {
		// Arrange
		int placeId = 187;
		TADDateTime localTime = new TADDateTime(2017, 3, 26, 2, 30, 0);

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);

		// Throws
		firstLocation.getUTCOffsetFromLocalTime(localTime);
	}

	@Test(expected = QueriedDateOutOfRangeException.class)
	public void calling_TimeService_And_GettingUTCOffset_WithWrongYear_Should_ThrowException() 
		throws AuthenticationException, ServerSideException, MissingTimeChangesException, 
		QueriedDateOutOfRangeException, LocalTimeDoesNotExistException {
		// Arrange
		int placeId = 187;
		TADDateTime localTime = new TADDateTime(2014, 3, 29, 2, 30, 0);

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);

		// Throws
		firstLocation.getUTCOffsetFromLocalTime(localTime);
	}

	@Test
	public void calling_TimeService_And_GettingUTCOffset_Should_ReturnCorrectOffset() 
		throws AuthenticationException, ServerSideException, MissingTimeChangesException, 
		QueriedDateOutOfRangeException, LocalTimeDoesNotExistException {
		// Arrange
		int placeId = 187;
		int year = Calendar.getInstance().get(Calendar.YEAR);

		TADDateTime localWinterTime = new TADDateTime(year, 2, 15, 2, 30, 0);
		TADDateTime localSummerTime = new TADDateTime(year, 7, 15, 2, 30, 0);
		TADDateTime localFallTime = new TADDateTime(year, 11, 15, 2, 30, 0);

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);

		TimeSpan utcWinterOffset = firstLocation
				.getUTCOffsetFromLocalTime(localWinterTime);
		TimeSpan utcSummerOffset = firstLocation
				.getUTCOffsetFromLocalTime(localSummerTime);
		TimeSpan utcFallOffset = firstLocation
				.getUTCOffsetFromLocalTime(localFallTime);

		// Assert
		assertEquals(TimeSpan.FromHours(1).getHours(),
				utcWinterOffset.getHours());
		assertEquals(TimeSpan.FromHours(2).getHours(),
				utcSummerOffset.getHours());
		assertEquals(TimeSpan.FromHours(1).getHours(), utcFallOffset.getHours());
	}

	@Test
	public void calling_TimeService_And_GettingUTCOffset_WithEdgeCases_Should_ReturnCorrectOffset() 
		throws AuthenticationException, ServerSideException, MissingTimeChangesException, 
		QueriedDateOutOfRangeException, LocalTimeDoesNotExistException {

		// Arrange
		String placeId = "usa/anchorage";
		int year = Calendar.getInstance().get(Calendar.YEAR);

		TADDateTime beforeDstStart = new TADDateTime(year, 3, 12, 1, 0, 0);
		TADDateTime afterDstStart = new TADDateTime(year, 3, 12, 3, 0, 0);
		TADDateTime beforeDstEnd = new TADDateTime(year, 11, 5, 1, 0, 0);
		TADDateTime afterDstEnd = new TADDateTime(year, 11, 5, 2, 0, 0);

		// Act
		TimeService timeservice = new TimeService(Config.AccessKey,
				Config.SecretKey);
		List<Location> result = timeservice.currentTimeForPlace(new LocationId(
				placeId));
		Location firstLocation = result.get(0);

		TimeSpan beforeDstStartOffset = firstLocation
				.getUTCOffsetFromLocalTime(beforeDstStart);
		TimeSpan afterDstStartOffset = firstLocation
				.getUTCOffsetFromLocalTime(afterDstStart);
		TimeSpan beforeDstEndOffset = firstLocation
				.getUTCOffsetFromLocalTime(beforeDstEnd);
		TimeSpan afterDstEndOffset = firstLocation
				.getUTCOffsetFromLocalTime(afterDstEnd);

		// Assert
		assertEquals(TimeSpan.FromHours(-9).getHours(),
				beforeDstStartOffset.getHours(), 0);
		assertEquals(TimeSpan.FromHours(-8).getHours(),
				afterDstStartOffset.getHours(), 0);
		assertEquals(TimeSpan.FromHours(-8).getHours(),
				beforeDstEndOffset.getHours(), 0);
		assertEquals(TimeSpan.FromHours(-9).getHours(),
				afterDstEndOffset.getHours(), 0);
	}

}
