package tool.updater;

import com.github.weisj.darklaf.LafManager;
import core.gui.theme.ImageUtilities;
import core.model.player.IMatchRoleID;
import module.series.statistics.DataDownloader;

import javax.swing.*;
import java.awt.*;

public class AlltidLinkTest {

    public static void main(String[] args) {

        var res = DataDownloader.instance().fetchLeagueTeamPowerRatings(3193, 10, 76);
        System.out.print(res);

    }

}
