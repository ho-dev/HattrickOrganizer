/*
 * Created on 5-nov-2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tool.updater;

import core.model.News;
import core.util.BrowserLauncher;
import core.util.HOLogger;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * @author Mirtillo
 *
 */
class NewsPanel extends JPanel {
	
	private static final long serialVersionUID = -6950711476669887530L;
	private JLabel header = new JLabel("",SwingConstants.LEFT); 
	private JButton b3 = new JButton("");
	private boolean linkEnabled = false;

	NewsPanel(News news) {
		super();
		jbInit(news);
	}

	private void jbInit(News news) {
		linkEnabled = false;
		if ((news.getLink() != null) && (news.getLink().length() > 1)) {
			linkEnabled = true;
		}
		
		switch (news.getType()) {
			case News.PLUGIN :
				{
					header.setText("Plugin Update");					
					break;
				}
			case News.MESSAGE :
				{
					header.setText("News from HO-Team");
					break;
				}
		}
		
		int dim = 1 + news.getMessages().size();
		if (linkEnabled) {
			dim++;
		}		
		setLayout(new GridLayout(dim, 1));
		add(header);
		for (int i = 0; i < news.getMessages().size(); i++) {
			JLabel l = new JLabel(""+news.getMessages().get(i),SwingConstants.LEFT);
			add(l);
		}
		if (linkEnabled) {
			b3.setText(news.getLink());
			add(b3);			
		}
		
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (linkEnabled) {
					try {
						BrowserLauncher.openURL(b3.getText());
					} catch (Exception ex) {
						HOLogger.instance().log(NewsPanel.class, ex);
					}
				}
			}
		});
	}
}