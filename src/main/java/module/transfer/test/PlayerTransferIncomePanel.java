package module.transfer.test;

import core.db.DBManager;
import core.model.player.Player;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerTransferIncomePanel extends JPanel {

	private static final long serialVersionUID = -5222711212315427296L;
	private JLabel sellingPriceValueLabel;
	private JLabel buyingPriceValueLabel;
	private JLabel wagesValueLabel;
	private JLabel transferFeesValueLabel;
	private JLabel motherClubValueLabel;
	private JLabel previousClubValueLabel;
	private JLabel profitValueLabel;

	PlayerTransferIncomePanel() {
		initComponents();
	}
	
	void setPlayer(Player player) {
		if (player != null) {
			NumberFormat nf = NumberFormat.getInstance();

			Transfer t = Transfer.getTransfer(player.getPlayerID());
			Date buyingDate = null;
			if (!player.isHomeGrown()) {
				buyingDate = t.purchaseDate;

			} else {
				buyingDate = new Date(DBManager.instance()
						.getSpielerFirstHRF(player.getPlayerID()).getHrfDate().getTime());
			}

			int purchasePrice = (t.purchasePrice > 0) ? t.purchasePrice / 10 : 0;
			int sellingPrice = (t.sellingPrice > 0) ? t.sellingPrice / 10 : 0;
			int wages = Calc.getWagesSum(player.getPlayerID(), buyingDate, t.sellingDate);
			int daysInTeam = Calc.getDaysBetween(t.sellingDate, buyingDate);
			double fee = sellingPrice * (TransferFee.getFee(daysInTeam) / 100);
			double feePreviousClub = sellingPrice * (TransferFee.feePreviousClub(2) / 100);
			double gewinn = sellingPrice - purchasePrice - wages - fee - feePreviousClub;

			this.sellingPriceValueLabel.setText(nf.format(sellingPrice));
			this.buyingPriceValueLabel.setText(nf.format(purchasePrice));
			this.wagesValueLabel.setText(nf.format(wages));
			this.transferFeesValueLabel.setText(nf.format(fee));
			this.motherClubValueLabel.setText(nf.format(sellingPrice * 0.02));
			this.previousClubValueLabel.setText(nf.format(feePreviousClub));
			this.profitValueLabel.setText(nf.format(gewinn));
		}
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel sellingPriceLabel = new JLabel("Verkaufspreis");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		add(sellingPriceLabel, gbc);

		this.sellingPriceValueLabel = new JLabel();
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(this.sellingPriceValueLabel, gbc);

		JLabel buyingPriceLabel = new JLabel("Kaufpreis");
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.WEST;
		add(buyingPriceLabel, gbc);

		this.buyingPriceValueLabel = new JLabel();
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(this.buyingPriceValueLabel, gbc);

		JLabel wagesLabel = new JLabel("Wages payed");
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.WEST;
		add(wagesLabel, gbc);

		this.wagesValueLabel = new JLabel();
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(this.wagesValueLabel, gbc);

		JLabel transferFeesLabel = new JLabel("Transfer fees");
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.WEST;
		add(transferFeesLabel, gbc);

		this.transferFeesValueLabel = new JLabel();
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(this.transferFeesValueLabel, gbc);
		
		JLabel motherClubLabel = new JLabel("Mother club");
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.WEST;
		add(motherClubLabel, gbc);
		
		this.motherClubValueLabel = new JLabel();
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(this.motherClubValueLabel, gbc);
		
		JLabel previousClubLabel = new JLabel("Previous club");
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.WEST;
		add(previousClubLabel, gbc);
		
		this.previousClubValueLabel = new JLabel();
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(this.previousClubValueLabel, gbc);

		JLabel profitLabel = new JLabel("Profit");
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.WEST;
		add(profitLabel, gbc);

		this.profitValueLabel = new JLabel();
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(this.profitValueLabel, gbc);
//		this.profitValueLabel.setFont(this.profitValueLabel.getFont().deriveFont(
//				this.profitValueLabel.getFont().getStyle() ^ Font.BOLD));
	}

}
