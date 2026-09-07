package core.gui.image;

import core.util.FileNameUtil;
import core.util.HOLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Image provider that loads images from URLs and stores successfully downloaded
 * images in a local file cache.
 * <p>
 * The cache works as follows:
 * <ul>
 *     <li>If a cached image exists and is younger than the configured cache TTL,
 *         it is returned without accessing the URL.</li>
 *     <li>If the cached image has expired, the URL is accessed again and the
 *         cached image is replaced when the download succeeds.</li>
 *     <li>If the URL returns HTTP 404, the cached image is removed and a
 *         {@code .notfound} marker is stored. While this marker is valid, no
 *         further automatic request is made for that URL.</li>
 *     <li>If another I/O error occurs while refreshing an expired image, the
 *         existing cached image is retained and returned as a stale fallback,
 *         if available.</li>
 *     <li>A forced reload bypasses both valid image cache entries and valid
 *         {@code .notfound} markers.</li>
 * </ul>
 * <p>
 * If the primary image cannot be provided, the same cache mechanism is applied
 * to the configured fallback URL.
 * <p>
 * Cache filenames are derived from the complete URL. Therefore, a changed URL
 * automatically results in a separate cache entry.
 */
public class CachedUrlImageProvider implements ImageProvider {

    private static final String IMAGE_FORMAT_NAME = "PNG";
    private static final String NOT_FOUND_EXTENSION = "notfound";

    private static final Duration CACHE_TTL = Duration.ofDays(30);

    private final Path cacheDirectory;
    private final String primaryUrl;
    private final String fallbackUrl;

    public CachedUrlImageProvider(Path cacheDirectory, String primaryUrl, String fallbackUrl) {
        this.cacheDirectory = cacheDirectory;
        this.primaryUrl = toUrlWithHttps(primaryUrl);
        this.fallbackUrl = toUrlWithHttps(fallbackUrl);
    }

    @Override
    public Optional<BufferedImage> load() {
        return load(false);
    }

    @Override
    public Optional<BufferedImage> reload() {
        return load(true);
    }

    private Optional<BufferedImage> load(boolean forceReload) {
        Optional<BufferedImage> primary = tryLoad(primaryUrl, forceReload);

        if (primary.isPresent()) {
            return primary;
        }

        if (StringUtils.isNotBlank(fallbackUrl)) {
            return tryLoad(fallbackUrl, forceReload);
        }

        return Optional.empty();
    }

    private Optional<BufferedImage> tryLoad(String urlString, boolean forceReload) {
        if (StringUtils.isBlank(urlString)) {
            return Optional.empty();
        }

        Path imagePath = cacheDirectory
            .toAbsolutePath()
            .resolve(toCacheFilename(urlString));

        Path notFoundPath = cacheDirectory
            .toAbsolutePath()
            .resolve(toNotFoundCacheFilename(urlString));

        Optional<BufferedImage> cachedImage = loadFromFile(imagePath);

        if (!forceReload) {
            // Positive cache hit
            if (cachedImage.isPresent() && isCacheEntryValid(imagePath)) {
                HOLogger.instance().log(CachedUrlImageProvider.class,
                    "Image loaded from cache '%s'.".formatted(imagePath));

                return cachedImage;
            }

            // Negative cache hit
            if (isCacheEntryValid(notFoundPath)) {
                return Optional.empty();
            }
        }

        try {
            Optional<BufferedImage> downloadedImage = loadFromUrl(urlString);

            if (downloadedImage.isPresent()) {
                saveToFile(downloadedImage.get(), imagePath);
                deleteIfExists(notFoundPath);
                return downloadedImage;
            }
        } catch (FileNotFoundException e) {
            // Cache the expected HTTP 404.
            deleteIfExists(imagePath);
            createNotFoundMarker(notFoundPath);
            return Optional.empty();
        } catch (IOException e) {
            HOLogger.instance().debug(CachedUrlImageProvider.class,
                "Image could not be loaded from URL '%s': %s: %s"
                    .formatted(
                        urlString,
                        e.getClass().getSimpleName(),
                        e.getMessage()
                    ));
        }

        // Refresh did not provide a usable image. Use stale cached image if available.
        return cachedImage;
    }

    private static Optional<BufferedImage> loadFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();

        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setConnectTimeout(4000);
        con.setReadTimeout(8000);

        try (InputStream in = con.getInputStream()) {
            return Optional.ofNullable(ImageIO.read(in));
        }
    }

    private static boolean isCacheEntryValid(Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }

        try {
            Instant validUntil = Files.getLastModifiedTime(path)
                .toInstant()
                .plus(CACHE_TTL);

            return Instant.now().isBefore(validUntil);
        } catch (IOException e) {
            HOLogger.instance().debug(CachedUrlImageProvider.class,
                "Could not determine age of cache entry '%s': %s: %s"
                    .formatted(
                        path,
                        e.getClass().getSimpleName(),
                        e.getMessage()
                    ));

            return false;
        }
    }

    private static Optional<BufferedImage> loadFromFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(ImageIO.read(path.toFile()));
        } catch (IOException e) {
            HOLogger.instance().error(CachedUrlImageProvider.class,
                "Failed to load image from file '%s': %s".formatted(path, e.getMessage()));
            return Optional.empty();
        }
    }

    private static void saveToFile(BufferedImage image, Path path) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            boolean written = ImageIO.write(image, IMAGE_FORMAT_NAME, path.toFile());

            if (written) {
                HOLogger.instance().log(CachedUrlImageProvider.class,
                    "Saved image (format '%s') to file '%s'.".formatted(IMAGE_FORMAT_NAME, path));
            } else {
                HOLogger.instance().warning(CachedUrlImageProvider.class,
                    "Could not save image (format '%s') to file '%s'.".formatted(IMAGE_FORMAT_NAME, path));
            }
        } catch (IOException e) {
            HOLogger.instance().error(CachedUrlImageProvider.class,
                "Failed to save image (format '%s') to file '%s': %s"
                    .formatted(IMAGE_FORMAT_NAME, path, e.getMessage()));
        }
    }

    private static String toNotFoundCacheFilename(String fixedUrl) {
        return "%s.%s".formatted(
            FileNameUtil.fileNameFromUrl(fixedUrl),
            NOT_FOUND_EXTENSION
        );
    }

    private static String toCacheFilename(String fixedUrl) {
        return "%s.%s".formatted(
            FileNameUtil.fileNameFromUrl(fixedUrl),
            IMAGE_FORMAT_NAME.toLowerCase()
        );
    }

    private static String toUrlWithHttps(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }

        return Strings.CS.startsWith(url, "//") ? "https:" + url : url;
    }

    private static void createNotFoundMarker(Path path) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.write(path, new byte[0]);
        } catch (IOException e) {
            HOLogger.instance().debug(CachedUrlImageProvider.class,
                "Could not create not-found cache entry '%s': %s: %s"
                    .formatted(
                        path,
                        e.getClass().getSimpleName(),
                        e.getMessage()
                    ));
        }
    }

    private static void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            HOLogger.instance().debug(CachedUrlImageProvider.class,
                "Could not delete cache entry '%s': %s: %s"
                    .formatted(
                        path,
                        e.getClass().getSimpleName(),
                        e.getMessage()
                    ));
        }
    }
}
