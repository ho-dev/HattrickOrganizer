package core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import core.file.xml.XMLParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {

    static class NoElementXMLParseException extends XMLParseException {
        public NoElementXMLParseException(String tagName) {super("No element with tag name '%s'.".formatted(tagName));}
    }

    static class MoreThanOneElementXMLParseException extends XMLParseException {
        public MoreThanOneElementXMLParseException(String tagName) {super("More than one element found with name '%s'.".formatted(tagName));}
    }

	private XMLUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

    /**
     * Gets the date between an opening and closing Xml tag. E.g. for
     * <tag>xyz</tag> the string xyz would be returned. If there are multiple
     * nodes with the specified tag name, the data of the first node found will
     * be returned.
     *
     * @param doc     a document.
     * @param tagName the name of the tag.
     * @return the data of the tag or <code>null</code> if no node with the
     * specified tag name could be found.
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
     * @param xmlSource the Xml source as a string.
     * @return the document created from the given Xml string.
     * @throws SAXException if an error occurs while parsing the Xml.
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
     * Gets the first node with the specified tag name from the given document.
     * If there are multiple nodes with the specified tag name, the first node
     * found will be returned.
     *
     * @param doc     the document
     * @param tagName the tag name of the node to find.
     * @return the first node found with the specified tag name or
     * <code>null</code> if no node with that name could be found.
     */
    public static Node getNode(Document doc, String tagName) {
        NodeList nl = doc.getElementsByTagName(tagName);
        if (!isEmpty(nl)) {
            return nl.item(0);
        }
        return null;
    }

    /**
     * Gets the child element with the specified tag name from the given
     * parent element.
     *
     * @param parent  the parent element
     * @param tagName the tag name of the element to find.
     * @return the first element found with the specified tag name
     * @throws XMLParseException When the element is not found or more than one exists
     * @throws NullPointerException When the parent element is null
     */
    public static Element getElement(Element parent, String tagName) {
        Objects.requireNonNull(parent);
        var nodes = getElements(parent.getChildNodes(), tagName);
        return switch (nodes.size()) {
            case 1 -> nodes.get(0);
            case 0 -> throw new NoElementXMLParseException(tagName);
            default -> throw new MoreThanOneElementXMLParseException(tagName);
        };
    }

    /**
     * Gets the child elements with the specified tag name from the given
     * parent element.
     *
     * @param parent  the parent element
     * @param tagName the tag name of the element to find.
     * @return List of elements found with the specified tag name
     * @throws XMLParseException When the element is not found or more than one exists
     * @throws NullPointerException When the parent element is null
     */
    public static List<Element>  getElements(Element parent, String tagName) {
        Objects.requireNonNull(parent);
        return  getElements(parent.getChildNodes(), tagName);
    }

    private static List<Element> getElements(NodeList childNodes, String tagName) {
        var elements = new ArrayList<Element>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            var node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(tagName)) {
                elements.add((Element) node);
            }
        }
        return elements;
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
     * @throws XMLParseException if no descendant
	 *         with that name could be found or the descendant found has no
	 *         value.
     * @throws NullPointerException When the parent element is null
	 */
	public static String getStringValue(Element ancestor, String tagName) {
        Element e = getElement(ancestor, tagName);
        Node c = e.getFirstChild();
        if (c != null) {
            return c.getNodeValue();
        }
        throw new NoElementXMLParseException(tagName);
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
     * @throws XMLParseException if more than one element with tag name are found
	 * @return the value of the descendant or <code>null</code> if no descendant
	 *         with that name could be found or the descendant found has no
	 *         value.
	 */
	public static String getStringValue(Element ancestor, String tagName, String defaultString) {
        try {
            return getStringValue(ancestor, tagName);
        }
        catch (NoElementXMLParseException e) {
            return defaultString;
        }
    }

    /**
     * Gets a string value from an attribute of a given element
     * @param node Element
     * @param tagName Attribute tag name
     * @return String
     * @throws XMLParseException Attribute not found
     */
    public static String getAttributeStringValue(Element node, String tagName) {
        var attribute = node.getAttributes().getNamedItem(tagName);
        if (attribute != null) { return attribute.getNodeValue(); }
        throw new NoElementXMLParseException(tagName);
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
     * Gets an integer value from the child element of a given parent element
     * @param element Parent element
     * @param tagName Child tag name
     * @return int value
     * @throws NumberFormatException Value is not a number
     * @throws XMLParseException Child not found
     */
    public static int getIntValue(Element element, String tagName) {
        var value = getStringValue(element, tagName);
        return Integer.parseInt(value);
    }

    /**
     * Gets an integer from an attribute of a given element
     * @param node Element
     * @param tagName Attribute tag name
     * @return int
     * @throws NumberFormatException Value is not a number
     * @throws XMLParseException Attribute not found
     */
    public static int getAttributeIntValue(Element node, String tagName) {
        var attribute = node.getAttributes().getNamedItem(tagName);
        if (attribute != null) { return Integer.parseInt(attribute.getNodeValue()); }
        throw new NoElementXMLParseException(tagName);
    }

    public static int getIntValue(Element element, String tagName, int defaultValue) {
        try {
            var value = getStringValue(element, tagName);
            return Integer.parseInt(value);
        } catch (NoElementXMLParseException e) {
            return defaultValue;
        }
    }

    public static Optional<Integer> findIntegerValue(Element element, String tagName) {
        try {
            var value = getStringValue(element, tagName);
            return Optional.of(Integer.parseInt(value));
        } catch (NoElementXMLParseException e) {
            return Optional.empty();
        }
    }

    public static boolean getBooleanValue(Element element, String tagName) {
        return Boolean.parseBoolean(getStringValue(element, tagName));
    }
}
