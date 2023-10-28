package module.playeranalysis;

import core.gui.ApplicationClosingListener;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.model.UserParameter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;


public class PlayerAnalyseMainPanel extends LazyImagePanel {

	private JButton arrangeButton;
	private JSplitPane splitPane;
	private SpielerAnalysePanel playersPanel1;
	private SpielerAnalysePanel playersPanel2;

	public final void setSpieler4Bottom(int spielerid) {
		if (this.playersPanel2 != null) {
			playersPanel2.setAktuelleSpieler(spielerid);
		}
	}

	public final void setSpieler4Top(int spielerid) {
		if (this.playersPanel1 != null) {
			playersPanel1.setAktuelleSpieler(spielerid);
		}
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
	}

	@Override
	protected void update() {
		// nothing to do here
	}

	private void addListeners() {
		HOMainFrame.INSTANCE.addApplicationClosingListener(new ApplicationClosingListener() {

			@Override
			public void applicationClosing() {
				saveSettings();
			}
		});

		arrangeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
					splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
				} else {
					splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
				}

				UserParameter.instance().spieleranalyseVertikal = !UserParameter.instance().spieleranalyseVertikal;
			}
		});
	}

	private void saveSettings() {
		UserParameter parameter = UserParameter.instance();
		parameter.spielerAnalysePanel_horizontalSplitPane = splitPane.getDividerLocation();
		playersPanel1.saveColumnOrder();
		playersPanel2.saveColumnOrder();
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		JPanel panel = new ImagePanel(new BorderLayout());

		arrangeButton = new JButton(ImageUtilities.getSvgIcon(HOIconName.TURN));
		arrangeButton.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString(
				"tt_SpielerAnalyse_drehen"));
		arrangeButton.setPreferredSize(new Dimension(24, 24));
		panel.add(arrangeButton, BorderLayout.WEST);
		panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		add(panel, BorderLayout.NORTH);

		playersPanel1 = new SpielerAnalysePanel(1);
		playersPanel2 = new SpielerAnalysePanel(2);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, playersPanel1, playersPanel2);
		splitPane
				.setDividerLocation(UserParameter.instance().spielerAnalysePanel_horizontalSplitPane);
		add(splitPane, BorderLayout.CENTER);

		if (!UserParameter.instance().spieleranalyseVertikal) {
			splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		} else {
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		}
	}
}
