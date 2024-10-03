package tool.arenasizer;

import core.model.HOVerwaltung;
import core.model.TranslationFacility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ArenaSizerDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private ArenaPanel panel;
	private DistributionStatisticsPanel historyPanel;
	private ArenaPanel infoPanel;
	private ControlPanel controlPanel;
	private JPanel toolbar;
	private JButton refreshButton = new JButton(TranslationFacility.tr("ls.button.apply"));

	public ArenaSizerDialog(JFrame owner){
		super(owner,true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initialize();
	}

	private void initialize() {
		setSize(900,430);
		setLayout(new BorderLayout());
		setTitle(TranslationFacility.tr("ArenaSizer"));
		add(getToolbar(), BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());

		JPanel panelC = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelC.add(getControlPanel());
		centerPanel.add(panelC,BorderLayout.NORTH);
		centerPanel.add(getTabbedPane(), BorderLayout.CENTER);

		add(centerPanel,BorderLayout.CENTER);

	}

	private JPanel getToolbar(){
		if(toolbar == null){
			toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING));
			toolbar.add(refreshButton);
			refreshButton.addActionListener(this);
			// reset
			// save
		}
		return toolbar;
	}

	private ControlPanel getControlPanel(){
		if(controlPanel == null){
			controlPanel = new ControlPanel();
		}
		return controlPanel;
	}

	private JPanel getHistoryPanel(){
		if(historyPanel == null){
			historyPanel = new DistributionStatisticsPanel();
		}
		return historyPanel;
	}

	ArenaPanel getArenaPanel(){
		if(panel == null){
			panel = new ArenaPanel();
		}
		return panel;
	}

	ArenaPanel getInfoPanel(){
		if(infoPanel == null){
			infoPanel = new ArenaPanel();
		}
		return infoPanel;
	}

	private JTabbedPane getTabbedPane(){
		if(tabbedPane == null){
			tabbedPane = new JTabbedPane();
			HOVerwaltung hoV = HOVerwaltung.instance();
			tabbedPane.addTab(TranslationFacility.tr("Stadion"), getArenaPanel());
			tabbedPane.addTab(hoV.getModel().getStadium().getStadienname(), getInfoPanel());
			tabbedPane.addTab(TranslationFacility.tr("Statistik"), getHistoryPanel());
		}
		return tabbedPane;
	}

	@Override
	public void setSize(int width, int height) {
	   super.setSize(width, height);

	   Dimension screenSize = getParent().getSize();
	   int x = (screenSize.width - getWidth()) / 2;
	   int y = (screenSize.height - getHeight()) / 2;

	   setLocation(getParent().getX()+x, getParent().getY()+y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		 if(e.getSource() == refreshButton){
			Stadium stadium = getControlPanel().getStadium();
			int[] supporter = getControlPanel().getModifiedSupporter();
            getArenaPanel().reinitArena(stadium, supporter[0],supporter[1],supporter[2]);
            getInfoPanel().reinitArena(HOVerwaltung.instance().getModel().getStadium(), supporter[0],supporter[1],supporter[2]);
		}
	}
}
