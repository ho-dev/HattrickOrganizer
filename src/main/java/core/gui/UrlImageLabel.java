package core.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class UrlImageLabel extends JLabel {

    public UrlImageLabel() {
        super(EMPTY, SwingConstants.CENTER);
        setOpaque(true);
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY));
        setText("Loading image …");

        Dimension dimension = new Dimension(220, 220);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
    }

    /**
     * Loads primary, then fallback, otherwise placeholder
     */
    public void loadWithFallback(String primaryUrl, String fallbackUrl) {
        final var fixedPrimaryUrl = toUrlWithHttps(primaryUrl);
        final var fixedFallbackUrl = toUrlWithHttps(fallbackUrl);

        showLoading();

        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() {
                BufferedImage img = tryLoad(fixedPrimaryUrl);
                if (img != null) {
                    return img;
                }

                if (StringUtils.isNotBlank(fixedFallbackUrl)) {
                    img = tryLoad(fixedFallbackUrl);
                }
                return img; // may be null
            }

            @Override
            protected void done() {
                try {
                    BufferedImage img = get();
                    if (img != null) {
                        showImage(img);
                    } else {
                        showPlaceholder("Image not available");
                    }
                } catch (Exception ex) {
                    // If the worker fails unexpectedly
                    showPlaceholder("Error loading image");
                }
            }
        }.execute();
    }

    private BufferedImage tryLoad(String urlString) {
        if (StringUtils.isBlank(urlString)) {
            return null;
        }

        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(4000);
            con.setReadTimeout(8000);

            try (InputStream in = con.getInputStream()) {
                // ImageIO.read may return null if the input is not a supported image format
                return ImageIO.read(in);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void showLoading() {
        setIcon(null);
        setText("Loading image …");
        setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY));
    }

    private void showImage(BufferedImage img) {
        setBorder(null);
        setText(null);
        setIcon(new ImageIcon(img)); // original size
        revalidate();
        repaint();
    }

    private void showPlaceholder(String text) {
        setIcon(null);
        setText(text);
        setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        revalidate();
        repaint();
    }

    private static String toUrlWithHttps(String url) {
        return Strings.CS.startsWith(url, "//") ? "https:" + url : url;
    }
}
