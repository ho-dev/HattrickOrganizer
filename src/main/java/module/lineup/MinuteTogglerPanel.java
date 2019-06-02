package module.lineup;

import core.model.HOVerwaltung;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public final class MinuteTogglerPanel extends JPanel {
	
	private List<JLabel> toggleKeys = new ArrayList();
	private List<JLabel> toggleKeysET = new ArrayList();
	private int current = 0;
	
	public MinuteTogglerPanel() {

		initComponents();
	}

	private void initComponents() {
		setLayout(new GridLayout(1,1));
		JLabel prevButton = new JLabel("<<", SwingConstants.CENTER);
		prevButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				shiftBackward(1);
				revalidate();
			}
		});
		add(prevButton);
		List<Double> toggleLabels = new ArrayList(HOVerwaltung.instance().getModel().getLineup().getRatings().getLeftDefense().keySet());
		Collections.sort(toggleLabels);
		for(int i=0;i<toggleLabels.size();i++) {
			JLabel toggleLabel = new JLabel(String.valueOf(toggleLabels.get(i).longValue()), SwingConstants.CENTER);
			toggleLabel.setForeground(Color.BLACK);
			if(i%2 == 0) toggleLabel.setBackground(Color.GREEN);
			else toggleLabel.setBackground(Color.RED);
			toggleLabel.setOpaque(true);
			if(toggleLabels.get(i) > 90d) {
				toggleKeysET.add(toggleLabel);
			} else {
				add(toggleLabel);
				toggleKeys.add(toggleLabel);
			}
		}
		toggleKeys.get(0).setForeground(Color.GREEN);
		toggleKeys.get(0).setBackground(Color.BLACK);
		JLabel nextButton = new JLabel(">>", SwingConstants.CENTER);
		nextButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				shiftForward(1);
				revalidate();
			}
		});
		add(nextButton);
		JLabel ETButton = new JLabel("ET", SwingConstants.CENTER); //toggle extra time visibility (on default: not visible)
		ETButton.addMouseListener(new MouseAdapter() {
			boolean enabled = false;

			@Override
			public void mousePressed(MouseEvent e) {
				if(enabled) {
					JLabel key = toggleKeys.get(current);
					for(JLabel ETKey: toggleKeysET) {
						toggleKeys.remove(ETKey);
						remove(ETKey);
					}
					if(current >= toggleKeys.size()) {
						reverseColor(key);
						current = toggleKeys.size() - 1;
						reverseColor(toggleKeys.get(current));
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

	private void shiftForward(int value) {
		ListIterator<JLabel> keyIterator = toggleKeys.listIterator(current);
		JLabel key = keyIterator.next();
		reverseColor(key);
		while(value != 0) {
			if(keyIterator.hasNext()) key = keyIterator.next();
			else {
				keyIterator = toggleKeys.listIterator();
				key = keyIterator.next();
			  }
			value--;
		}
		if(keyIterator.hasPrevious()) current = keyIterator.previousIndex();
		else current = toggleKeys.size() - 1;
		reverseColor(key);
	}

	private void shiftBackward(int value) {
		ListIterator<JLabel> keyIterator = toggleKeys.listIterator(current + 1);
		JLabel key = keyIterator.previous();
		reverseColor(key);
		while(value != 0) {
			if(keyIterator.hasPrevious()) {
				key = keyIterator.previous();
			} else {
				keyIterator = toggleKeys.listIterator(toggleKeys.size());
				key = keyIterator.previous();
			  }
			value--;
		}
		if(keyIterator.hasNext()) current = keyIterator.nextIndex();
		else current = 0;
		reverseColor(key);
	}

	private void reverseColor(JLabel target) {
		Color fg = target.getForeground();
        Color bg = target.getBackground();
		target.setForeground(bg);
		target.setBackground(fg);
	}
}
