package module.youth;

import core.gui.comp.table.HOTableModel;
import core.gui.model.PlayerColumn;
import core.gui.model.UserColumnFactory;
import core.gui.model.YouthPlayerColumn;
import core.model.HOVerwaltung;
import core.model.player.Player;
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
        columns =  UserColumnFactory.createYouthPlayerColumns();
        initData();
    }

    @Override
    protected void initData() {

        var youthplayers = HOVerwaltung.instance().getModel().getCurrentYouthPlayers();

        m_clData = new Object[youthplayers.size()][columns.length];

        int playernum=0;
        for ( var player: youthplayers ) {
            int columnnum=0;
            for (var col: columns){
                m_clData[playernum][columnnum] = ((YouthPlayerColumn)col).getTableEntry(player, null);
            }
        }
    }

    public void setValues(List<YouthPlayer> currentYouthPlayers) {

    }

    public void reInitData() {

    }
}
