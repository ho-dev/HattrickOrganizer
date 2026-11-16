package core.file.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.assertj.core.api.Assertions.assertThat;

/** XMLManager.parseString drops comments; one holding regex characters once hung it forever. */
class XMLManagerCommentTest {

    @Test
    @Timeout(5)
    void commentsWithRegexCharactersAreRemoved() {
        final var doc = XMLManager.parseString("""
                <?xml version="1.0" encoding="utf-8"?>
                <!-- built from (docs/raw/chpp/x.md), version 1.4 [a-z]* $1 -->
                <HattrickData><!-- second ( --><Value>7</Value></HattrickData>
                """);

        assertThat(doc).isNotNull();
        assertThat(doc.getDocumentElement().getElementsByTagName("Value").item(0).getTextContent()).isEqualTo("7");
    }

    @Test
    @Timeout(5)
    void anUnclosedCommentEndsTheLoop() {
        assertThat(XMLManager.parseString("<HattrickData><!-- never closed</HattrickData>")).isNull();
    }
}
