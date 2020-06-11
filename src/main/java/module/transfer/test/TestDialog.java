package module.transfer.test;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.player.Player;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	private WagesSumPanel wagesSumPanel;
	private PlayerTransferIncomePanel playerTransferIncomePanel;
	private OverviewPanel overviewPanel;

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

		Collections.sort(player, new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				return o1.getFullName().compareTo(o2.getFullName());
			}
		});

		CBItm[] array = new CBItm[player.size()];
		for (int i = 0; i < player.size(); i++) {
			array[i] = new CBItm(player.get(i));
		}

		this.cbox = new JComboBox(array);
		this.cbox.setSelectedItem(null);
		getContentPane().add(this.cbox, BorderLayout.NORTH);

		this.textArea = new JTextArea("", 30, 60);
		getContentPane().add(this.textArea, BorderLayout.CENTER);

		this.cbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				spielerChanged();
			}
		});

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

			Transfer t = Transfer.getTransfer(player.getSpielerID());
			Date buyingDate = null;
			if (!player.isHomeGrown()) {
				buyingDate = t.purchaseDate;

			} else {
				buyingDate = new Date(DBManager.instance()
						.getSpielerFirstHRF(player.getSpielerID()).getHrfDate().getTime());
			}

			StringBuilder sb = new StringBuilder();
			int purchasePrice = (t.purchasePrice > 0) ? t.purchasePrice / 10 : 0;
			int sellingPrice = (t.sellingPrice > 0) ? t.sellingPrice / 10 : 0;
			sb.append("Gekauft für ").append(purchasePrice).append("\n");
			sb.append("Verkauft für ").append(sellingPrice).append("\n");

			int wages = Calc.getWagesSum(player.getSpielerID(), buyingDate, t.sellingDate);

			sb.append("wages payed: ").append(wages).append("\n");
			int daysInTeam = Calc.getDaysBetween(t.sellingDate, buyingDate);
			double fee = sellingPrice * (TransferFee.getFee(daysInTeam) / 100);
			sb.append("Gebühr für den Spielervermittler: ").append(fee).append("\n");

			double feePreviousClub = sellingPrice * (TransferFee.feePreviousClub(2) / 100);
			sb.append("Prämie für vorherigen Verein: ").append(feePreviousClub).append("\n");

			double gewinn = sellingPrice - purchasePrice - wages - fee - feePreviousClub;
			sb.append("Gewinn: ").append(gewinn).append("\n");

			// sb.append("Age   -   Wage" + "\n");
			// for (Wage wage : wages) {
			// sb.append(wage.getAge() + " - " + wage.getWage() + "\n");
			// }
			// sb.append("\n\n");
			// if (!player.isHomeGrown()) {
			// sb.append("Bought at: " +
			// Calc.getBuyingDates(player.getSpielerID()).get(0));
			// }
			//
			// int ageDays = Calc.getAgeAt(new Date(), player.getSpielerID());
			// int age = ageDays / 112;
			// int days = ageDays % 112;
			//
			// sb.append("\n\n");
			// sb.append("Age: " + age + "." + days);
			//
			// sb.append("\n\n");
			// sb.append("Birthdays from 17 to 30\n");
			// List<Birthday> birthdays =
			// Calc.getBirthdays(player.getSpielerID(), 17, 30);
			// for (Birthday birthday : birthdays) {
			// sb.append(birthday.getAge()).append(" ").append(birthday.getDate()).append("\n");
			// }

			this.textArea.setText(sb.toString());

			if (this.wagesOverviewPanel != null) {
				this.wagesOverviewPanel.setPlayer(player);
			}
			if (this.wagesSumPanel != null) {
				this.wagesSumPanel.setPlayer(player);
			}
			if (this.playerTransferIncomePanel != null) {
				this.playerTransferIncomePanel.setPlayer(player);
			}
		} else {
			this.textArea.setText("");
			if (this.wagesOverviewPanel != null) {
				this.wagesOverviewPanel.setPlayer(null);
			}
			if (this.wagesSumPanel != null) {
				this.wagesSumPanel.setPlayer(null);
			}
			if (this.playerTransferIncomePanel != null) {
				this.playerTransferIncomePanel.setPlayer(null);
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

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				DBManager.instance().disconnect();
			}
		});

		JFrame frame = new JFrame();
		TestDialog dlg = new TestDialog(frame);
		dlg.setVisible(true);
	}
}
