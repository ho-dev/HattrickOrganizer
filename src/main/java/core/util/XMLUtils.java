package core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;

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

	private XMLUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
	 * Gets the date between an opening and closing xml tag. E.g. for
	 * <tag>xyz</tag> the string xyz would be returned. If there are multiple
	 * nodes with the specified tag name, the data of the first node found will
	 * be returned.
	 *
	 * @param doc
	 *            a document.
	 * @param tagName
	 *            the name of the tag.
	 * @return the data of the tag or <code>null</code> if no node with the
	 *         specified tag name could be found.
	 */
	public static String getTagData(Document doc, String tagName) {
		Node node = getNode(doc, tagName);
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
	 * @param tagName
	 *            the tag name of the element to find.
	 * @return the first element found with the specified tag name
     * @exception NoSuchElementException
     *                When the element is not found.
	 */
    public static Element getElement(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        if (!isEmpty(nl)) {
            return (Element) nl.item(0);
        }
        throw new NoSuchElementException("No element with tag name " + tagName);
    }

    public static boolean hasElement(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        return !isEmpty(nl);
    }

	/**
	 * Gets the value from the first descendant of a given ancestor, where the
	 * descendant has the specified name.
	 *
	 * @param ancestor
	 *            the ancestor element
	 * @param tagName
	 *            the tag name of the descendant to find.
	 * @return the value of the descendant
     * @exception NoSuchElementException if no descendant
	 *         with that name could be found or the descendant found has no
	 *         value.
	 */
	public static String getStringValue(Element ancestor, String tagName) {
		if (ancestor != null) {
			NodeList nl = ancestor.getElementsByTagName(tagName);
			if (!isEmpty(nl)) {
				Element e = (Element) nl.item(0);
				Node c = e.getFirstChild();
				if (c != null) {
					return c.getNodeValue();
				}
			}
		}
		throw new NoSuchElementException("No element with tag name " + tagName);
	}

	/**
	 * Gets the value from the first descendant of a given ancestor, where the
	 * descendant has the specified name.
	 *
	 * @param ancestor
	 *            the ancestor element
	 * @param tagName
	 *            the tag name of the descendant to find.
	 * @param defaultString
	 *            a default string to return if the value found is
	 *            <code>null</code>.
	 * @return the value of the descendant or <code>null</code> if no descendant
	 *         with that name could be found or the descendant found has no
	 *         value.
	 */
	public static String getStringValue(Element ancestor, String tagName, String defaultString) {
        try {
            return getStringValue(ancestor, tagName);
        }
        catch (NoSuchElementException ignored) {
        }
        return defaultString;
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

    /**
     * Gets an integer value from the first child element of a given parent element
     * @param element Parent element
     * @param tagName Child tag name
     * @return int value
     * @exception NumberFormatException Value is not a number
     * @exception NoSuchElementException Child not found
     */
    public static int getIntValue(Element element, String tagName) {
        var value = getStringValue(element, tagName);
        return Integer.parseInt(value);
    }

    public static int getIntValue(Element element, String tagName, int defaultValue) {
        try {
            var value = getStringValue(element, tagName);
            return Integer.parseInt(value);
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static Integer getIntegerValue(Element element, String tagName) {
        try {
            var value = getStringValue(element, tagName);
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean getBooleanValue(Element element, String tagName) {
        return Boolean.parseBoolean(getStringValue(element, tagName));
    }
}
