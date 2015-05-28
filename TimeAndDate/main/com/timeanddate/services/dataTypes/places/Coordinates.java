package com.timeanddate.services.dataTypes.places;

/**
 * 
 * @author Cato Auestad <cato@timeanddate.com>
 *
 */
public class Coordinates {
	private double _latitude;
	private double _longitude;
	
	public double getLatitude() {
		return _latitude;
	}
	
	public double getLongitude() {
		return _longitude;
	}

	public Coordinates(double latitude, double longitude) {
		_latitude = latitude;
		_longitude = longitude;
	}
}
