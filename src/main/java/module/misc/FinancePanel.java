// %1645621351:de.hattrickorganizer.gui.info%
package module.misc;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.misc.Economy;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;



/**
 * Displays the finances for the current and previous weeks
 */
final class FinancePanel extends JPanel {

	private static final long serialVersionUID = 5220006612961140628L;

    //~ Instance fields ----------------------------------------------------------------------------
	private final ColorLabelEntry salariesLabel = new ColorLabelEntry("");
    private final ColorLabelEntry totalCostLabel = new ColorLabelEntry("");
    private final ColorLabelEntry youthLabel = new ColorLabelEntry("");
    private final ColorLabelEntry otherCostsLabel = new ColorLabelEntry("");
    private final ColorLabelEntry stadiumLabel = new ColorLabelEntry("");
    private final ColorLabelEntry staffLabel = new ColorLabelEntry("");
    private final ColorLabelEntry interestExpensesLabel = new ColorLabelEntry("");
    private final ColorLabelEntry revenueTotalLabel = new ColorLabelEntry("");
    private final ColorLabelEntry otherIncomeLabel = new ColorLabelEntry("");
    private final ColorLabelEntry sponsorsLabel = new ColorLabelEntry("");
    private final ColorLabelEntry attendanceLabel = new ColorLabelEntry("");
    private final ColorLabelEntry profitLossLabel = new ColorLabelEntry("");
    private final ColorLabelEntry balanceLabel = new ColorLabelEntry("");
    private boolean currentFinance = true;

    final GridBagLayout layout = new GridBagLayout();
    final GridBagConstraints constraints = new GridBagConstraints();
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new FinancePanel object.
     *
     */
    protected FinancePanel(boolean currentFinance) {
        this.currentFinance = currentFinance;
        initComponents();
    }

    void setLabels() {
        final Economy finances = HOVerwaltung.instance().getModel().getEconomy();
        final float factor = core.model.UserParameter.instance().faktorGeld;

        if (currentFinance) {
            balanceLabel.setSpecialNumber((finances.getCash() / factor)
                                            + (finances.getExpectedWeeksTotal() / factor), true);
            attendanceLabel.setSpecialNumber(finances.getIncomeSpectators() / factor, true);
            stadiumLabel.setSpecialNumber(-finances.getCostsArena() / factor, true);
            sponsorsLabel.setSpecialNumber(finances.getIncomeSponsors() / factor, true);
            salariesLabel.setSpecialNumber(-finances.getCostsPlayers() / factor, true);
            otherIncomeLabel.setSpecialNumber(finances.getIncomeTemporary() / factor, true);
            otherCostsLabel.setSpecialNumber(-finances.getCostsTemporary() / factor, true);
            staffLabel.setSpecialNumber(-finances.getCostsStaff() / factor, true);
            youthLabel.setSpecialNumber(-finances.getCostsYouth() / factor, true);
            interestExpensesLabel.setSpecialNumber(-finances.getCostsFinancial() / factor, true);
            revenueTotalLabel.setSpecialNumber(finances.getIncomeSum() / factor, true);
            totalCostLabel.setSpecialNumber(-finances.getCostsSum() / factor, true);
            profitLossLabel.setSpecialNumber(finances.getExpectedWeeksTotal() / factor, true);
        } else {
            balanceLabel.setSpecialNumber(finances.getCash() / factor, true);
            attendanceLabel.setSpecialNumber(finances.getLastIncomeSpectators() / factor, true);
            stadiumLabel.setSpecialNumber(-finances.getLastCostsArena() / factor, true);
            sponsorsLabel.setSpecialNumber(finances.getLastIncomeSponsors() / factor, true);
            salariesLabel.setSpecialNumber(-finances.getLastCostsPlayers() / factor, true);
            otherIncomeLabel.setSpecialNumber(finances.getLastIncomeTemporary() / factor, true);
            otherCostsLabel.setSpecialNumber(-finances.getLastCostsTemporary() / factor, true);
            staffLabel.setSpecialNumber(-finances.getLastCostsStaff() / factor, true);
            youthLabel.setSpecialNumber(-finances.getLastCostsYouth() / factor, true);
            interestExpensesLabel.setSpecialNumber(-finances.getLastCostsFinancial() / factor, true);
            revenueTotalLabel.setSpecialNumber(finances.getLastIncomeSum() / factor, true);
            totalCostLabel.setSpecialNumber(-finances.getLastCostsSum() / factor, true);
            profitLossLabel.setSpecialNumber(finances.getLastWeeksTotal() / factor, true);
        }
    }

    private void initComponents() {

        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

        if (currentFinance) {
            setBorder(BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("DieseWoche")));
        } else {
            setBorder(BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("Vorwoche")));
        }

        JLabel label;

        setLayout(layout);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Kontostand"));
        add(label,balanceLabel.getComponent(false),0,0);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Einnahmen"),SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Ausgaben"),SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Zuschauer"));
        add(label,attendanceLabel.getComponent(false),0,2);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Stadion"));
        add(label,stadiumLabel.getComponent(false),2,2);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Sponsoren"));
        add(label,sponsorsLabel.getComponent(false),0,3);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Spielergehaelter"));
        add(label,salariesLabel.getComponent(false),2,3);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Sonstiges"));
        add(label,otherIncomeLabel.getComponent(false),0,4);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Sonstiges"));
        add(label,otherCostsLabel.getComponent(false),2,4);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Trainerstab"));
        add(label,staffLabel.getComponent(false),2,5);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Jugend"));
        add(label,youthLabel.getComponent(false),2,6);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Zinsaufwendungen"));
        add(label,interestExpensesLabel.getComponent(false),2,7);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Gesamteinnahmen"));
        add(label,revenueTotalLabel.getComponent(false),0,8);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Gesamtausgaben"));
        add(label,totalCostLabel.getComponent(false),2,8);

        if (currentFinance) {
            label = new JLabel(HOVerwaltung.instance().getLanguageString("ErwarteterGewinnVerlust"));
        } else {
            label = new JLabel(HOVerwaltung.instance().getLanguageString("VorwocheGewinnVerlust"));
        }

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.gridwidth = 3;
        layout.setConstraints(label, constraints);
        add(label);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 3;
        constraints.gridy = 9;
        constraints.gridwidth = 1;
        layout.setConstraints(profitLossLabel.getComponent(false), constraints);
        add(profitLossLabel.getComponent(false));
    }

    private void add(JLabel label,Component comp, int x, int y){
    	constraints.anchor = GridBagConstraints.WEST;
    	constraints.gridx = x;
    	constraints.gridy = y;
    	constraints.gridwidth = 1;
    	layout.setConstraints(label, constraints);
    	add(label);
    	constraints.anchor = GridBagConstraints.EAST;
    	constraints.gridx = x+1;
    	constraints.gridy = y;
    	constraints.gridwidth = 1;
    	layout.setConstraints(comp, constraints);
    	add(comp);
    }
}
