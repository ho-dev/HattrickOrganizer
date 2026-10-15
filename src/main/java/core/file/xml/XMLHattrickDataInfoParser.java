package core.file.xml;

import core.util.HODateTime;
import core.util.XMLUtils;
import hattrickdata.HattrickDataInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class XMLHattrickDataInfoParser {

    private static final String ELEMENT_NAME_HATTRICK_DATA = "HattrickData";
    private static final String ELEMENT_NAME_FILE_NAME = "FileName";
    private static final String ELEMENT_NAME_VERSION = "Version";
    private static final String ELEMENT_NAME_USER_ID = "UserID";
    private static final String ELEMENT_NAME_FETCHED_DATE = "FetchedDate";

    private XMLHattrickDataInfoParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Parses the Document {@code doc} to get the {@link HattrickDataInfo} from it.
     *
     * @param doc DOM-document
     * @return {@link HattrickDataInfo}
     * @throws XMLParseException in case of a document {@code null} or parser errors
     */
    public static HattrickDataInfo parse(Document doc) {
        if (doc == null) {
            throw new XMLParseException("XML document is null");
        }

        final var hattrickDataElement = getChildElementWithTagName(doc, ELEMENT_NAME_HATTRICK_DATA);

        return HattrickDataInfo.builder()
            .fileName(parseFileName(doc))
            .version(parseStringFromChild(hattrickDataElement, ELEMENT_NAME_VERSION))
            .userId(parseIntFromChild(hattrickDataElement, ELEMENT_NAME_USER_ID))
            .fetchedDate(parseHODateTimeFromChild(hattrickDataElement, ELEMENT_NAME_FETCHED_DATE))
            .build();
    }

    /**
     * Tries to extract the file name from a Hattrick CHPP XML document.
     * Returns an empty Optional if the XML is invalid or the file name
     * cannot be extracted.
     *
     * @param xmlString the XML content to parse
     * @return an Optional containing the file name, or an empty Optional if parsing fails
     */
    public static Optional<String> tryParseFileName(String xmlString) {
        try {
            return Optional.of(parseFileName(xmlString));
        } catch (XMLParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Extracts the file name from a Hattrick CHPP XML string.
     *
     * @param xmlString the XML content to parse
     * @return the file name extracted from the XML document
     * @throws XMLParseException if the XML is invalid or the file name cannot be extracted
     */
    public static String parseFileName(String xmlString) {
        return parseFileName(createDocument(xmlString));
    }

    private static Document createDocument(String xmlString) {
        try {
            return XMLUtils.createDocument(xmlString);
        } catch (SAXException | RuntimeException e) {
            throw new XMLParseException("Cannot create Document from XML-String.", e);
        }
    }

    /**
     * Extracts the file name from a Hattrick CHPP XML document.
     * The file name is expected to be a direct child element of the HattrickData root element.
     *
     * @param doc the XML document to parse
     * @return the file name extracted from the XML document
     * @throws XMLParseException if the document is null, the file name is missing or empty,
     *                           or the XML structure is invalid
     */
    public static String parseFileName(Document doc) {
        if (doc == null) {
            throw new XMLParseException("XML document is null");
        }

        final var hattrickDataElement = getChildElementWithTagName(doc, ELEMENT_NAME_HATTRICK_DATA);
        final String fileName = parseStringFromChild(hattrickDataElement, ELEMENT_NAME_FILE_NAME);
        if (fileName.isBlank()) {
            throw new XMLParseException("Empty element '%s'.".formatted(ELEMENT_NAME_FILE_NAME));
        }

        return fileName;
    }

    /**
     * Tries to parse the Hattrick CHPP XML document into a {@link HattrickDataInfo}.
     * Returns an empty Optional if parsing fails.
     *
     * @param doc the XML document to parse
     * @return an Optional containing the parsed HattrickDataInfo, or an empty Optional if parsing fails
     */
    public static Optional<HattrickDataInfo> tryParse(Document doc) {
        try {
            return Optional.of(parse(doc));
        } catch (XMLParseException e) {
            return Optional.empty();
        }
    }

    private static Element getChildElementWithTagName(Node node, String tagName) {
        final var childElements = getChildElementsWithTagName(node, tagName);
        if (childElements.isEmpty()) {
            throw new XMLParseException("Expected 1 element with name '%s' but found none.".formatted(tagName));
        } else if (childElements.size() > 1) {
            throw new XMLParseException("Expected 1 element with name '%s' but found %d with that name.".formatted(tagName, childElements.size()));
        }
        return childElements.get(0);
    }

    private static List<Element> getChildElementsWithTagName(Node node, String tagName) {
        return getChildNodeStream(node)
            .filter(n -> n.getNodeName().equals(tagName))
            .filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
            .map(Element.class::cast)
            .toList();
    }

    private static Stream<Node> getChildNodeStream(Node node) {
        return toStream(node.getChildNodes());
    }

    private static Stream<Node> toStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item);
    }

    @SuppressWarnings("SameParameterValue")
    private static HODateTime parseHODateTimeFromChild(Element element, String childTagName) {
        return parseHODateTime(getChildElementWithTagName(element, childTagName));
    }

    @SuppressWarnings("SameParameterValue")
    private static String parseStringFromChild(Element element, String childTagName) {
        return parseString(getChildElementWithTagName(element, childTagName));
    }

    @SuppressWarnings("SameParameterValue")
    private static int parseIntFromChild(Element element, String childTagName) {
        return parseInt(getChildElementWithTagName(element, childTagName));
    }

    private static HODateTime parseHODateTime(Element element) {
        return HODateTime.fromHT(parseString(element));
    }

    private static String parseString(Element element) {
        return XMLManager.getFirstChildNodeValue(element);
    }

    private static int parseInt(Element element) {
        final var value = XMLManager.getFirstChildNodeValue(element);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new XMLParseException("Element '%s': Cannot parse int from value '%s'.".formatted(element.getTagName(), value), e);
        }
    }
}
