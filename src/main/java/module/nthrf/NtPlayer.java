package module.nthrf;

class NtPlayer {

	private long playerId;
	private String firstName;
	private String nickName;
	private String lastName;
	private int shirtNumber;
	private int tsi;
	private int form;
	private int salary;
	private int agreeability;
	private int aggressiveness;
	private int honesty;
	private int xp;
	private int leaderShip;
	private int speciality;
	private int tranferlisted;
	private int nativeLeagueId;
	private int countryId = -1;
	private int ageYears;
	private int ageDays;
	private int caps;
	private int capsU20;
	private int yellowCards;
	private int cards;
	private int injury;
	private int staminaSkill;
	private int keeperSkill;
	private int playmakerSkill;
	private int scorerSkill;
	private int passingSkill;
	private int wingerSkill;
	private int defenderSkill;
	private int setPiecesSkill;
	private int careerGoals;
	private int careerHattricks;
	private int leagueGoals;

	private boolean isTrainer = false;
	private int trainerType;
	private int trainerSkill;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		if (nickName.isEmpty()) {
			return firstName + " " + lastName;
		}
		return firstName + " " + nickName + " " + lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String name) {
		firstName = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String name) {
		nickName = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String name) {
		lastName = name;
	}

	public int getShirtNumber() {
		return shirtNumber;
	}

	public void setShirtNumber(int shirtNumber) {
		this.shirtNumber = shirtNumber;
	}

	public int getTsi() {
		return tsi;
	}

	public void setTsi(int tsi) {
		this.tsi = tsi;
	}

	public int getForm() {
		return form;
	}

	public void setForm(int form) {
		this.form = form;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

	public int getLeaderShip() {
		return leaderShip;
	}

	public void setLeaderShip(int leaderShip) {
		this.leaderShip = leaderShip;
	}

	public int getSpeciality() {
		return speciality;
	}

	public void setSpeciality(int speciality) {
		this.speciality = speciality;
	}

	public int getTranferlisted() {
		return tranferlisted;
	}

	public void setTranferlisted(boolean tranferlisted) {
		if (tranferlisted)
			this.tranferlisted = 1;
		else
			this.tranferlisted = 0;
	}

	public int getCountryId() {
		if (countryId >= 0) {
			return countryId;
		}
		// fallback, if country is not set
		if (nativeLeagueId == 128) return 135;
		return nativeLeagueId;
	}

	public int getAgeYears() {
		return ageYears;
	}

	public void setAgeYears(int ageYears) {
		this.ageYears = ageYears;
	}

	public int getAgeDays() {
		return ageDays;
	}

	public void setAgeDays(int ageDays) {
		this.ageDays = ageDays;
	}

	public int getCaps() {
		return caps;
	}

	public void setCaps(int caps) {
		this.caps = caps;
	}

	public int getCapsU20() {
		return capsU20;
	}

	public void setCapsU20(int capsU20) {
		this.capsU20 = capsU20;
	}

	public int getCards() {
		return cards;
	}

	public void setCards(int cards) {
		this.cards = cards;
	}

	public int getInjury() {
		return injury;
	}

	public void setInjury(int injury) {
		this.injury = injury;
	}

	public int getStaminaSkill() {
		return staminaSkill;
	}

	public void setStaminaSkill(int staminaSkill) {
		this.staminaSkill = staminaSkill;
	}

	public int getPlaymakerSkill() {
		return playmakerSkill;
	}

	public void setPlaymakerSkill(int playmakerSkill) {
		this.playmakerSkill = playmakerSkill;
	}

	public int getScorerSkill() {
		return scorerSkill;
	}

	public void setScorerSkill(int scorerSkill) {
		this.scorerSkill = scorerSkill;
	}

	public int getPassingSkill() {
		return passingSkill;
	}

	public void setPassingSkill(int passingSkill) {
		this.passingSkill = passingSkill;
	}

	public int getWingerSkill() {
		return wingerSkill;
	}

	public void setWingerSkill(int wingerSkill) {
		this.wingerSkill = wingerSkill;
	}

	public int getDefenderSkill() {
		return defenderSkill;
	}

	public void setDefenderSkill(int defenderSkill) {
		this.defenderSkill = defenderSkill;
	}

	public int getSetPiecesSkill() {
		return setPiecesSkill;
	}

	public void setSetPiecesSkill(int setPiecesSkill) {
		this.setPiecesSkill = setPiecesSkill;
	}

	public int getCareerGoals() {
		return careerGoals;
	}

	public void setCareerGoals(int careerGoals) {
		this.careerGoals = careerGoals;
	}

	public int getCareerHattricks() {
		return careerHattricks;
	}

	public void setCareerHattricks(int careerHattricks) {
		this.careerHattricks = careerHattricks;
	}

	public int getLeagueGoals() {
		return leagueGoals;
	}

	public void setLeagueGoals(int leagueGoals) {
		this.leagueGoals = leagueGoals;
	}

	public int getYellowCards() {
		return yellowCards;
	}

	public void setYellowCards(int yellowCards) {
		this.yellowCards = yellowCards;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public int getAgreeability() {
		return agreeability;
	}

	public void setAgreeability(int agreeability) {
		this.agreeability = agreeability;
	}

	public int getAggressiveness() {
		return aggressiveness;
	}

	public void setAggressiveness(int aggressiveness) {
		this.aggressiveness = aggressiveness;
	}

	public int getHonesty() {
		return honesty;
	}

	public void setHonesty(int honesty) {
		this.honesty = honesty;
	}

	public int getNationalLeagueId() {
		return nativeLeagueId;
	}

	public void setNativeLeagueId(int nativeLeagueId) {
		this.nativeLeagueId = nativeLeagueId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getKeeperSkill() {
		return keeperSkill;
	}

	public void setKeeperSkill(int keeperSkill) {
		this.keeperSkill = keeperSkill;
	}

	public int getNativeLeagueId() {
		return nativeLeagueId;
	}

	public boolean isTrainer() {
		return isTrainer;
	}

	public void setTrainer(boolean isTrainer) {
		this.isTrainer = isTrainer;
	}

	public int getTrainerType() {
		return trainerType;
	}

	public void setTrainerType(int trainerType) {
		this.trainerType = trainerType;
	}

	public int getTrainerSkill() {
		return trainerSkill;
	}

	public void setTrainerSkill(int trainerSkill) {
		this.trainerSkill = trainerSkill;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("--- NtPlayer " + getName() + " (" + playerId + ") ---");
		sb.append("\n\tshirtNumber: " + shirtNumber);
		sb.append("\n\tTSI: " + tsi);
		sb.append("\n\tForm: " + form);
		sb.append("\n\tXP: " + xp + ", Leadership: " + leaderShip);
		sb.append("\n\tSalary: " + salary);
		sb.append("\n\tAgreeability: " + agreeability);
		sb.append("\n\tAggressiveness: " + aggressiveness + ", honesty: " + honesty);
		sb.append("\n\tSpecial: " + speciality);
		sb.append("\n\tTransferlisted: " + tranferlisted);
		sb.append("\n\tNativeLeague/Country: " + nativeLeagueId + " / " + getCountryId());
		sb.append("\n\tAge: " + ageYears + "." + ageDays);
		sb.append("\n\tCaps: " + caps + " / " + capsU20);
		sb.append("\n\tCards: " + cards + " / " + yellowCards);
		sb.append("\n\tInjury: " + injury);
		sb.append("\n\tStamina: " + staminaSkill);
		sb.append("\n\tKeeper: " + keeperSkill);
		sb.append("\n\tPlaymaking: " + playmakerSkill);
		sb.append("\n\tScoring: " + scorerSkill);
		sb.append("\n\tPassing: " + passingSkill);
		sb.append("\n\tWinger: " + wingerSkill);
		sb.append("\n\tDefender: " + defenderSkill);
		sb.append("\n\tSetPieces: " + setPiecesSkill);
		sb.append("\n\tcareerGoals: " + careerGoals + ", leagueGoals: " + leagueGoals + ", careerHattricks: " + careerHattricks);
		sb.append("\n\tTrainer: " + isTrainer + ", type: " + trainerType + ", skill: " + trainerSkill);
		return sb.toString();
	}
}
