package core.model.match;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;

import javax.swing.*;

public interface IMatchType {

    int getMatchTypeId();

    int getIconArrayIndex();

    default Icon getIcon(){
        return ThemeManager.getIcon(HOIconName.MATCHICONS[this.getIconArrayIndex()]) ;
    };

    String getName();
    default boolean isCompetitive() {
        return false;
    }
}
