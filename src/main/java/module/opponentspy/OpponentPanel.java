//package module.opponentspy;
//
//import core.gui.HOMainFrame;
//import core.gui.comp.panel.ImagePanel;
//import core.model.match.MatchKurzInfo;
//import core.model.match.MatchLineup;
//import core.model.match.MatchLineupPlayer;
//import core.model.match.MatchLineupTeam;
//import core.model.match.Matchdetails;
//import core.model.player.IMatchRoleID;
//import core.model.player.Player;
//import core.net.MyConnector;
//import core.net.OnlineWorker;
//import core.net.login.LoginWaitDialog;
//import core.util.Helper;
////import module.opponentspy.OpponentTeam.OpponentRatingPanel;
////import module.opponentspy.OpponentTeam.OpponentTeamManager;
//
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JTabbedPane;
//import javax.swing.JTextField;
//
//
//public class OpponentPanel extends ImagePanel implements ActionListener{
//
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 899702920206003429L;
//
//
//
//
//
//	JLabel labelTEAMID = new JLabel("Team Id");
//	JTextField textTEAMID = new JTextField();
//	JLabel labelNATIONALTEAMID = new JLabel("National/U20 Team Id");
//	JTextField textNATIONALTEAMID = new JTextField();
//	JTextField textTransferSearch = new JTextField(100);
//	JLabel labelPlayerId = new JLabel("Player Id");
//	JTextField textPlayerId = new JTextField();
//	JLabel labelFileName = new JLabel("Base file name");
//	JTextField textLabelFile = new JTextField("c:/temp/HO/Opponent-");
//
//	JButton downloadButton = new JButton("Download");
//	JButton calculateButton = new JButton("Calculate");
//	JButton playersButton = new JButton("Download players");
//	JButton transferButton = new JButton("Get Transfer Players");
//	JButton resetTransferButton = new JButton("Reset transfer text");
//	JButton downloadPlayerButton = new JButton("Download single player");
//	JButton calculateTeamButton = new JButton("Calculate ratings");
//	GridBagConstraints constraints;
//
//	//OpponentRatingPanel ratingPanel;
//
//	public OpponentPanel() {
//
//		initComponents();
//	}
//
//
//
//		private void initComponents() {
//
//			JTabbedPane tabbedPane = new JTabbedPane();
//			add(tabbedPane);
//
//			tabbedPane.addTab("Transfer testing", getTransferTestPanel());
//
//			//ratingPanel = new OpponentRatingPanel();
//			//tabbedPane.addTab("Rating testing", ratingPanel);
//
//		}
//
//		private JPanel getTransferTestPanel() {
//
//			JPanel panel = new JPanel();
//
//			constraints = new GridBagConstraints();
//			panel.setLayout(new GridBagLayout());
//
//			constraints.weightx = 0;
//			constraints.weighty = 0;
//
//			constraints.gridx = 0;
//			constraints.gridy = 0;
//			constraints.fill = GridBagConstraints.HORIZONTAL;
//			panel.add(labelTEAMID, constraints);
//			constraints.gridx = 1;
//			panel.add(textTEAMID, constraints);
//
//			constraints.gridx = 0;
//			constraints.gridy = 1;
//			constraints.fill = GridBagConstraints.HORIZONTAL;
//			panel.add(labelNATIONALTEAMID, constraints);
//			constraints.gridx = 1;
//			panel.add(textNATIONALTEAMID, constraints);
//
//			constraints.gridx = 0;
//			constraints.gridy = 2;
//			constraints.fill = GridBagConstraints.HORIZONTAL;
//			panel.add(labelPlayerId, constraints);
//			constraints.gridx = 1;
//			panel.add(textPlayerId, constraints);
//
//			constraints.gridx = 0;
//			constraints.gridy = 3;
//			constraints.fill = GridBagConstraints.HORIZONTAL;
//			panel.add(labelFileName, constraints);
//			constraints.gridx = 1;
//			panel.add(textLabelFile, constraints);
//
//
//			constraints.gridx = 1;
//			constraints.gridy = 11;
//			panel.add(downloadPlayerButton, constraints);
//
//
//			constraints.gridx = 1;
//			constraints.gridy = 12;
//			panel.add(downloadButton, constraints);
//
//			constraints.gridx = 1;
//			constraints.gridy = 13;
//			panel.add(calculateButton, constraints);
//
//			constraints.gridx = 1;
//			constraints.gridy = 14;
//			panel.add(playersButton, constraints);
//
//			constraints.gridy = 15;
//			setDefaultTransferText();
//			panel.add(textTransferSearch);
//
//
//			constraints.gridy = 17;
//			panel.add(resetTransferButton, constraints);
//
//			constraints.gridx = 1;
//			constraints.gridy = 19;
//			panel.add(transferButton, constraints);
//
//			constraints.gridx = 1;
//			constraints.gridy = 20;
//			panel.add(calculateTeamButton, constraints);
//
//
//
//			calculateButton.addActionListener(this);
//			downloadButton.addActionListener(this);
//			playersButton.addActionListener(this);
//			transferButton.addActionListener(this);
//			resetTransferButton.addActionListener(this);
//			downloadPlayerButton.addActionListener(this);
//			calculateTeamButton.addActionListener(this);
//
//			return panel;
//
//		}
//
//
//
//
//		 public void startDownload (int teamId) {
//			 	// no, not static.
//
////		   		GregorianCalendar start = new GregorianCalendar();
////			    start.add(Calendar.MONTH, -8);
//
//			 	// recent matches should fit better with "up to now". As it was, it returned no matches.
//			    List<MatchKurzInfo> matchInfos = OnlineWorker.getMatches(teamId, new Date());
//			    Collections.reverse(matchInfos); // Newest first
//			    for (MatchKurzInfo match : matchInfos) {
//			    	if (match.getMatchStatus() != MatchKurzInfo.FINISHED) {
//			    		continue;
//			    	}
//
//			    	System.out.println("MatchId: " + match.getMatchID() + " - " + match.getHeimName() +
//			    			" Away: " + match.getGastName() +  "  " + match.getHeimTore() + " - " + match.getGastTore());
//
//			    	// Temporary, the fetchDetails dies without it, but there should probably not be HOMainFrame here...
//			    	// If you send null instead of waitDlg you can step into it, and get a hint of where the error happens. It is a nice test.
//			    	LoginWaitDialog waitDlg = new LoginWaitDialog(HOMainFrame.instance(), false);
//
//			    	Matchdetails details = OnlineWorker.fetchDetails(match.getMatchID(), match.getMatchTyp(), null, waitDlg);
//
//			    	if (details != null) {
//			    		System.out.println("Found details!");
//			    	}
//
//			    	MatchLineup homeLineup = OnlineWorker.fetchLineup(match.getMatchID(), match.getHeimID(), match.getMatchTyp());
//
//			    	if (homeLineup == null) {
//			    		System.out.println("failed to get lineup");
//			    	} else {
//			    		MatchLineupTeam homeTeam = homeLineup.getHeim();
//
//			    		System.out.println("Home team players:");
//			    		System.out.println("");
//			    		for (MatchLineupPlayer player : homeTeam.getAufstellung()) {
//			    			System.out.println(player.getPositionName() + " - " + player.getSpielerName() + " Stars: " + player.getRatingStarsEndOfMatch());
//			    		}
//
//
//			    	}
//
//			    }
//
//
//		 }
//
//		private void setDefaultTransferText() {
//			textTransferSearch.setText("&ageMin=20&ageMax=22&skillType1=8&minSkillValue1=7&maxSkillValue1=9");
//		}
//
//		@Override
//   		public void actionPerformed(ActionEvent e) {
//			if (e.getSource() == downloadButton) {
//				int teamId = 0;
//				   try {
//
//					     // So trivial error it was hard to see. The parse was not assigned to teamId.
//					     // Which in my case meant startDownload was not called below.
////					             teamId = Integer.parseInt(textTEAMID.getText());
//
//
//				     } catch (Exception ex) {
//			             System.out.println("Failed to parse number from Team Id field!");
//			          }
//
//				   // Only do download if parsing was successful.
//				   // Init to 0 as that is not a teamId.
////						   if (teamId > 0) {
////							   startDownload(teamId);
////						   }
//
//
//			}
//			if (e.getSource() == calculateButton) {
//				// One single test calculation
//			    OppPlayerSkillEstimator estimator = new OppPlayerSkillEstimator();
//
//			    // Mihai Orzea 23 years, 28700 TSI, 90840 wage,
//			    // 12 keeper, 5 defending, 6 stamina, 1 set pieces, 5 experience, 7 form.
//
//			    OpponentPlayer orzea = estimator.calcPlayer(23, 90940, 29700, 7, 6, 0, IMatchRoleID.KEEPER);
//			    orzea.setName("Orzea");
//
//			    // Stig-Arne Tellbratt 23 years, 49280 TSI, 238440 wage, possibly not adjusted since 3 days old
//			    // 15 keeper, 11 defending, 15 set pieces, 8 stamina 6 experience, 7 form
//
//			    OpponentPlayer tellbratt = estimator.calcPlayer(23,  238440, 49280, 7, 8, 0, IMatchRoleID.KEEPER);
//			    tellbratt.setName("Tellbratt");
//
////					    printPlayer(tellbratt);
////					    printPlayer(orzea);
//			}
//			if (e.getSource() == playersButton) {
//
//				int teamId = 0;
//				try {
//
//				      teamId = Integer.parseInt(textTEAMID.getText());
//
//
//			    } catch (Exception ex) {
//		             System.out.println("Failed to parse number from Team Id field!");
//		        }
//
//				if (teamId != 0) {
//					List<Player> players = OnlineWorker.getTeamPlayers(teamId);
//
//					if (players != null) {
//						System.out.println(players.size() + " players found!");
//
//						for (Player player : players) {
//
//							System.out.println("Id: " + player.getSpielerID() + " Name: " + player.getName() + " TSI: " + player.getTSI() + " Wage: " + player.getGehalt());
//
//						}
//					}
//				}
//			}
//			if (e.getSource() == transferButton) {
//
//				try {
//
//					if (! textTransferSearch.getText().isEmpty())
//					{
//						String xmlString = MyConnector.instance().getHattrickXMLFile("?file=transfersearch&version=1.0" + textTransferSearch.getText());
//						List<TransferPlayer> transfers = TransferDownload.parseTransferSearchFromString(xmlString);
//
//						List<TransferPlayer> extraTransfers = new ArrayList<TransferPlayer>();
//
//						for (TransferPlayer player : transfers) {
//
//							TransferDownload.updatePlayerSalary(player);
//
//
//							TransferPlayer extraPlayer = new TransferPlayer();
//							extraPlayer.calcVariables = new CalcVariables(player.calcVariables);
//							extraPlayer.id = player.id;
//							extraPlayer.name = player.name;
//
//							// Make a separate list for a second test.
//							extraTransfers.add(extraPlayer);
//
//						}
//
//
//
//
//
//
//						new TransferPlayerTester(new SkillAdjuster()).testTransferPlayers(transfers, textLabelFile.getText(), false);
//
//						new TransferPlayerTester(new TestSkillAdjuster()).testTransferPlayers(extraTransfers, textLabelFile.getText(), true);
//
//						// Make a
//
//					}
//
//				} catch (Exception ex) {
//					System.out.println("Error: " + ex.getMessage());
//				}
//			}
//
//			if (e.getSource() == resetTransferButton) {
//				setDefaultTransferText();
//			}
//
//			if (e.getSource() == downloadPlayerButton) {
//				int playerId = 0;
//				try {
//					playerId = Integer.parseInt(textPlayerId.getText());
//				}
//				catch (Exception ex){
//					return;
//				}
////
////						Player player = OnlineWorker.getPlayer(playerId);
////						if (player == null) {
////							System.out.println("Null was returned :(");
////						} else {
////							System.out.println(player.getSpielerID() + " " + player.getName() + " Was downloaded!");
////						}
//			}
//
//			if (e.getSource() == calculateTeamButton) {
//
//				int teamId;
//				try {
//					teamId = Integer.parseInt(textTEAMID.getText());
//					if (teamId <= 0)
//						throw new Exception();
//
//				} catch (Exception exception) {
//
//					Helper.showMessage(this, "You need to put a valid Team ID" + "\n", "Error",
//							JOptionPane.ERROR_MESSAGE);
//					return;
//				}
//
//				//OpponentTeamManager manager = new OpponentTeamManager();
//				//manager.setTeamId(teamId);
//
//				//ratingPanel.setModel(manager.getOpponentModel());
//
//			}
//
//   	    }
//
//}
//
//
//
//
//