package core.gui;

import core.gui.image.ImageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class AsyncImageLabel extends JLabel {

    private long loadCounter = 0;

    public AsyncImageLabel() {
        super(EMPTY, SwingConstants.CENTER);
        setOpaque(true);
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY));
        setText("Loading image …");

        Dimension dimension = new Dimension(220, 220);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
    }

    public void load(ImageProvider imageProvider) {
        final long currentLoad = ++loadCounter;

        showLoading();

        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() {
                return imageProvider.load().orElse(null);
            }

            @Override
            protected void done() {
                if (currentLoad != loadCounter) {
                    return;
                }

                try {
                    BufferedImage img = get();

                    if (img != null) {
                        showImage(img);
                    } else {
                        showPlaceholder("Image not available");
                    }
                } catch (Exception ex) {
                    showPlaceholder("Error loading image");
                }
            }
        }.execute();
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
}
