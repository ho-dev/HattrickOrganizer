package module.youth;

import core.gui.comp.table.HOTableModel;
import core.model.player.YouthPlayer;

import java.util.List;

public class YouthPlayerOverviewColumnModel extends HOTableModel {
    /**
     * constructor
     *
     * @param id column model id
     */
    public YouthPlayerOverviewColumnModel(int id) {
        super(id,"YouthPlayerOverview");
        initData();
    }

    @Override
    protected void initData() {

    }

    public void setValues(List<YouthPlayer> currentYouthPlayers) {

    }

    public void reInitData() {

    }
}
