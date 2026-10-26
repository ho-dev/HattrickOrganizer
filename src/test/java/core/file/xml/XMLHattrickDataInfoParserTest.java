package core.file.xml;

import core.util.XMLUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Document;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XMLHattrickDataInfoParserTest {

    private static final String VALID_XML = """
        <?xml version="1.0" encoding="utf-8"?>
        <HattrickData>
            <FileName>arenadetails.xml</FileName>
            <Version>1.7</Version>
            <UserID>2192218</UserID>
            <FetchedDate>2024-08-31 10:06:22</FetchedDate>
        </HattrickData>
        """;

    @Test
    void test_parse() throws Exception {
        // given
        Document doc = XMLUtils.createDocument(VALID_XML);

        // when
        var result = XMLHattrickDataInfoParser.parse(doc);

        // then
        assertThat(result).isNotNull();
        assertThat(result.fileName()).isEqualTo("arenadetails.xml");
        assertThat(result.version()).isEqualTo("1.7");
        assertThat(result.userId()).isEqualTo(2192218);
        assertThat(result.fetchedDate().toHT()).isEqualTo("2024-08-31 10:06:22");
    }

    @Test
    void test_parse_nullDocument() {
        // noinspection DataFlowIssue
        assertThatThrownBy(() -> XMLHattrickDataInfoParser.parse(null))
            .isInstanceOf(XMLParseException.class)
            .hasMessage("XML document is null");
    }

    @Test
    void test_parse_invalidUserId() throws Exception {
        // given
        String xml = VALID_XML.replace(
            "<UserID>2192218</UserID>",
            "<UserID>invalid</UserID>");

        Document doc = XMLUtils.createDocument(xml);

        // when / then
        assertThatThrownBy(() -> XMLHattrickDataInfoParser.parse(doc))
            .isInstanceOf(XMLParseException.class)
            .hasMessageContaining("Cannot parse int")
            .hasCauseInstanceOf(NumberFormatException.class);
    }

    @Test
    void test_tryParse_validDocument() throws Exception {
        // given
        Document doc = XMLUtils.createDocument(VALID_XML);

        // when
        var result = XMLHattrickDataInfoParser.tryParse(doc);

        // then
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().fileName()).isEqualTo("arenadetails.xml");
    }

    @Test
    void test_tryParse_nullDocument() {
        assertThat(XMLHattrickDataInfoParser.tryParse(null)).isEmpty();
    }

    @Test
    void test_tryParse_invalidUserId() throws Exception {
        // given
        String xml = VALID_XML.replace(
            "<UserID>2192218</UserID>",
            "<UserID>invalid</UserID>");

        Document doc = XMLUtils.createDocument(xml);

        // when / then
        assertThat(XMLHattrickDataInfoParser.tryParse(doc)).isEmpty();
    }

    @Test
    void test_parseFileName_fromDocument() throws Exception {
        // given
        Document doc = XMLUtils.createDocument(VALID_XML);

        // when
        String result = XMLHattrickDataInfoParser.parseFileName(doc);

        // then
        assertThat(result).isEqualTo("arenadetails.xml");
    }

    @Test
    void test_parseFileName_fromString() {
        assertThat(XMLHattrickDataInfoParser.parseFileName(VALID_XML)).isEqualTo("arenadetails.xml");
    }

    @Test
    void test_parseFileName_nullDocument() {
        // noinspection DataFlowIssue
        assertThatThrownBy(
            () -> XMLHattrickDataInfoParser.parseFileName((Document) null))
            .isInstanceOf(XMLParseException.class)
            .hasMessage("XML document is null");
    }

    private static Stream<Arguments> test_parseFileName_throwsExceptionWithExpectedMessage() {
        return Stream.of(
            Arguments.of("<HattrickDat/>", "Expected 1 element with name 'HattrickData' but found none.", "Invalid root element"),
            Arguments.of("<HattrickData/>", "Expected 1 element with name 'FileName' but found none.", "Missing FileName element"),
            Arguments.of("<HattrickData><FileNam/></HattrickData>", "Expected 1 element with name 'FileName' but found none.", "Misspelled FileName element"),
            Arguments.of("<HattrickData><FileName/></HattrickData>", "Empty element 'FileName'.", "Self-closing FileName element"),
            Arguments.of("<HattrickData><FileName></FileName></HattrickData>", "Empty element 'FileName'.", "Empty FileName element"),
            Arguments.of("<HattrickData><FileName>FileName1</FileName><FileName>FileName2</FileName></HattrickData>", "Expected 1 element with name 'FileName' but found 2 with that name.", "Duplicate FileName elements"),
            Arguments.of("<HattrickData><FileName>   </FileName></HattrickData>", "Empty element 'FileName'.", "Whitespace-only FileName element"),
            Arguments.of("<HattrickData><Nested><FileName>arenadetails.xml</FileName></Nested></HattrickData>", "Expected 1 element with name 'FileName' but found none.", "Nested FileName element")
        );
    }

    @ParameterizedTest(name = "[{index}] {2}: {1}")
    @MethodSource
    void test_parseFileName_throwsExceptionWithExpectedMessage(String xml, String expectedMessage, String testName) {
        assertThatThrownBy(
            () -> XMLHattrickDataInfoParser.parseFileName(xml))
            .isInstanceOf(XMLParseException.class)
            .hasMessageContaining(expectedMessage);
    }

    @Test
    void test_parseFileName_invalidXml() {
        assertThatThrownBy(
            () -> XMLHattrickDataInfoParser.parseFileName("<HattrickData>"))
            .isInstanceOf(XMLParseException.class)
            .hasMessage("Cannot create Document from XML-String.")
            .hasCauseInstanceOf(org.xml.sax.SAXException.class);
    }

    @Test
    void test_tryParseFileName_validXml() {
        assertThat(XMLHattrickDataInfoParser.tryParseFileName(VALID_XML)).contains("arenadetails.xml");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "<HattrickData>",
        "<HattrickData/>",
        "<HattrickData><FileName/></HattrickData>",
        "<HattrickData><FileName>   </FileName></HattrickData>"
    })
    void test_tryParseFileName_invalidXmlOrMissingFileName(String xml) {
        assertThat(XMLHattrickDataInfoParser.tryParseFileName(xml)).isEmpty();
    }

    @Test
    void test_tryParseFileName_invalidOtherMetadata() {
        // given
        String xml = """
            <HattrickData>
                <FileName>matchdetails.xml</FileName>
                <UserID>invalid</UserID>
                <FetchedDate>invalid</FetchedDate>
            </HattrickData>
            """;

        // when / then
        assertThat(XMLHattrickDataInfoParser.tryParseFileName(xml)).contains("matchdetails.xml");
    }

    private static Stream<Arguments> test_parse_missingMetadata() {
        return Stream.of(
            Arguments.of("FileName", "<FileName>arenadetails.xml</FileName>"),
            Arguments.of("Version", "<Version>1.7</Version>"),
            Arguments.of("UserID", "<UserID>2192218</UserID>"),
            Arguments.of("FetchedDate", "<FetchedDate>2024-08-31 10:06:22</FetchedDate>")
        );
    }

    @ParameterizedTest(name = "[{index}] Missing {0} element")
    @MethodSource
    void test_parse_missingMetadata(String elementName, String elementToRemove) throws Exception {
        // given
        String xml = VALID_XML.replace(elementToRemove, "");
        Document doc = XMLUtils.createDocument(xml);

        // when / then
        assertThatThrownBy(() -> XMLHattrickDataInfoParser.parse(doc))
            .isInstanceOf(XMLParseException.class)
            .hasMessage("Expected 1 element with name '%s' but found none.".formatted(elementName));
    }
}
