package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.timeanddate.services.ConvertTimeService;
import com.timeanddate.services.ConvertedTimes;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.places.Coordinates;
import com.timeanddate.services.dataTypes.places.Location;
import com.timeanddate.services.dataTypes.places.LocationId;
import com.timeanddate.services.dataTypes.time.TADDateTime;

public class ConvertTimeServiceTests {
	public static TimeZone UsTimezone = TimeZone.getTimeZone("America/Alaska");
	public static Calendar UsTimestamp = Calendar.getInstance(UsTimezone);

	public static TimeZone ArticTimezone = TimeZone
			.getTimeZone("Antarctica/Troll");
	public static Calendar ArticTimestamp = Calendar.getInstance(TimeZone
			.getTimeZone("UTC"));

	public final String fromCountry = "Norway";
	public final String fromCity = "Oslo";
	public static LocationId fromCoords;
	public String fromFormat = "norway/oslo";
	public static LocationId fromId;

	public final String toUsState = "Alaska";
	public final String toUsCountry = "USA";
	public final String toUsCity = "Anchorage";
	public String toUsFormat = "usa/anchorage";
	public LocationId toUsId;

	public final String toArticCountry = "Antarctica";
	public final String toArticCity = "Troll";
	public String toArticFormat = "antarctica/troll";
	public LocationId toArticId;

	public final TADDateTime timeToConvert = new TADDateTime(2015, 05, 14, 11,
			1, 54);
	public final TADDateTime timeToConvertInUTC = new TADDateTime(2015, 05, 14,
			9, 1, 54);
	public final TADDateTime expectedConvertedTimeInAnchorage = new TADDateTime(
			2015, 05, 14, 1, 1, 54);
	public final TADDateTime expectedConvertedTimeInTroll = new TADDateTime(
			2015, 05, 14, 11, 1, 54);

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

	@Before
	public void Setup() {
		fromCoords = new LocationId(new Coordinates(59.913d, 10.752d));
		fromId = new LocationId(fromFormat);
		toUsId = new LocationId(toUsFormat);
		ArticTimestamp.setTimeInMillis(Calendar.getInstance(ArticTimezone)
				.getTimeInMillis());
		toArticId = new LocationId(toArticFormat);
	}

	@Test
	public void Calling_ConvertTimeService_WithNoId_And_WithDateTime_Should_ReturnCorrectConvertedTime() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		ConvertedTimes result = service.convertTime(fromId, timeToConvert);
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		HasCorrectUtc(timeToConvertInUTC, result.Utc.getDateTime());
	}

	@Test
	public void Calling_ConvertTimeService_WithOneToId_And_WithDateTime_Should_ReturnCorrectConvertedTime() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		ConvertedTimes result = service
				.convertTime(fromId, timeToConvert, toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(oslo.getGeography().getName(), fromCity);
		assertEquals(oslo.getGeography().getCountry().getName(), fromCountry);

		HasCorrectUtc(timeToConvertInUTC, result.Utc.getDateTime());
		HasCorrectLocation(expectedConvertedTimeInAnchorage, anchorage);
	}

	@Test
	public void Calling_ConvertTimeService_WithMultipleToIds_And_WithDateTime_Should_ReturnCorrectConvertedTime() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);
		toId.add(toArticId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		ConvertedTimes result = service
				.convertTime(fromId, timeToConvert, toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");
		Location troll = getLocationById(result, "4365");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(troll.getGeography().getCountry().getName(), toArticCountry);
		assertEquals(troll.getGeography().getName(), toArticCity + " Station");

		assertEquals(oslo.getGeography().getName(), fromCity);
		assertEquals(oslo.getGeography().getCountry().getName(), fromCountry);

		HasCorrectLocation(expectedConvertedTimeInAnchorage, anchorage);
		HasCorrectLocation(expectedConvertedTimeInTroll, troll);
		HasCorrectUtc(timeToConvertInUTC, result.Utc.getDateTime());
	}

	@Test
	public void Calling_ConvertTimeService_WithMultipleToIds_And_WithISO_Should_ReturnCorrectConvertedTime() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);
		toId.add(toArticId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		ConvertedTimes result = service.convertTime(fromId,
				timeToConvert.toString(), toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");
		Location troll = getLocationById(result, "4365");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(toArticCountry, troll.getGeography().getCountry().getName());
		assertEquals(toArticCity + " Station", troll.getGeography().getName());

		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		HasCorrectLocation(expectedConvertedTimeInAnchorage, anchorage);
		HasCorrectLocation(expectedConvertedTimeInTroll, troll);
		HasCorrectUtc(timeToConvertInUTC, result.Utc.getDateTime());
	}

	@Test
	public void Calling_ConvertTimeService_WithNoId_And_WithISO_Should_ReturnCorrectConvertedTime() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		ConvertedTimes result = service.convertTime(fromId,
				timeToConvert.toString());
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		HasCorrectUtc(timeToConvertInUTC, result.Utc.getDateTime());
	}

	@Test
	public void Calling_ConvertTimeService_WithOneToId_And_WithISO_Should_ReturnCorrectConvertedTime() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		ConvertedTimes result = service.convertTime(fromId,
				timeToConvert.toString(), toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		HasCorrectLocation(expectedConvertedTimeInAnchorage, anchorage);
		HasCorrectUtc(timeToConvertInUTC, result.Utc.getDateTime());
	}

	@Test
	public void Calling_ConvertTimeService_WithoutTimeChanges_Should_NotReturnTimeChanges() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeTimeChanges(false);
		ConvertedTimes result = service.convertTime(fromId,
				timeToConvert.toString(), toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		for (Location loc : result.Locations) {
			assertEquals(0, loc.getTimeChanges().size());
		}
	}

	@Test
	public void Calling_ConvertTimeService_WithTimeChanges_Should_ReturnTimeChanges() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeTimeChanges(true);
		ConvertedTimes result = service.convertTime(fromId,
				timeToConvert.toString(), toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		for (Location loc : result.Locations) {
			assertNotNull(loc.getTimeChanges());
		}
	}

	@Test
	public void Calling_ConvertTimeService_WithoutTimezone_Should_NotReturnTZInformation() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeTimezoneInformation(false);
		ConvertedTimes result = service.convertTime(fromId,
				timeToConvert.toString(), toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		for (Location loc : result.Locations) {
			assertNull(loc.getTime().getTimezone());
		}
	}

	@Test
	public void Calling_ConvertTimeService_WithTimezone_Should_ReturnTZInformation() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeTimezoneInformation(true);
		ConvertedTimes result = service.convertTime(fromId,
				timeToConvert.toString(), toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		for (Location loc : result.Locations) {
			assertNotNull(loc.getTime().getTimezone());
		}
	}

	@Test
	public void Calling_ConvertTimeService_WithRadius_Should_ReturnCorrectLocation() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		List<LocationId> toId = new ArrayList<LocationId>();
		toId.add(toUsId);

		// Act
		ConvertTimeService service = new ConvertTimeService(Config.AccessKey,
				Config.SecretKey);
		service.setRadius(50);
		ConvertedTimes result = service.convertTime(fromCoords,
				timeToConvert.toString(), toId);
		Location anchorage = getLocationById(result, "18");
		Location oslo = getLocationById(result, "187");

		// Assert
		assertEquals(toUsState, anchorage.getGeography().getState());
		assertEquals(toUsCity, anchorage.getGeography().getName());

		assertEquals(fromCity, oslo.getGeography().getName());
		assertEquals(fromCountry, oslo.getGeography().getCountry().getName());

		for (Location loc : result.Locations) {
			assertNotNull(loc.getTime().getTimezone());
		}
	}

	public void HasCorrectLocation(TADDateTime date, Location location) {
		assertEquals(date.getYear(), location.getTime().getDateTime().getYear());
		assertEquals(date.getMonth(), location.getTime().getDateTime().getMonth());
		assertEquals(date.getDayOfMonth(),
				location.getTime().getDateTime().getDayOfMonth());
		assertEquals(date.getHour(), location.getTime().getDateTime().getHour());
		assertEquals(date.getMinute(), location.getTime().getDateTime().getMinute());
	}

	public void HasCorrectUtc(TADDateTime utc, TADDateTime date) {
		assertEquals(utc.getYear(), date.getYear());
		assertEquals(utc.getMonth(), date.getMonth());
		assertEquals(utc.getDayOfMonth(), date.getDayOfMonth());
		assertEquals(utc.getHour(), date.getHour());
		assertEquals(utc.getMinute(), date.getMinute());
		assertEquals(utc.getSecond(), date.getSecond());
	}

	private Location getLocationById(ConvertedTimes ct, String id) {
		for (Location loc : ct.Locations) {
			if (loc.getId().equals(id))
				return loc;
		}

		return null;
	}
}
