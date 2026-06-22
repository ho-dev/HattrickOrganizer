package core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileNameUtilTest {

    @Test
    void fileNameFromUrlReturnsExpectedSha256Hash() {
        String url = "https://res.hattrick.org/arenas/22/216/2155/2154092/custom-220-100.jpg";

        String result = FileNameUtil.fileNameFromUrl(url);

        assertThat(result).isEqualTo("a7a1b9b8f2f53a7e2e13f60b160070791fe9d90c05167643381e255207a1179d");
    }

    @Test
    void fileNameFromUrlReturnsSameHashForSameUrl() {
        String url = "https://example.com/image.jpg";

        String first = FileNameUtil.fileNameFromUrl(url);
        String second = FileNameUtil.fileNameFromUrl(url);

        assertThat(first).isEqualTo(second);
    }

    @Test
    void fileNameFromUrlReturnsDifferentHashesForDifferentUrls() {
        String first = FileNameUtil.fileNameFromUrl("https://example.com/image1.jpg");
        String second = FileNameUtil.fileNameFromUrl("https://example.com/image2.jpg");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void fileNameFromUrlReturns64CharacterHexString() {
        String result = FileNameUtil.fileNameFromUrl("https://example.com/image.jpg");

        assertThat(result)
            .hasSize(64)
            .matches("[0-9a-f]{64}");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void fileNameFromUrlThrowsNullPointerExceptionForNullUrl() {
        assertThatThrownBy(() -> FileNameUtil.fileNameFromUrl(null))
            .isInstanceOf(NullPointerException.class);
    }
}
