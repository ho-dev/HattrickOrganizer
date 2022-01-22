package core.model.player;

import core.HO;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.net.MyConnector;
import core.util.HOLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 *     Class to hold player avatar information
 *
 *     Avatar is represented by overlay of graphic elements available
 *     on HT website
 */
public class PlayerAvatar {

    // Fields - Instance Variables  ====================
    private Integer m_playerID;
    private String m_bgImage;
    private List<Layer> m_layers;

    // Setters and Getters ==================
    public Integer getPlayerID() {
        return m_playerID;
    }

    public void set_bgImage(String m_bgImage) {
        this.m_bgImage = m_bgImage;
    }

    public List<Layer> getlayers() {
        return m_layers;
    }

    // Constructor and methods ;
    public PlayerAvatar(Integer playerID, String bgImage, List<Layer> layers) {
        this.m_playerID = playerID;
        this.m_bgImage = fixURL(bgImage);
        this.m_layers = layers.stream().map(elt -> fixURL(elt)).collect(Collectors.toList());
    }


    private Layer fixURL(Layer input_layer) {
        int x = input_layer.x();
        int y = input_layer.y();
        String url = fixURL(input_layer.urlElement());

        return new Layer(x, y, url);
    }

    private String fixURL(String input_url) {

        String prefixURL = "https://www84.hattrick.org";
        if (input_url.startsWith("http")) {
            return input_url;
        }
        return prefixURL + input_url;
    }

    public void generateAvatar(Path pathAvatar) throws IOException {

        var FirstLayer = m_layers.get(0);
        int x0, y0;

        URL url  = new URL(FirstLayer.urlElement());
        BufferedImage img = ImageIO.read(url.openStream());

        BufferedImage avatar = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = avatar.getGraphics();

        g.drawImage(img, 0, 0, null);

        x0 = FirstLayer.x();
        y0 = FirstLayer.y();

        for (Layer layer : m_layers.stream().skip(1).collect(Collectors.toList())) {
            url = new URL(layer.urlElement());
            img = ImageIO.read(url.openStream());
            g.drawImage(img, layer.x() - x0, layer.y() - y0, null);
        }

        // Save as new image
        String pathName = pathAvatar.resolve(this.m_playerID + ".png").toString();
        ImageIO.write(avatar, "PNG", new File(pathName));
    }



}


