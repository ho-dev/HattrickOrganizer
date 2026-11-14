package core.db;

import core.gui.comp.tabbedPane.HOTabInfo;

import java.sql.Types;
import java.util.List;

public class HOTabInfoTable extends AbstractTable {

    public static final String TABLENAME = "HOTABINFO";

    HOTabInfoTable(ConnectionManager adapter) {
        super(TABLENAME, adapter);
        idColumns = 2;
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
            ColumnDescriptor.Builder.newInstance().setColumnName("tabbedPaneId").setGetter((o) -> ((HOTabInfo) o).getTabbedPaneId()).setSetter((o, v) -> ((HOTabInfo) o).setTabbedPaneId((String) v)).setType(Types.VARCHAR).setLength(256).isPrimaryKey(false).build(),
            ColumnDescriptor.Builder.newInstance().setColumnName("tabId").setGetter((o) -> ((HOTabInfo) o).getTabId()).setSetter((o, v) -> ((HOTabInfo) o).setTabId((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
            ColumnDescriptor.Builder.newInstance().setColumnName("tabIndex").setGetter((o) -> ((HOTabInfo) o).getTabIndex()).setSetter((o, v) -> ((HOTabInfo) o).setTabIndex((int) v)).setType(Types.INTEGER).isNullable(false).build()
        };
    }

    public List<HOTabInfo> loadHOTabInfo(String tabbedPaneId) {
        var rs = connectionManager.executePreparedQuery(createSelectStatement("WHERE tabbedPaneId=? ORDER BY tabIndex ASC"), tabbedPaneId);
        return load(HOTabInfo.class, rs);
    }

    public void storeHOTabInfo(List<HOTabInfo> tabs) {
        for (var tab : tabs) {
            store(tab);
        }
    }

    public void deleteHOTabInfo(List<HOTabInfo> tabs) {
        for (var tab : tabs) {
            delete(tab);
        }
    }
}
