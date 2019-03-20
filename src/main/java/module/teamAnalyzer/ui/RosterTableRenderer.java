// %3062152101:hoplugins.teamAnalyzer.ui%
package module.teamAnalyzer.ui;

import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.PlayerInfo;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;




public class RosterTableRenderer extends DefaultTableCellRenderer {
    //~ Methods ------------------------------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = -3338455733573545222L;

	/*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        try {
            // setBackground(Color.WHITE);
            setOpaque(true);
            setToolTipText(null);
            setIcon(null);

            if (isSelected) {
                // this.setBackground(Color.LIGHT_GRAY);
            }

            if (column > 3) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            if (column == 9) {
                int v = Integer.parseInt((String) value);
                ImageIcon icon =ThemeManager.getIcon(HOIconName.SPECIAL[v]);
                setIcon(icon);
                setText("");
                return this;
            }

            int status = Integer.parseInt("" + table.getModel().getValueAt(row, 13));

            switch (status) {
                case PlayerDataManager.INJURED:
                    setForeground(Color.RED);
                    break;

                case PlayerDataManager.SUSPENDED:
                    setForeground(Color.RED);
                    break;

                case PlayerDataManager.SOLD:
                    setForeground(Color.BLUE);
                    break;

                default:
                    setForeground(Color.BLACK);
                    break;
            }

            /*
               int pos = ((Integer) table.getValueAt(row, 2)).intValue();
               int posCode = Commons.getModel().getHelper().getPosition(pos);
               switch (posCode) {
               case IMatchRoleID.keeper:
                   setForeground(Color.BLACK);
                   break;
               case IMatchRoleID.insideBack1:
                   setForeground(Color.BLUE.darker().darker());
                   break;
               case IMatchRoleID.leftBack:
                   setForeground(Color.GREEN.darker().darker());
                   break;
               case IMatchRoleID.insideMid1:
                   setForeground(Color.YELLOW.darker().darker());
                   break;
               case IMatchRoleID.leftWinger:
                   setForeground(Color.ORANGE.darker().darker());
                   break;
               case IMatchRoleID.forward1:
                   setForeground(Color.RED.darker().darker());
                   break;
               }
             */
            int playerId = Integer.parseInt("" + table.getModel().getValueAt(row, 14));
            PlayerInfo oldInfo = SystemManager.getPlugin().getMainPanel().getRosterPanel()
                                              .getPrevious(playerId);

            if (oldInfo.getPlayerId() == 0) {
                return this;
            }

            switch (column) {
                case 6: {
                    int v = Integer.parseInt("" + value);
                    int diff = v - oldInfo.getForm();

                    
                   setIcon( ImageUtilities.getImageIcon4Veraenderung(diff,true));


                    break;
                }

                case 7: {
                    int v = Integer.parseInt("" + value);
                    int diff = v - oldInfo.getExperience();


                        setIcon( ImageUtilities.getImageIcon4Veraenderung(diff,true));


                    break;
                }

                case 8: {
                    int v = Integer.parseInt("" + value);
                    int diff = v - oldInfo.getTSI();
                    String desc = "";

                    setText(getText() + " " + desc);
                    break;
                }
            }
        } catch (Exception e) {
            setText("!!!"); //$NON-NLS-1$
            setToolTipText(e.toString());
        }

        return this;
    }
}
