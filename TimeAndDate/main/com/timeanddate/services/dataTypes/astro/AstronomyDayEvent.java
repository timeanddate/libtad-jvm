package com.timeanddate.services.dataTypes.astro;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.timeanddate.services.common.MalformedXMLException;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.dataTypes.time.TADDateTime;

/**
 * 
 * @author Cato Auestad {@literal <cato@timeanddate.com>}
 *
 */
public class AstronomyDayEvent {
	private AstronomyEventCode _type;
	private TADDateTime _ISOTime;
	private TADDateTime _UTCTime;
	private double _altitude;
	private double _azimuth;
	private double _distance;
	private double _illuminated;
	
	/**
	 * Indicates the type of the event.
	 */
	public AstronomyEventCode getType() {
		return _type;
	}

	/**
	 * Local time at which the event is happening (including UTC offset). The
	 * time does not include the seconds.
	 */
	public TADDateTime getISOTime() {
		return _ISOTime;
	}

	/**
	 * UTC time at which the event is happening. The time does not include the
	 * seconds.
	 */
	public TADDateTime getUTCTime() {
		return _UTCTime;
	}

	/**
	 * Altitude in degrees of the center of the queried astronomical object
	 * above an ideal horizon.
	 * <p>
	 * Only for meridian type events.
	 */
	public double getAltitude() {
		return _altitude;
	}

	/**
	 * Horizontal direction of the astronomical object at set/rise time
	 * (referring to true north). North is 0 degrees, east is 90 degrees, south
	 * is 180 degrees and west is 270 degrees.
	 * <p>
	 * Only for rise and set type events.
	 */
	public double getAzimuth() {
		return _azimuth;
	}

	/**
	 * Distance in kilometers of the earth's center to the center of the queried
	 * astronomical object in kilometers.
	 * <p>
	 * Only for meridian type events.
	 */
	public double getDistance() {
		return _distance;
	}

	/**
	 * The fraction of the Moon's surface illuminated by the Sun's rays as seen
	 * from the selected location.
	 * <p>
	 * Only for the moon for meridian type events.
	 */
	public double getIlluminated() {
		return _illuminated;
	}

	public static AstronomyDayEvent fromNode(Node node) {
		AstronomyDayEvent event = new AstronomyDayEvent();
		NamedNodeMap attr = node.getAttributes();
		Node type = attr.getNamedItem("type");
		Node utctime = attr.getNamedItem("utctime");
		Node isotime = attr.getNamedItem("isotime");
		Node altitude = attr.getNamedItem("altitude");
		Node distance = attr.getNamedItem("distance");
		Node azimuth = attr.getNamedItem("azimuth");
		Node illuminated = attr.getNamedItem("illuminated");

		if (type != null)
			try {
				event._type = StringUtils.resolveAstronomyEventCode(type
						.getTextContent());
			} catch (DOMException | MalformedXMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		if (utctime != null)
			event._UTCTime = new TADDateTime(utctime.getTextContent());

		if (isotime != null)
			event._ISOTime = new TADDateTime(isotime.getTextContent());

		if (altitude != null)
			event._altitude = Double.parseDouble(altitude.getTextContent());

		if (distance != null)
			event._distance = Double.parseDouble(distance.getTextContent());

		if (azimuth != null)
			event._azimuth = Double.parseDouble(azimuth.getTextContent());

		if (illuminated != null)
			event._illuminated = Double
					.parseDouble(illuminated.getTextContent());

		return event;
	}
}
