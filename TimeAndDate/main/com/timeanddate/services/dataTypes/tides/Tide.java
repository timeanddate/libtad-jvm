package com.timeanddate.services.dataTypes.tides;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.MalformedXMLException;
import com.timeanddate.services.common.XmlUtils;

import com.timeanddate.services.dataTypes.time.TADTime;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class Tide {
	private TADTime _time;
	private float _amplitude;
	private TidalPhase _phase;

	/**
	 * Date/time of the specific tidal data point.
	 */
	public TADTime getTime() {
		return _time;
	}

	/**
	 * The elevation of tidal water above or below mean sea level.
	 */
	public float getAmplitude() {
		return _amplitude;
	}

	/**
	 * The current tidal phase.
	 */
	public TidalPhase getPhase() {
		return _phase;
	}

	public static Tide fromNode(Node node) throws MalformedXMLException {
		Tide tide = new Tide();
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children)) {
			switch (child.getNodeName()) {
			case "time":
				tide._time = TADTime.fromNode(child);
				break;
			case "amplitude":
				tide._amplitude = Float.parseFloat(child.getTextContent());
				break;
			case "phase":
				String name = child.getTextContent();
				try {
					tide._phase = TidalPhase.valueOf(name.substring(0, 1).toUpperCase() + name.substring(1));
				} catch (IllegalArgumentException e) {
					throw new MalformedXMLException(
							"The XML returned from Time and Date contained an unsupported name: "
							+ name);
				}
				break;
			}
		}

		return tide;
	}
}
