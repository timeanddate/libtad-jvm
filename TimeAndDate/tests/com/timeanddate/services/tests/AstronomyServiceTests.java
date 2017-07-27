package com.timeanddate.services.tests;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import com.timeanddate.services.AstronomyService;
import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.DateTimeUtils;
import com.timeanddate.services.common.QueriedDateOutOfRangeException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.astro.AstronomyDay;
import com.timeanddate.services.dataTypes.astro.AstronomyDayEvent;
import com.timeanddate.services.dataTypes.astro.AstronomyEventClass;
import com.timeanddate.services.dataTypes.astro.AstronomyEventCode;
import com.timeanddate.services.dataTypes.astro.AstronomyLocation;
import com.timeanddate.services.dataTypes.astro.AstronomyObjectDetails;
import com.timeanddate.services.dataTypes.astro.AstronomyObjectType;
import com.timeanddate.services.dataTypes.places.Coordinates;
import com.timeanddate.services.dataTypes.places.LocationId;
import com.timeanddate.services.dataTypes.time.TADDateTime;

public class AstronomyServiceTests {

	@Test
	public void Calling_AstronomyService_WithoutEnddate_ReturnsCorrectAstronomyInfo() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);
		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId place = new LocationId("usa/anchorage");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate);
		AstronomyLocation anchorage = result.get(0);

		// Assert
		for (AstronomyLocation loc : result) {
			for (AstronomyObjectDetails obj : loc.getObjects())
				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getMonth() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());
				}
		}

		for (AstronomyObjectDetails obj : anchorage.getObjects())
			for (AstronomyDay day : obj.getDays()) {
				for (AstronomyDayEvent ev : day.getEvents())
					assertTrue(ev.getAzimuth() > 0d);
			}

		assertEquals("United States", anchorage.getGeography().getCountry().getName());
		assertEquals("us", anchorage.getGeography().getCountry().getId());
		assertEquals(61.188d, anchorage.getGeography().getCoordinates().getLatitude(), 0d);
		assertEquals(-149.887d, anchorage.getGeography().getCoordinates().getLongitude(), 0d);
		assertEquals("18", anchorage.getId());
	}

	@Test
	public void Calling_AstronomyService_WithEnddate_Should_ReturnCorrectAstronomyInfo() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);
		TADDateTime endDate = new TADDateTime(2014, 1, 30);
		AstronomyObjectType type = AstronomyObjectType.Moon;
		LocationId place = new LocationId("usa/anchorage");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate);

		// Assert
		for (AstronomyLocation loc : result) {
			for (AstronomyObjectDetails obj : loc.getObjects())
				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(DateTimeUtils.ToMinuteCompare(day.getDate(),
							startDate) >= 0
							&& DateTimeUtils.ToMinuteCompare(day.getDate(), endDate) < 0);
				}

			assertTrue(loc.getGeography().getCountry().getName().equals("United States"));
		}
		// Assert.IsTrue (result.All (x => x.Objects.All (y => y.Days.All (z =>
		// z.Date.Value.Date >= startDate && z.Date.Value.Date <= endDate))));
	}

	@Test
	public void Calling_AstronomyService_WithoutEnddate_And_OnlyOneInclusions_Should_ReturnCorrectAstronomyInfo() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);
		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId place = new LocationId("norway/oslo");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setAstronomyEventTypes(EnumSet.of(AstronomyEventClass.MERIDIAN));
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate);

		// Assert
		for (AstronomyLocation loc : result) {
			assertEquals("Norway", loc.getGeography().getCountry().getName());

			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertEquals(1, obj.getDays().size());

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getMonth() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());

					for (AstronomyDayEvent ev : day.getEvents()) {
						assertTrue(ev.getType() == AstronomyEventCode.Meridian
								|| ev.getType() == AstronomyEventCode.AntiMeridian);
					}
				}
			}
		}
	}

	@Test
	public void Calling_AstronomyService_WithEnddate_And_SeveralInclusions_Should_ReturnsCorrectAstronomyInfo() 
		throws AuthenticationException, QueriedDateOutOfRangeException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);
		TADDateTime endDate = new TADDateTime(2014, 1, 30);
		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId place = new LocationId("norway/oslo");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setAstronomyEventTypes(EnumSet.of(AstronomyEventClass.ASTRONOMICALTWILIGHT,
				AstronomyEventClass.NAUTICALTWILIGHT));
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate, endDate);

		// Assert
		for (AstronomyLocation loc : result) {
			assertEquals("Norway", loc.getGeography().getCountry().getName());

			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertEquals(30, obj.getDays().size());

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getMonth() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());

					for (AstronomyDayEvent ev : day.getEvents()) {
						assertTrue(ev.getType() == AstronomyEventCode.AstronomicalTwilightEnds
								|| ev.getType() == AstronomyEventCode.AstronomicalTwilightStarts
								|| ev.getType() == AstronomyEventCode.NauticalTwilightStarts
								|| ev.getType() == AstronomyEventCode.NauticalTwilightEnds);
					}
				}
			}
		}
	}

	@Test
	public void Calling_AstronomyService_WithoutLatLong_Should_ReturnsCorrectAstronomyInfo() 
		throws AuthenticationException, QueriedDateOutOfRangeException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);
		TADDateTime endDate = new TADDateTime(2014, 1, 30);
		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId place = new LocationId("norway/oslo");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeCoordinates(false);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate, endDate);

		// Assert
		for (AstronomyLocation loc : result) {
			assertNull(loc.getGeography().getCoordinates());

			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertEquals(30, obj.getDays().size());

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getMonth() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());
				}
			}
		}

		// Assert.IsTrue (result.All (x => x.Objects.All (y => y.Days.All (z =>
		// z.Date.Value.Date >= startDate.Date))));
	}

	@Test
	public void Calling_AstronomyService_WithLatLong_WithoutISOTime_Should_ReturnCorrectAstronomyInfo() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);

		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId place = new LocationId("norway/oslo");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeCoordinates(true);
		service.setIncludeISOTime(false);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate);

		// Assert
		for (AstronomyLocation loc : result) {
			assertEquals(59.913d, loc.getGeography().getCoordinates().getLatitude(), 0d);
			assertEquals(10.740d, loc.getGeography().getCoordinates().getLongitude(), 0d);

			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertEquals(1, obj.getDays().size());

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getMonth() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());
				}
			}
		}
	}

	@Test
	public void Calling_AstronomyService_WithoutISOTime_Should_ReturnsCorrectAstronomyInfo() 
		throws AuthenticationException, QueriedDateOutOfRangeException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);
		TADDateTime endDate = new TADDateTime(2014, 1, 30);
		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId place = new LocationId("norway/oslo");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeISOTime(false);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate, endDate);

		// Assert
		for (AstronomyLocation loc : result) {
			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertEquals(30, obj.getDays().size());

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getMonth() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());

					for (AstronomyDayEvent ev : day.getEvents()) {
						assertNull(ev.getISOTime());
					}
				}
			}
		}
	}

	@Test
	public void Calling_AstronomyService_WithISOTime_Should_ReturnsCorrectAstronomyInfo() 
		throws AuthenticationException, QueriedDateOutOfRangeException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);
		TADDateTime endDate = new TADDateTime(2014, 1, 30);
		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId place = new LocationId("norway/oslo");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeISOTime(true);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate, endDate);

		// Assert
		for (AstronomyLocation loc : result) {
			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertEquals(30, obj.getDays().size());

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getYear() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());

					for (AstronomyDayEvent ev : day.getEvents()) {
						assertThat(ev.getISOTime().getYear(), not(1));
						assertEquals(1, ev.getISOTime().getMonth());
					}
				}
			}
		}
	}

	@Test
	public void Calling_AstronomyService_WithoutUTCTime_Should_ReturnsCorrectAstronomyInfo() 
		throws AuthenticationException, QueriedDateOutOfRangeException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 3, 2);
		TADDateTime endDate = new TADDateTime(2014, 3, 20);
		AstronomyObjectType type = AstronomyObjectType.Moon;
		LocationId place = new LocationId("norway/oslo");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeUTCTime(false);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate, endDate);

		// Assert
		for (AstronomyLocation loc : result) {
			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertTrue(obj.getDays().size() > 1);

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getMonth() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());

					for (AstronomyDayEvent ev : day.getEvents()) {
						assertNull(ev.getUTCTime());
					}
				}
			}
		}
	}

	@Test
	public void Calling_AstronomyService_WithUTCTime_Should_ReturnsCorrectAstronomyInfo() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 2, 3);
		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId place = new LocationId("norway/oslo");

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeUTCTime(true);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				place, startDate);

		// Assert
		for (AstronomyLocation loc : result) {
			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertTrue(obj.getDays().size() >= 1);

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
					assertTrue(day.getDate().getYear() == startDate.getYear());
					assertTrue(day.getDate().getMonth() >= startDate.getMonth());
					assertTrue(day.getDate().getDayOfMonth() >= startDate
							.getDayOfMonth());

					for (AstronomyDayEvent ev : day.getEvents()) {
						assertEquals(2014, ev.getUTCTime().getYear());
						assertEquals(2, ev.getUTCTime().getMonth());
						assertEquals(3, ev.getUTCTime().getDayOfMonth());
					}
				}
			}
		}
	}

	@Test
	public void Calling_AstronomyService_WithRadiusTime_Should_ReturnsCorrectAstronomyInfo() 
		throws AuthenticationException, IllegalArgumentException, ServerSideException {
		// Arrange
		TADDateTime startDate = new TADDateTime(2014, 1, 1);
		AstronomyObjectType type = AstronomyObjectType.Sun;
		LocationId drammenCoords = new LocationId(new Coordinates(59.743d,
				10.204d));

		// Act
		AstronomyService service = new AstronomyService(Config.AccessKey,
				Config.SecretKey);
		service.setRadius(50);
		List<AstronomyLocation> result = service.getAstronomicalInfo(type,
				drammenCoords, startDate);

		// Assert
		for (AstronomyLocation loc : result) {
			assertEquals("Norway", loc.getGeography().getCountry().getName());
			assertEquals("Drammen", loc.getGeography().getName());

			for (AstronomyObjectDetails obj : loc.getObjects()) {
				assertTrue(obj.getDays().size() >= 1);

				for (AstronomyDay day : obj.getDays()) {
					assertNotNull(day.getDate());
				}
			}
		}
	}
}
