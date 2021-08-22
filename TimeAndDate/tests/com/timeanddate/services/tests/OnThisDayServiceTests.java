package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import com.timeanddate.services.OnThisDayService;
import com.timeanddate.services.OnThisDayEvents;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.onthisday.Event;
import com.timeanddate.services.dataTypes.onthisday.Person;
import com.timeanddate.services.dataTypes.onthisday.OTDEventType;
import com.timeanddate.services.dataTypes.time.TADDateTime;

public class OnThisDayServiceTests {

	@Test
	public void Calling_OnThisDayService_WithMonth_And_WithDay_Should_ReturnEvents() throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		int month = 5;
		int day = 24;

		TADDateTime expectedDate = new TADDateTime(2021, 5, 24);

		// Act
		OnThisDayService onthisdayService = new OnThisDayService(Config.AccessKey, Config.SecretKey);
		OnThisDayEvents result = onthisdayService.eventsOnThisDay(month, day);
		Event firstEvent = result.Events.get(0);
		Person firstBirth = result.Births.get(0);
		Person firstDeath = result.Deaths.get(0);

		// Assert
		assertNotNull(firstEvent);
		assertNotNull(firstBirth);
		assertNotNull(firstDeath);

		assertEquals(expectedDate.getMonth(), firstEvent.getDate().getDateTime().getMonth());
		assertEquals(expectedDate.getDayOfMonth(), firstEvent.getDate().getDateTime().getDayOfMonth());

		assertEquals(expectedDate.getMonth(),
				firstBirth.getBirthDate().getDateTime().getMonth());
		assertEquals(expectedDate.getDayOfMonth(),
				firstBirth.getBirthDate().getDateTime().getDayOfMonth());

		assertEquals(expectedDate.getMonth(),
				firstDeath.getDeathDate().getDateTime().getMonth());
		assertEquals(expectedDate.getDayOfMonth(),
				firstDeath.getDeathDate().getDateTime().getDayOfMonth());

	}

	@Test
	public void Calling_OnThisDayService_With_Events_Should_Return_Events_Only() throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		int month = 5;
		int day = 24;
		EnumSet<OTDEventType> type = EnumSet.of(OTDEventType.EVENTS);

		// Act
		OnThisDayService onthisdayService = new OnThisDayService(Config.AccessKey, Config.SecretKey);
		onthisdayService.setEventTypes(type);
		OnThisDayEvents result = onthisdayService.eventsOnThisDay(month, day);

		// Assert
		assertTrue(result.Events.size() > 0);
		assertEquals(0, result.Births.size());
		assertEquals(0, result.Deaths.size());
	}

	@Test
	public void Calling_OnThisDayService_With_Births_Should_Return_Births_Only() throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		int month = 5;
		int day = 24;
		EnumSet<OTDEventType> type = EnumSet.of(OTDEventType.BIRTHS);

		// Act
		OnThisDayService onthisdayService = new OnThisDayService(Config.AccessKey, Config.SecretKey);
		onthisdayService.setEventTypes(type);
		OnThisDayEvents result = onthisdayService.eventsOnThisDay(month, day);

		// Assert
		assertTrue(result.Births.size() > 0);
		assertEquals(0, result.Events.size());
		assertEquals(0, result.Deaths.size());
	}

	@Test
	public void Calling_OnThisDayService_With_Deaths_Should_Return_Deaths_Only() throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
		// Arrange
		int month = 5;
		int day = 24;
		EnumSet<OTDEventType> type = EnumSet.of(OTDEventType.DEATHS);

		// Act
		OnThisDayService onthisdayService = new OnThisDayService(Config.AccessKey, Config.SecretKey);
		onthisdayService.setEventTypes(type);
		OnThisDayEvents result = onthisdayService.eventsOnThisDay(month, day);

		// Assert
		assertTrue(result.Deaths.size() > 0);
		assertEquals(0, result.Events.size());
		assertEquals(0, result.Births.size());
	}
}
