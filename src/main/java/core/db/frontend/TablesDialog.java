package core.db.frontend;

import core.db.DBManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;
import java.sql.ResultSet;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

final class TablesDialog extends JDialog implements MouseListener {
	@Serial
    private static final long serialVersionUID = -1584823279333655850L;
	private JList<String> tablelist;
    private JTable tableColumns;
    
    TablesDialog(SQLDialog owner) {
        super(owner, "Tables");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initialize();
    }

    private void initialize() {
        int width = (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
        int heigth = (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
        setLocation((width - 450) / 2, (heigth - 410) / 2);
        setSize(450, 410);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getMiddlePanel(), BorderLayout.WEST);
        getContentPane().add(getTablePanel(), BorderLayout.CENTER);
    }

    private JScrollPane getMiddlePanel() {
        return new JScrollPane(getList());
    }

    private JScrollPane getTablePanel() {
        return new JScrollPane(getTable());
    }

    private JList<String> getList()
    {
        if(tablelist == null)
        {
            tablelist = new JList<>(DBManager.instance().getConnectionManager().getAllTableNames());
            tablelist.addMouseListener(this);
        }
        return tablelist;
    }

    private JScrollPane getTable()
    {
        tableColumns = new JTable(new DummyTableModel(null, null));
        tableColumns.addMouseListener(this);
        tableColumns.setAutoResizeMode(0);
        JScrollPane scroll = new JScrollPane(tableColumns);
        scroll.setPreferredSize(new Dimension(0, 150));
        return scroll;
    }

    private Object[][] setTable(String tablename)
        throws Exception
    {
        ResultSet rs = DBManager.instance().getConnectionManager().executeQuery("SELECT * FROM " + tablename + " where 1 = 2");
        int columns = rs.getMetaData().getColumnCount();
        var columnData = new Object[columns][4];
        for(int i = 0; i < columns; i++)
        {
            columnData[i][0] = rs.getMetaData().getColumnName(i + 1);
            columnData[i][1] = rs.getMetaData().getColumnTypeName(i + 1);
            columnData[i][2] = rs.getMetaData().getColumnDisplaySize(i + 1);
            columnData[i][3] = rs.getMetaData().isNullable(i + 1)==0?"false":"true";
        }

        rs.close();
        return columnData;
    }

    public void mouseClicked(MouseEvent e)
    {
        JTextPane area = ((SQLDialog)getOwner()).getTextArea();
        if(e.getSource() instanceof JList)
            if(e.getClickCount() == 2)
                area.setText(area.getText() + " " + getList().getSelectedValue());
            else
                refresh();
        if((e.getSource() instanceof JTable) && e.getClickCount() == 2)
            area.setText(area.getText() + " " + tableColumns.getValueAt(tableColumns.getSelectedRow(), 0));
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    private void refresh()
    {
        String tableName = getList().getSelectedValue().toString();
        try
        {
            DummyTableModel model1 = new DummyTableModel(setTable(tableName), COLUMN_NAMES);
            tableColumns.setModel(model1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static final String[] COLUMN_NAMES = {
        "NAME", "TYP", "SIZE", "Nullable"
    };
}