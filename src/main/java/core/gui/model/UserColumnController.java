package core.gui.model;

import core.db.DBManager;
import core.gui.comp.table.HOTableModel;
import module.matches.statistics.MatchesOverviewColumnModel;
import module.specialEvents.SpecialEventsTableModel;
import module.teamAnalyzer.ui.RecapPanelTableModel;
import module.training.ui.TrainingProgressTableModel;
import module.training.ui.model.ChangesTableModel;
import module.training.ui.EffectTableModel;
import module.training.ui.model.SkillupTableModel;
import module.training.ui.TrainingPredictionTableModel;
import module.training.ui.model.TrainingSettingsTableModel;
import module.transfer.history.PlayerTransferTableModel;
import module.transfer.history.TransferTableModel;
import module.transfer.scout.TransferScoutingTableModel;
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
		TEAMANALYZERRECAP(10),
		TEAMTRANSFER(11),
		PLAYERTRANSFER(12),
		SPECIALEVENTS(13),
		TRANSFERSCOUT(14),
		TRAININGPROGRESS(15),
		TRAININGPLAYSERSKILLCHANGE(16),
        TRAININGPREDICTION(17),
        TRAININGANALYSIS(18),
        TRAININGSETTINGSFUTURE(19),
        TRAININGSETTINGSPAST(20),
        TRAININGEFFECT(21);

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
	private PlayerOverviewTableModel playerOverviewColumnModel	= null;
	
	/** model for lineup table **/
	private PlayerOverviewTableModel lineupColumnModel			= null;
	
	/** model for player analysis **/
	private final PlayerAnalysisModel[] playerAnalysisModels 		= new PlayerAnalysisModel[2];

	// Youth module
	private YouthPlayerOverviewTableModel youthPlayerOverviewColumnModel;
	private YouthTrainingViewTableModel youthTrainingViewColumnModel;
	private YouthPlayerDetailsTableModel youthPlayerDetailsTableModel;
	private RecapPanelTableModel teamAnalyzerRecapModel;

	private TransferTableModel transferTableModel;
	private PlayerTransferTableModel playerTransferTableModel;
	private TransferScoutingTableModel transferScoutingTableModel;

	private SpecialEventsTableModel specialEventsTableModel;

	private TrainingProgressTableModel trainingProgressTableModel;
	private SkillupTableModel trainingPlayerSkillChangeTableModel;
    private TrainingPredictionTableModel trainingPredictionTableModel;
    private ChangesTableModel trainingAnalysisTableModel;
    private TrainingSettingsTableModel trainingSettingsFutureTableModel;
    private TrainingSettingsTableModel trainingSettingsPastTableModel;
    private EffectTableModel effectTableModel;

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
		final DBManager dbManager = DBManager.instance();

		dbManager.loadHOColumModel(getMatchesModel());
		dbManager.loadHOColumModel(getPlayerOverviewModel());
		dbManager.loadHOColumModel(getLineupModel());
		dbManager.loadHOColumModel(getAnalysisModel(1));
		dbManager.loadHOColumModel(getAnalysisModel(2));
		dbManager.loadHOColumModel(getMatchesOverview1ColumnModel());

		dbManager.loadHOColumModel(getYouthTrainingViewColumnModel());
		dbManager.loadHOColumModel(getYouthPlayerOverviewColumnModel());
		dbManager.loadHOColumModel(getYouthPlayerDetailsColumnModel());
		dbManager.loadHOColumModel(getTeamAnalyzerRecapModel());
		dbManager.loadHOColumModel(getTransferTableModel());
		dbManager.loadHOColumModel(getPlayerTransferTableModel());
		dbManager.loadHOColumModel(getTransferScoutingTableModel());
		dbManager.loadHOColumModel(getSpecialEventsTableModel());
        dbManager.loadHOColumModel(getTrainingProgressTableModel());
        dbManager.loadHOColumModel(getTrainingPredictionTableModel());
        dbManager.loadHOColumModel(getTrainingPlayerSkillChangesTableModel());
        dbManager.loadHOColumModel(getTrainingAnalysisTableModel());
        dbManager.loadHOColumModel(getTrainingSettingsFutureTableModel());
        dbManager.loadHOColumModel(getTrainingSettingsPastTableModel());
        dbManager.loadHOColumModel(getTrainingEffectTableModel());
	}

    public EffectTableModel getTrainingEffectTableModel() {
        if ( this.effectTableModel == null){
            this.effectTableModel = new EffectTableModel(ColumnModelId.TRAININGEFFECT);
        }
        return this.effectTableModel;
    }


    public TrainingSettingsTableModel getTrainingSettingsPastTableModel() {
        if (this.trainingSettingsPastTableModel == null){
            this.trainingSettingsPastTableModel = new TrainingSettingsTableModel(ColumnModelId.TRAININGSETTINGSPAST, "ls.module.training.settings.past");
        }
        return this.trainingSettingsPastTableModel;
    }

    public TrainingSettingsTableModel getTrainingSettingsFutureTableModel() {
        if (this.trainingSettingsFutureTableModel == null){
            this.trainingSettingsFutureTableModel = new TrainingSettingsTableModel(ColumnModelId.TRAININGSETTINGSFUTURE, "ls.module.training.settings.future");
        }
        return this.trainingSettingsFutureTableModel;
    }

    public ChangesTableModel getTrainingAnalysisTableModel() {
        if ( this.trainingAnalysisTableModel == null){
            this.trainingAnalysisTableModel = new ChangesTableModel(ColumnModelId.TRAININGANALYSIS);
        }
        return this.trainingAnalysisTableModel;
    }

    public TrainingProgressTableModel getTrainingProgressTableModel() {
        if ( this.trainingProgressTableModel == null){
            this.trainingProgressTableModel = new TrainingProgressTableModel(ColumnModelId.TRAININGPROGRESS);
        }
        return this.trainingProgressTableModel;
    }

    public TrainingPredictionTableModel getTrainingPredictionTableModel() {
        if ( this.trainingPredictionTableModel == null){
            this.trainingPredictionTableModel = new TrainingPredictionTableModel(ColumnModelId.TRAININGPREDICTION);
        }
        return this.trainingPredictionTableModel;
    }

    public SkillupTableModel getTrainingPlayerSkillChangesTableModel() {
		if ( this.trainingPlayerSkillChangeTableModel == null){
			this.trainingPlayerSkillChangeTableModel = new SkillupTableModel(ColumnModelId.TRAININGPLAYSERSKILLCHANGE);
		}
		return this.trainingPlayerSkillChangeTableModel;
	}

	public SpecialEventsTableModel getSpecialEventsTableModel() {
		if(specialEventsTableModel == null) {
			specialEventsTableModel = new SpecialEventsTableModel(ColumnModelId.SPECIALEVENTS);
		}
		return specialEventsTableModel;
	}

	/**
	 * 
	 * @return PlayerAnalysisModel
	 */
	public PlayerAnalysisModel getAnalysisModel(int instance){
		if(playerAnalysisModels[instance-1] == null)
			playerAnalysisModels[instance-1] = new PlayerAnalysisModel(ColumnModelId.PLAYERANALYSIS1, instance);
		
		return playerAnalysisModels[instance-1];
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
	public PlayerOverviewTableModel getPlayerOverviewModel(){
		if(playerOverviewColumnModel == null){
			playerOverviewColumnModel = new PlayerOverviewTableModel(ColumnModelId.PLAYEROVERVIEW);
		}
		return playerOverviewColumnModel;
	}
	
	/**
	 * 
	 * @return LineupColumnModel
	 */
	public PlayerOverviewTableModel getLineupModel(){
		if(lineupColumnModel == null){
			lineupColumnModel = new PlayerOverviewTableModel(ColumnModelId.LINEUP, "Aufstellung");
		}
		return lineupColumnModel;
	}

	/**
	 * return all model as Vector
	 * @return HOTableModels
	 */
	public Vector<HOTableModel> getAllModels() {
		Vector<HOTableModel> v = new Vector<>();
		v.add(getPlayerOverviewModel());
		v.add(getLineupModel());
		v.add(getAnalysisModel(1));
		v.add(getAnalysisModel(2));
		v.add(getTeamAnalyzerRecapModel());
		v.add(getYouthPlayerOverviewColumnModel());
		v.add(getYouthPlayerDetailsColumnModel());
		v.add(getTrainingProgressTableModel());
		v.add(getTrainingPlayerSkillChangesTableModel());
        v.add(getTrainingSettingsPastTableModel());
        v.add(getTrainingSettingsFutureTableModel());
		// MatchesOverView1Model should not add in this vector, because columns should not be edited
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

	public RecapPanelTableModel getTeamAnalyzerRecapModel() {
		if (teamAnalyzerRecapModel == null) {
			teamAnalyzerRecapModel = new RecapPanelTableModel(ColumnModelId.TEAMANALYZERRECAP);
		}
		return teamAnalyzerRecapModel;
	}

	public TransferTableModel getTransferTableModel(){
		if (transferTableModel==null){
			transferTableModel = new TransferTableModel();
		}
		return transferTableModel;
	}
	public PlayerTransferTableModel getPlayerTransferTableModel(){
		if (playerTransferTableModel==null){
			playerTransferTableModel = new PlayerTransferTableModel();
		}
		return playerTransferTableModel;
	}
	public TransferScoutingTableModel getTransferScoutingTableModel(){
		if (transferScoutingTableModel==null){
			transferScoutingTableModel = new TransferScoutingTableModel();
		}
		return transferScoutingTableModel;
	}
}
