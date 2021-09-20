package com.timeanddate.services.dataTypes.tides;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.common.MalformedXMLException;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class Station {
	private StationInfo _source;
	private String _matchparam;
	private List<Tide> _result;

	/**
	 * The source station for the predicted tidal data.
	 */
	public StationInfo getSource() {
		return _source;
	}

	/**
	 * The part of the queried placeid that this location matches.
	 */
	public String getMatchParam() {
		return _matchparam;
	}

	/**
	 * Requested tidal information.
	 */
	public List<Tide> getResult() {
		return _result;
	}

	private Station() {
		_result = new ArrayList<Tide>();
	}

	public static Station fromNode(Node node) {
		Station station = new Station();
		NamedNodeMap attr = node.getAttributes();
		Node matchParam = attr.getNamedItem("matchparam");
		NodeList children = node.getChildNodes();

		for (Node child : XmlUtils.asList(children)) {
			switch (child.getNodeName()) {
			case "source":
				station._source = StationInfo.fromNode(child);
				break;
			case "result":
				for (Node tide : XmlUtils.asList(child.getChildNodes())) {
					try {
						station._result.add(Tide.fromNode(tide));
					} catch (MalformedXMLException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}

		station._matchparam = matchParam.getTextContent();

		return station;
	}
}
