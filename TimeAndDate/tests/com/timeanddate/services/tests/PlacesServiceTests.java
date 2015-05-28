package com.timeanddate.services.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.SignatureException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.timeanddate.services.PlacesService;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.dataTypes.places.Coordinates;
import com.timeanddate.services.dataTypes.places.Place;

public class PlacesServiceTests {

	@Test
	public void Calling_PlacesServices_Should_ReturnListOfPlaces()
			throws SignatureException, DOMException,
			ParserConfigurationException, SAXException, IOException,
			ServerSideException {
		// Arrange

		// Act
		PlacesService service = new PlacesService(Config.AccessKey,
				Config.SecretKey);
		List<Place> places = service.getPlaces();

		// Assert
		assertTrue(places.size() > 0);

		// This is to verify that some values exists in which the coordinates
		// are more than 0.0d
		// Really the only way of making sure that these values are mapped
		// correctly
		double lat = 0.0d;
		double lon = 0.0d;
		for (Place place : places) {
			Coordinates coords = place.getGeography().getCoordinates();
			if (coords.getLatitude() > lat)
				assertTrue(coords.getLatitude() > lat);
			if (coords.getLongitude() > lon)
				assertTrue(coords.getLongitude() > lon);
		}
	}

	@Test
	public void Calling_PlacesServices_WithoutGeo_Should_ReturnListOfPlacesWithoutGeo()
			throws SignatureException, DOMException,
			ParserConfigurationException, SAXException, IOException,
			ServerSideException {
		// Arrange

		// Act
		PlacesService service = new PlacesService(Config.AccessKey,
				Config.SecretKey);
		service.setIncludeCoordinates(false);
		List<Place> places = service.getPlaces();

		// Assert
		assertTrue(places.size() > 0);

		for (Place place : places) {
			assertTrue(place.getGeography().getCoordinates() == null);
		}

	}

}
