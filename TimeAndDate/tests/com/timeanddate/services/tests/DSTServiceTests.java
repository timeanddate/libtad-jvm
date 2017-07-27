package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.timeanddate.services.DSTService;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.dst.DST;
import com.timeanddate.services.dataTypes.dst.DSTSpecialType;

public class DSTServiceTests {

	@Test
	public void Calling_DstService_Should_ReturnAllDst() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		int expectedReturnedCount = 128;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		List<DST> result = service.getDaylightSavingTime();
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertEquals(expectedReturnedCount, result.size());

		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithYear_Should_ReturnAllDst() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		int year = 2014;
		int expectedReturnedCount = 132;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		List<DST> result = service.getDaylightSavingTime(year);
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertEquals(expectedReturnedCount, result.size());

		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithCountry_Should_ReturnAllDst() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		String countryCode = "no";
		String country = "Norway";

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		List<DST> result = service.getDaylightSavingTime(countryCode);
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertFalse(service.getIncludeOnlyDstCountries());
		assertEquals(country, sampleCountry.getRegion().getCountry().getName());
		assertEquals(1, result.size());

		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithCountry_And_WithYear_Should_ReturnAllDst() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		String countryCode = "no";
		int year = 2014;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		List<DST> result = service.getDaylightSavingTime(countryCode, year);
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertFalse(service.getIncludeOnlyDstCountries());
		assertEquals(1, result.size());
		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithoutPlacesForEveryCountry_Should_ReturnAllDstWithoutPlaces() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		int year = 2014;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		service.setIncludePlacesForEveryCountry(false);
		List<DST> result = service.getDaylightSavingTime(year);
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertFalse(service.getIncludePlacesForEveryCountry());

		for (DST dst : result) {
			assertEquals(0, dst.getRegion().getLocations().size());
		}

		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithPlacesForEveryCountry_Should_ReturnAnyDstWithPlaces() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		int year = 2014;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		List<DST> result = service.getDaylightSavingTime(year);
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertTrue(service.getIncludePlacesForEveryCountry());
		for (DST dst : result) {
			assertTrue(dst.getRegion().getLocations().size() > 0);
		}

		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithoutTimeChanges_Should_NotReturnAnyTimeChanges() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		int year = 2014;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		service.setIncludeTimeChanges(false);
		List<DST> result = service.getDaylightSavingTime(year);
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertFalse(service.getIncludeTimeChanges());
		for (DST dst : result) {
			assertEquals(0, dst.getTimeChanges().size());
		}

		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithTimeChanges_Should_ReturnAnyTimeChanges() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		int year = 2014;
		boolean timeChangesExist = false;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		service.setIncludeTimeChanges(true);
		List<DST> result = service.getDaylightSavingTime(year);
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertTrue(service.getIncludeTimeChanges());
		for (DST dst : result) {
			if (dst.getTimeChanges().size() > 0)
				timeChangesExist = true;
		}

		assertTrue(timeChangesExist);
		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithOnlyDstCountries_Should_ReturnOnlyDstCountries() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		int year = 2014;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		service.setIncludeOnlyDstCountries(true);
		List<DST> result = service.getDaylightSavingTime(year);
		DST sampleCountry = getSampleCountry(result, "Norway");

		// Assert
		assertTrue(service.getIncludeOnlyDstCountries());
		assertEquals(132, result.size());

		HasValidSampleCountry(sampleCountry);
	}

	@Test
	public void Calling_DstService_WithoutOnlyDstCountries_Should_ReturnAllCountries() 
			throws AuthenticationException, ServerSideException {
		// Arrage
		int year = 2014;

		// Act
		DSTService service = new DSTService(
				Config.AccessKey, Config.SecretKey);
		service.setIncludeOnlyDstCountries(false);
		List<DST> result = service.getDaylightSavingTime(year);
		List<DST> dstAllYear = new ArrayList<DST>();
		List<DST> noDstAllYear = new ArrayList<DST>();
		DST sampleCountry = getSampleCountry(result, "Norway");

		for (DST dst : result) {
			if (dst.getSpecial() == DSTSpecialType.DaylightSavingTimeAllYear)
				dstAllYear.add(dst);
			else if (dst.getSpecial() == DSTSpecialType.NoDaylightSavingTime)
				noDstAllYear.add(dst);
		}

		// Assert
		assertFalse(service.getIncludeOnlyDstCountries());
		assertEquals(348, result.size());
		assertTrue(dstAllYear.size() > 0);
		assertTrue(noDstAllYear.size() > 0);

		HasValidSampleCountry(sampleCountry);
	}

	public void HasValidSampleCountry(DST norway) {
		assertEquals("Oslo", norway.getRegion().getBiggestPlace());
		assertEquals("no", norway.getRegion().getCountry().getId());

		assertTrue(norway.getDstEnd().getYear() > 1);
		assertTrue(norway.getDstStart().getYear() > 1);
		assertEquals("CEST", norway.getDstTimezone().getAbbrevation());
		assertEquals(3600, norway.getDstTimezone().getBasicOffset());
		assertEquals(3600, norway.getDstTimezone().getDSTOffset());
		assertEquals(7200, norway.getDstTimezone().getTotalOffset());
		assertEquals("Central European Summer Time", norway.getDstTimezone().getName());
		assertEquals(2, norway.getDstTimezone().getOffset().getHours());
		assertEquals(0, norway.getDstTimezone().getOffset().getMinutes());

		assertEquals("CET", norway.getStandardTimezone().getAbbrevation());
		assertEquals(3600, norway.getStandardTimezone().getBasicOffset());
		assertEquals(0, norway.getStandardTimezone().getDSTOffset());
	}

	private DST getSampleCountry(List<DST> result, String name) {
		for (DST dst : result) {
			if (dst.getRegion().getCountry().getName().equals(name))
				return dst;
		}

		return null;
	}
}
