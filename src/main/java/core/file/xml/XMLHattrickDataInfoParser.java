package core.file.xml;

import core.util.HODateTime;
import core.util.XMLUtils;
import hattrickdata.HattrickDataInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.util.Optional;

public final class XMLHattrickDataInfoParser {

    private static final String ELEMENT_NAME_FILE_NAME = "FileName";
    private static final String ELEMENT_NAME_VERSION = "Version";
    private static final String ELEMENT_NAME_USER_ID = "UserID";
    private static final String ELEMENT_NAME_FETCHED_DATE = "FetchedDate";

    private XMLHattrickDataInfoParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     *
     * @param doc
     * @return
     * @throws XMLParseException in case of parser errors
     */
    public static HattrickDataInfo parse(Document doc) {
        if (doc == null) {
            throw new XMLParseException("XML document is null");
        }

        var hattrickDataInfoBuilder = HattrickDataInfo.builder();

        Element root = doc.getDocumentElement();

        // FileName
        hattrickDataInfoBuilder.fileName(parseFileName(doc));

        // Version
        Element element = (Element) root.getElementsByTagName(ELEMENT_NAME_VERSION).item(0);
        final var version = XMLManager.getFirstChildNodeValue(element);
        hattrickDataInfoBuilder.version(version);

        // UserId
        element = (Element) root.getElementsByTagName(ELEMENT_NAME_USER_ID).item(0);
        hattrickDataInfoBuilder.userId(parseInt(element));

        // FetchedDate
        element = (Element) root.getElementsByTagName(ELEMENT_NAME_FETCHED_DATE).item(0);
        hattrickDataInfoBuilder.fetchedDate(HODateTime.fromHT(XMLManager.getFirstChildNodeValue(element)));

        return hattrickDataInfoBuilder.build();
    }

    public static Optional<String> tryParseFileName(String xmlString) {
        try {
            return Optional.of(parseFileName(xmlString));
        } catch (XMLParseException e) {
            return Optional.empty();
        }
    }

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

    public static String parseFileName(Document doc) {
        if (doc == null) {
            throw new XMLParseException("XML document is null");
        }

        Element root = doc.getDocumentElement();
        Element element = (Element) root.getElementsByTagName(ELEMENT_NAME_FILE_NAME).item(0);

        String fileName = XMLManager.getFirstChildNodeValue(element);
        if (fileName.isBlank()) {
            throw new XMLParseException("Missing or empty XML element: " + ELEMENT_NAME_FILE_NAME);
        }

        return fileName;
    }

    public static Optional<HattrickDataInfo> tryParse(Document doc) {
        try {
            return Optional.of(parse(doc));
        } catch (XMLParseException e) {
            return Optional.empty();
        }
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
