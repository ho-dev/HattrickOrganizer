package core.db;

import core.gui.comp.tabbedPane.HOTabInfo;

import java.sql.Types;
import java.util.List;

public class HOTabInfoTable extends AbstractTable {

    public static final String TABLENAME = "HOTABINFO";

    HOTabInfoTable(ConnectionManager adapter){
        super(TABLENAME, adapter);
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
            ColumnDescriptor.Builder.newInstance().setColumnName("tabbedPaneID").setGetter((o) -> ((HOTabInfo) o).getTabbedPaneID()).setSetter((o, v) -> ((HOTabInfo) o).setTabbedPaneID((String)v)).setType(Types.VARCHAR).setLength(256).isPrimaryKey(false).build(),
            ColumnDescriptor.Builder.newInstance().setColumnName("tabID").setGetter((o) -> ((HOTabInfo) o).getTabID()).setSetter((o, v) -> ((HOTabInfo) o).setTabID((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.newInstance().setColumnName("tabIndex").setGetter((o) -> ((HOTabInfo) o).getTabIndex()).setSetter((o, v) -> ((HOTabInfo) o).setTabIndex((int) v)).setType(Types.INTEGER).isNullable(false).build()
        };
    }

    public List<HOTabInfo> loadHOTabInfo(String tabbedPaneID) {
        var rs = connectionManager.executePreparedQuery(createSelectStatement("WHERE tabbedPaneID=? ORDER BY tabIndex ASC"), tabbedPaneID);
        return load(HOTabInfo.class, rs);
    }
}
