// %1814492999:de.hattrickorganizer.gui.lineup%
/*
 * AufstellungsRatingPanel.java
 *
 * Created on 23. November 2004, 09:11
 */
package module.lineup;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.RasenPanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.util.Helper;
import module.pluginFeedback.FeedbackPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;



/**
 * Zeigt das Rating für eine Aufstellung an
 *
 * @author Pirania
 */
final class AufstellungsRatingPanel extends RasenPanel {

	private static final long serialVersionUID = -8938268226990652913L;

    //~ Static fields/initializers -----------------------------------------------------------------
    public static final boolean REIHENFOLGE_STURM2VERTEIDIGUNG = false;

    //~ Instance fields ----------------------------------------------------------------------------

    double bottomcenter;
    double bottomleft;
    double bottomright;
    double middle;
    double topcenter;
    double topleft;
    double topright;

    private ColorLabelEntry m_clBottomCenterCompare = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.CENTER);
	private ColorLabelEntry m_clBottomCenterMain = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.RIGHT);
	private ColorLabelEntry m_clBottomLeftCompare = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.CENTER);
	private ColorLabelEntry m_clBottomLeftMain = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.RIGHT);
	private ColorLabelEntry m_clBottomRightCompare = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.CENTER);
	private ColorLabelEntry m_clBottomRightMain = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.RIGHT);
	private ColorLabelEntry m_clMiddleCompare = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.CENTER);
	private ColorLabelEntry m_clMiddleMain = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.RIGHT);
	private ColorLabelEntry m_clTopCenterCompare = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.CENTER);
	private ColorLabelEntry m_clTopCenterMain = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.RIGHT);
	private ColorLabelEntry m_clTopLeftCompare = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.CENTER);
	private ColorLabelEntry m_clTopLeftMain = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.RIGHT);
	private ColorLabelEntry m_clTopRightCompare = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.CENTER);
	private ColorLabelEntry m_clTopRightMain = new ColorLabelEntry("",
			Color.BLACK, Color.WHITE, SwingConstants.RIGHT);
    private Dimension GROESSE = new Dimension(Helper.calcCellWidth(80), Helper.calcCellWidth(25));
    private JLabel m_clBottomCenterText = new JLabel("", SwingConstants.LEFT);
    private JLabel m_clBottomLeftText = new JLabel("", SwingConstants.LEFT);
    private JLabel m_clBottomRightText = new JLabel("", SwingConstants.LEFT);
    private JLabel m_clMiddleText = new JLabel("", SwingConstants.LEFT);
    private JLabel m_clTopCenterText = new JLabel("", SwingConstants.LEFT);
    private JLabel m_clTopLeftText = new JLabel("", SwingConstants.LEFT);
    private JLabel m_clTopRightText = new JLabel("", SwingConstants.LEFT);
    private JPanel m_clBottomCenterPanel = new JPanel(new BorderLayout());
    private JPanel m_clBottomLeftPanel = new JPanel(new BorderLayout());
    private JPanel m_clBottomRightPanel = new JPanel(new BorderLayout());
    private JPanel m_clMiddlePanel = new JPanel(new BorderLayout());
    private JPanel m_clTopCenterPanel = new JPanel(new BorderLayout());
    private JPanel m_clTopLeftPanel = new JPanel(new BorderLayout());
    private JPanel m_clTopRightPanel = new JPanel(new BorderLayout());
    private NumberFormat m_clFormat;
    private boolean m_bReihenfolge = REIHENFOLGE_STURM2VERTEIDIGUNG;
    private final JButton copyButton = new JButton();
    private final JButton feedbackButton = new JButton();

    // ~ Constructors
	// -------------------------------------------------------------------------------

    /**
     * Creates a new instance of AufstellungsRatingPanel
     */
    protected AufstellungsRatingPanel() {
        initComponents();

        if (core.model.UserParameter.instance().anzahlNachkommastellen == 1) {
            m_clFormat = Helper.DEFAULTDEZIMALFORMAT;
        } else {
            m_clFormat = Helper.DEZIMALFORMAT_2STELLEN;
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Clear all fields.
     */
    public void clear() {
        m_clTopLeftText.setText("");
        m_clTopLeftMain.clear();
        m_clTopLeftCompare.clear();
        m_clTopCenterText.setText("");
        m_clTopCenterMain.clear();
        m_clTopCenterCompare.clear();
        m_clTopRightText.setText("");
        m_clTopRightMain.clear();
        m_clTopRightCompare.clear();
        m_clMiddleText.setText("");
        m_clMiddleMain.clear();
        m_clMiddleCompare.clear();
        m_clBottomLeftText.setText("");
        m_clBottomLeftMain.clear();
        m_clBottomLeftCompare.clear();
        m_clBottomCenterText.setText("");
        m_clBottomCenterMain.clear();
        m_clBottomCenterCompare.clear();
        m_clBottomRightText.setText("");
        m_clBottomRightMain.clear();
        m_clBottomRightCompare.clear();
    }

    protected void setBottomCenter(double value) {
        m_clBottomCenterMain.setText(m_clFormat.format(value));
        m_clBottomCenterCompare.setSpecialNumber((float) (value - bottomcenter), false);
        bottomcenter = value;
    }

    protected void setBottomCenterText(String text) {
        m_clBottomCenterText.setText(text);
    }

    protected void setBottomLeft(double value) {
        m_clBottomLeftMain.setText(m_clFormat.format(value));
        m_clBottomLeftCompare.setSpecialNumber((float) (value - bottomleft), false);
        bottomleft = value;
    }

    protected void setBottomLeftText(String text) {
        m_clBottomLeftText.setText(text);
    }

    protected void setBottomRight(double value) {
        m_clBottomRightMain.setText(m_clFormat.format(value));
        m_clBottomRightCompare.setSpecialNumber((float) (value - bottomright), false);
        bottomright = value;
    }

    protected void setBottomRightText(String text) {
        m_clBottomRightText.setText(text);
    }

    protected void setMiddle(double value) {
        m_clMiddleMain.setText(m_clFormat.format(value));
        m_clMiddleCompare.setSpecialNumber((float) (value - middle), false);
        middle = value;
    }

    protected void setMiddleText(String text) {
        m_clMiddleText.setText(text);
    }

    protected void setReihenfolge(boolean reihenfolge) {
        m_bReihenfolge = REIHENFOLGE_STURM2VERTEIDIGUNG;

        initToolTips();
    }

    protected void setTopCenter(double value) {
        m_clTopCenterMain.setText(m_clFormat.format(value));
        m_clTopCenterCompare.setSpecialNumber((float) (value - topcenter), false);
        topcenter = value;
    }

    protected void setTopCenterText(String text) {
        m_clTopCenterText.setText(text);
    }

    protected void setTopLeft(double value) {
        m_clTopLeftMain.setText(m_clFormat.format(value));
        m_clTopLeftCompare.setSpecialNumber((float) (value - topleft), false);
        topleft = value;
    }

    ////////////////////////////////////////////////////////////////////////////
    protected void setTopLeftText(String text) {
        m_clTopLeftText.setText(text);
    }

    protected void setTopRight(double value) {
        m_clTopRightMain.setText(m_clFormat.format(value));
        m_clTopRightCompare.setSpecialNumber((float) (value - topright), false);
        topright = value;
    }

    protected void setTopRightText(String text) {
        m_clTopRightText.setText(text);
    }

    protected void calcColorBorders() {
        final int faktor = 60;
        double temp = 0d;
        Color tempcolor = null;
        final double durchschnitt = (topleft + topcenter + topright + middle + bottomleft
                                    + bottomcenter + bottomright) / 7d;

        //Topleft
        temp = topleft - durchschnitt;

        if (temp < 0) {
            tempcolor = new Color(Math.min(255, (int) (Math.abs(temp) * faktor)), 0, 0);
        } else {
            tempcolor = new Color(0, Math.min(255, (int) (temp * faktor)), 0);
        }

        m_clTopLeftPanel.setBorder(BorderFactory.createLineBorder(tempcolor, 2));

        //Topcenter
        temp = topcenter - durchschnitt;

        if (temp < 0) {
            tempcolor = new Color(Math.min(255, (int) (Math.abs(temp) * faktor)), 0, 0);
        } else {
            tempcolor = new Color(0, Math.min(255, (int) (temp * faktor)), 0);
        }

        m_clTopCenterPanel.setBorder(BorderFactory.createLineBorder(tempcolor, 2));

        //TopRight
        temp = topright - durchschnitt;

        if (temp < 0) {
            tempcolor = new Color(Math.min(255, (int) (Math.abs(temp) * faktor)), 0, 0);
        } else {
            tempcolor = new Color(0, Math.min(255, (int) (temp * faktor)), 0);
        }

        m_clTopRightPanel.setBorder(BorderFactory.createLineBorder(tempcolor, 2));

        //Middel
        temp = middle - durchschnitt;

        if (temp < 0) {
            tempcolor = new Color(Math.min(255, (int) (Math.abs(temp) * faktor)), 0, 0);
        } else {
            tempcolor = new Color(0, Math.min(255, (int) (temp * faktor)), 0);
        }

        m_clMiddlePanel.setBorder(BorderFactory.createLineBorder(tempcolor, 2));

        //Bottomleft
        temp = bottomleft - durchschnitt;

        if (temp < 0) {
            tempcolor = new Color(Math.min(255, (int) (Math.abs(temp) * faktor)), 0, 0);
        } else {
            tempcolor = new Color(0, Math.min(255, (int) (temp * faktor)), 0);
        }

        m_clBottomLeftPanel.setBorder(BorderFactory.createLineBorder(tempcolor, 2));

        //BottomCenter
        temp = bottomcenter - durchschnitt;

        if (temp < 0) {
            tempcolor = new Color(Math.min(255, (int) (Math.abs(temp) * faktor)), 0, 0);
        } else {
            tempcolor = new Color(0, Math.min(255, (int) (temp * faktor)), 0);
        }

        m_clBottomCenterPanel.setBorder(BorderFactory.createLineBorder(tempcolor, 2));

        //Bottomricht
        temp = bottomright - durchschnitt;

        if (temp < 0) {
            tempcolor = new Color(Math.min(255, (int) (Math.abs(temp) * faktor)), 0, 0);
        } else {
            tempcolor = new Color(0, Math.min(255, (int) (temp * faktor)), 0);
        }

        m_clBottomRightPanel.setBorder(BorderFactory.createLineBorder(tempcolor, 2));
    }

    /**
     * Initialize GUI components.
     */
    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(1, 1, 1, 1);

        setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));
        setLayout(layout);

        JComponent tempcomponent;
        JPanel temppanel;
        JPanel mainpanel;
        JPanel innerpanel;
        JPanel subpanel;

        GridBagLayout sublayout = new GridBagLayout();
        GridBagConstraints subconstraints = new GridBagConstraints();
        subconstraints.anchor = GridBagConstraints.CENTER;
        subconstraints.fill = GridBagConstraints.HORIZONTAL;
        subconstraints.weightx = 1.0;
        subconstraints.weighty = 0.0;
        subconstraints.insets = new Insets(1, 1, 1, 1);
        subpanel = new JPanel(sublayout);
        subpanel.setOpaque(false);

        //Platzhalter
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10),
                                                     Helper.calcCellWidth(2)));
        subconstraints.gridx = 1;
        subconstraints.gridy = 1;
        subconstraints.gridwidth = 1;

        sublayout.setConstraints(tempcomponent, subconstraints);
        subpanel.add(tempcomponent);

        //Top Center
        temppanel = new JPanel(new GridLayout(1, 2));
        temppanel.setOpaque(true);
        m_clTopCenterMain.setFontStyle(Font.BOLD);
        tempcomponent = m_clTopCenterMain.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);
        tempcomponent = m_clTopCenterCompare.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);

        innerpanel = new JPanel(new GridLayout(2, 1));
        innerpanel.setBackground(Color.white);
        innerpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        innerpanel.add(m_clTopCenterText);
        innerpanel.add(temppanel);

        m_clTopCenterPanel.setBackground(Color.WHITE);
        m_clTopCenterText.setFont(m_clTopCenterText.getFont().deriveFont(m_clTopCenterText.getFont().getSize2D() - 1f));
        m_clTopCenterText.setOpaque(true);
        m_clTopCenterPanel.add(innerpanel, BorderLayout.CENTER);
        m_clTopCenterPanel.setPreferredSize(GROESSE);

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.white);
        mainpanel.add(m_clTopCenterPanel, BorderLayout.CENTER);
        mainpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        subconstraints.gridx = 2;
        subconstraints.gridy = 1;
        subconstraints.gridwidth = 3;
        sublayout.setConstraints(mainpanel, subconstraints);
        subpanel.add(mainpanel);

        //Platzhalter
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10),
                                                     Helper.calcCellWidth(2)));
        subconstraints.gridx = 5;
        subconstraints.gridy = 1;
        subconstraints.gridwidth = 1;

        sublayout.setConstraints(tempcomponent, subconstraints);
        subpanel.add(tempcomponent);

        constraints.gridx = 0;
        constraints.gridy = 0;
        layout.setConstraints(subpanel, constraints);
        add(subpanel);

        ////////////////////////////////////////////////////////////////////////
        sublayout = new GridBagLayout();
        subconstraints = new GridBagConstraints();
        subconstraints.anchor = GridBagConstraints.CENTER;
        subconstraints.fill = GridBagConstraints.HORIZONTAL;
        subconstraints.weightx = 1.0;
        subconstraints.weighty = 0.0;
        subconstraints.insets = new Insets(1, 1, 1, 1);
        subpanel = new JPanel(sublayout);
        subpanel.setOpaque(false);

        //Top Left
        temppanel = new JPanel(new GridLayout(1, 2));
        temppanel.setOpaque(true);
        m_clTopLeftMain.setFontStyle(Font.BOLD);
        tempcomponent = m_clTopLeftMain.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);
        tempcomponent = m_clTopLeftCompare.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);

        innerpanel = new JPanel(new GridLayout(2, 1));
        innerpanel.setBackground(Color.white);
        innerpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        innerpanel.add(m_clTopLeftText);
        innerpanel.add(temppanel);

        m_clTopLeftPanel.setBackground(Color.WHITE);
        m_clTopLeftText.setFont(m_clTopLeftText.getFont().deriveFont(m_clTopLeftText.getFont().getSize2D() - 1f));
        m_clTopLeftText.setOpaque(true);
        m_clTopLeftPanel.add(innerpanel, BorderLayout.CENTER);
        m_clTopLeftPanel.setPreferredSize(GROESSE);

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.white);
        mainpanel.add(m_clTopLeftPanel, BorderLayout.CENTER);
        mainpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        subconstraints.gridx = 1;
        subconstraints.gridy = 2;
        subconstraints.gridwidth = 2;
        sublayout.setConstraints(mainpanel, subconstraints);
        subpanel.add(mainpanel);

        //Platzhalter
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10),
                                                     Helper.calcCellWidth(2)));
        subconstraints.gridx = 3;
        subconstraints.gridy = 2;
        subconstraints.gridwidth = 1;
        subconstraints.weightx = 0.0;

        sublayout.setConstraints(tempcomponent, subconstraints);
        subpanel.add(tempcomponent);

        //Top Right
        temppanel = new JPanel(new GridLayout(1, 2));
        temppanel.setOpaque(true);
        m_clTopRightMain.setFontStyle(Font.BOLD);
        tempcomponent = m_clTopRightMain.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);
        tempcomponent = m_clTopRightCompare.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);

        innerpanel = new JPanel(new GridLayout(2, 1));
        innerpanel.setBackground(Color.white);
        innerpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        innerpanel.add(m_clTopRightText);
        innerpanel.add(temppanel);

        m_clTopRightPanel.setBackground(Color.WHITE);
        m_clTopRightText.setFont(m_clTopRightText.getFont().deriveFont(m_clTopRightText.getFont().getSize2D() - 1f));
        m_clTopRightText.setOpaque(true);
        m_clTopRightPanel.add(innerpanel, BorderLayout.CENTER);
        m_clTopRightPanel.setPreferredSize(GROESSE);

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.white);
        mainpanel.add(m_clTopRightPanel, BorderLayout.CENTER);
        mainpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        subconstraints.gridx = 4;
        subconstraints.gridy = 2;
        subconstraints.gridwidth = 2;
        subconstraints.weightx = 1.0;
        sublayout.setConstraints(mainpanel, subconstraints);
        subpanel.add(mainpanel);

        constraints.gridx = 0;
        constraints.gridy = 1;
        layout.setConstraints(subpanel, constraints);
        add(subpanel);

        ////////////////////////////////////////////////////////////////////////
        //Platzhalter
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10),
                                                     Helper.calcCellWidth(2)));
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.0;

        layout.setConstraints(tempcomponent, constraints);
        add(tempcomponent);

        ////////////////////////////////////////////////////////////////////////
        sublayout = new GridBagLayout();
        subconstraints = new GridBagConstraints();
        subconstraints.anchor = GridBagConstraints.CENTER;
        subconstraints.fill = GridBagConstraints.HORIZONTAL;
        subconstraints.weightx = 1.0;
        subconstraints.weighty = 0.0;
        subconstraints.insets = new Insets(1, 1, 1, 1);
        subpanel = new JPanel(sublayout);
        subpanel.setOpaque(false);

        //Platzhalter
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10),
                                                     Helper.calcCellWidth(2)));
        subconstraints.gridx = 1;
        subconstraints.gridy = 4;
        subconstraints.gridwidth = 1;

        sublayout.setConstraints(tempcomponent, subconstraints);
        subpanel.add(tempcomponent);

        //Middle
        temppanel = new JPanel(new GridLayout(1, 2));
        temppanel.setOpaque(true);
        m_clMiddleMain.setFontStyle(Font.BOLD);
        tempcomponent = m_clMiddleMain.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);
        tempcomponent = m_clMiddleCompare.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);

        innerpanel = new JPanel(new GridLayout(2, 1));
        innerpanel.setBackground(Color.white);
        innerpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        innerpanel.add(m_clMiddleText);
        innerpanel.add(temppanel);

        m_clMiddlePanel.setBackground(Color.WHITE);
        m_clMiddleText.setFont(m_clMiddleText.getFont().deriveFont(m_clMiddleText.getFont().getSize2D() - 1f));
        m_clMiddleText.setOpaque(true);
        m_clMiddlePanel.add(innerpanel, BorderLayout.CENTER);
        m_clMiddlePanel.setPreferredSize(GROESSE);

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.white);
        mainpanel.add(m_clMiddlePanel, BorderLayout.CENTER);
        mainpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        subconstraints.gridx = 2;
        subconstraints.gridy = 4;
        subconstraints.gridwidth = 3;
        sublayout.setConstraints(mainpanel, subconstraints);
        subpanel.add(mainpanel);

        //Platzhalter
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10),
                                                     Helper.calcCellWidth(2)));
        subconstraints.gridx = 5;
        subconstraints.gridy = 4;
        subconstraints.gridwidth = 1;

        sublayout.setConstraints(tempcomponent, subconstraints);
        subpanel.add(tempcomponent);

        constraints.gridx = 0;
        constraints.gridy = 3;
        layout.setConstraints(subpanel, constraints);
        add(subpanel);

        ////////////////////////////////////////////////////////////////////////
        //Platzhalter
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10),
                                                     Helper.calcCellWidth(2)));
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 0.0;

        layout.setConstraints(tempcomponent, constraints);
        add(tempcomponent);

        ////////////////////////////////////////////////////////////////////////
        sublayout = new GridBagLayout();
        subconstraints = new GridBagConstraints();
        subconstraints.anchor = GridBagConstraints.CENTER;
        subconstraints.fill = GridBagConstraints.HORIZONTAL;
        subconstraints.weightx = 1.0;
        subconstraints.weighty = 0.0;
        subconstraints.insets = new Insets(1, 1, 1, 1);
        subpanel = new JPanel(sublayout);
        subpanel.setOpaque(false);

        //Bottom Left
        temppanel = new JPanel(new GridLayout(1, 2));
        temppanel.setOpaque(true);
        m_clBottomLeftMain.setFontStyle(Font.BOLD);
        tempcomponent = m_clBottomLeftMain.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);
        tempcomponent = m_clBottomLeftCompare.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);

        innerpanel = new JPanel(new GridLayout(2, 1));
        innerpanel.setBackground(Color.white);
        innerpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        innerpanel.add(m_clBottomLeftText);
        innerpanel.add(temppanel);

        m_clBottomLeftPanel.setBackground(Color.WHITE);
        m_clBottomLeftText.setFont(m_clBottomLeftText.getFont().deriveFont(m_clBottomLeftText.getFont().getSize2D() - 1f));
        m_clBottomLeftText.setOpaque(true);
        m_clBottomLeftPanel.add(innerpanel, BorderLayout.CENTER);
        m_clBottomLeftPanel.setPreferredSize(GROESSE);

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.white);
        mainpanel.add(m_clBottomLeftPanel, BorderLayout.CENTER);
        mainpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        subconstraints.gridx = 1;
        subconstraints.gridy = 6;
        subconstraints.gridwidth = 2;
        subconstraints.weightx = 1.0;
        sublayout.setConstraints(mainpanel, subconstraints);
        subpanel.add(mainpanel);

        //Platzhalter
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10),
                                                     Helper.calcCellWidth(2)));
        subconstraints.gridx = 3;
        subconstraints.gridy = 6;
        subconstraints.gridwidth = 1;
        subconstraints.weightx = 0.0;

        sublayout.setConstraints(tempcomponent, subconstraints);
        subpanel.add(tempcomponent);

        //Bottom Right
        temppanel = new JPanel(new GridLayout(1, 2));
        temppanel.setOpaque(true);
        m_clBottomRightMain.setFontStyle(Font.BOLD);
        tempcomponent = m_clBottomRightMain.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);
        tempcomponent = m_clBottomRightCompare.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);

        innerpanel = new JPanel(new GridLayout(2, 1));
        innerpanel.setBackground(Color.white);
        innerpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        innerpanel.add(m_clBottomRightText);
        innerpanel.add(temppanel);

        m_clBottomRightPanel.setBackground(Color.WHITE);
        m_clBottomRightText.setFont(m_clBottomRightText.getFont().deriveFont(m_clBottomRightText.getFont().getSize2D() - 1f));
        m_clBottomRightText.setOpaque(true);
        m_clBottomRightPanel.add(innerpanel, BorderLayout.CENTER);
        m_clBottomRightPanel.setPreferredSize(GROESSE);

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.white);
        mainpanel.add(m_clBottomRightPanel, BorderLayout.CENTER);
        mainpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        subconstraints.gridx = 4;
        subconstraints.gridy = 6;
        subconstraints.gridwidth = 2;
        subconstraints.weightx = 1.0;
        sublayout.setConstraints(mainpanel, subconstraints);
        subpanel.add(mainpanel);

        constraints.gridx = 0;
        constraints.gridy = 5;
        layout.setConstraints(subpanel, constraints);
        add(subpanel);

        ////========================= BOTTOM ROW ===============================
        sublayout = new GridBagLayout();
        subconstraints = new GridBagConstraints();
        subconstraints.anchor = GridBagConstraints.CENTER;
        subconstraints.fill = GridBagConstraints.HORIZONTAL;
        subconstraints.weightx = 1.0;
        subconstraints.weighty = 0.0;
        subconstraints.insets = new Insets(1, 1, 1, 1);
        subpanel = new JPanel(sublayout);
        subpanel.setOpaque(false);


        //left bottom spacer
        tempcomponent = new JLabel();
        tempcomponent.setFont(tempcomponent.getFont().deriveFont(tempcomponent.getFont().getSize2D() - 2f));
        tempcomponent.setPreferredSize(new Dimension(Helper.calcCellWidth(10), 1));
        subconstraints.gridx = 1;
        subconstraints.gridy = 7;
        subconstraints.gridwidth = 1;
        sublayout.setConstraints(tempcomponent, subconstraints);
        subpanel.add(tempcomponent);


        //Bottom Center
        temppanel = new JPanel(new GridLayout(1, 2));
        temppanel.setOpaque(true);
        m_clBottomCenterMain.setFontStyle(Font.BOLD);
        tempcomponent = m_clBottomCenterMain.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);
        tempcomponent = m_clBottomCenterCompare.getComponent(false);
        tempcomponent.setOpaque(true);
        temppanel.add(tempcomponent);

        innerpanel = new JPanel(new GridLayout(2, 1));
        innerpanel.setBackground(Color.white);
        innerpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        innerpanel.add(m_clBottomCenterText);
        innerpanel.add(temppanel);

        m_clBottomCenterPanel.setBackground(Color.WHITE);
        m_clBottomCenterText.setFont(m_clBottomCenterText.getFont().deriveFont(m_clBottomCenterText.getFont().getSize2D() - 1f));
        m_clBottomCenterText.setOpaque(true);
        m_clBottomCenterPanel.add(innerpanel, BorderLayout.CENTER);
        m_clBottomCenterPanel.setPreferredSize(GROESSE);

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.white);
        mainpanel.add(m_clBottomCenterPanel, BorderLayout.CENTER);
        mainpanel.setBorder(BorderFactory.createLineBorder(Color.white));
        subconstraints.gridx = 2;
        subconstraints.gridy = 7;
        subconstraints.gridwidth = 3;
        sublayout.setConstraints(mainpanel, subconstraints);
        subpanel.add(mainpanel);


        //--- BOTTOM RIGHT:   Copy Rating and Feedback button
        temppanel = new JPanel(new BorderLayout());
        temppanel.setOpaque(false);
        JPanel subButtonPanel = new JPanel();
        subButtonPanel.setOpaque(false);

        feedbackButton.setToolTipText(HOVerwaltung.instance().getLanguageString("Lineup.Feedback.ToolTip"));
        feedbackButton.setIcon(ThemeManager.getIcon(HOIconName.FEEDBACK));
        feedbackButton.addActionListener(e -> new FeedbackPanel());
        feedbackButton.setPreferredSize(new Dimension(24, 24));
        feedbackButton.setMaximumSize(new Dimension(24, 24));
        feedbackButton.setOpaque(false);
        feedbackButton.setContentAreaFilled(false);
        feedbackButton.setBorderPainted(false);
        subButtonPanel.add(feedbackButton);

        copyButton.setToolTipText(HOVerwaltung.instance().getLanguageString("Lineup.CopyRatings.ToolTip"));
        copyButton.setIcon(ThemeManager.getIcon(HOIconName.INFO));
        copyButton.addActionListener(new CopyListener(this));
        copyButton.setPreferredSize(new Dimension(18, 18));
        copyButton.setMaximumSize(new Dimension(18, 18));
        copyButton.setOpaque(false);
        copyButton.setContentAreaFilled(false);
        copyButton.setBorderPainted(false);
        subButtonPanel.add(copyButton);

        temppanel.add(subButtonPanel, BorderLayout.CENTER);

        subconstraints.gridx = 5;
        subconstraints.gridy = 7;
        subconstraints.gridwidth = 1;
        sublayout.setConstraints(temppanel, subconstraints);
        subpanel.add(temppanel);

        constraints.gridx = 0;
        constraints.gridy = 6;
        layout.setConstraints(subpanel, constraints);
        add(subpanel);


        ////////////////////////////////////////////////////////////////////////
        initToolTips();

        //Alle zahlen auf 0, Default ist -oo
        m_clTopLeftCompare.setSpecialNumber(0f, false);
        m_clTopCenterCompare.setSpecialNumber(0f, false);
        m_clTopRightCompare.setSpecialNumber(0f, false);
        m_clMiddleCompare.setSpecialNumber(0f, false);
        m_clBottomLeftCompare.setSpecialNumber(0f, false);
        m_clBottomCenterCompare.setSpecialNumber(0f, false);
        m_clBottomRightCompare.setSpecialNumber(0f, false);
    }

    /**
     * Initialize all tool tips.
     */
    private void initToolTips() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
            m_clTopLeftText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_clTopLeftMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_clTopLeftCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_clTopCenterText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_clTopCenterMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_clTopCenterCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_clTopRightText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_clTopRightMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_clTopRightCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_clMiddleText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_clMiddleMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_clMiddleCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_clBottomLeftText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_clBottomLeftMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_clBottomLeftCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_clBottomCenterText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_clBottomCenterMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_clBottomCenterCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_clBottomRightText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_clBottomRightMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_clBottomRightCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
        } else {
            m_clTopLeftText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_clTopLeftMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_clTopLeftCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"));
            m_clTopCenterText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_clTopCenterMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_clTopCenterCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"));
            m_clTopRightText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_clTopRightMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_clTopRightCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"));
            m_clMiddleText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_clMiddleMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_clMiddleCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"));
            m_clBottomLeftText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_clBottomLeftMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_clBottomLeftCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"));
            m_clBottomCenterText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_clBottomCenterMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_clBottomCenterCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"));
            m_clBottomRightText.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_clBottomRightMain.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
            m_clBottomRightCompare.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"));
        }
    }

    String getMidfieldRating() {
    	return m_clFormat.format(middle);
    }
    String getLeftDefenseRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
        	return m_clFormat.format(topright);
        } else {
        	return m_clFormat.format(bottomleft);
        }
    }
    String getCentralDefenseRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
        	return m_clFormat.format(topcenter);
        } else {
        	return m_clFormat.format(bottomcenter);
        }
    }
    String getRightDefenseRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
        	return m_clFormat.format(topleft);
        } else {
        	return m_clFormat.format(bottomright);
        }
    }

    String getLeftAttackRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
        	return m_clFormat.format(bottomright);
        } else {
        	return m_clFormat.format(topleft);
        }
    }
    String getCentralAttackRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
        	return m_clFormat.format(bottomcenter);
        } else {
        	return m_clFormat.format(topcenter);
        }
    }
    String getRightAttackRating() {
        if (m_bReihenfolge == REIHENFOLGE_STURM2VERTEIDIGUNG) {
        	return m_clFormat.format(bottomleft);
        } else {
        	return m_clFormat.format(topright);
        }
    }

}
