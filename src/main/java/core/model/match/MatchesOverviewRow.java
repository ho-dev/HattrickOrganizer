package core.model.match;

/**
 * Model for MatchesOverviewTable on the matches panel
 */
public final class MatchesOverviewRow {
	public static final int TYPE_TITLE = -1;
	public static final int TYPE_ALL 	= 0;
	public static final int TYPE_SYSTEM = 1;
	public static final int TYPE_TACTICS = 2;
	public static final int TYPE_MOT 	= 3;
	public static final int TYPE_WEATHER = 4;
	
	private int type;
	private int typeValue = Integer.MIN_VALUE;
	private int count;
	private int win;
	private int loss;
	private int draw;
	private int homeGoals;
	private int awayGoals;
	private String description;
	private String homeColumn;
	private String awayColumn;
	
	public MatchesOverviewRow(String description, int type){
		this.description = description;
		this.type = type;
	}
	
	public MatchesOverviewRow(String description, int type, int typeValue){
		this.description = description;
		this.type = type;
		this.typeValue = typeValue;
		setColumn(type);
	}
	
	private void setColumn(int type){
		switch(type){
		case TYPE_TACTICS:
			homeColumn = "HeimTacticType";
			awayColumn = "GastTacticType";
			break;
		case TYPE_MOT:
			homeColumn = "HeimEinstellung";
			awayColumn = "GastEinstellung";
			break;
		case TYPE_WEATHER:
			homeColumn = "WetterId";
			awayColumn = "WetterId";
			break;
		}
	}
	
	public String getColumnName(boolean home){
		return home?homeColumn:awayColumn;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = this.count + count;
	}
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = this.win + win;
	}
	public int getLoss() {
		return loss;
	}
	public void setLoss(int loss) {
		this.loss = this.loss + loss;
	}
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = this.draw + draw;
	}
	public int getHomeGoals() {
		return homeGoals;
	}
	public void setHomeGoals(int homeGoals) {
		this.homeGoals = this.homeGoals + homeGoals;
	}
	public int getAwayGoals() {
		return awayGoals;
	}
	public void setAwayGoals(int awayGoals) {
		this.awayGoals = this.awayGoals + awayGoals;
	}

	public int getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(int typeValue) {
		this.typeValue = typeValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public void setMatchResult(int ihomeGoals, int iawayGoals, boolean home){
		count++;
		homeGoals += home ? ihomeGoals : iawayGoals;
		awayGoals += home ? iawayGoals : ihomeGoals;
		
		if(ihomeGoals > iawayGoals){
			if(home)
				win++;
			else
				loss++;
		} else if(ihomeGoals < iawayGoals){
			if(home)
				loss++;
			else
				win++;
		} else if(ihomeGoals == iawayGoals)
			draw++;
	}
	
	@Override
	public String toString(){
		return getDescription();
	}
	
}
