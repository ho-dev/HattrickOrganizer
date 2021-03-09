package core.gui.comp.icon;


import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.player.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static core.gui.theme.HOIconName.*;
import static core.gui.theme.HOIconName.ONEYELLOW_TINY;

public class StatusIcon implements Icon {
    public final static int ICON_SIZE = 12;
    public final static int ICON_SPACE = 2;
    private static final Map<Object, Object> TRANSFERLISTED_ICON_COLOR_MAP = Map.of("foregroundColor", ThemeManager.getColor(HOColorName.PLAYER_SPECIALTY_COLOR));
    private static final Icon TRANSFERLISTED_ICON = ImageUtilities.getSvgIcon(TRANSFERLISTED_TINY, TRANSFERLISTED_ICON_COLOR_MAP, 14, 14);
    private static final Icon SUSPENDED_ICON = ImageUtilities.getSvgIcon(SUSPENDED_TINY, ICON_SIZE, ICON_SIZE);
    private static final Icon TWO_YELLOW_ICON = ImageUtilities.getSvgIcon(TWOYELLOW_TINY, ICON_SIZE, ICON_SIZE);
    private static final Icon ONE_YELLOW_ICON = ImageUtilities.getSvgIcon(ONEYELLOW_TINY, ICON_SIZE, ICON_SIZE);

    private final List<Icon> icons = new ArrayList<>();

    public StatusIcon(Player player) {
        setPlayer(player);
    }

    public void setPlayer(Player player) {
        if (player.getTransferlisted() > 0) {
            icons.add(TRANSFERLISTED_ICON);
        }
        if (player.isRedCarded()) {
            icons.add(SUSPENDED_ICON);
        } else if (player.getGelbeKarten() == 2) {
            icons.add(TWO_YELLOW_ICON);
        } else if (player.getGelbeKarten() == 1) {
            icons.add(ONE_YELLOW_ICON);
        }
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        for (int i = 0; i < icons.size(); i++) {
            Icon cur = icons.get(i);
            int offset = x+ICON_SIZE*i;
            if (i > 0) {
                offset += ICON_SPACE*i;
            }
            cur.paintIcon(c, g, offset, y);
        }
    }

    @Override
    public int getIconWidth() {
        return ICON_SIZE*icons.size() + ICON_SPACE*(icons.size()-1);
    }

    @Override
    public int getIconHeight() {
        return ICON_SIZE;
    }
}
