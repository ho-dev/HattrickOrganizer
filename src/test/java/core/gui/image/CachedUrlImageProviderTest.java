package core.gui.image;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import core.util.FileNameUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class CachedUrlImageProviderTest {

    private static final Duration CACHE_TTL = Duration.ofDays(30);

    private enum Response {
        IMAGE,
        NOT_FOUND,
        SERVER_ERROR
    }

    @TempDir
    Path cacheDirectory;

    private HttpServer server;

    private final AtomicInteger primaryRequests = new AtomicInteger();
    private final AtomicInteger fallbackRequests = new AtomicInteger();

    private final AtomicReference<Response> primaryResponse =
        new AtomicReference<>(Response.IMAGE);

    private final AtomicReference<Response> fallbackResponse =
        new AtomicReference<>(Response.IMAGE);

    private byte[] imageBytes;
    private String primaryUrl;
    private String fallbackUrl;

    @BeforeEach
    void setUp() throws IOException {
        imageBytes = createPng();

        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);

        server.createContext("/primary.png", exchange -> {
            primaryRequests.incrementAndGet();
            respond(exchange, primaryResponse.get());
        });

        server.createContext("/fallback.png", exchange -> {
            fallbackRequests.incrementAndGet();
            respond(exchange, fallbackResponse.get());
        });

        server.start();

        String baseUrl = "http://localhost:" + server.getAddress().getPort();

        primaryUrl = baseUrl + "/primary.png";
        fallbackUrl = baseUrl + "/fallback.png";
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void loadDownloadsAndCachesPrimaryImage() {
        var provider = createProvider();

        var image = provider.load();

        assertThat(image).isPresent();
        assertThat(primaryRequests).hasValue(1);
        assertThat(fallbackRequests).hasValue(0);

        assertThat(imageCachePath(primaryUrl))
            .exists()
            .isRegularFile();

        assertThat(notFoundCachePath(primaryUrl))
            .doesNotExist();
    }

    @Test
    void loadUsesCachedImageWithoutDownloadingAgain() {
        var provider = createProvider();

        provider.load();
        var image = provider.load();

        assertThat(image).isPresent();
        assertThat(primaryRequests).hasValue(1);
        assertThat(fallbackRequests).hasValue(0);
    }

    @Test
    void loadCachesNotFoundAndUsesFallbackImage() {
        primaryResponse.set(Response.NOT_FOUND);

        var provider = createProvider();

        var firstImage = provider.load();

        assertThat(firstImage).isPresent();
        assertThat(primaryRequests).hasValue(1);
        assertThat(fallbackRequests).hasValue(1);

        assertThat(notFoundCachePath(primaryUrl))
            .exists()
            .isRegularFile();

        assertThat(imageCachePath(primaryUrl))
            .doesNotExist();

        assertThat(imageCachePath(fallbackUrl))
            .exists()
            .isRegularFile();

        var secondImage = provider.load();

        assertThat(secondImage).isPresent();

        // Both results are now served from the cache.
        assertThat(primaryRequests).hasValue(1);
        assertThat(fallbackRequests).hasValue(1);
    }

    @Test
    void loadRefreshesExpiredCachedImage() throws IOException {
        var provider = createProvider();

        provider.load();

        Path cachedImage = imageCachePath(primaryUrl);
        expire(cachedImage);

        var image = provider.load();

        assertThat(image).isPresent();
        assertThat(primaryRequests).hasValue(2);
    }

    @Test
    void loadRetriesPrimaryUrlAfterNotFoundCacheExpires() throws IOException {
        primaryResponse.set(Response.NOT_FOUND);

        var provider = createProvider();

        provider.load();

        Path notFoundMarker = notFoundCachePath(primaryUrl);
        expire(notFoundMarker);

        provider.load();

        assertThat(primaryRequests).hasValue(2);

        // Fallback image itself is still valid and therefore not downloaded again.
        assertThat(fallbackRequests).hasValue(1);
    }

    @Test
    void reloadIgnoresCachedImage() {
        var provider = createProvider();

        provider.load();

        assertThat(primaryRequests).hasValue(1);

        provider.reload();

        assertThat(primaryRequests).hasValue(2);
        assertThat(fallbackRequests).hasValue(0);
    }

    @Test
    void reloadFindsPrimaryImageAfterPreviousNotFound() {
        primaryResponse.set(Response.NOT_FOUND);

        var provider = createProvider();

        provider.load();

        assertThat(notFoundCachePath(primaryUrl)).exists();
        assertThat(primaryRequests).hasValue(1);
        assertThat(fallbackRequests).hasValue(1);

        // A custom stadium image has been created in the meantime.
        primaryResponse.set(Response.IMAGE);

        var image = provider.reload();

        assertThat(image).isPresent();
        assertThat(primaryRequests).hasValue(2);

        // Primary succeeds, so fallback does not need to be reloaded.
        assertThat(fallbackRequests).hasValue(1);

        assertThat(imageCachePath(primaryUrl)).exists();
        assertThat(notFoundCachePath(primaryUrl)).doesNotExist();
    }

    @Test
    void loadUsesStaleCachedImageWhenRefreshFails() throws IOException {
        var provider = createProvider();

        provider.load();

        Path cachedImage = imageCachePath(primaryUrl);
        expire(cachedImage);

        primaryResponse.set(Response.SERVER_ERROR);

        var image = provider.load();

        assertThat(image).isPresent();
        assertThat(primaryRequests).hasValue(2);
        assertThat(fallbackRequests).hasValue(0);

        // The existing cached image must not be deleted on an I/O error.
        assertThat(cachedImage)
            .exists()
            .isRegularFile();

        assertThat(notFoundCachePath(primaryUrl))
            .doesNotExist();
    }

    @Test
    void loadDeletesStaleCachedImageWhenPrimaryReturnsNotFound() throws IOException {
        var provider = createProvider();

        // Initially the primary image exists and is cached.
        provider.load();

        Path cachedImage = imageCachePath(primaryUrl);

        assertThat(cachedImage).exists();

        // Force the cached image to expire and remove the image from the server.
        expire(cachedImage);
        primaryResponse.set(Response.NOT_FOUND);

        var image = provider.load();

        // Fallback image is returned.
        assertThat(image).isPresent();

        // The stale primary image is removed because the server explicitly returned 404.
        assertThat(cachedImage).doesNotExist();

        assertThat(notFoundCachePath(primaryUrl))
            .exists()
            .isRegularFile();

        assertThat(primaryRequests).hasValue(2);
        assertThat(fallbackRequests).hasValue(1);
    }

    @Test
    void reloadDeletesCachedImageWhenPrimaryReturnsNotFound() {
        var provider = createProvider();

        provider.load();

        Path cachedImage = imageCachePath(primaryUrl);
        assertThat(cachedImage).exists();

        primaryResponse.set(Response.NOT_FOUND);

        var image = provider.reload();

        assertThat(image).isPresent();

        assertThat(cachedImage).doesNotExist();
        assertThat(notFoundCachePath(primaryUrl)).exists();

        assertThat(primaryRequests).hasValue(2);
        assertThat(fallbackRequests).hasValue(1);
    }

    private CachedUrlImageProvider createProvider() {
        return new CachedUrlImageProvider(
            cacheDirectory,
            primaryUrl,
            fallbackUrl
        );
    }

    private Path imageCachePath(String url) {
        return cacheDirectory.resolve(
            FileNameUtil.fileNameFromUrl(url) + ".png"
        );
    }

    private Path notFoundCachePath(String url) {
        return cacheDirectory.resolve(
            FileNameUtil.fileNameFromUrl(url) + ".notfound"
        );
    }

    private void respond(HttpExchange exchange, Response response) throws IOException {
        try (exchange) {
            switch (response) {
                case IMAGE -> {
                    exchange.getResponseHeaders()
                        .set("Content-Type", "image/png");

                    exchange.sendResponseHeaders(200, imageBytes.length);
                    exchange.getResponseBody().write(imageBytes);
                }

                case NOT_FOUND -> exchange.sendResponseHeaders(404, -1);

                case SERVER_ERROR -> exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    private static byte[] createPng() throws IOException {
        BufferedImage image = new BufferedImage(
            2,
            2,
            BufferedImage.TYPE_INT_RGB
        );

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        }
    }

    private static void expire(Path path) throws IOException {
        Files.setLastModifiedTime(
            path,
            FileTime.from(Instant.now().minus(CACHE_TTL).minusSeconds(1))
        );
    }
}
