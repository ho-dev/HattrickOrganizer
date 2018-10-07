package core.module;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class ModuleConfigPanel extends JPanel  {

	private static final long serialVersionUID = 1L;
	private ModuleConfigPanelTable table;

   
    
	public ModuleConfigPanel(){
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100, 300));
		add(new JScrollPane(getTable()),BorderLayout.CENTER);
		
	}
	
	private ModuleConfigPanelTable getTable(){
		if(table == null)
			table = new ModuleConfigPanelTable();
		return table;
	}

	
}
