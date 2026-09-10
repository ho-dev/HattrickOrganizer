package tool.arenasizer;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.util.HOLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;


class ControlPanel extends JPanel implements ActionListener {

	private static final BigDecimal HUNDRED = new BigDecimal(100);
	private JButton m_jbUbernehmenGesamt = new JButton(TranslationFacility.tr("Calculate"));
	private JTextField m_jtfFans = new JTextField(7);
	private JTextField m_jtfGesamtgroesse = new JTextField(7);

	private JTextField vipField = new JTextField(7);
	private JTextField basicField = new JTextField(7);//Basic
	private JTextField terracesField = new JTextField(7);//Terraces
	private JTextField roofField = new JTextField(7);//Roof

	private JFormattedTextField vipPercentField = new JFormattedTextField(new DecimalFormat("###.# %"));
	private JFormattedTextField basicPercentField = new JFormattedTextField(new DecimalFormat("###.# %"));
	private JFormattedTextField terracesPercentField = new JFormattedTextField(new DecimalFormat("###.# %"));
	private JFormattedTextField roofPercentField = new JFormattedTextField(new DecimalFormat("###.# %"));

	private JFormattedTextField factorNormalField = new JFormattedTextField(new DecimalFormat("##"));
	private JLabel exampleLabel = new JLabel("");
	final GridBagLayout layout2 = new GridBagLayout();
	final GridBagConstraints constraints2 = new GridBagConstraints();

	ControlPanel() {
		initialize();
	}

	private void initialize() {
		constraints2.fill = GridBagConstraints.WEST;
		constraints2.weightx = 0.0;
		constraints2.weighty = 0.0;
		constraints2.insets = new Insets(3, 3, 3, 3);

		setLayout(layout2);
		JLabel label;

		label = new JLabel(TranslationFacility.tr("Gesamtgroesse"));
		addToLayout(label, 0, 0);
		setFieldProperties(m_jtfGesamtgroesse);
		// m_jtfGesamtgroesse.addFocusListener(this);
		addToLayout(m_jtfGesamtgroesse, 1, 0);


		label = new JLabel(TranslationFacility.tr("ls.club.arena.terraces"));
		addToLayout(label, 2, 0);
		setFieldProperties(terracesPercentField);
		addToLayout(terracesPercentField, 3, 0);

		label = new JLabel(TranslationFacility.tr("ls.club.arena.basicseating"));
		addToLayout(label, 4, 0);
		setFieldProperties(basicPercentField);
		addToLayout(basicPercentField, 5, 0);

		label = new JLabel(TranslationFacility.tr("ls.club.arena.seatsunderroof"));
		addToLayout(label, 6, 0);
		setFieldProperties(roofPercentField);
		addToLayout(roofPercentField, 7, 0);

		label = new JLabel(TranslationFacility.tr("ls.club.arena.seatsinvipboxes"));
		addToLayout(label, 8, 0);
		setFieldProperties(vipPercentField);
		addToLayout(vipPercentField, 9, 0);


		terracesField.setHorizontalAlignment(SwingConstants.RIGHT);
		addToLayout(terracesField, 3, 1);

		basicField.setHorizontalAlignment(SwingConstants.RIGHT);
		addToLayout(basicField, 5, 1);

		roofField.setHorizontalAlignment(SwingConstants.RIGHT);
		addToLayout(roofField, 7, 1);

		vipField.setHorizontalAlignment(SwingConstants.RIGHT);
		addToLayout(vipField, 9, 1);

		m_jbUbernehmenGesamt.setToolTipText(TranslationFacility.tr("Calculate"));
		m_jbUbernehmenGesamt.addActionListener(this);
		addToLayout(m_jbUbernehmenGesamt, 1, 1);


		label = new JLabel(TranslationFacility.tr("Fans"));
		addToLayout(label, 0, 2);

		m_jtfFans.setHorizontalAlignment(SwingConstants.RIGHT);
		addToLayout(m_jtfFans, 1, 2);

		label = new JLabel(TranslationFacility.tr("Durchschnitt"));
		addToLayout(label, 2, 2);
		setFieldProperties(factorNormalField);
		addToLayout(factorNormalField, 3, 2);

		constraints2.gridwidth = 2;
		addToLayout(exampleLabel, 4, 2);
		constraints2.gridwidth = 1;

		Dimension dim = vipField.getPreferredSize();
		vipPercentField.setPreferredSize(dim);
		basicPercentField.setPreferredSize(dim);
		terracesPercentField.setPreferredSize(dim);
		roofPercentField.setPreferredSize(dim);

		factorNormalField.setPreferredSize(dim);

		initStadium();
	}

	private void addToLayout(JComponent c, int x, int y) {
		constraints2.gridx = x;
		constraints2.gridy = y;
		layout2.setConstraints(c, constraints2);
		add(c);
	}

	private void setFieldProperties(JTextField txt) {
		txt.setHorizontalAlignment(SwingConstants.RIGHT);
		vipPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
	}

	//Init aus dem HRF
	private void initStadium() {
		//Nur, wenn es eine HRFArena ist
		Stadium m_clStadium = HOVerwaltung.instance().getModel().getStadium();
		int fans = HOVerwaltung.instance().getModel().getClub().getFans();

		m_jtfFans.setText(fans + "");
		terracesField.setText(m_clStadium.getTerraces() + "");
		basicField.setText(m_clStadium.getBasicSeating() + "");
		roofField.setText(m_clStadium.getUnderRoofSeating() + "");
		vipField.setText(m_clStadium.getVipBox() + "");
		m_jtfGesamtgroesse.setText(m_clStadium.getTotalSize() + "");

		terracesPercentField.setValue(ArenaSizer.TERRACES_PERCENT);
		basicPercentField.setValue(ArenaSizer.BASICS_PERCENT);
		roofPercentField.setValue(ArenaSizer.ROOF_PERCENT);
		vipPercentField.setValue(ArenaSizer.VIP_PERCENT);

		factorNormalField.setValue(ArenaSizer.SUPPORTER_NORMAL);
		exampleLabel.setText(createExampleLabelText(fans, ((Number)factorNormalField.getValue()).intValue()));
	}

    private static String createExampleLabelText(int fans, int factorNormal) {
        final int expectedNumberOfSpectators = fans * factorNormal;
        return "=> %d * %d = %d (%s)".formatted(fans, factorNormal, expectedNumberOfSpectators, TranslationFacility.tr("Zuschauer"));
    }

	Stadium getStadium() {
		int steh = 0;
		int sitz = 0;
		int ueber = 0;
		int loge = 0;

		try {
			steh = Integer.parseInt(terracesField.getText());
			sitz = Integer.parseInt(basicField.getText());
			ueber = Integer.parseInt(roofField.getText());
			loge = Integer.parseInt(vipField.getText());
		} catch (NumberFormatException e) {
			HOLogger.instance().log(getClass(), "Fehler: keine Zahl");
		}

		final Stadium stadium = new Stadium();
		stadium.setTerraces(steh);
		stadium.setBasicSeating(sitz);
		stadium.setUnderRoofSeating(ueber);
		stadium.setVipBox(loge);

		m_jtfGesamtgroesse.setText(stadium.getTotalSize() + "");
		return stadium;
	}

	int getSupporter() {
		return Integer.parseInt(m_jtfFans.getText());
	}

	int[] getModifiedSupporter() {
		int[] supporter = new int[3];
		supporter[0] = (((Number) factorNormalField.getValue()).intValue() + 5) * getSupporter();
		supporter[1] = ((Number) factorNormalField.getValue()).intValue() * getSupporter();
		supporter[2] = (((Number) factorNormalField.getValue()).intValue() - 5) * getSupporter();

		// the wrong place for this, only temp playCE
        final int fans = Integer.parseInt(m_jtfFans.getText());
        final int factorNormal = ((Number) factorNormalField.getValue()).intValue();
        exampleLabel.setText(createExampleLabelText(fans, factorNormal));

		return supporter;
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		BigDecimal groesse = BigDecimal.ZERO;

		try {
			groesse = new BigDecimal(Integer.parseInt(m_jtfGesamtgroesse.getText()));
		} catch (NumberFormatException e) {
			HOLogger.instance().log(getClass(), "Fehler: keine Zahl");
		}
		BigDecimal tPercent = new BigDecimal(terracesPercentField.getValue().toString());
		BigDecimal bPercent = new BigDecimal(basicPercentField.getValue().toString());
		BigDecimal rPercent = new BigDecimal(roofPercentField.getValue().toString());
		BigDecimal vPercent = new BigDecimal(vipPercentField.getValue().toString());
		BigDecimal sum = tPercent.add(bPercent).add(rPercent).add(vPercent);
		if (sum.multiply(HUNDRED).compareTo(HUNDRED) != 0) {
			JOptionPane.showMessageDialog(getTopLevelAncestor(), sum.multiply(HUNDRED).setScale(1) + " % <> " + HUNDRED + " %", TranslationFacility.tr("Fehler"), JOptionPane.ERROR_MESSAGE);
			terracesPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG));
			basicPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG));
			roofPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG));
			vipPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_ERROR_FG));
		} else {
			terracesPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
			basicPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
			roofPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));
			vipPercentField.setForeground(ThemeManager.getColor(HOColorName.LABEL_FG));

			terracesField.setText(tPercent.multiply(groesse).intValue() + "");
			basicField.setText(bPercent.multiply(groesse).intValue() + "");
			roofField.setText(rPercent.multiply(groesse).intValue() + "");
			vipField.setText(vPercent.multiply(groesse).intValue() + "");
			//In Tabelle übernehmen
			//m_jbUbernehmen.doClick();
			//}
		}
	}

}
