package tool.updater;

import java.awt.*;
import javax.swing.*;
import core.gui.comp.HyperLinkLabel;
import core.net.MyConnector;

public class UpdaterPanel extends JPanel {

	private String version;
	private String releaseNoteUrl;
	private String updateLink;
	
	public UpdaterPanel(String version, String releaseNoteUrl) {
		this.version = version;
		this.releaseNoteUrl = releaseNoteUrl;
		this.updateLink = "";
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		initLayout();
	}

	public UpdaterPanel(String version, String releaseNoteUrl, String updateLink) {
		this.version = version;
		this.releaseNoteUrl = releaseNoteUrl;
		this.updateLink = updateLink;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setPreferredSize( new Dimension( 680, 300 ) );
		initLayout();
	}
	
	private void initLayout() {
		initLabelVersion();
		initHiperLink();
	    add(Box.createRigidArea(new Dimension(10,0)));
	    initReleaseNotesPanel();
	}
	
	// Create Version panel
	private void initLabelVersion() {
		JPanel panel = new JPanel();
	    JLabel label = new JLabel(version);	    
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder());
	    panel.add(label);

		panel.add(Box.createRigidArea(new Dimension(0,10)));
	    add(panel);
	}

	// Create hiperlink
	private void initHiperLink() {
		if(!updateLink.equals("")) {
			JPanel panel = new JPanel();
			JLabel linkLabel = new HyperLinkLabel(updateLink, updateLink);

			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.setBorder(BorderFactory.createCompoundBorder());
			panel.add(linkLabel);

			panel.add(Box.createRigidArea(new Dimension(0, 10)));
			add(panel);
		}
	}
	
	// Create Release Notes panel
	private void initReleaseNotesPanel() {

        JTextPane panel  = new JTextPane();
        panel.setContentType("text/html;charset=UTF-8");
        try {
			var is = MyConnector.instance().getWebFile(releaseNoteUrl, false);
			panel.read(is, "Release notes");
            //panel.setPage(releaseNoteUrl);
        } catch (Exception  e) {
            panel.setText(""+e.getCause());
        }
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension( 680, 300 ));

		add(scrollPane);
    }


}
