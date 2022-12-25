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
    private final static Map<Object, Object> TRANSFERLISTED_ICON_COLOR_MAP = Map.of("foregroundColor", ThemeManager.getColor(HOColorName.PLAYER_SPECIALTY_COLOR));

    private int ICON_SIZE = 12;
    private int ICON_SPACE = 2;
    private Icon TRANSFERLISTED_ICON = ImageUtilities.getSvgIcon(TRANSFERLISTED_TINY, TRANSFERLISTED_ICON_COLOR_MAP, 14, 14);
    private Icon SUSPENDED_ICON = ImageUtilities.getSvgIcon(SUSPENDED_TINY, ICON_SIZE, ICON_SIZE);
    private Icon TWO_YELLOW_ICON = ImageUtilities.getSvgIcon(TWOYELLOW_TINY, ICON_SIZE, ICON_SIZE);
    private Icon ONE_YELLOW_ICON = ImageUtilities.getSvgIcon(ONEYELLOW_TINY, ICON_SIZE, ICON_SIZE);

    private final static int LARGE_ICON_SIZE = 16;
    private final static int LARGE_ICON_SPACE = 3;
    private final static Icon LARGE_TRANSFERLISTED_ICON = ImageUtilities.getSvgIcon(TRANSFERLISTED_TINY, TRANSFERLISTED_ICON_COLOR_MAP, 19, 19);
    private final static Icon LARGE_SUSPENDED_ICON = ImageUtilities.getSvgIcon(SUSPENDED_TINY, LARGE_ICON_SIZE, LARGE_ICON_SIZE);
    private final static Icon LARGE_TWO_YELLOW_ICON = ImageUtilities.getSvgIcon(TWOYELLOW_TINY, LARGE_ICON_SIZE, LARGE_ICON_SIZE);
    private final static Icon LARGE_ONE_YELLOW_ICON = ImageUtilities.getSvgIcon(ONEYELLOW_TINY, LARGE_ICON_SIZE, LARGE_ICON_SIZE);

    private final List<Icon> icons = new ArrayList<>();

    public StatusIcon(Player player, boolean isLarge) {
        if(isLarge){
            ICON_SIZE = LARGE_ICON_SIZE;
            ICON_SPACE = LARGE_ICON_SPACE;
            TRANSFERLISTED_ICON = LARGE_TRANSFERLISTED_ICON;
            SUSPENDED_ICON = LARGE_SUSPENDED_ICON;
            TWO_YELLOW_ICON = LARGE_TWO_YELLOW_ICON;
            ONE_YELLOW_ICON = LARGE_ONE_YELLOW_ICON;
        }

        setPlayer(player);
    }

    public StatusIcon(Player player) {
        this(player, false);
    }

    public void setPlayer(Player player) {
        if (player.getTransferlisted() > 0) {
                icons.add(TRANSFERLISTED_ICON);
        }
        if (player.isRedCarded()) {
                icons.add(SUSPENDED_ICON);
        }
        else if (player.getCards() == 2) {
                icons.add(TWO_YELLOW_ICON);
        }
        else if (player.getCards() == 1) {
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
        if ( icons.size() == 0 ) return 0;
        return ICON_SIZE*icons.size() + ICON_SPACE*(icons.size()-1);
    }

    @Override
    public int getIconHeight() {
        return ICON_SIZE;
    }
}
