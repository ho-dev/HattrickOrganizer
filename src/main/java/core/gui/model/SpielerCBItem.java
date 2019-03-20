// %1287661405:de.hattrickorganizer.gui.model%
package core.gui.model;

import core.datatype.ComboItem;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.SpielerLabelEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.model.HOVerwaltung;
import core.model.player.Player;

import java.awt.Component;

import javax.swing.JList;

public class SpielerCBItem implements Comparable<SpielerCBItem>, ComboItem {

	public static javax.swing.JLabel m_jlLeer = new javax.swing.JLabel(" ");
	public SpielerLabelEntry m_clEntry = new SpielerLabelEntry(null, null, 0f, true, true);
	private Player m_clPlayer;
	private String m_sText;
	private float m_fPositionsBewertung;

	/**
	 * Creates a new SpielerCBItem object.
	 * 
	 */
	public SpielerCBItem(String text, float poswert, Player player) {
		m_sText = text;
		m_clPlayer = player;
		m_fPositionsBewertung = poswert;
		m_clEntry = new SpielerLabelEntry(null, null, 0f, true, true);
	}

	public SpielerCBItem(String text, float poswert, Player player, boolean useCustomText) {
		m_sText = text;
		m_clPlayer = player;
		m_fPositionsBewertung = poswert;
		if (useCustomText == true) {
			m_clEntry = new SpielerLabelEntry(null, null, 0f, true, true, true, text);
		} else {
			m_clEntry = new SpielerLabelEntry(null, null, 0f, true, true);
		}
	}

	public final Component getListCellRendererComponent(JList jList, int index, boolean isSelected) {
		final Player player = getSpieler();

		// Kann für Tempspieler < 0 sein && player.getSpielerID () > 0 )
		if (player != null) {
			m_clEntry.updateComponent(player, HOVerwaltung.instance().getModel().getLineup()
					.getPositionBySpielerId(player.getSpielerID()), getPositionsBewertung(),
					m_sText);

			return m_clEntry.getComponent(isSelected);
		} else {
			m_jlLeer.setOpaque(true);
			m_jlLeer.setBackground(isSelected ? HODefaultTableCellRenderer.SELECTION_BG
					: ColorLabelEntry.BG_STANDARD);
			return m_jlLeer;
		}
	}

	public final void setPositionsBewertung(float m_sPositionsBewertung) {
		this.m_fPositionsBewertung = m_sPositionsBewertung;
	}

	public final float getPositionsBewertung() {
		return m_fPositionsBewertung;
	}

	public final void setSpieler(Player player) {
		m_clPlayer = player;
	}

	public final Player getSpieler() {
		return m_clPlayer;
	}

	public final void setText(String text) {
		m_sText = text;
	}

	@Override
	public final String getText() {
		return m_sText;
	}

	public final void setValues(String text, float poswert, Player player) {
		m_sText = text;
		m_clPlayer = player;
		m_fPositionsBewertung = poswert;
	}

	@Override
	public final int compareTo(SpielerCBItem obj) {

		final SpielerCBItem cbitem = obj;

		if ((cbitem.getSpieler() != null) && (getSpieler() != null)) {
			if (getPositionsBewertung() > cbitem.getPositionsBewertung()) {
				return -1;
			} else if (getPositionsBewertung() < cbitem.getPositionsBewertung()) {
				return 1;
			} else {
				return getSpieler().getName().compareTo(cbitem.getSpieler().getName());
			}
		} else if (cbitem.getSpieler() == null) {
			return -1;
		} else {
			return 1;
		}

	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof SpielerCBItem) {
			final SpielerCBItem temp = (SpielerCBItem) obj;

			if ((this.getSpieler() != null) && (temp.getSpieler() != null)) {
				if (this.getSpieler().getSpielerID() == temp.getSpieler().getSpielerID()) {
					return true;
				}
			} else if ((this.getSpieler() == null) && (temp.getSpieler() == null)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public final String toString() {
		return m_sText;
	}

	@Override
	public int getId() {
		if (this.m_clPlayer != null) {
			return this.m_clPlayer.getSpielerID();
		}
		return -1;
	}
}
