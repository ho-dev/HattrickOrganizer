package core.gui.model;

import core.db.DBManager;
import core.gui.comp.table.HOTableModel;
import module.lineup.LineupTableModel;
import module.matches.statistics.MatchesOverviewColumnModel;
import module.teamAnalyzer.ui.RecapPanelTableModel;
import module.youth.YouthPlayerDetailsTableModel;
import module.youth.YouthPlayerOverviewTableModel;
import module.youth.YouthTrainingViewTableModel;

import java.util.Vector;

/**
 * Controller for the UserColumns.
 * Create columns and managed the models
 * @author Thorsten Dietz
 * @since 1.36
 *
 */
public final class UserColumnController {



	public enum ColumnModelId {
		MATCHES(1),
		PLAYEROVERVIEW(2),
		LINEUP(3),
		PLAYERANALYSIS1(4),
		PLAYERANALYSIS2(5),
		MATCHESOVERVIEW(6),
		YOUTHPLAYEROVERVIEW(7),
		YOUTHPLAYERDETAILS(8),
		YOUTHTRAININGVIEW(9),
		TEAMANALYZERRECAP(10);

		private final int value;
		ColumnModelId(int value){this.value=value;}
		public int getValue() {return value;}
	}

	/** singleton **/
	private static final UserColumnController columnController = new UserColumnController();
	
	/** model for matches table **/
	private  MatchesColumnModel matchesColumnModel			= null;

	/** model for matches statistic table **/
	private  MatchesOverviewColumnModel matchesOverview1ColumnModel		= null;
	
	/** model for player overview **/
	private  PlayerOverviewModel playerOverviewColumnModel	= null;
	
	/** model for lineup table **/
	private  LineupTableModel lineupColumnModel			= null;
	
	/** model for player analysis **/
	private PlayerAnalysisModel playerAnalysis1Model 		= null;
	
	/** model for player analysis **/
	private PlayerAnalysisModel playerAnalysis2Model 		= null;

	// Youth module
	private YouthPlayerOverviewTableModel youthPlayerOverviewColumnModel;
	private YouthTrainingViewTableModel youthTrainingViewColumnModel;
	private YouthPlayerDetailsTableModel youthPlayerDetailsTableModel;
	private RecapPanelTableModel teamAnalyzerRecapModel;

	/**
	 * constructor
	 *
	 */
	private UserColumnController(){
		
	}
	/**
	 * singelton
	 * @return UserColumnController
	 */
	public static UserColumnController instance(){
		return columnController;
	}
	
	/**
	 * load all models from db
	 *
	 */
	public void load() {
		final DBManager dbZugriff = DBManager.instance();

		dbZugriff.loadHOColumModel(getMatchesModel());
		dbZugriff.loadHOColumModel(getPlayerOverviewModel());
		dbZugriff.loadHOColumModel(getLineupModel());
		dbZugriff.loadHOColumModel(getAnalysis1Model());
		dbZugriff.loadHOColumModel(getAnalysis2Model());
		dbZugriff.loadHOColumModel(getMatchesOverview1ColumnModel());

		dbZugriff.loadHOColumModel(getYouthTrainingViewColumnModel());
		dbZugriff.loadHOColumModel(getYouthPlayerOverviewColumnModel());
		dbZugriff.loadHOColumModel(getYouthPlayerDetailsColumnModel());
		dbZugriff.loadHOColumModel(getTeamAnalyzerRecapModell());
	}
	
	/**
	 * 
	 * @return PlayerAnalysisModel
	 */
	public PlayerAnalysisModel getAnalysis1Model(){
		if(playerAnalysis1Model == null)
			playerAnalysis1Model = new PlayerAnalysisModel(ColumnModelId.PLAYERANALYSIS1, 1);
		
		return playerAnalysis1Model;
	}
	
	/**
	 * 
	 * @return PlayerAnalysisModel
	 */
	public PlayerAnalysisModel getAnalysis2Model(){
		if(playerAnalysis2Model == null)
			playerAnalysis2Model = new PlayerAnalysisModel(ColumnModelId.PLAYERANALYSIS2, 2);
		
		return playerAnalysis2Model;
	}
	/**
	 * 
	 * @return MatchesColumnModel
	 */
	public MatchesColumnModel getMatchesModel(){
		if(matchesColumnModel == null)
			matchesColumnModel = new MatchesColumnModel(ColumnModelId.MATCHES);
		
		return matchesColumnModel;
	}
	
	public MatchesOverviewColumnModel getMatchesOverview1ColumnModel(){
		if(matchesOverview1ColumnModel == null)
			matchesOverview1ColumnModel = new MatchesOverviewColumnModel(ColumnModelId.MATCHESOVERVIEW);
		return matchesOverview1ColumnModel;	
	}
	
	/**
	 * 
	 * @return PlayerOverviewModel
	 */
	public PlayerOverviewModel getPlayerOverviewModel(){
		if(playerOverviewColumnModel == null){
			playerOverviewColumnModel = new PlayerOverviewModel(ColumnModelId.PLAYEROVERVIEW);
		}
		return playerOverviewColumnModel;
	}
	
	/**
	 * 
	 * @return LineupColumnModel
	 */
	public LineupTableModel getLineupModel(){
		if(lineupColumnModel == null){
			lineupColumnModel = new LineupTableModel(ColumnModelId.LINEUP);
		}
		return lineupColumnModel;
	}
	

	/**
	 * return all model as Vector
	 * @return
	 */
	public Vector<HOTableModel> getAllModels() {
		Vector<HOTableModel> v = new Vector<>();
		v.add(getPlayerOverviewModel());
		v.add(getLineupModel());
		v.add(getAnalysis1Model());
		v.add(getAnalysis2Model());
		v.add(getTeamAnalyzerRecapModell());
		// MatchesOverView1Model should not add in this vector, because columns should not be edit
		return v;
	}

	public YouthPlayerOverviewTableModel getYouthPlayerOverviewColumnModel() {
		if(youthPlayerOverviewColumnModel == null){
			youthPlayerOverviewColumnModel = new YouthPlayerOverviewTableModel(ColumnModelId.YOUTHPLAYEROVERVIEW);
		}
		return youthPlayerOverviewColumnModel;
	}

	public YouthTrainingViewTableModel getYouthTrainingViewColumnModel() {
		if(youthTrainingViewColumnModel == null){
			youthTrainingViewColumnModel = new YouthTrainingViewTableModel(ColumnModelId.YOUTHTRAININGVIEW);
		}
		return youthTrainingViewColumnModel;
	}
	public YouthPlayerDetailsTableModel getYouthPlayerDetailsColumnModel() {
		if(youthPlayerDetailsTableModel == null){
			youthPlayerDetailsTableModel = new YouthPlayerDetailsTableModel(ColumnModelId.YOUTHPLAYERDETAILS);
		}
		return youthPlayerDetailsTableModel;
	}

	public RecapPanelTableModel getTeamAnalyzerRecapModell() {
		if (teamAnalyzerRecapModel==null){
			teamAnalyzerRecapModel = new RecapPanelTableModel(ColumnModelId.TEAMANALYZERRECAP);
		}
		return teamAnalyzerRecapModel;
	}
}
