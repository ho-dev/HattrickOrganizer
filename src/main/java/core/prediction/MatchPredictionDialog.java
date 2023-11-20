package core.prediction;

import core.gui.HOMainFrame;
import core.model.UserParameter;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serial;

import javax.swing.JDialog;


public class MatchPredictionDialog extends JDialog {

	@Serial
	private static final long serialVersionUID = 1L;

	public MatchPredictionDialog(MatchEnginePanel panel, String match){
		super(HOMainFrame.instance(),"",true);
		initialize(panel);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(match);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				var comp = (Component)e.getSource();
				UserParameter.instance().matchPredictionDialog_Width = comp.getWidth();
				UserParameter.instance().matchPredictionDialog_Height = comp.getHeight();
			}
		});

		setVisible(true);
	}

	private void initialize(MatchEnginePanel panel) {
		getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        setResizable(true);
        setSize(UserParameter.instance().matchPredictionDialog_Width, UserParameter.instance().matchPredictionDialog_Height);
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
