package core.net;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenTest {

    private static final String TOKEN = "Hattrick";
    private static final String ENCRYPTED_TOKEN = "BYnnla[c";

    private static final String TOKEN_SECRET = "Organizer";
    private static final String ENCRYPTED_TOKEN_SECRET = "Gl_Yhat]l";

    private static final AccessToken ACCESS_TOKEN = new AccessToken(ENCRYPTED_TOKEN, ENCRYPTED_TOKEN_SECRET);

    @Test
    void constructor() {
        var accessToken = new AccessToken(ENCRYPTED_TOKEN, ENCRYPTED_TOKEN_SECRET);
        assertThat(accessToken.encryptedToken()).isEqualTo(ENCRYPTED_TOKEN);
        assertThat(accessToken.encryptedTokenSecret()).isEqualTo(ENCRYPTED_TOKEN_SECRET);
    }

    @Test
    void ofEncryptedData() {
        var accessToken = AccessToken.ofEncryptedData(ENCRYPTED_TOKEN, ENCRYPTED_TOKEN_SECRET);
        assertThat(accessToken.getToken()).isEqualTo(TOKEN);
        assertThat(accessToken.getTokenSecret()).isEqualTo(TOKEN_SECRET);
        assertThat(accessToken.encryptedToken()).isEqualTo(ENCRYPTED_TOKEN);
        assertThat(accessToken.encryptedTokenSecret()).isEqualTo(ENCRYPTED_TOKEN_SECRET);
    }

    @Test
    void ofData() {
        var accessToken = AccessToken.ofData(TOKEN, TOKEN_SECRET);
        assertThat(accessToken.getToken()).isEqualTo(TOKEN);
        assertThat(accessToken.getTokenSecret()).isEqualTo(TOKEN_SECRET);
        assertThat(accessToken.encryptedToken()).isEqualTo(ENCRYPTED_TOKEN);
        assertThat(accessToken.encryptedTokenSecret()).isEqualTo(ENCRYPTED_TOKEN_SECRET);
    }

    @Test
    void getToken() {
        var token = ACCESS_TOKEN.getToken();
        assertThat(token).isEqualTo(TOKEN);
    }

    @Test
    void getTokenSecret() {
        var token = ACCESS_TOKEN.getTokenSecret();
        assertThat(token).isEqualTo(TOKEN_SECRET);
    }
}
