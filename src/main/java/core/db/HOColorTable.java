package core.db;

import core.gui.theme.HOColor;
import java.sql.Types;
import java.util.List;

public class HOColorTable extends AbstractTable {

    public final static String TABLENAME = "HOColor";

    /**
     * constructor
     *
     * @param connectionManager Connection manager
     */
    public HOColorTable(ConnectionManager connectionManager) {
        super(TABLENAME, connectionManager);
        idColumns = 2;
    }

    @Override
    protected void initColumns() {
        columns = new ColumnDescriptor[]{
                ColumnDescriptor.Builder.newInstance().setColumnName("NAME").setGetter((o) -> ((HOColor) o).getName()).setSetter((o, v) -> ((HOColor) o).setName((String) v)).setType(Types.VARCHAR).isNullable(false).setLength(255).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("THEME").setGetter((o) -> ((HOColor) o).getTheme()).setSetter((o, v) -> ((HOColor) o).setTheme((String) v)).setType(Types.VARCHAR).isNullable(false).setLength(255).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("COLOR_REFERENCE").setGetter((o) -> ((HOColor) o).getColorReference()).setSetter((o, v) -> ((HOColor) o).setColorReference((String) v)).setType(Types.VARCHAR).isNullable(true).setLength(255).build(),
                ColumnDescriptor.Builder.newInstance().setColumnName("VALUE").setGetter((o) -> ((HOColor) o).getValue()).setSetter((o, v) -> ((HOColor) o).setValue((Integer) v)).setType(Types.INTEGER).isNullable(true).build(),
        };
    }

    @Override
    protected String createSelectStatement() {
        return createSelectStatement("WHERE THEME=?");
    }

    public List<HOColor> load(String theme) {
        return load(HOColor.class, theme);
    }
}
