package com.timeanddate.services.dataTypes.places;

import com.timeanddate.services.common.StringUtils;

public class LocationId {
	private String _textualId;
	private int _numericId = -1;
	private Coordinates _coordinatesId;

	public String getId() {
		String id;
		if (_numericId > 0)
			id = Integer.toString(_numericId);
		else if (_textualId != null && !_textualId.isEmpty())
			id = _textualId;
		else if (_coordinatesId != null)
			id = StringUtils.placeIdByCoordinates(
					_coordinatesId.getLatitude(),
					_coordinatesId.getLongitude());
		else 
			id = "";

		return id;
	}
	
	public Coordinates getCoordinates() throws NullPointerException {
		if(_coordinatesId == null)
			throw new NullPointerException("Coordinates does not exist on this object");
		
		return _coordinatesId;
	}
	
	/**
	 * Create a LocationId based on a textual ID
	 * 
	 * @param textualId
	 *            Can be country code, country name, city, etc
	 */
	public LocationId(String textualId) {
		_textualId = textualId;
	}

	/**
	 * Create a LocationId based on an internal integer ID
	 * 
	 * @param numericId
	 *            Usually an integer ID that is returned from a previous API
	 *            call
	 */
	public LocationId(int numericId) {
		_numericId = numericId;
	}

	/**
	 * Create a LocationId based on coordinates
	 * 
	 * @param coordinates
	 *            Provide an coordinate object to LocationId.
	 */
	public LocationId(Coordinates coordinates) {
		_coordinatesId = coordinates;
	}
	
	public LocationId(LocationRef ref) {
		_textualId = ref.getId();
	}
	
	public LocationId(Place place) {
		_numericId = place.getId();
	}

	public static LocationId Cast(Place place) {
		return new LocationId(place.getId());
	}
}
