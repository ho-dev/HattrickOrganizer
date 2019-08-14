// %1126721330323:hoplugins.transfers.ui%
package module.transfer.history;


import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.net.login.LoginWaitDialog;
import core.util.Helper;
import core.util.CurrencyUtils;
import module.training.ui.comp.DividerListener;
import module.transfer.PlayerTransfer;
import module.transfer.ui.layout.TableLayout;
import module.transfer.ui.layout.TableLayoutConstants;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * Pane to show transfer histry information for your own team.
 *
 * @author <a href=mailto:nethyperon@users.sourceforge.net>Boy van der Werf</a>
 */
public class HistoryPane extends JSplitPane {
    //~ Instance fields ----------------------------------------------------------------------------

    /**
	 *
	 */
	private static final long serialVersionUID = 5465572622813044852L;
	private ButtonModel spinSeason;
    private JLabel amountTransfers = new JLabel("", SwingConstants.RIGHT);
    private JLabel amountTransfersIn = new JLabel("", SwingConstants.RIGHT);
    private JLabel amountTransfersOut = new JLabel("", SwingConstants.RIGHT);
    private JSpinner spinner = new JSpinner();
    private List<PlayerTransfer> transfers;
    private PlayerDetailPanel playerDetailPanel = new PlayerDetailPanel();
    private TeamTransfersPane transferPane;
    private TotalsPanel pricePanel;
    private TotalsPanel tsiPanel;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates an instance of the HistoryPane
     */
    public HistoryPane() {
        super(JSplitPane.VERTICAL_SPLIT);

        // Create side panel
        final double[][] sizes = {
                               {TableLayoutConstants.PREFERRED, TableLayoutConstants.FILL},
                               {
                                   TableLayoutConstants.PREFERRED, 10, TableLayoutConstants.PREFERRED,
                                   TableLayoutConstants.FILL, TableLayoutConstants.PREFERRED, TableLayoutConstants.PREFERRED,
                                   TableLayoutConstants.PREFERRED
                               }
                           };
        final JPanel sidePanel = new ImagePanel();
        sidePanel.setLayout(new TableLayout(sizes));
        sidePanel.setOpaque(false);
        HOVerwaltung hoV = HOVerwaltung.instance();
        final JPanel filterPanel = new ImagePanel();
        filterPanel.setLayout(new TableLayout(new double[][]{
                                                  {
                                                      10, TableLayoutConstants.PREFERRED, 50,
                                                      TableLayoutConstants.FILL, 10
                                                  },
                                                  {10, TableLayoutConstants.PREFERRED, TableLayoutConstants.PREFERRED,TableLayoutConstants.PREFERRED}
                                              }));

        final JRadioButton rb1 = new JRadioButton(hoV.getLanguageString("AllSeasons")); //$NON-NLS-1$
        rb1.setFocusable(false);
        rb1.setOpaque(false);

        final JRadioButton rb2 = new JRadioButton(hoV.getLanguageString("Season")); //$NON-NLS-1$
        spinSeason = rb2.getModel();
        rb2.setFocusable(false);
        rb2.setOpaque(false);

        if (hoV.getModel().getBasics().getSeason() > 0) {
            spinner.setModel(new SpinnerNumberModel(hoV.getModel().getBasics().getSeason(), 1,
            		hoV.getModel().getBasics().getSeason(), 1));
        } else {
            rb2.setEnabled(false);
            spinner.setModel(new SpinnerNumberModel());
        }

        spinner.setFocusable(false);
        spinner.setEnabled(false);
        spinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    refresh();
                }
            });

        rb1.setSelected(true);

        rb1.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                	JRadioButton button = (JRadioButton)e.getSource();
                    if(button.getModel().isPressed()){
                    	spinner.setEnabled(false);
                    	refresh();
                    }
                }
            });
        rb2.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                	JRadioButton button = (JRadioButton)e.getSource();
                    if(button.getModel().isPressed()){
                    	spinner.setEnabled(true);
                    	refresh();
                    }
                }
            });

        JButton button = new JButton(HOVerwaltung.instance().getLanguageString("Menu.refreshData"));
        button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				 HOVerwaltung hoV = HOVerwaltung.instance();
				 int teamId = hoV.getModel().getBasics().getTeamId();
	            if ( teamId != 0 && !hoV.getModel().getBasics().isNationalTeam()) {
	                final StringBuffer sBuffer = new StringBuffer();

	                sBuffer.append(hoV.getLanguageString("UpdConfirmMsg.0"));
	                sBuffer.append("\n" + hoV.getLanguageString("UpdConfirmMsg.1"));
	                sBuffer.append("\n" + hoV.getLanguageString("UpdConfirmMsg.2"));
	                sBuffer.append("\n\n" + hoV.getLanguageString("UpdConfirmMsg.3"));

	                final int choice = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
	                                                                 sBuffer.toString(),
	                                                                 HOVerwaltung.instance().getLanguageString("confirmation.title"),
	                                                                 JOptionPane.YES_NO_OPTION);

	                if (choice == JOptionPane.YES_OPTION) {
	                    try {
	                        final JWindow waitWindow = new LoginWaitDialog(HOMainFrame.instance());
	                        waitWindow.setVisible(true);

	                        DBManager.instance().updateTeamTransfers(teamId);
	                        waitWindow.setVisible(false);
	                        waitWindow.dispose();
	                    } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }

	                    refresh();
	                }
	            } else {
	            	 Helper.showMessage(HOMainFrame.instance(),hoV.getLanguageString("UpdMsg"), "", 1);
	            }

			}
		});


        filterPanel.add(button,"1,1,2,1");
        filterPanel.add(rb1, "1, 2"); //$NON-NLS-1$
        filterPanel.add(rb2, "1, 3"); //$NON-NLS-1$
        filterPanel.add(spinner, "2, 3"); //$NON-NLS-1$


        final ButtonGroup bg = new ButtonGroup();
        bg.add(rb1);
        bg.add(rb2);

        final JPanel amountPanel = new ImagePanel();
        amountPanel.setLayout(new TableLayout(new double[][]{
                                                  {.25, 75, 25, .5, 75, 25, .25},
                                                  {10, 20, 20}
                                              }));

        final JLabel amTrans = new JLabel(hoV.getLanguageString("Transfers") + ":", SwingConstants.LEFT);
        final JLabel amTransIn = new JLabel(hoV.getLanguageString("In") + ":", SwingConstants.LEFT);
        amTransIn.setIcon(ThemeManager.getIcon(HOIconName.TRANSFER_IN));

        final JLabel amTransOut = new JLabel(hoV.getLanguageString("Out") + ":", SwingConstants.LEFT);
        amTransOut.setIcon(ThemeManager.getIcon(HOIconName.TRANSFER_OUT));
        amountPanel.add(amTrans, "1, 1");
        amountPanel.add(amountTransfers, "2, 1");
        amountPanel.add(amTransIn, "4, 1");
        amountPanel.add(amountTransfersIn, "5, 1");
        amountPanel.add(amTransOut, "4, 2");
        amountPanel.add(amountTransfersOut, "5, 2");
        pricePanel = new TotalsPanel(hoV.getLanguageString("Price"),
        		CurrencyUtils.CURRENCYSYMBOL);
        tsiPanel = new TotalsPanel(hoV.getLanguageString("ls.player.tsi")); //$NON-NLS-1$

        sidePanel.add(filterPanel, "0, 0");
        sidePanel.add(new JSeparator(), "0, 2");
        sidePanel.add(amountPanel, "0, 4");
        sidePanel.add(pricePanel, "0, 5");
        sidePanel.add(tsiPanel, "0, 6");

        final JScrollPane sidePane = new JScrollPane(sidePanel);
        sidePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidePane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create the top panel and add it to the split pane
        final JPanel topPanel = new ImagePanel();
        topPanel.setLayout(new BorderLayout());

        transferPane = new TeamTransfersPane(playerDetailPanel);

        topPanel.add(transferPane, BorderLayout.CENTER);
        topPanel.add(sidePane, BorderLayout.WEST);

        setDividerLocation(UserParameter.instance().transferHistoryPane_splitPane); //$NON-NLS-1$
        addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                                  new DividerListener(DividerListener.transferHistoryPane_splitPane)); //$NON-NLS-1$

        setLeftComponent(topPanel);
        setRightComponent(playerDetailPanel);

        refresh();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Refresh the displayed information.
     */
    public final void refresh() {
        if (spinSeason.isSelected()) {
            final SpinnerNumberModel model = (SpinnerNumberModel) this.spinner.getModel();
            this.transfers = DBManager.instance().getTransfers(model.getNumber().intValue(), true, true);
        } else {
            this.transfers = DBManager.instance().getTransfers(0, true, true);
        }

        final TransferTotals totals = TransferTotals.calculateTotals(transfers);
        pricePanel.setValues(totals.getBuyPriceTotal(), totals.getBuyPriceAvg(),
                             totals.getSellPriceTotal(), totals.getSellPriceAvg());
        amountTransfers.setText(Integer.toString(totals.getAmountSell() + totals.getAmountBuy()));
        amountTransfersIn.setText(Integer.toString(totals.getAmountBuy()));
        amountTransfersOut.setText(Integer.toString(totals.getAmountSell()));
        tsiPanel.setValues(totals.getBuyTsiTotal(), totals.getBuyTsiAvg(),
                           totals.getSellTsiTotal(), totals.getSellTsiAvg());
        pricePanel.revalidate();

        transferPane.refresh(this.transfers);
    }
}
