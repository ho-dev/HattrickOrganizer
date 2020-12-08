package module.lineup.ratings;

import core.model.HOVerwaltung;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.rating.RatingPredictionManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.*;

public final class MinuteTogglerPanel extends JPanel {
	
	private Icon whiteGreenClock = ThemeManager.getScaledIcon(HOIconName.WHITE_GREEN_CLOCK, 20, 20);
	private Icon greenWhiteClock = ThemeManager.getScaledIcon(HOIconName.GREEN_WHITE_CLOCK, 20, 20);
	private Icon whiteRedClock = ThemeManager.getScaledIcon(HOIconName.WHITE_RED_CLOCK, 20, 20);
	private Icon redWhiteClock = ThemeManager.getScaledIcon(HOIconName.RED_WHITE_CLOCK, 20, 20);
	private Icon ratingsGraphIcon = ThemeManager.getScaledIcon(HOIconName.RATING_GRAPH, 20, 20);
	private JLabel ratingsGraph = new JLabel(ratingsGraphIcon);
	private JLabel avg90Clock = new JLabel(whiteGreenClock);
	private JLabel avg120Clock = new JLabel(redWhiteClock);
	private final List<JLabel> toggleKeys = new ArrayList<>();
	private final List<JLabel> toggleKeysET = new ArrayList<>();
	private List<Double> toggleLabels = null;
	private final LineupRatingPanel parent;
	private int current = -1; //default to regular time average
	
	public MinuteTogglerPanel(LineupRatingPanel parent) {
		this.parent = parent;
	}

	public void load() {
		if(toggleLabels != null) return;
		toggleLabels = new ArrayList<>(HOVerwaltung.instance().getModel().getLineup().getRatings().getLeftDefense().keySet());
		initComponents();
	}

	private void initComponents() {
		setLayout(new GridBagLayout());

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		avg90Clock.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(current >= 0) reverseColor(toggleKeys.get(current));
				else if(current == -2) avg120Clock.setIcon(redWhiteClock);
				else if(current == -1) return;
				current = -1;
				avg90Clock.setIcon(whiteGreenClock);
				revalidate();
				parent.setRatings();
			}
		});

		avg120Clock.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(current >= 0) reverseColor(toggleKeys.get(current));
				else if(current == -1) avg90Clock.setIcon(greenWhiteClock);
				else if(current == -2) return;
				current = -2;
				avg120Clock.setIcon(whiteRedClock);
				revalidate();
				parent.setRatings();
			}
		});

		ratingsGraph.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
			new RatingChartFrame();
			}
		});

		avg90Clock.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Lineup_RatingsPanel_Green_Clock"));
		avg120Clock.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Lineup_RatingsPanel_Red_Clock"));
		ratingsGraph.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Lineup_RatingsPanel_RatingGraph"));

		JPanel clocksPanel = new JPanel();
		clocksPanel.add(avg90Clock);
		clocksPanel.add(ratingsGraph);
		clocksPanel.add(avg120Clock);
		add(clocksPanel, constraints);

		constraints.gridx = GridBagConstraints.RELATIVE; //revert
		constraints.gridy = GridBagConstraints.RELATIVE; //to
		constraints.gridwidth = 1;                       //default

		JLabel prevButton = new JLabel("<<", SwingConstants.CENTER);
		prevButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				shiftBackward(1);
				revalidate();
				parent.setRatings();
			}
		});
		add(prevButton, constraints);
		toggleLabels.remove(-90d);  //remove 90' and 120'
		toggleLabels.remove(-120d); //average placeholder labels
		Collections.sort(toggleLabels);
		for(final int[] i={0};i[0]<toggleLabels.size();i[0]++) {
			JLabel toggleLabel = new JLabel(String.valueOf(toggleLabels.get(i[0]).longValue()), SwingConstants.CENTER);
			toggleLabel.addMouseListener(new MouseAdapter() {
				final int labelIndex = i[0];

				@Override
				public void mousePressed(MouseEvent e) {
					if(current >= 0) reverseColor(toggleKeys.get(current));
					else if(current == -1) avg90Clock.setIcon(greenWhiteClock);
					else if(current == -2) avg120Clock.setIcon(redWhiteClock);
					current = labelIndex;
					reverseColor(toggleKeys.get(labelIndex));
					revalidate();
					parent.setRatings();
				}
			});
			toggleLabel.setForeground(Color.BLACK);
			if(i[0]%2 == 0) toggleLabel.setBackground(Color.LIGHT_GRAY);
			else toggleLabel.setBackground(Color.WHITE);
			toggleLabel.setOpaque(true);
			if(toggleLabels.get(i[0]) == (45d + RatingPredictionManager.EPSILON)) {
				toggleLabel.setText(String.valueOf(45));
				toggleLabel.setBorder(BorderFactory.createMatteBorder(0, 6, 0, 0, Color.RED));
			}
			if(toggleLabels.get(i[0]) == (90d + RatingPredictionManager.EPSILON)) {
				toggleLabel.setText(String.valueOf(90));
				toggleLabel.setBorder(BorderFactory.createMatteBorder(0, 6, 0, 0, Color.RED));
			}
			if(toggleLabels.get(i[0]) <= 90d) {
				add(toggleLabel, constraints);
				toggleKeys.add(toggleLabel);
			} else if(toggleLabels.get(i[0]) <= 120d) {
				toggleKeysET.add(toggleLabel);
			}
		}
		JLabel nextButton = new JLabel(">>", SwingConstants.CENTER);
		nextButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				shiftForward(1);
				revalidate();
				parent.setRatings();
			}
		});
		add(nextButton, constraints);
		JLabel ETButton = new JLabel("ET", SwingConstants.CENTER); //toggle extra time visibility (on default: not visible)
		ETButton.addMouseListener(new MouseAdapter() {
			boolean enabled = false;

			@Override
			public void mousePressed(MouseEvent e) {
				if(enabled) {
					JLabel key = null;
					if(current >= 0) key = toggleKeys.get(current);
					for(JLabel ETKey: toggleKeysET) {
						toggleKeys.remove(ETKey);
						remove(ETKey);
					}
					if(current >= toggleKeys.size()) {
						assert key != null;
						reverseColor(key);
						current = toggleKeys.size() - 1;
						reverseColor(toggleKeys.get(current));
						parent.setRatings();
					}
				} else {
					for(JLabel ETKey: toggleKeysET) {
						toggleKeys.add(ETKey);
						add(ETKey, constraints, toggleKeys.size() + 1);
					}
				  }
				enabled = !enabled;
				revalidate();
			}
		});
		add(ETButton, constraints);
	}

	private void shiftForward(int value) {
		if(value < 1) return;
		ListIterator<JLabel> keyIterator;
		JLabel key = null;
		if(current >= 0) {
			keyIterator = toggleKeys.listIterator(current);
			key = keyIterator.next();
			reverseColor(key);
		} else {
			keyIterator = toggleKeys.listIterator(toggleKeys.size());
			if(current == -1) avg90Clock.setIcon(greenWhiteClock);
			else if(current == -2) avg120Clock.setIcon(redWhiteClock);
		  }
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
		if(value < 1) return;
		ListIterator<JLabel> keyIterator;
		JLabel key = null;
		if(current >= 0) {
			keyIterator = toggleKeys.listIterator(current + 1);
			key = keyIterator.previous();
			reverseColor(key);
		} else {
			keyIterator = toggleKeys.listIterator();
			if(current == -1) avg90Clock.setIcon(greenWhiteClock);
			else if(current == -2) avg120Clock.setIcon(redWhiteClock);
		  }
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

	public Double getCurrentKey() {
		if(current >= 0) return toggleLabels.get(current);
		else if(current == -2) return -120d;
		else return -90d;
	}
}
