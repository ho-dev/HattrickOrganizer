package core.option;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import tool.updater.TableEditor;
import tool.updater.TableModel;
import tool.updater.UpdaterCellRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableColumnModel;


/**
 * 
 * @author Thorsten Dietz
 * @since 1.36
 *
 */
public class UserColumnsPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JComboBox m_jcbColumnModel 	= null;
	private JTable table 				= null;
	private final String [] columnNames = new String[]{" ", HOVerwaltung.instance().getLanguageString("column")};
	private final Icon lockedImage = ThemeManager.getIcon(HOIconName.LOCKED);
	protected UserColumnsPanel(){
		initComponents();
	}
	
	private void initComponents(){
		setLayout(new BorderLayout());
		add(getTopPanel(),BorderLayout.NORTH);
		add(getMiddlePanel(),BorderLayout.CENTER);
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel getTopPanel(){
		JPanel panel = new ImagePanel();
		m_jcbColumnModel = new JComboBox(UserColumnController.instance().getAllModels());
		m_jcbColumnModel.addActionListener( this );
		
		panel.add(m_jcbColumnModel);
		return panel;
	}
	
	/**
	 * return the panel within JTable
	 * @return JPanel
	 */
	private JPanel getMiddlePanel(){
		JPanel panel = new JPanel();
		 panel.setLayout(new BorderLayout());
	     panel.add(createTable());
	    panel.setPreferredSize(new Dimension(100, 300));
		return panel;
	}
	
	private HOTableModel getSelectedModel(){
		return (HOTableModel)m_jcbColumnModel.getSelectedItem();
	}
    /**
     * Creates a table with checkboxes for UserColumns of the selected model
     *
     * @return JScrollPane including the table
     */
    protected JScrollPane createTable() {
        table = new JTable(getModel(getSelectedModel().getColumns()));
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new UpdaterCellRenderer());
        table.getColumn(columnNames[0]).setCellEditor(new TableEditor());

        final TableColumnModel tableColumnModel = table.getColumnModel();
        tableColumnModel.getColumn(0).setMaxWidth(50);
        tableColumnModel.getColumn(0).setPreferredWidth(50);

		return new JScrollPane(table);
    }

    protected TableModel getModel(UserColumn[] dbColumns) {
        Object[][] value = new Object[dbColumns.length][2];

        for (int i = 0; i < dbColumns.length; i++) {

            value[i][0] = getCheckbox(dbColumns[i]);
            value[i][1] = dbColumns[i];
        }

		return new TableModel(value, columnNames);
    }

    protected JCheckBox getCheckbox(UserColumn column) {
        JCheckBox tmp = new JCheckBox();
        tmp.setOpaque(false);
        tmp.setSelected(column.isDisplay());
        tmp.setHorizontalAlignment(SwingConstants.CENTER);
        if(column.isEditable())
        	tmp.addActionListener( this );
        else{
        	tmp.setIcon(lockedImage);
        }
        return tmp;
    }

    /**
     * action
     */
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == m_jcbColumnModel){
			table.setModel(getModel(getSelectedModel().getColumns()));
			 table.getColumn(columnNames[0]).setCellEditor(new TableEditor());
			final TableColumnModel tableColumnModel = table.getColumnModel();
		        tableColumnModel.getColumn(0).setMaxWidth(50);
		        tableColumnModel.getColumn(0).setPreferredWidth(50);
		}
		
		if(arg0.getSource() instanceof JCheckBox){
			var col = (UserColumn)table.getValueAt(table.getSelectedRow(),1);
			var doDisplay = ((JCheckBox)arg0.getSource()).isSelected();
			col.setDisplay(doDisplay);
			if (doDisplay && col.getPreferredWidth()<25){
				col.setPreferredWidth(25);
			}
			OptionManager.instance().setRestartNeeded();
		}
		
	}
	
}
