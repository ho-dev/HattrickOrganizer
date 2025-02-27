package module.transfer.history;


import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.util.CurrencyUtils;
import core.util.Helper;
import module.training.ui.comp.DividerListener;
import module.transfer.PlayerTransfer;
import module.transfer.TransfersPanel;
import module.transfer.XMLParser;
import module.transfer.ui.layout.TableLayout;
import module.transfer.ui.layout.TableLayoutConstants;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Pane to show transfer history information for your own team.
 *
 */
public class HistoryPane extends JSplitPane {

	private final ButtonModel spinSeason;
    private final JLabel amountTransfers = new JLabel("", SwingConstants.RIGHT);
    private final JLabel amountTransfersIn = new JLabel("", SwingConstants.RIGHT);
    private final JLabel amountTransfersOut = new JLabel("", SwingConstants.RIGHT);
    private final JSpinner spinner = new JSpinner();
    private final TeamTransfersPane transferPane;
    private final TotalsPanel pricePanel;
    private final TotalsPanel tsiPanel;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates an instance of the HistoryPane
     */
    public HistoryPane(TransfersPanel transfersPanel) {
        super(JSplitPane.VERTICAL_SPLIT);

        var fontSize = UserParameter.instance().fontSize;
        // Create side panel
        final double[][] sizes = {
                               {TableLayoutConstants.PREFERRED, TableLayoutConstants.FILL},
                               {
                                   TableLayoutConstants.PREFERRED, fontSize, TableLayoutConstants.PREFERRED,
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
                                                      fontSize, TableLayoutConstants.PREFERRED, 4*fontSize,
                                                      TableLayoutConstants.FILL, fontSize
                                                  },
                                                  {fontSize, TableLayoutConstants.PREFERRED, TableLayoutConstants.PREFERRED,TableLayoutConstants.PREFERRED}
                                              }));

        final JRadioButton rb1 = new JRadioButton(TranslationFacility.tr("AllSeasons")); //$NON-NLS-1$
        rb1.setFocusable(false);
        rb1.setOpaque(false);

        final JRadioButton rb2 = new JRadioButton(TranslationFacility.tr("Season")); //$NON-NLS-1$
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
        spinner.addChangeListener(e -> refresh());

        rb1.setSelected(true);

        rb1.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if(button.getModel().isPressed()){
                spinner.setEnabled(false);
                refresh();
            }
        });
        rb2.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if(button.getModel().isPressed()){
                spinner.setEnabled(true);
                refresh();
            }
        });

        JButton button = new JButton(TranslationFacility.tr("Menu.refreshData"));
        button.addActionListener(e -> {

            HOVerwaltung hoV1 = HOVerwaltung.instance();
            int teamId = hoV1.getModel().getBasics().getTeamId();
            if (teamId != 0 && !hoV1.getModel().getBasics().isNationalTeam()) {

                String sBuffer = TranslationFacility.tr("UpdConfirmMsg.0") +
                        "\n" + TranslationFacility.tr("UpdConfirmMsg.1") +
                        "\n" + TranslationFacility.tr("UpdConfirmMsg.2") +
                        "\n\n" + TranslationFacility.tr("UpdConfirmMsg.3");

                final int choice = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
                        sBuffer,
                        TranslationFacility.tr("confirmation.title"),
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        HOMainFrame.instance().resetInformation();
                        XMLParser.updateTeamTransfers(teamId);
                        HOMainFrame.instance().setInformationCompleted();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    refresh();
                }
            } else {
                Helper.showMessage(HOMainFrame.instance(), TranslationFacility.tr("UpdMsg"), "", 1);
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
                                                  {.25, 6*fontSize, 2*fontSize, .5, 6*fontSize, 2*fontSize, .25},
                                                  {fontSize, 2*fontSize, 2*fontSize}
                                              }));

        final JLabel amTrans = new JLabel(TranslationFacility.tr("Transfers") + ":", SwingConstants.LEFT);
        final JLabel amTransIn = new JLabel(TranslationFacility.tr("In") + ":", SwingConstants.LEFT);
        amTransIn.setIcon(ImageUtilities.getTransferInIcon());

        final JLabel amTransOut = new JLabel(TranslationFacility.tr("Out") + ":", SwingConstants.LEFT);
        amTransOut.setIcon(ImageUtilities.getTransferOutIcon());
        amountPanel.add(amTrans, "1, 1");
        amountPanel.add(amountTransfers, "2, 1");
        amountPanel.add(amTransIn, "4, 1");
        amountPanel.add(amountTransfersIn, "5, 1");
        amountPanel.add(amTransOut, "4, 2");
        amountPanel.add(amountTransfersOut, "5, 2");
        pricePanel = new TotalsPanel(TranslationFacility.tr("Price"),
        		CurrencyUtils.CURRENCYSYMBOL);
        tsiPanel = new TotalsPanel(TranslationFacility.tr("ls.player.tsi")); //$NON-NLS-1$

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

        transferPane = new TeamTransfersPane(transfersPanel);

        topPanel.add(transferPane, BorderLayout.CENTER);
        topPanel.add(sidePane, BorderLayout.WEST);

        setDividerLocation(UserParameter.instance().transferHistoryPane_splitPane); //$NON-NLS-1$
        addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                                  new DividerListener(DividerListener.transferHistoryPane_splitPane)); //$NON-NLS-1$

        setLeftComponent(topPanel);
        setRightComponent(transferPane.getPlayerDetailPanel());

        //refresh();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Refresh the displayed information.
     */
    public final void refresh() {
        List<PlayerTransfer> transfers;
        if (spinSeason.isSelected()) {
            final SpinnerNumberModel model = (SpinnerNumberModel) this.spinner.getModel();
            transfers = DBManager.instance().getTransfers(model.getNumber().intValue(), true, true);
        } else {
            transfers = DBManager.instance().getTransfers(0, true, true);
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

        transferPane.refresh(transfers);
    }
    public void storeUserSettings(){
        transferPane.storeUserSettings();
    }

    public void selectTransfer(int transferId) {
        transferPane.selectTransfer(transferId);
    }
}
