package core.playeravatars;

import core.HO;
import core.db.DBManager;
import core.db.user.UserManager;
import core.file.xml.XMLArenaParser;
import core.file.xml.XMLAvatarsParser;
import core.model.HOVerwaltung;
import core.net.MyConnector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class PlayerAvatars {

    public static void main(String[] args) throws IOException {

    HO.setPortable_version(true);
    DBManager.instance().loadUserParameter();
    HOVerwaltung.instance().loadLatestHoModel();
    HOVerwaltung.instance().setResource("English");
//    HOVerwaltung.instance().set

    final MyConnector mc = MyConnector.instance();
    int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
//    Map<String, String> arenaDataMap = XMLAvatarsParser.parseAvatarsFromString(mc.getAvatars(teamId));

    URL url  = new URL("https://www84.hattrick.org/Img/Avatar/backgrounds/card1.png");
    BufferedImage bg = ImageIO.read(url.openStream());

    url = new URL("https://www84.hattrick.org/Img/Avatar/backgrounds/bg_blue.png");
    BufferedImage overlay = ImageIO.read(url.openStream());

    url = new URL("http://res.hattrick.org/kits/27/261/2607/2606193/body6.png");
    BufferedImage body = ImageIO.read(url.openStream());

    url = new URL("https://www84.hattrick.org/Img/Avatar/faces/f9a.png");
    BufferedImage face = ImageIO.read(url.openStream());

    // create the new image, canvas size is the max. of both image sizes
    int w = Math.max(bg.getWidth(), bg.getWidth());
    int h = Math.max(bg.getHeight(), bg.getHeight());
    BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

    // paint both images, preserving the alpha channels
    Graphics g = combined.getGraphics();
    g.drawImage(bg, 0, 0, null);
    g.drawImage(overlay, 9, 10, null);
    g.drawImage(body, 9, 10, null);
    g.drawImage(face, 9, 10, null);

    // Save as new image
//    Path tempImgPath = Paths.get(UserManager.instance().getDbParentFolder() , "img");
    Path tempImgPath = Paths.get("/home/sabry/Code/HO/", "img");
    Path teamLogoPath = tempImgPath.resolve("players_avatar");
//    String combinedFileName = teamLogoPath.resolve("combined.png").toString();
    ImageIO.write(combined, "PNG", new File(String.valueOf(teamLogoPath), "combined.png"));

    }

}
