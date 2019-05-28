package module.lineup;

import core.model.HOVerwaltung;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public final class MinuteTogglerPanel extends JPanel {
	
	private List<JLabel> toggleKeys = new ArrayList();
	private List<JLabel> toggleKeysET = new ArrayList();
	
	public MinuteTogglerPanel() {

		initComponents();
	}

	private void initComponents() {
		setLayout(new GridLayout(1,1));
		add(new JLabel("<<", SwingConstants.CENTER));
		List<Double> toggleLabels = new ArrayList(HOVerwaltung.instance().getModel().getLineup().getRatings().getLeftDefense().keySet());
		Collections.sort(toggleLabels);
		for(int i=0;i<toggleLabels.size();i++) {
			JLabel toggleLabel = new JLabel(String.valueOf(toggleLabels.get(i).longValue()), SwingConstants.CENTER);
			if(i%2 == 0) toggleLabel.setBackground(java.awt.Color.GREEN);
			else toggleLabel.setBackground(java.awt.Color.RED);
			toggleLabel.setOpaque(true);
			if(toggleLabels.get(i) > 90d) {
				toggleKeysET.add(toggleLabel);
			} else {
				add(toggleLabel);
				toggleKeys.add(toggleLabel);
			}
		}
		add(new JLabel(">>", SwingConstants.CENTER));
		JLabel ETButton = new JLabel("ET", SwingConstants.CENTER); //toggle extra time visibility (on default: not visible)
		ETButton.addMouseListener(new MouseAdapter() {
			boolean enabled = false;

			@Override
			public void mousePressed(MouseEvent e) {
				if(enabled) {
					for(JLabel ETKey: toggleKeysET) {
						toggleKeys.remove(ETKey);
						remove(ETKey);
					}
				} else {
					for(JLabel ETKey: toggleKeysET) {
						toggleKeys.add(ETKey);
						add(ETKey, toggleKeys.size());
					}
				  }
				enabled = !enabled;
				revalidate();
			}
		});
		add(ETButton);
	}
}
