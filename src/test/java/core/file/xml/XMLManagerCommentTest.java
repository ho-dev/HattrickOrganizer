package core.file.xml;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XMLManagerCommentTest {

    @Test
    void commentsWithRegexCharactersAreIgnored() {
        final var doc = XMLManager.parseString("""
                <?xml version="1.0" encoding="utf-8"?>
                <!-- built from (docs/raw/chpp/x.md), version 1.4 [a-z]* $1 -->
                <HattrickData><!-- second ( --><Value>7</Value></HattrickData>
                """);

        assertThat(doc).isNotNull();
        assertThat(doc.getDocumentElement().getElementsByTagName("Value").item(0).getTextContent()).isEqualTo("7");
    }

    @Test
    void aCommentBeforeAValueIsNotReadAsTheValue() {
        final var doc = XMLManager.parseString("<HattrickData><Value><!-- old value -->7</Value></HattrickData>");

        assertThat(doc).isNotNull();
        assertThat(XMLManager.xmlValue(doc.getDocumentElement(), "Value")).isEqualTo("7");
    }

    @Test
    void anUnclosedCommentIsRejected() {
        assertThat(XMLManager.parseString("<HattrickData><!-- never closed</HattrickData>")).isNull();
    }
}
