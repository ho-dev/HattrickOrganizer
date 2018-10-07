package core.gui.model;

import core.db.DBManager;
import core.gui.comp.table.HOTableModel;
import module.lineup.LineupTableModel;
import module.matches.statistics.MatchesOverviewColumnModel;

import java.util.Vector;

/**
 * Controller for the UserColumns.
 * Create columns and managed the models
 * @author Thorsten Dietz
 * @since 1.36
 *
 */
public final class UserColumnController {

	/** singleton **/
	private static UserColumnController columnController = new UserColumnController();
	
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
	public final void load(){
		final DBManager dbZugriff = DBManager.instance();
		
		dbZugriff.loadHOColumModel( getMatchesModel() );
		dbZugriff.loadHOColumModel( getPlayerOverviewModel() );
		dbZugriff.loadHOColumModel( getLineupModel() );
		dbZugriff.loadHOColumModel( getAnalysis1Model() );
		dbZugriff.loadHOColumModel( getAnalysis2Model() );
		dbZugriff.loadHOColumModel( getMatchesOverview1ColumnModel() );
	}
	
	/**
	 * 
	 * @return PlayerAnalysisModel
	 */
	public final PlayerAnalysisModel getAnalysis1Model(){
		if(playerAnalysis1Model == null)
			playerAnalysis1Model = new PlayerAnalysisModel(4,1);
		
		return playerAnalysis1Model;
	}
	
	/**
	 * 
	 * @return PlayerAnalysisModel
	 */
	public final PlayerAnalysisModel getAnalysis2Model(){
		if(playerAnalysis2Model == null)
			playerAnalysis2Model = new PlayerAnalysisModel(5,2);
		
		return playerAnalysis2Model;
	}
	/**
	 * 
	 * @return MatchesColumnModel
	 */
	public final MatchesColumnModel getMatchesModel(){
		if(matchesColumnModel == null)
			matchesColumnModel = new MatchesColumnModel(1);
		
		return matchesColumnModel;
	}
	
	public final MatchesOverviewColumnModel getMatchesOverview1ColumnModel(){
		if(matchesOverview1ColumnModel == null)
			matchesOverview1ColumnModel = new MatchesOverviewColumnModel(6);
		return matchesOverview1ColumnModel;	
	}
	
	/**
	 * 
	 * @return PlayerOverviewModel
	 */
	public final PlayerOverviewModel getPlayerOverviewModel(){
		if(playerOverviewColumnModel == null){
			playerOverviewColumnModel = new PlayerOverviewModel(2);
		}
		return playerOverviewColumnModel;
	}
	
	/**
	 * 
	 * @return LineupColumnModel
	 */
	public final LineupTableModel getLineupModel(){
		if(lineupColumnModel == null){
			lineupColumnModel = new LineupTableModel(3);
			
		}
		return lineupColumnModel;
	}
	

	/**
	 * return all model as Vector
	 * @return
	 */
	public Vector<HOTableModel> getAllModels(){
		Vector<HOTableModel> v = new Vector<HOTableModel>();
		
		v.add( getPlayerOverviewModel() );
		v.add( getLineupModel() );
		v.add( getAnalysis1Model() );
		v.add( getAnalysis2Model() );
		// MatchesOverView1Model should not add in this vector, because columns should not be edit
		return v;
	}
	

	

	

	

}
