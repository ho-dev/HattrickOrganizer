package core.file.xml;

import core.util.XMLUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Document;

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

    @ParameterizedTest
    @ValueSource(strings = {
        "<HattrickData/>",
        "<HattrickData><FileName/></HattrickData>",
        "<HattrickData><FileName></FileName></HattrickData>",
        "<HattrickData><FileName>   </FileName></HattrickData>"
    })
    void test_parseFileName_missingOrEmptyFileName(String xml) {
        assertThatThrownBy(
            () -> XMLHattrickDataInfoParser.parseFileName(xml))
            .isInstanceOf(XMLParseException.class)
            .hasMessageContaining("Missing or empty XML element");
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
}
