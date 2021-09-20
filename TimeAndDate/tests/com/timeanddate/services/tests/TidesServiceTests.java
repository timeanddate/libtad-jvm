package com.timeanddate.services.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.TidesService;
import com.timeanddate.services.dataTypes.tides.*;
import com.timeanddate.services.dataTypes.places.LocationId;
import com.timeanddate.services.dataTypes.time.TADDateTime;

public class TidesServiceTests {
    @Test
    public void Calling_TidesService_For_A_Specific_Date_Interval_Should_ReturnResults()
    	throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
        // Arrange
        var location = new LocationId("norway/stavanger");
        var startDate = new TADDateTime(2021, 9, 8);
        var endDate = new TADDateTime(2021, 9, 8, 23, 59, 59);

        // Act
        var tidesService = new TidesService(Config.AccessKey, Config.SecretKey);
        tidesService.setStartDate(startDate);
        tidesService.setEndDate(endDate);
        var result = tidesService.getTidalData(location);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Stavanger", result.get(0).getSource().getName());
        assertEquals("Reference Station", result.get(0).getSource().getType());
        assertEquals("norway/stavanger", result.get(0).getMatchParam());

        for (Tide tide : result.get(0).getResult()) {
            assertEquals(9, tide.getTime().getDateTime().getMonth());
            assertEquals(8, tide.getTime().getDateTime().getDayOfMonth());
            assertTrue(tide.getPhase() == TidalPhase.High || tide.getPhase() == TidalPhase.Low);
        }
    }

    @Test
    public void Calling_TidesService_Without_OnlyHighLow_Shoud_Return_Intermediate_Points()
    	throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
        // Arrange
        var location = new LocationId("norway/stavanger");

        // Act
        var tidesService = new TidesService(Config.AccessKey, Config.SecretKey);
        tidesService.setOnlyHighLow(false);
        var result = tidesService.getTidalData(location);

        // Assert
        assertEquals(1, result.size());
        assertTrue(
            result.get(0).getResult().stream().anyMatch(x -> (x.getPhase() == TidalPhase.Ebb) || (x.getPhase() == TidalPhase.Flood))
        );
    }

    @Test
    public void Requesting_Tidal_Data_For_A_Subordinate_Station_Should_Resolve_To_A_Subordinate_Station()
    	throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
        // Arrange
        var location = new LocationId("norway/sola");

        // Act
        var tidesService = new TidesService(Config.AccessKey, Config.SecretKey);
        tidesService.setSubordinate(true);
        var result = tidesService.getTidalData(location);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Subordinate Station", result.get(0).getSource().getType());
    }

    @Test
    public void Requesting_Tidal_Data_In_Intervals_of_60_minutes()
    	throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
        // Arrange
        var location = new LocationId("norway/stavanger");

        // Act
        var tidesService = new TidesService(Config.AccessKey, Config.SecretKey);
        tidesService.setInterval(60);
        tidesService.setOnlyHighLow(false);
        var result = tidesService.getTidalData(location);

        // Assert
        assertEquals(1, result.size());
        assertEquals(24, result.get(0).getResult().size());
    }

    @Test
    public void Requesting_Tidal_Data_In_Intervals_of_30_minutes()
    	throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
        // Arrange
        var location = new LocationId("norway/stavanger");

        // Act
        var tidesService = new TidesService(Config.AccessKey, Config.SecretKey);
        tidesService.setInterval(30);
        tidesService.setOnlyHighLow(false);
        var result = tidesService.getTidalData(location);

        // Assert
        assertEquals(1, result.size());
        assertEquals(48, result.get(0).getResult().size());
    }

    @Test
    public void Requesting_Tidal_Data_In_Intervals_of_15_minutes()
    	throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
        // Arrange
        var location = new LocationId("norway/stavanger");

        // Act
        var tidesService = new TidesService(Config.AccessKey, Config.SecretKey);
        tidesService.setInterval(15);
        tidesService.setOnlyHighLow (false);
        var result = tidesService.getTidalData(location);

        // Assert
        assertEquals(1, result.size());
        assertEquals(96, result.get(0).getResult().size());
    }

    @Test
    public void Requesting_Tidal_Data_In_Intervals_of_5_minutes()
    	throws AuthenticationException, URISyntaxException, ServerSideException, MalformedURLException {
        // Arrange
        var location = new LocationId("norway/stavanger");

        // Act
        var tidesService = new TidesService(Config.AccessKey, Config.SecretKey);
        tidesService.setInterval(5);
        tidesService.setOnlyHighLow(false);
        var result = tidesService.getTidalData(location);

        // Assert
        assertEquals(1, result.size());
        assertEquals(288, result.get(0).getResult().size());
    }

}
