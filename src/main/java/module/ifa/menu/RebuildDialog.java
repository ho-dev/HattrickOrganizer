package module.ifa.menu;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.model.TranslationFacility;
import module.ifa.PluginIfaUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RebuildDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -5212562044591100095L;
	
	JLabel infoLabel = new JLabel(TranslationFacility.tr("ifa.reload.info"));
	JButton okButton = new JButton(TranslationFacility.tr("ifa.reload.reloadbutton"));
	JButton cancelButton = new JButton(TranslationFacility.tr("ls.button.cancel"));
	HOMainFrame mainFrame = null;
	Boolean updatePerformed = false;
	
	
	public RebuildDialog (HOMainFrame mainFrame) {
		super(mainFrame, TranslationFacility.tr("ls.menu.modules.ifa.reloadallmatches"));
		this.mainFrame = mainFrame;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		init();
	
	}
	

	private void init() {
		JPanel content = new JPanel();

		content.setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(4, 4, 4, 4);
        
        
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        content.add(infoLabel, constraints);

        constraints.weightx = 0;
        constraints.weighty = 0;

        constraints.gridwidth = 1;
        constraints.gridy = 2;
        content.add(okButton, constraints);

        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 2;
        content.add(cancelButton, constraints);
        
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
        
		setContentPane(content);
		pack();

		Dimension size = mainFrame.getToolkit().getScreenSize();
		if (size.width > this.getSize().width) { // open dialog in the middle of
													// the screen
			this.setLocation((size.width / 2) - (this.getSize().width / 2),
					(size.height / 2) - (this.getSize().height / 2));
        
			
		}
		setResizable(false);
	}


	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == cancelButton) {
			dispose();
		} else if (event.getSource() == okButton) {
			DBManager.instance().deleteIFAMatches();
			PluginIfaUtils.updateMatchesTable();
			RefreshManager.instance().doRefresh();
			dispose();
		}
	}
}
