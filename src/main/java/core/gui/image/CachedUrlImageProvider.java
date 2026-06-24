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
import java.util.Optional;

public class CachedUrlImageProvider implements ImageProvider {

    private static final String IMAGE_FORMAT_NAME = "PNG";

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
        Optional<BufferedImage> primary = tryLoad(primaryUrl);

        if (primary.isPresent()) {
            return primary;
        }

        if (StringUtils.isNotBlank(fallbackUrl)) {
            return tryLoad(fallbackUrl);
        }

        return Optional.empty();
    }

    private Optional<BufferedImage> tryLoad(String urlString) {
        if (StringUtils.isBlank(urlString)) {
            return Optional.empty();
        }

        Path fullPath = cacheDirectory
            .toAbsolutePath()
            .resolve(toCacheFilename(urlString));

        return loadFromFile(fullPath)
            .or(() -> loadFromUrlAndSave(urlString, fullPath));
    }

    private static Optional<BufferedImage> loadFromUrlAndSave(String urlString, Path fullPath) {
        Optional<BufferedImage> fromUrl = loadFromUrl(urlString);
        fromUrl.ifPresent(bufferedImage -> saveToFile(bufferedImage, fullPath));
        return fromUrl;
    }

    private static Optional<BufferedImage> loadFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();

            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(4000);
            con.setReadTimeout(8000);

            try (InputStream in = con.getInputStream()) {
                return Optional.ofNullable(ImageIO.read(in));
            }
        } catch (FileNotFoundException e) {
            // Expected if the club has no custom stadium image.
            return Optional.empty();
        } catch (IOException e) {
            HOLogger.instance().debug(CachedUrlImageProvider.class,
                "Image could not be loaded from URL '%s': %s: %s".formatted(urlString, e.getClass().getSimpleName(), e.getMessage()));
            return Optional.empty();
        }
    }

    private static Optional<BufferedImage> loadFromFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }

        try {
            Optional<BufferedImage> image = Optional.ofNullable(ImageIO.read(path.toFile()));

            if (image.isPresent()) {
                HOLogger.instance().log(CachedUrlImageProvider.class,
                    "Image loaded from file '%s'.".formatted(path));
            }

            return image;
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
}
