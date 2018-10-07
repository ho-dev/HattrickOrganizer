package module.lineup.substitution.positionchooser;

import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import module.lineup.substitution.PlayerPositionItem;
import module.lineup.substitution.positionchooser.PositionSelectionEvent.Change;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PositionChooser extends JPanel {

	private static final long serialVersionUID = 7378929734827883010L;
	private static final Color COLOR_BG = new Color(39, 127, 49);
	private final Color COLOR_POS_DEFAULT = COLOR_BG;
	private final Color COLOR_POS_OCCUPIED = Color.LIGHT_GRAY;
	private final Color COLOR_POS_SELECTED = Color.YELLOW;
	private final LinkedHashMap<Integer, PositionPanel> positions = new LinkedHashMap<Integer, PositionPanel>();
	private final List<PositionSelectionListener> positionSelectionListeners = new ArrayList<PositionSelectionListener>();
	private Integer selected;

	public PositionChooser() {
		positions.put(Integer.valueOf(ISpielerPosition.keeper), new PositionPanel(ISpielerPosition.keeper));
		positions.put(Integer.valueOf(ISpielerPosition.rightBack), new PositionPanel(ISpielerPosition.rightBack));
		positions.put(Integer.valueOf(ISpielerPosition.rightCentralDefender), new PositionPanel(
				ISpielerPosition.rightCentralDefender));
		positions.put(Integer.valueOf(ISpielerPosition.middleCentralDefender), new PositionPanel(
				ISpielerPosition.middleCentralDefender));
		positions.put(Integer.valueOf(ISpielerPosition.leftCentralDefender), new PositionPanel(
				ISpielerPosition.leftCentralDefender));
		positions.put(Integer.valueOf(ISpielerPosition.leftBack), new PositionPanel(ISpielerPosition.leftBack));
		positions.put(Integer.valueOf(ISpielerPosition.rightWinger), new PositionPanel(
				ISpielerPosition.rightWinger));
		positions.put(Integer.valueOf(ISpielerPosition.rightInnerMidfield), new PositionPanel(
				ISpielerPosition.rightInnerMidfield));
		positions.put(Integer.valueOf(ISpielerPosition.centralInnerMidfield), new PositionPanel(
				ISpielerPosition.centralInnerMidfield));
		positions.put(Integer.valueOf(ISpielerPosition.leftInnerMidfield), new PositionPanel(
				ISpielerPosition.leftInnerMidfield));
		positions.put(Integer.valueOf(ISpielerPosition.leftWinger),
				new PositionPanel(ISpielerPosition.leftWinger));
		positions.put(Integer.valueOf(ISpielerPosition.rightForward), new PositionPanel(
				ISpielerPosition.rightForward));
		positions.put(Integer.valueOf(ISpielerPosition.centralForward), new PositionPanel(
				ISpielerPosition.centralForward));
		positions.put(Integer.valueOf(ISpielerPosition.leftForward), new PositionPanel(
				ISpielerPosition.leftForward));

		initComponents();
	}

	public void init(Map<Integer, PlayerPositionItem> lineupPositions) {
		for (Integer positionKey : this.positions.keySet()) {
			PlayerPositionItem item = lineupPositions.get(positionKey);
			if (item != null) {
				this.positions.get(positionKey).setPlayer(item.getSpieler());
			} else {
				this.positions.get(positionKey).setPlayer(null);
			}
		}
	}

	public void select(Integer position) {
		if (this.selected == position) {
			return;
		}
		if (this.selected != null) {
			this.positions.get(this.selected).setSelected(false);
			firePositionSelectionChanged(new PositionSelectionEvent(this.selected, Change.DESELECTED));
		}
		this.selected = position;
		if (this.selected != null) {
			this.positions.get(this.selected).setSelected(true);
			firePositionSelectionChanged(new PositionSelectionEvent(this.selected, Change.SELECTED));
		}
	}

	public void addPositionSelectionListener(PositionSelectionListener listener) {
		this.positionSelectionListeners.add(listener);
	}

	public void removePositionSelectionListener(PositionSelectionListener listener) {
		this.positionSelectionListeners.remove(listener);
	}

	private void firePositionSelectionChanged(PositionSelectionEvent event) {
		for (int i = this.positionSelectionListeners.size() - 1; i >= 0; i--) {
			this.positionSelectionListeners.get(i).selectionChanged(event);
		}
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(Color.WHITE));
		setBackground(COLOR_BG);

		MouseListener myMouseListener = new MyMouseListener();

		Iterator<PositionPanel> positionPanelIterator = positions.values().iterator();
		int space = 4;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(space, space, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 2;
		PositionPanel pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		gbc.gridx = GridBagConstraints.RELATIVE;
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		gbc.insets = new Insets(space, space, 0, space);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);

		gbc.gridy = 2;
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		gbc.insets = new Insets(space, space, 0, space);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);

		gbc.gridy = 3;
		gbc.gridx = 1;
		gbc.insets = new Insets(space, space, space, 0);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		gbc.gridx = GridBagConstraints.RELATIVE;
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
		pos = positionPanelIterator.next();
		pos.addMouseListener(myMouseListener);
		add(pos, gbc);
	}

	private class PositionPanel extends JPanel {

		private static final long serialVersionUID = 6025107478898829134L;
		private Integer position;
		private Spieler player;
		private boolean selected = false;

		public PositionPanel(Integer position) {
			this.position = position;
			initComponents();
		}

		public void setPlayer(Spieler player) {
			this.player = player;
			if (isOccupied()) {
				setBackground(COLOR_POS_OCCUPIED);
				setToolTipText(player.getName());
			} else {
				setBackground(COLOR_POS_DEFAULT);
				setToolTipText(null);
			}
		}

		public Spieler getPlayer() {
			return this.player;
		}

		public Integer getPosition() {
			return this.position;
		}

		public boolean isOccupied() {
			return this.player != null;
		}

		public void setSelected(boolean select) {
			this.selected = select;
			updateView();
		}

		public boolean isSelected() {
			return this.selected;
		}

		private void initComponents() {
			setBorder(BorderFactory.createLineBorder(Color.WHITE));
			Dimension size = new Dimension(14, 10);
			setMinimumSize(size);
			setMaximumSize(size);
			setPreferredSize(size);
			setBackground(COLOR_POS_DEFAULT);
		}

		private void updateView() {
			if (isSelected()) {
				setBackground(COLOR_POS_SELECTED);
			} else if (isOccupied()) {
				setBackground(COLOR_POS_OCCUPIED);
			} else {
				setBackground(COLOR_POS_DEFAULT);
			}
			
		}
	}

	private class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseEntered(MouseEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			PositionPanel posPanel = (PositionPanel) e.getSource();
			select(posPanel.getPosition());
		}
	}
}
