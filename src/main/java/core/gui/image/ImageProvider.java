package core.gui.image;

import java.awt.image.BufferedImage;
import java.util.Optional;

@FunctionalInterface
public interface ImageProvider {

    Optional<BufferedImage> load();
}
