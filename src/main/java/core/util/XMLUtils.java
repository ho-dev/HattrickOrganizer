package core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {

	/**
	 * Utility class - private constructor enforces noninstantiability.
	 */
	private XMLUtils() {
	}

	/**
	 * Gets the date between an opening and closing xml tag. E.g. for
	 * <tag>xyz</tag> the string xyz would be returned. If there are multiple
	 * nodes with the specified tag name, the data of the first node found will
	 * be returned.
	 * 
	 * @param doc
	 *            a document.
	 * @param tagname
	 *            the name of the tag.
	 * @return the data of the tag or <code>null</code> if no node with the
	 *         specified tag name could be found.
	 */
	public static String getTagData(Document doc, String tagname) {
		Node node = getNode(doc, tagname);
		if (node != null) {
			Node child = node.getFirstChild();
			if (child != null) {
				return child.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Creates a document from an XML string.
	 * 
	 * @param xmlSource
	 *            the xml source as a string.
	 * @return the document created from the given xml string.
	 * @throws SAXException
	 *             if an error occurs while parsing the xml.
	 */
	public static Document createDocument(String xmlSource) throws SAXException {
		Reader xml = new StringReader(xmlSource);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(new InputSource(xml));
		} catch (ParserConfigurationException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Finds a node in a document and returns the value from an attribute of
	 * this node. If there are multiple nodes with the specified tag name, the
	 * attribute value of the first node found will be returned.
	 * 
	 * @param document
	 *            the document.
	 * @param nodeName
	 *            the name of the node .
	 * @param attributeName
	 *            the name of the node's attribute.
	 * @return the value of the node's attribute or <code>null</code> if either
	 *         no node with the specified tag name or no attribute with the
	 *         specified attribute name could be found.
	 */
	public static String getAttributeValueFromNode(Document document,
			String nodeName, String attributeName) {
		Node node = getNode(document, nodeName);
		if (node != null) {
			Node attribute = getAttributeValue(node, attributeName);
			if (attribute != null) {
				return attribute.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Gets the value from a node's attribute.
	 * 
	 * @param node
	 *            the node.
	 * @param attributeName
	 *            the name of the attribute.
	 * @return the value of the attribute with the specified name or
	 *         <code>null</code> if there is no attribute with that name.
	 */
	public static Node getAttributeValue(Node node, String attributeName) {
		return (node.hasAttributes()) ? node.getAttributes().getNamedItem(
				attributeName) : null;

	}

	/**
	 * Gets the first node with the specified tag name from the given document.
	 * If there are multiple nodes with the specified tag name, the first node
	 * found will be returned.
	 * 
	 * @param doc
	 *            the document
	 * @param tagname
	 *            the tag name of the node to find.
	 * @return the first node found with the specified tag name or
	 *         <code>null</code> if no node with that name could be found.
	 */
	public static Node getNode(Document doc, String tagname) {
		NodeList nl = doc.getElementsByTagName(tagname);
		if (!isEmpty(nl)) {
			return nl.item(0);
		}
		return null;
	}

	/**
	 * Gets the first child element with the specified tag name from the given
	 * parent element. If there are multiple elements with the specified tag
	 * name, the first element found will be returned.
	 * 
	 * @param parent
	 *            the parent element
	 * @param tagname
	 *            the tag name of the element to find.
	 * @return the first element found with the specified tag name or
	 *         <code>null</code> if no element with that name could be found.
	 */
	public static Element getElement(Element parent, String tagname) {
		NodeList nl = parent.getElementsByTagName(tagname);
		if (!isEmpty(nl)) {
			return (Element) nl.item(0);
		}
		return null;
	}

	/**
	 * Gets the value from the first descendant of a given ancestor, where the
	 * descendant has the specified name.
	 * 
	 * @param ancestor
	 *            the ancestor element
	 * @param tagname
	 *            the tag name of the descendant to find.
	 * @return the value of the descendant or <code>null</code> if no descendant
	 *         with that name could be found or the descendant found has no
	 *         value.
	 */
	public static String getValueFromDescendant(Element ancestor, String tagname) {
		if (ancestor != null) {
			NodeList nl = ancestor.getElementsByTagName(tagname);
			if (!isEmpty(nl)) {
				Element e = (Element) nl.item(0);
				Node c = e.getFirstChild();
				if (c != null) {
					return c.getNodeValue();
				}
			}
		}
		return null;
	}

	/**
	 * Gets the value from the first descendant of a given ancestor, where the
	 * descendant has the specified name.
	 * 
	 * @param ancestor
	 *            the ancestor element
	 * @param tagname
	 *            the tag name of the descendant to find.
	 * @param defaultString
	 *            a default string to return if the value found is
	 *            <code>null</code>.
	 * @return the value of the descendant or <code>null</code> if no descendant
	 *         with that name could be found or the descendant found has no
	 *         value.
	 */
	public static String getValueFromDescendant(Element ancestor,
			String tagname, String defaultString) {
		String val = getValueFromDescendant(ancestor, tagname);
		return (val != null) ? val : defaultString;
	}

	/**
	 * Null-safe check if a nodelist is empty.
	 * 
	 * @param nodeList
	 *            the nodelist to check.
	 * @return <code>true</code> if the given nodelist is <code>null</code> or
	 *         empty.
	 */
	public static boolean isEmpty(NodeList nodeList) {
		return nodeList == null || nodeList.getLength() == 0;
	}
}
