package com.timeanddate.services.common;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.AbstractList;
import java.util.RandomAccess;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {
	public static void checkForErrors(String xml)
			throws ParserConfigurationException, SAXException, IOException,
			DOMException, ServerSideException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document document = builder.parse(is);
		Element root = document.getDocumentElement();
		NodeList node = root.getElementsByTagName("error");

		if (node.getLength() > 0) {
			Node error = node.item(0);
			throw new ServerSideException(error.getTextContent());
		}
	}

	public static List<Node> asList(NodeList n) {
		return n.getLength() == 0 ? Collections.<Node> emptyList()
				: new NodeListWrapper(n);
	}

	static final class NodeListWrapper extends AbstractList<Node> implements
			RandomAccess {
		private final NodeList list;

		NodeListWrapper(NodeList l) {
			list = l;
		}

		public Node get(int index) {
			return list.item(index);
		}

		public int size() {
			return list.getLength();
		}
	}
}
