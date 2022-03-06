package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;
import core.util.HODateTime;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class TestDialog extends JDialog {

	private static final long serialVersionUID = -2583288103393550257L;
	private JTextArea textArea;
	private JComboBox cbox;
	private WagesOverviewPanel wagesOverviewPanel;

	public TestDialog(Window parent) {
		super(parent, ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents();
		addListeners();
		pack();
	}

	private void addListeners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private void initComponents() {
		getContentPane().setLayout(new BorderLayout());
		List<Player> player = HOVerwaltung.instance().getModel().getCurrentPlayers();
		player.addAll(HOVerwaltung.instance().getModel().getFormerPlayers());

		player.sort(Comparator.comparing(Player::getFullName));

		CBItm[] array = new CBItm[player.size()];
		for (int i = 0; i < player.size(); i++) {
			array[i] = new CBItm(player.get(i));
		}

		this.cbox = new JComboBox(array);
		this.cbox.setSelectedItem(null);
		getContentPane().add(this.cbox, BorderLayout.NORTH);

		this.textArea = new JTextArea("", 30, 60);
		getContentPane().add(this.textArea, BorderLayout.CENTER);

		this.cbox.addActionListener(e -> spielerChanged());

		 this.wagesOverviewPanel = new WagesOverviewPanel();
		 getContentPane().add(this.wagesOverviewPanel, BorderLayout.SOUTH);

		// this.wagesSumPanel = new WagesSumPanel();
		// getContentPane().add(this.wagesSumPanel, BorderLayout.SOUTH);

//		this.playerTransferIncomePanel = new PlayerTransferIncomePanel();
//		getContentPane().add(this.playerTransferIncomePanel, BorderLayout.SOUTH);
		
//		this.overviewPanel = new OverviewPanel();
//		getContentPane().add(this.overviewPanel, BorderLayout.SOUTH);
	}

	private void spielerChanged() {
		if (this.cbox.getSelectedItem() != null) {
			Player player = ((CBItm) this.cbox.getSelectedItem()).getPlayer();

			// List<Wage> wages = Wage.getWagesByAge(player.getSpielerID());

			Transfer t = Transfer.getTransfer(player.getPlayerID());
			HODateTime buyingDate;
			if (!player.isHomeGrown()) {
				buyingDate = t.purchaseDate;

			} else {
				buyingDate = DBManager.instance().getSpielerFirstHRF(player.getPlayerID()).getHrfDate();
			}

			StringBuilder sb = new StringBuilder();
			int purchasePrice = (t.purchasePrice > 0) ? t.purchasePrice / 10 : 0;
			int sellingPrice = (t.sellingPrice > 0) ? t.sellingPrice / 10 : 0;
			sb.append("Gekauft für ").append(purchasePrice).append("\n");
			sb.append("Verkauft für ").append(sellingPrice).append("\n");

			int wages = Calc.getWagesSum(player.getPlayerID(), buyingDate, t.sellingDate);

			sb.append("wages payed: ").append(wages).append("\n");
			var daysInTeam = Duration.between( buyingDate.instant, t.sellingDate.instant).toDays();
			double fee = sellingPrice * (TransferFee.getFee((int)daysInTeam) / 100);
			sb.append("Gebühr für den Spielervermittler: ").append(fee).append("\n");

			double feePreviousClub = sellingPrice * (TransferFee.feePreviousClub(2) / 100);
			sb.append("Prämie für vorherigen Verein: ").append(feePreviousClub).append("\n");

			double gewinn = sellingPrice - purchasePrice - wages - fee - feePreviousClub;
			sb.append("Gewinn: ").append(gewinn).append("\n");

			this.textArea.setText(sb.toString());

			if (this.wagesOverviewPanel != null) {
				this.wagesOverviewPanel.setPlayer(player);
			}
		} else {
			this.textArea.setText("");
			if (this.wagesOverviewPanel != null) {
				this.wagesOverviewPanel.setPlayer(null);
			}
		}

	}

	private class CBItm {
		Player player;

		CBItm(Player player) {
			this.player = player;
		}

		@Override
		public String toString() {
			return player.getFullName();
		}

		public Player getPlayer() {
			return this.player;
		}
	}

	public static void main(String[] args) {
		HOVerwaltung.instance().setResource(UserParameter.instance().sprachDatei);
		HOVerwaltung.instance().loadLatestHoModel();
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> DBManager.instance().disconnect()));

		JFrame frame = new JFrame();
		TestDialog dlg = new TestDialog(frame);
		dlg.setVisible(true);
	}
}
