package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.timeanddate.services.DialCodeService;
import com.timeanddate.services.DialCodes;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.dialCode.Composition;
import com.timeanddate.services.dataTypes.dialCode.PhoneNumberElementType;
import com.timeanddate.services.dataTypes.places.Location;
import com.timeanddate.services.dataTypes.places.LocationId;

public class DialCodeServiceTests {

	@Test
	public void Calling_DialCodeService_WithToId_Should_ReturnTheCorrectDialCode() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		LocationId osloId = new LocationId("norway/oslo");

		// Act
		DialCodeService service = new DialCodeService(Config.AccessKey,
				Config.SecretKey);
		DialCodes result = service.getDialCode(osloId);

		// Assert
		for (Location loc : result.Locations) {
			assertEquals("Norway", loc.getGeography().getCountry().getName());
		}
	}

	@Test
	public void Calling_DialCodeService_WithToId_And_WithFromId_Should_ReturnTheCorrectDialCode() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		LocationId osloId = new LocationId("norway/oslo");
		LocationId newYorkId = new LocationId("usa/new-york");

		// Act
		DialCodeService service = new DialCodeService(Config.AccessKey,
				Config.SecretKey);
		DialCodes result = service.getDialCode(osloId, newYorkId);

		// Assert
		String norway = "";
		String us = "";

		for (Location loc : result.Locations) {
			if (loc.getGeography().getCountry().getName().equals("Norway"))
				norway = loc.getGeography().getCountry().getName();
			else if (loc.getGeography().getCountry().getName().equals("United States"))
				us = loc.getGeography().getCountry().getName();
		}

		assertEquals("Norway", norway);
		assertEquals("United States", us);
	}

	@Test
	public void Calling_DialCodeService_WithToId_And_WithFromId_And_WithNumber_Should_ReturnTheCorrectDialCode() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		LocationId osloId = new LocationId("norway/oslo");
		LocationId newYorkId = new LocationId("usa/new-york");
		int sampleNumber = 1234567;

		// Act
		DialCodeService service = new DialCodeService(Config.AccessKey,
				Config.SecretKey);
		DialCodes result = service.getDialCode(osloId, newYorkId, sampleNumber);

		Composition intl = result.Compositions.get(0);
		Composition ctry = result.Compositions.get(1);
		Composition local = result.Compositions.get(2);

		Location newYork = result.Locations.get(0);
		Location oslo = result.Locations.get(1);

		// Assert
		assertEquals(intl.getPhoneNumberElement(),
				PhoneNumberElementType.InternationalPrefix);
		assertEquals("011", intl.getNumber());
		assertTrue(intl.getDescription() != null && !intl.getDescription().isEmpty());

		assertEquals(local.getPhoneNumberElement(),
				PhoneNumberElementType.LocalNumber);
		assertEquals(sampleNumber, Integer.parseInt(local.getNumber()));
		assertTrue(local.getDescription() != null && !local.getDescription().isEmpty());

		assertEquals(ctry.getPhoneNumberElement(),
				PhoneNumberElementType.CountryPrefix);
		assertEquals("47", ctry.getNumber());
		assertTrue(ctry.getDescription() != null && !ctry.getDescription().isEmpty());

		assertNotNull(oslo.getTime());
		assertNotNull(oslo.getTime().getISO());
		assertNotNull(oslo.getTime().getTimezone());

		assertNotNull(newYork.getTime());
		assertNotNull(newYork.getTime().getISO());
		assertNotNull(newYork.getTime().getTimezone());

		assertEquals("011 47 1234567", result.Number);

		String norway = "";
		String us = "";

		for (Location loc : result.Locations) {
			if (loc.getGeography().getCountry().getName().equals("Norway"))
				norway = loc.getGeography().getCountry().getName();
			else if (loc.getGeography().getCountry().getName().equals("United States"))
				us = loc.getGeography().getCountry().getName();
		}

		assertNotEquals("", norway);
		assertNotEquals("", us);
	}

	@Test
	public void Calling_DialCodeService_WithToId_And_WithoutLocation_Should_NotReturnLocations() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		LocationId osloId = new LocationId("norway/oslo");

		// Act
		DialCodeService service = new DialCodeService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeLocations(false);
		DialCodes result = service.getDialCode(osloId);

		// Assert
		assertEquals(0, result.Locations.size());
	}

	@Test
	public void Calling_DialCodeService_WithToId_And_WithoutLatLong_Should_NotReturnLocations() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		LocationId osloId = new LocationId("norway/oslo");

		// Act
		DialCodeService service = new DialCodeService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeCoordinates(false);
		DialCodes result = service.getDialCode(osloId);

		// Assert
		for (Location loc : result.Locations) {
			assertNull(loc.getGeography().getCoordinates());
		}
	}

	@Test
	public void Calling_DialCodeService_WithToId_And_WithoutTZInfo_Should_NotReturnTZInfo() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		LocationId osloId = new LocationId("norway/oslo");

		// Act
		DialCodeService service = new DialCodeService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeTimezoneInformation(false);
		DialCodes result = service.getDialCode(osloId);

		// Assert
		for (Location loc : result.Locations) {
			assertNull(loc.getTime().getTimezone());
		}
	}

	@Test
	public void Calling_DialCodeService_WithToId_And_WithoutCurrentTime_Should_NotReturnCurrentTime() 
		throws AuthenticationException, ServerSideException {
		// Arrange
		LocationId osloId = new LocationId("norway/oslo");

		// Act
		DialCodeService service = new DialCodeService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeCurrentTime(false);
		DialCodes result = service.getDialCode(osloId);

		// Assert
		for (Location loc : result.Locations) {
			assertNull(loc.getTime());
		}

	}
}
