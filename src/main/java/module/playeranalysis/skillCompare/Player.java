package module.playeranalysis.skillCompare;

import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;

import static core.model.player.IMatchRoleID.KEEPER;
import static core.model.player.IMatchRoleID.UNKNOWN;

/**
 * @author KickMuck
 */
public class Player
{
	private core.model.player.Player m_Player;
	private String m_FirstName;
	private String m_NickName;
	private String m_LastName;
	private int m_Age;
	private int m_ID;
	private int m_Wages;
	private int m_TSI;
	private int m_Nationality;
	private int m_Leadership;
	private int m_Experience;
	private int m_Stamina;
	private int m_Form;
	private int m_Keeping;
	private int m_Defending;
	private int m_Playmaking;
	private int m_Passing;
	private int m_Winger;
	private int m_Scoring;
	private int m_SetPieces;
	private int m_Loyalty;
	private int m_HomeGrown;
	private int m_OldExperience;
	private int m_OldStamina;
	private int m_OldForm;
	private int m_OldKeeping;
	private int m_OldDefending;
	private int m_OldPlaymaking;
	private int m_OldPassing;
	private int m_OldWinger;
	private int m_OldScoring;
	private int m_OldSetPieces;
	private int m_OldLoyalty;
	private int m_OldHomeGrown;
	private float m_PosVal_GK;
	private float m_PosVal_CD;
	private float m_PosVal_CD_TW;
	private float m_PosVal_CD_O;
	private float m_PosVal_WB;
	private float m_PosVal_WB_TM;
	private float m_PosVal_WB_O;
	private float m_PosVal_WB_D;
	private float m_PosVal_IM;
	private float m_PosVal_IM_TW;
	private float m_PosVal_IM_O;
	private float m_PosVal_IM_D;
	private float m_PosVal_W;
	private float m_PosVal_W_TM;
	private float m_PosVal_W_O;
	private float m_PosVal_W_D;
	private float m_PosVal_F;
	private float m_PosVal_F_D;
	private float m_PosVal_F_TW;
	private float m_OldPosVal_GK;
	private float m_OldPosVal_CD;
	private float m_OldPosVal_CD_TW;
	private float m_OldPosVal_CD_O;
	private float m_OldPosVal_WB;
	private float m_OldPosVal_WB_TM;
	private float m_OldPosVal_WB_O;
	private float m_OldPosVal_WB_D;
	private float m_OldPosVal_IM;
	private float m_OldPosVal_IM_TW;
	private float m_OldPosVal_IM_O;
	private float m_OldPosVal_IM_D;
	private float m_OldPosVal_W;
	private float m_OldPosVal_W_TM;
	private float m_OldPosVal_W_O;
	private float m_OldPosVal_W_D;
	private float m_OldPosVal_F;
	private float m_OldPosVal_F_D;
	private float m_OldPosVal_F_TW;
	//private byte m_BestPosition;
	private float m_BestPositionRating;
	private byte m_OldBestPosition;
	private float m_OldBestPositionRating;
	private String m_Group;
	private int m_Speciality;
	
	//Konstruktor
	public Player(core.model.player.Player player)
	{
		m_Player = player;
		setPlayerValues();
		setOldSkillValues();
		setOldPositionValues();
		setNewSkillValues();
		setNewPositionValues();
		setPlayerValues();
	}
	
	public Player()	{}
	
	/* @function getPositionCompareAsString(int position)
	 * returns a string in the format ("1.35;0.15") where the first value is the
	 * new position value and the second is the change from the original value
	 * 
	 * int position
	 * 
	 * return: String
	 */
	public String getPositionCompareAsString(byte position)
	{
		String s = "";
		switch (position) {
			case KEEPER -> s += getPosVal_GK() + ";" + (getPosVal_GK() - getOldPosVal_GK());
			case IMatchRoleID.CENTRAL_DEFENDER -> s += getPosVal_CD() + ";" + (getPosVal_CD() - getOldPosVal_CD());
			case IMatchRoleID.CENTRAL_DEFENDER_OFF -> s += getPosVal_CD_O() + ";" + (getPosVal_CD_O() - getOldPosVal_CD_O());
			case IMatchRoleID.CENTRAL_DEFENDER_TOWING -> s += getPosVal_CD_TW() + ";" + (getPosVal_CD_TW() - getOldPosVal_CD_TW());
			case IMatchRoleID.BACK -> s += getPosVAL_WB() + ";" + (getPosVAL_WB() - getOldPosVal_WB());
			case IMatchRoleID.BACK_TOMID -> s += getPosVal_WB_TM() + ";" + (getPosVal_WB_TM() - getOldPosVal_WB_TM());
			case IMatchRoleID.BACK_OFF -> s += getPosVal_WB_O() + ";" + (getPosVal_WB_O() - getOldPosVal_WB_O());
			case IMatchRoleID.BACK_DEF -> s += getPosVal_WB_D() + ";" + (getPosVal_WB_D() - getOldPosVAL_WB_D());
			case IMatchRoleID.MIDFIELDER -> s += getPosVal_IM() + ";" + (getPosVal_IM() - getOldPosVal_IM());
			case IMatchRoleID.MIDFIELDER_OFF -> s += getPosVal_IM_O() + ";" + (getPosVal_IM_O() - getOldPosVal_IM_O());
			case IMatchRoleID.MIDFIELDER_DEF -> s += getPosVal_IM_D() + ";" + (getPosVal_IM_D() - getOldPosVal_IM_D());
			case IMatchRoleID.MIDFIELDER_TOWING -> s += getPosVal_IM_TW() + ";" + (getPosVal_IM_TW() - getOldPosVal_IM_TW());
			case IMatchRoleID.WINGER -> s += getPosVal_W() + ";" + (getPosVal_W() - getOldPosVal_W());
			case IMatchRoleID.WINGER_OFF -> s += getPosVal_W_O() + ";" + (getPosVal_W_O() - getOldPosVal_W_O());
			case IMatchRoleID.WINGER_DEF -> s += getPosVal_W_D() + ";" + (getPosVal_W_D() - getOldPosVal_W_D());
			case IMatchRoleID.WINGER_TOMID -> s += getPosVal_W_TM() + ";" + (getPosVal_W_TM() - getOldPosVal_W_TM());
			case IMatchRoleID.FORWARD -> s += getPosVal_F() + ";" + (getPosVal_F() - getOldPosVal_F());
			case IMatchRoleID.FORWARD_DEF -> s += getPosVal_F_D() + ";" + (getPosVal_F_D() - getOldPosVal_F_D());
			case IMatchRoleID.FORWARD_TOWING -> s += getPosVal_F_TW() + ";" + (getPosVal_F_TW() - getOldPosVal_F_TW());
		}
		return s;
	}
	
	/* @function getSkillCompareAsDouble(int skill)
	 * returns a Double in format (5.06) where 5 is the new skill value
	 * and 06  is the old value multiplied by 0.01
	 * 
	 * int skill
	 * 
	 * return: double
	 */
	public double getSkillCompareAsDouble(int skill)
	{
		double combined = 0;
		switch (skill) {
			case 0 -> combined = getExperience() + (getOldExperience() * 0.01);
			case 1 -> combined = getForm() + (getOldForm() * 0.01);
			case 2 -> combined = getStamina() + (getOldStamina() * 0.01);
			case 3 -> combined = getKeeping() + (getOldKeeping() * 0.01);
			case 4 -> combined = getDefending() + (getOldDefending() * 0.01);
			case 5 -> combined = getPlaymaking() + (getOldPlaymaking() * 0.01);
			case 6 -> combined = getPassing() + (getOldPassing() * 0.01);
			case 7 -> combined = getWinger() + (getOldWinger() * 0.01);
			case 8 -> combined = getScoring() + (getOldScoring() * 0.01);
			case 9 -> combined = getSetPieces() + (getOldSetPieces() * 0.01);
			case 10 -> combined = getLoyalty() + (getOldLoyalty() * 0.01);
			case 11 -> combined = getHomeGrown() + (getOldHomeGrown() * 0.01);
		}
		return combined;
	}

	/* @function changeSkill(int skill, int wert)
	 * Function that changes the values of the players
	 * Called by changePlayerSkillValues() 
	 */
	public void changeSkill(int skill, int wert)
	{
		switch (skill) {
			case 0 -> m_Player.setExperience(wert);
			case 1 -> m_Player.setForm(wert);
			case 2 -> m_Player.setStamina(wert);
			case 3 -> m_Player.setGoalkeeperSkill(wert);
			case 4 -> m_Player.setDefendingSkill(wert);
			case 5 -> m_Player.setPlaymakingSkill(wert);
			case 6 -> m_Player.setPassingSkill(wert);
			case 7 -> m_Player.setWingerSkill(wert);
			case 8 -> m_Player.setScoringSkill(wert);
			case 9 -> m_Player.setSetPiecesSkill(wert);
			case 10 -> m_Player.setLoyalty(wert);
			case 11 -> {
				if (wert == 2)
					m_Player.setHomeGrown(true);
				else if (wert == 1)
					m_Player.setHomeGrown(false);
            }
		}
	}
	
	/* @function changePlayerSkillValues(boolean direction)
	 * Function that calculates the values of the player
	 * for changing in the data store.
	 * Is used to calculate the rating of each position with 
	 * Player.calcPositionValue()
	 * 
	 * boolean direction: determines if the new or original values 
	 * 		should be saved in the database
	 * 		true: save new values
	 * 		false: save old values
	 */
	public void changePlayerSkillValues(boolean direction)
	{
		// Array for the old values
		int[] oldSkillValues = getOldSkillValues();
		
		if(direction)
		{
			// Array for newly set values
			int[] newRatings = PlayerComparePanel.getNewRating();
			// Array for the changes
			int[] changedSkills = PlayerComparePanel.getChangeRatingBy();
			
			for(int j = 0; j < newRatings.length; j++)
			{
				if(newRatings[j] == 0)
				{
					if(changedSkills[j] != 0)
					{
						setNewSkillValues(j, (oldSkillValues[j] + changedSkills[j]));
						changeSkill(j, (oldSkillValues[j] + changedSkills[j]));
					}
				}
				else
				{
					setNewSkillValues(j, newRatings[j]);
					changeSkill(j, newRatings[j]);
				}
			}
			setNewPositionValues();
		}
		else
		{
			for(int i = 0; i < oldSkillValues.length; i++)
			{
				changeSkill(i, oldSkillValues[i]);
			}
		}
		
	}
	
	public void resetPlayers()
	{
		int[] oldSkillValues = getOldSkillValues();
		for(int i = 0; i < oldSkillValues.length; i++)
		{
			changeSkill(i, oldSkillValues[i]);
			setNewSkillValues(i,oldSkillValues[i]);
		}
	}
	
	public void setPlayerValues()
	{
		setID(m_Player.getPlayerId());
		setFirstName(m_Player.getFirstName());
		setNickName(m_Player.getNickName());
		setLastName(m_Player.getLastName());
		setAge(m_Player.getAge());
		setWages(m_Player.getWage());
		setTSI(m_Player.getTsi());
		setNationality(m_Player.getNationalityId());
		setLeadership(m_Player.getLeadership());
		setGroup(m_Player.getTeamGroup());
		setSpeciality(m_Player.getSpecialty());
	}
	
	public void setOldSkillValues()
	{
		setOldExperience(m_Player.getExperience());
		setOldForm(m_Player.getForm());
		setOldStamina(m_Player.getStamina());
		setOldKeeping(m_Player.getGoalkeeperSkill());
		setOldDefending(m_Player.getDefendingSkill());
		setOldPlaymaking(m_Player.getPlaymakingSkill());
		setOldPassing(m_Player.getPassingSkill());
		setOldWinger(m_Player.getWingerSkill());
		setOldScoring(m_Player.getScoringSkill());
		setOldSetPieces(m_Player.getSetPiecesSkill());
		setOldLoyalty(m_Player.getLoyalty());
		setOldHomeGrown(m_Player.isHomeGrown() ? 2 : 1);
	}
	
	public int[] getOldSkillValues()
	{
		int[] oldSkills = new int[12];
		oldSkills[0] = getOldExperience();
		oldSkills[1] = getOldForm();
		oldSkills[2] = getOldStamina();
		oldSkills[3] = getOldKeeping();
		oldSkills[4] = getOldDefending();
		oldSkills[5] = getOldPlaymaking();
		oldSkills[6] = getOldPassing();
		oldSkills[7] = getOldWinger();
		oldSkills[8] = getOldScoring();
		oldSkills[9] = getOldSetPieces();
		oldSkills[10] = getOldLoyalty();
		oldSkills[11] = getOldHomeGrown();
		return oldSkills;
	}
	public void setNewSkillValues()
	{
		setExperience(m_Player.getExperience());
		setForm(m_Player.getForm());
		setStamina(m_Player.getStamina());
		setKeeping(m_Player.getGoalkeeperSkill());
		setDefending(m_Player.getDefendingSkill());
		setPlaymaking(m_Player.getPlaymakingSkill());
		setPassing(m_Player.getPassingSkill());
		setWinger(m_Player.getWingerSkill());
		setScoring(m_Player.getScoringSkill());
		setSetPieces(m_Player.getSetPiecesSkill());
		setLoyalty(m_Player.getLoyalty());
		setHomeGrown(m_Player.isHomeGrown() ? 2 : 1);
	}
	
	public void setNewSkillValues(int skill, int wert)
	{
		switch (skill) {
			case 0 -> setExperience(wert);
			case 1 -> setForm(wert);
			case 2 -> setStamina(wert);
			case 3 -> setKeeping(wert);
			case 4 -> setDefending(wert);
			case 5 -> setPlaymaking(wert);
			case 6 -> setPassing(wert);
			case 7 -> setWinger(wert);
			case 8 -> setScoring(wert);
			case 9 -> setSetPieces(wert);
			case 10 -> setLoyalty(wert);
			case 11 -> setHomeGrown(wert);
		}
	}
	
	public void setOldPositionValues()
	{
		var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
		setOldPos_GK((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, KEEPER));
		setOldPosVal_CD((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.CENTRAL_DEFENDER));
		setOldPosVal_CD_TW((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.CENTRAL_DEFENDER_TOWING));
		setOldPosVal_CD_O((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.CENTRAL_DEFENDER_OFF));
		setOldPosVal_WB((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.BACK));
		setOldPosVal_WB_TM((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.BACK_TOMID));
		setOldPosVal_WB_O((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.BACK_OFF));
		setOldPosVal_WB_D((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.BACK_DEF));
		setOldPosVal_IM((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.MIDFIELDER));
		setOldPosVal_IM_O((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.MIDFIELDER_OFF));
		setOldPosVal_IM_D((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.MIDFIELDER_DEF));
		setOldPosVal_IM_TW((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.MIDFIELDER_TOWING));
		setOldPosVal_W((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.WINGER));
		setOldPosVal_W_D((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.WINGER_DEF));
		setOldPosVal_W_TM((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.WINGER_TOMID));
		setOldPosVal_W_O((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.WINGER_OFF));
		setOldPosVal_F((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.FORWARD));
		setOldPosVal_F_D((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.FORWARD_DEF));
		setOldPosVal_F_TW((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.FORWARD_TOWING));
		setOldBestPosition(m_Player.getIdealPosition());
		setOldBestPositionRating((float)m_Player.getIdealPositionRating());
	}
	
	public void setNewPositionValues()
	{
		var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
		setPosVal_GK((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, KEEPER));
		setPosVal_CD((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.CENTRAL_DEFENDER));
		setPosVal_CD_TW((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.CENTRAL_DEFENDER_TOWING));
		setPosVal_CD_O((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.CENTRAL_DEFENDER_OFF));
		setPosVal_WB((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.BACK));
		setPosVAL_WB_TM((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.BACK_TOMID));
		setPosVal_WB_O((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.BACK_OFF));
		setPosVAL_WB_D((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.BACK_DEF));
		setPosVal_IM((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.MIDFIELDER));
		setPosVal_IM_O((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.MIDFIELDER_OFF));
		setPosVal_IM_D((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.MIDFIELDER_DEF));
		setPosVal_IM_TW((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.MIDFIELDER_TOWING));
		setPosVal_W((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.WINGER));
		setPosVal_W_D((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.WINGER_DEF));
		setPosVal_W_TM((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.WINGER_TOMID));
		setPosVal_W_O((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.WINGER_OFF));
		setPosVal_F((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.FORWARD));
		setPosVal_F_D((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.FORWARD_DEF));
		setPosVal_F_TW((float)ratingPredictionModel.getPlayerMatchAverageRating(m_Player, IMatchRoleID.FORWARD_TOWING));
		setBestPositionRating((float)m_Player.getIdealPositionRating());
		changePlayerSkillValues(false);
	}

	public int getAge() {
		return m_Age;
	}
	public void setAge(int val) {
		m_Age = val;
	}
	public byte getBestPosition() {
		if ( m_Player != null) return m_Player.getIdealPosition();
		return UNKNOWN;
	}

	public byte getOldBestPosition() {
		return m_OldBestPosition;
	}
	public void setOldBestPosition(byte val) {
		m_OldBestPosition = val;
	}
	public float getBestPositionRating() {
		return m_BestPositionRating;
	}
	public void setBestPositionRating(float val) {
		m_BestPositionRating = val;
	}
	public float getOldBestPositionRating() {
		return m_OldBestPositionRating;
	}
	public void setOldBestPositionRating(float val) {
		m_OldBestPositionRating = val;
	}
	public int getExperience() {
		return m_Experience;
	}
	public void setExperience(int val) {
		m_Experience = val;
	}
	public int getOldExperience() {
		return m_OldExperience;
	}
	public void setOldExperience(int val) {
		m_OldExperience = val;
	}
	public int getWinger() {
		return m_Winger;
	}
	public void setWinger(int val) {
		m_Winger = val;
	}
	public int getOldWinger() {
		return m_OldWinger;
	}
	public void setOldWinger(int val) {
		m_OldWinger = val;
	}
	public int getForm() {
		return m_Form;
	}
	public void setForm(int form) {
		m_Form = form;
	}
	public int getOldForm() {
		return m_OldForm;
	}
	public void setOldForm(int val) {
		m_OldForm = val;
	}
	public int getLeadership() {
		return m_Leadership;
	}
	public void setLeadership(int val) {
		m_Leadership = val;
	}
	public int getWages() {
		return m_Wages;
	}
	public void setWages(int val) {
		m_Wages = val;
	}
	public String getGroup() {
		return m_Group;
	}
	public void setGroup(String val) {
		m_Group = val;
	}
	public int getId() {
		return m_ID;
	}
	public void setID(int val) {
		m_ID = val;
	}
	public int getStamina() {
		return m_Stamina;
	}
	public void setStamina(int val) {
		m_Stamina = val;
	}
	public int getOldStamina() {
		return m_OldStamina;
	}
	public void setOldStamina(int val) {
		m_OldStamina = val;
	}

	public void setFirstName(String name) {
		m_FirstName = name;
	}

	public void setNickName(String name) {
		m_NickName = name;
	}

	public void setLastName(String name) {
		m_LastName = name;
	}
	public String getFullName() {
		return m_FirstName + " " + m_NickName + " " + m_LastName;
	}
	public int getNationality() {
		return m_Nationality;
	}
	public void setNationality(int val) {
		m_Nationality = val;
	}
	public float getPosVAL_WB() {
		return m_PosVal_WB;
	}
	public void setPosVal_WB(float val) {
		m_PosVal_WB = val;
	}
	public float getOldPosVal_WB() {
		return m_OldPosVal_WB;
	}
	public void setOldPosVal_WB(float val) {
		m_OldPosVal_WB = val;
	}
	public float getPosVal_WB_D() {
		return m_PosVal_WB_D;
	}
	public void setPosVAL_WB_D(float val) {
		m_PosVal_WB_D = val;
	}
	public float getOldPosVAL_WB_D() {
		return m_OldPosVal_WB_D;
	}
	public void setOldPosVal_WB_D(float val) {
		m_OldPosVal_WB_D = val;
	}
	public float getPosVal_WB_TM() {
		return m_PosVal_WB_TM;
	}
	public void setPosVAL_WB_TM(float val) {
		m_PosVal_WB_TM = val;
	}
	public float getOldPosVal_WB_TM() {
		return m_OldPosVal_WB_TM;
	}
	public void setOldPosVal_WB_TM(float val) {
		m_OldPosVal_WB_TM = val;
	}
	public float getPosVal_WB_O() {
		return m_PosVal_WB_O;
	}
	public void setPosVal_WB_O(float val) {
		m_PosVal_WB_O = val;
	}
	public float getOldPosVal_WB_O() {
		return m_OldPosVal_WB_O;
	}
	public void setOldPosVal_WB_O(float val) {
		m_OldPosVal_WB_O = val;
	}
	public float getPosVal_W() {
		return m_PosVal_W;
	}
	public void setPosVal_W(float val) {
		m_PosVal_W = val;
	}
	public float getOldPosVal_W() {
		return m_OldPosVal_W;
	}
	public void setOldPosVal_W(float val) {
		m_OldPosVal_W = val;
	}
	public float getPosVal_W_D() {
		return m_PosVal_W_D;
	}
	public void setPosVal_W_D(float val) {
		m_PosVal_W_D = val;
	}
	public float getOldPosVal_W_D() {
		return m_OldPosVal_W_D;
	}
	public void setOldPosVal_W_D(float val) {
		m_OldPosVal_W_D = val;
	}
	public float getPosVal_W_TM() {
		return m_PosVal_W_TM;
	}
	public void setPosVal_W_TM(float val) {
		m_PosVal_W_TM = val;
	}
	public float getOldPosVal_W_TM() {
		return m_OldPosVal_W_TM;
	}
	public void setOldPosVal_W_TM(float val) {
		m_OldPosVal_W_TM = val;
	}
	public float getPosVal_W_O() {
		return m_PosVal_W_O;
	}
	public void setPosVal_W_O(float val) {
		m_PosVal_W_O = val;
	}
	public float getOldPosVal_W_O() {
		return m_OldPosVal_W_O;
	}
	public void setOldPosVal_W_O(float val) {
		m_OldPosVal_W_O = val;
	}
	public float getPosVal_CD() {
		return m_PosVal_CD;
	}
	public void setPosVal_CD(float val) {
		m_PosVal_CD = val;
	}
	public float getOldPosVal_CD() {
		return m_OldPosVal_CD;
	}
	public void setOldPosVal_CD(float val) {
		m_OldPosVal_CD = val;
	}
	public float getPosVal_CD_TW() {
		return m_PosVal_CD_TW;
	}
	public void setPosVal_CD_TW(float val) {
		m_PosVal_CD_TW = val;
	}
	public float getOldPosVal_CD_TW() {
		return m_OldPosVal_CD_TW;
	}
	public void setOldPosVal_CD_TW(float val) {
		m_OldPosVal_CD_TW = val;
	}
	public float getPosVal_CD_O() {
		return m_PosVal_CD_O;
	}
	public void setPosVal_CD_O(float val) {
		m_PosVal_CD_O = val;
	}
	public float getOldPosVal_CD_O() {
		return m_OldPosVal_CD_O;
	}
	public void setOldPosVal_CD_O(float val) {
		m_OldPosVal_CD_O = val;
	}
	public float getPosVal_IM() {
		return m_PosVal_IM;
	}
	public void setPosVal_IM(float val) {
		m_PosVal_IM = val;
	}
	public float getOldPosVal_IM() {
		return m_OldPosVal_IM;
	}
	public void setOldPosVal_IM(float val) {
		m_OldPosVal_IM = val;
	}
	public float getPosVal_IM_TW() {
		return m_PosVal_IM_TW;
	}
	public void setPosVal_IM_TW(float val) {
		m_PosVal_IM_TW = val;
	}
	public float getOldPosVal_IM_TW() {
		return m_OldPosVal_IM_TW;
	}
	public void setOldPosVal_IM_TW(float val) {
		m_OldPosVal_IM_TW = val;
	}
	public float getPosVal_IM_D() {
		return m_PosVal_IM_D;
	}
	public void setPosVal_IM_D(float val) {
		m_PosVal_IM_D = val;
	}
	public float getOldPosVal_IM_D() {
		return m_OldPosVal_IM_D;
	}
	public void setOldPosVal_IM_D(float val) {
		m_OldPosVal_IM_D = val;
	}
	public float getPosVal_IM_O() {
		return m_PosVal_IM_O;
	}
	public void setPosVal_IM_O(float val) {
		m_PosVal_IM_O = val;
	}
	public float getOldPosVal_IM_O() {
		return m_OldPosVal_IM_O;
	}
	public void setOldPosVal_IM_O(float val) {
		m_OldPosVal_IM_O = val;
	}
	public float getPosVal_F() {
		return m_PosVal_F;
	}
	public void setPosVal_F(float val) {
		m_PosVal_F = val;
	}
	public float getOldPosVal_F() {
		return m_OldPosVal_F;
	}
	public void setOldPosVal_F(float val) {
		m_OldPosVal_F = val;
	}
	public float getPosVal_F_D() {
		return m_PosVal_F_D;
	}
	public void setPosVal_F_D(float val) {
		m_PosVal_F_D = val;
	}
	public float getOldPosVal_F_D() {
		return m_OldPosVal_F_D;
	}
	public void setOldPosVal_F_D(float val) {
		m_OldPosVal_F_D = val;
	}
	public float getPosVal_F_TW() {
		return m_PosVal_F_TW;
	}
	public void setPosVal_F_TW(float val) {
		m_PosVal_F_TW = val;
	}
	public float getOldPosVal_F_TW() {
		return m_OldPosVal_F_TW;
	}
	public void setOldPosVal_F_TW(float val) {
		m_OldPosVal_F_TW = val;
	}
	public float getPosVal_GK() {
		return m_PosVal_GK;
	}
	public void setPosVal_GK(float val) {
		m_PosVal_GK = val;
	}
	public float getOldPosVal_GK() {
		return m_OldPosVal_GK;
	}
	public void setOldPos_GK(float val) {
		m_OldPosVal_GK = val;
	}
	public int getPassing() {
		return m_Passing;
	}
	public void setPassing(int val) {
		m_Passing = val;
	}
	public int getOldPassing() {
		return m_OldPassing;
	}
	public void setOldPassing(int val) {
		m_OldPassing = val;
	}
	public int getPlaymaking() {
		return m_Playmaking;
	}
	public void setPlaymaking(int val) {
		m_Playmaking = val;
	}
	public int getOldPlaymaking() {
		return m_OldPlaymaking;
	}
	public void setOldPlaymaking(int val) {
		m_OldPlaymaking = val;
	}
	public int getSpeciality() {
		return m_Speciality;
	}
	public void setSpeciality(int val) {
		m_Speciality = val;
	}
	public int getSetPieces() {
		return m_SetPieces;
	}
	public void setSetPieces(int val) {
		m_SetPieces = val;
	}
	public int getOldSetPieces() {
		return m_OldSetPieces;
	}
	public void setOldSetPieces(int val) {
		m_OldSetPieces = val;
	}
	public int getScoring() {
		return m_Scoring;
	}
	public void setScoring(int val) {
		m_Scoring = val;
	}
	public int getOldScoring() {
		return m_OldScoring;
	}
	public void setOldScoring(int val) {
		m_OldScoring = val;
	}
	public int getTSI() {
		return m_TSI;
	}
	public void setTSI(int val) {
		m_TSI = val;
	}
	public int getKeeping() {
		return m_Keeping;
	}
	public void setKeeping(int val) {
		m_Keeping = val;
	}
	public int getOldKeeping() {
		return m_OldKeeping;
	}
	public void setOldKeeping(int val) {
		m_OldKeeping = val;
	}
	public int getDefending() {
		return m_Defending;
	}
	public void setDefending(int val) {
		m_Defending = val;
	}
	public int getOldDefending() {
		return m_OldDefending;
	}
	public void setOldDefending(int val) {
		m_OldDefending = val;
	}
	public int getLoyalty() {
		return m_Loyalty;
	}
	public void setLoyalty(int val) {
		m_Loyalty = val;
	}
	public int getOldLoyalty() {
		return m_OldLoyalty;
	}
	public void setOldLoyalty(int val) {
		m_OldLoyalty= val;
	}
	public int getHomeGrown() {
		return m_HomeGrown;
	}
	public void setHomeGrown(int val) {
		m_HomeGrown = val;
	}
	public int getOldHomeGrown() {
		return m_OldHomeGrown;
	}
	public void setOldHomeGrown(int val) {
		m_OldHomeGrown= val;
	}
}
