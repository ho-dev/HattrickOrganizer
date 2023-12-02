package tool.hrfExplorer;

import core.model.HOVerwaltung;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;


public class HrfExplorerDialog extends JDialog {
	
	private static final long serialVersionUID = -6591856825578209977L;

	public HrfExplorerDialog(JFrame owner){
		super(owner,true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initialize();
	}

	private void initialize() {
		setSize(1024,668);
		setLayout(new BorderLayout());
		setTitle(HOVerwaltung.instance().getLanguageString("Tab_HRF-Explorer"));
		
		add(new HrfExplorer(), BorderLayout.CENTER);
		
	}

	@Override
	public void setSize(int width, int height) {  
	   super.setSize(width, height);  
		    
	   Dimension screenSize = getParent().getSize();  
	   int x = (screenSize.width - getWidth()) / 2;  
	   int y = (screenSize.height - getHeight()) / 2;  
	    
	   setLocation(getParent().getX()+x, getParent().getY()+y);     
	}
}
