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

import static core.util.Helper.getTranslation;


/**
 * Displays the finances for the current and previous weeks
 */
final class FinancePanel extends JPanel {

    //~ Instance fields ----------------------------------------------------------------------------
	private final ColorLabelEntry salariesLabel = new ColorLabelEntry("");
    private final ColorLabelEntry totalCostLabel = new ColorLabelEntry("");
    private final ColorLabelEntry youthLabel = new ColorLabelEntry("");
    private final ColorLabelEntry newSigningsLabel = new ColorLabelEntry("");
    private final ColorLabelEntry otherCostsLabel = new ColorLabelEntry("");
    private final ColorLabelEntry stadiumMaintenanceLabel = new ColorLabelEntry("");
    private final ColorLabelEntry stadiumBuildingLabel = new ColorLabelEntry("");
    private final ColorLabelEntry staffLabel = new ColorLabelEntry("");
    private final ColorLabelEntry interestExpensesLabel = new ColorLabelEntry("");
    private final ColorLabelEntry revenueTotalLabel = new ColorLabelEntry("");
    private final ColorLabelEntry otherIncomeLabel = new ColorLabelEntry("");
    private final ColorLabelEntry sponsorsLabel = new ColorLabelEntry("");
    private final ColorLabelEntry sponsorsBonusLabel = new ColorLabelEntry("");
    private final ColorLabelEntry playerSalesIncomeLabel = new ColorLabelEntry("");
    private final ColorLabelEntry commissionIncomeLabel = new ColorLabelEntry("");
    private final ColorLabelEntry attendanceLabel = new ColorLabelEntry("");
    private final ColorLabelEntry profitLossLabel = new ColorLabelEntry("");
    private final ColorLabelEntry cashFundsLabel = new ColorLabelEntry("");
    private boolean currentFinance;

    final GridBagLayout layout = new GridBagLayout();
    final GridBagConstraints constraints = new GridBagConstraints();
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new FinancePanel object.
     *
     */
    FinancePanel(boolean currentFinance) {
        this.currentFinance = currentFinance;
        initComponents();
    }

    void setLabels() {
        final Economy finances = HOVerwaltung.instance().getModel().getEconomy();
        final float factor = core.model.UserParameter.instance().FXrate;

        if (currentFinance) {
            cashFundsLabel.setSpecialNumber((finances.getCash() / factor)
                                            + (finances.getExpectedWeeksTotal() / factor), true, false);
            attendanceLabel.setSpecialNumber(finances.getIncomeSpectators() / factor, true, false);
            sponsorsLabel.setSpecialNumber(finances.getIncomeSponsors() / factor, true, false);
            sponsorsBonusLabel.setSpecialNumber(finances.getIncomeSponsorsBonus() / factor, true, false);
            otherIncomeLabel.setSpecialNumber(finances.getIncomeTemporary() / factor, true, false);

            salariesLabel.setSpecialNumber(-finances.getCostsPlayers() / factor, true, false);
            stadiumMaintenanceLabel.setSpecialNumber(-finances.getCostsArena() / factor, true, false);
            stadiumBuildingLabel.setSpecialNumber(-finances.getCostsArenaBuilding() / factor, true, false);
            otherCostsLabel.setSpecialNumber(-finances.getCostsTemporary() / factor, true, false);
            staffLabel.setSpecialNumber(-finances.getCostsStaff() / factor, true, false);
            youthLabel.setSpecialNumber(-finances.getCostsYouth() / factor, true, false);
            newSigningsLabel.setSpecialNumber(-finances.getCostsBoughtPlayers() / factor, true, false);
            interestExpensesLabel.setSpecialNumber(-finances.getCostsFinancial() / factor, true, false);
            revenueTotalLabel.setSpecialNumber(finances.getIncomeSum() / factor, true, false);
            totalCostLabel.setSpecialNumber(-finances.getCostsSum() / factor, true, false);
            profitLossLabel.setSpecialNumber(finances.getExpectedWeeksTotal() / factor, true, false);
        } else {
            cashFundsLabel.setSpecialNumber(finances.getCash() / factor, true, false);
            attendanceLabel.setSpecialNumber(finances.getLastIncomeSpectators() / factor, true, false);
            sponsorsLabel.setSpecialNumber(finances.getLastIncomeSponsors() / factor, true, false);
            sponsorsBonusLabel.setSpecialNumber(finances.getLastIncomeSponsorsBonus() / factor, true, false);
            otherIncomeLabel.setSpecialNumber(finances.getLastIncomeTemporary() / factor, true, false);

            salariesLabel.setSpecialNumber(-finances.getLastCostsPlayers() / factor, true, false);
            stadiumMaintenanceLabel.setSpecialNumber(-finances.getLastCostsArena() / factor, true, false);
            stadiumBuildingLabel.setSpecialNumber(-finances.getLastCostsArenaBuilding() / factor, true, false);
            otherCostsLabel.setSpecialNumber(-finances.getLastCostsTemporary() / factor, true, false);
            staffLabel.setSpecialNumber(-finances.getLastCostsStaff() / factor, true, false);
            youthLabel.setSpecialNumber(-finances.getLastCostsYouth() / factor, true, false);
            newSigningsLabel.setSpecialNumber(-finances.getCostsBoughtPlayers() / factor, true, false);
            interestExpensesLabel.setSpecialNumber(-finances.getLastCostsFinancial() / factor, true, false);
            revenueTotalLabel.setSpecialNumber(finances.getLastIncomeSum() / factor, true, false);
            totalCostLabel.setSpecialNumber(-finances.getLastCostsSum() / factor, true, false);
            profitLossLabel.setSpecialNumber(finances.getLastWeeksTotal() / factor, true, false);
        }
    }

    private void initComponents() {

        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));


        var title = currentFinance ? getTranslation("DieseWoche") : getTranslation("Vorwoche"); //This week / next week
        var titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleColor(ThemeManager.getColor(HOColorName.LINEUP_HIGHLIGHT_FG));
        setBorder(titledBorder);

        JLabel label;

        setLayout(layout);

        label = new JLabel(getTranslation("ls.finance.cash"));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        add(label, cashFundsLabel.getComponent(false),0,0);

        label = new JLabel(" ",SwingConstants.CENTER);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        add(label);

        label = new JLabel(getTranslation("ls.finance.revenue"),SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBackground(ColorLabelEntry.BG_STANDARD.darker());
        label.setOpaque(true);
        constraints.gridy = 2;;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel(getTranslation("ls.finance.revenue.match_takings"));  //Match takings
        constraints.fill = GridBagConstraints.NONE;
        add(label,attendanceLabel.getComponent(false),0,3);

        label = new JLabel(getTranslation("ls.finance.revenue.sponsors"));  //Sponsors
        add(label,sponsorsLabel.getComponent(false),0,4);

        label = new JLabel(getTranslation("ls.finance.revenue.sponsors_bonuses"));  //Sponsors Bonus
        add(label,sponsorsBonusLabel.getComponent(false),0,5);

        label = new JLabel(getTranslation("ls.finance.revenue.player_sales"));
        add(label,playerSalesIncomeLabel.getComponent(false),0,6);

        label = new JLabel(getTranslation("ls.finance.revenue.commission"));
        add(label,commissionIncomeLabel.getComponent(false),0,7);

        label = new JLabel(getTranslation("ls.finance.other"));
        add(label,otherIncomeLabel.getComponent(false),0,8);

        label = new JLabel(getTranslation("Gesamteinnahmen"));  // Total Revenue
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        add(label,revenueTotalLabel.getComponent(false),0,11);


        label = new JLabel(getTranslation("ls.finance.expenses"),SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBackground(ColorLabelEntry.BG_STANDARD.darker());
        label.setOpaque(true);
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel(getTranslation("ls.finance.expenses.wages"));  // Wages
        constraints.fill = GridBagConstraints.NONE;
        add(label,salariesLabel.getComponent(false),2,3);

        label = new JLabel(getTranslation("ls.finance.expenses.stadium_maintenance"));
        add(label, stadiumMaintenanceLabel.getComponent(false),2,4);

        label = new JLabel(getTranslation("ls.finance.expenses.stadium_building"));
        add(label, stadiumMaintenanceLabel.getComponent(false),2,5);

        label = new JLabel(getTranslation("ls.finance.expenses.staf"));
        add(label,staffLabel.getComponent(false),2,6);

        label = new JLabel(getTranslation("ls.finance.expenses.youth_scouting"));
        add(label,youthLabel.getComponent(false),2,7);

        label = new JLabel(getTranslation("ls.finance.expenses.new_signings"));
        add(label,newSigningsLabel.getComponent(false),2,8);

        label = new JLabel(getTranslation("ls.finance.other"));
        add(label,otherCostsLabel.getComponent(false),2,9);

        label = new JLabel(getTranslation("ls.finance.expenses.interest"));
        add(label,interestExpensesLabel.getComponent(false),2,10);

        label = new JLabel(getTranslation("Gesamtausgaben"));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        add(label,totalCostLabel.getComponent(false),2,11);

        if (currentFinance) {
            label = new JLabel(getTranslation("ErwarteterGewinnVerlust"));
        } else {
            label = new JLabel(getTranslation("VorwocheGewinnVerlust"));
        }

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 12;
        constraints.gridwidth = 3;
        layout.setConstraints(label, constraints);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        add(label);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 3;
        constraints.gridy = 12;
        constraints.gridwidth = 1;
        layout.setConstraints(profitLossLabel.getComponent(false), constraints);
        profitLossLabel.setFont(profitLossLabel.getFont().deriveFont(Font.BOLD));
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
