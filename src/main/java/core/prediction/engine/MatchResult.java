package core.prediction.engine;


public class MatchResult  {

	private int[] resultDetail = new int[25];

	private int matchNumber = 0;
	private int homeWin = 0;
	private int awayWin = 0;
	private int draw = 0;
	private int homeGoals = 0;
	private int homeChances = 0;
	private int guestGoals = 0;
	private int guestChances = 0;

	//left, middle, right
	private final int[] homeSuccess = { 0, 0, 0 };
	private final int[] homeFailed = { 0, 0, 0 };
	private final int[] guestSuccess = { 0, 0, 0 };
	private final int[] guestFailed = { 0, 0, 0 };

	public void addActions(Action[] actions) {

		matchNumber++;
		int matchHomeGoals = 0;
		int matchHomeChances = 0;
		int matchGuestGoals = 0;
		int matchGuestChances = 0;

		//left, middle, right
		final int[] matchHomeSuccess = { 0, 0, 0 };
		final int[] matchHomeFailed = { 0, 0, 0 };
		final int[] matchGuestSuccess = { 0, 0, 0 };
		final int[] matchGuestFailed = { 0, 0, 0 };

		for (int i = 0; i < actions.length; i++) {
			final Action element = (Action) actions[i];

			if (element.isHomeTeam()) {
				matchHomeChances++;

				if (element.isScore()) {
					matchHomeGoals++;

					if (element.getArea() == -1) {
						matchHomeSuccess[0]++;
					} else if (element.getArea() == 0) {
						matchHomeSuccess[1]++;
					} else {
						matchHomeSuccess[2]++;
					}
				} else {
					if (element.getArea() == -1) {
						matchHomeFailed[0]++;
					} else if (element.getArea() == 0) {
						matchHomeFailed[1]++;
					} else {
						matchHomeFailed[2]++;
					}
				}
			} else {
				matchGuestChances++;

				if (element.isScore()) {
					matchGuestGoals++;

					if (element.getArea() == -1) {
						matchGuestSuccess[0]++;
					} else if (element.getArea() == 0) {
						matchGuestSuccess[1]++;
					} else {
						matchGuestSuccess[2]++;
					}
				} else {
					if (element.getArea() == -1) {
						matchGuestFailed[0]++;
					} else if (element.getArea() == 0) {
						matchGuestFailed[1]++;
					} else {
						matchGuestFailed[2]++;
					}
				}
			}
		}

		int away = matchGuestGoals;
		if (away > 4) {
			away = 4;
		}
		int home = matchHomeGoals;
		if (home > 4) {
			home = 4;
		}
		resultDetail[(home * 5) + away]++;

		//Werte zusamenzählen
		homeGoals += matchHomeGoals;
		homeChances += matchHomeChances;
		guestGoals += matchGuestGoals;
		guestChances += matchGuestChances;
		homeSuccess[0] += matchHomeSuccess[0];
		homeSuccess[1] += matchHomeSuccess[1];
		homeSuccess[2] += matchHomeSuccess[2];
		homeFailed[0] += matchHomeFailed[0];
		homeFailed[1] += matchHomeFailed[1];
		homeFailed[2] += matchHomeFailed[2];
		guestSuccess[0] += matchGuestSuccess[0];
		guestSuccess[1] += matchGuestSuccess[1];
		guestSuccess[2] += matchGuestSuccess[2];
		guestFailed[0] += matchGuestFailed[0];
		guestFailed[1] += matchGuestFailed[1];
		guestFailed[2] += matchGuestFailed[2];

		if (matchHomeGoals > matchGuestGoals) {
			homeWin++;
		} else if (matchHomeGoals < matchGuestGoals) {
			awayWin++;
		} else {
			draw++;
		}

		// free memory
		actions = null;
	}

	public void addMatchResult(MatchResult result) {

		matchNumber++;

		int away = result.getGuestGoals();
		if (away > 4) {
			away = 4;
		}
		int home = result.getHomeGoals();
		if (home > 4) {
			home = 4;
		}
		resultDetail[(home * 5) + away]++;

		//Werte zusamenzählen
		homeGoals += result.getHomeGoals();
		homeChances += result.getHomeChances();
		guestGoals += result.getGuestGoals();
		guestChances += result.getGuestChances();
		homeSuccess[0] += result.getHomeSuccess()[0];
		homeSuccess[1] += result.getHomeSuccess()[1];
		homeSuccess[2] += result.getHomeSuccess()[2];
		homeFailed[0] += result.getHomeFailed()[0];
		homeFailed[1] += result.getHomeFailed()[1];
		homeFailed[2] += result.getHomeFailed()[2];
		guestSuccess[0] += result.getGuestSuccess()[0];
		guestSuccess[1] += result.getGuestSuccess()[1];
		guestSuccess[2] += result.getGuestSuccess()[2];
		guestFailed[0] += result.getGuestFailed()[0];
		guestFailed[1] += result.getGuestFailed()[1];
		guestFailed[2] += result.getGuestFailed()[2];

		if (result.getHomeGoals() > result.getGuestGoals()) {
			homeWin++;
		} else if (result.getHomeGoals() < result.getGuestGoals()) {
			awayWin++;
		} else {
			draw++;
		}
		// free memory
		result = null;
	}

	public int getGuestChances() {
		return guestChances;
	}

	public int[] getGuestFailed() {
		return guestFailed;
	}

	public int getGuestGoals() {
		return guestGoals;
	}

	public int[] getGuestSuccess() {
		return guestSuccess;
	}

	public int getHomeChances() {
		return homeChances;
	}

	public int[] getHomeFailed() {
		return homeFailed;
	}

	public int getHomeGoals() {
		return homeGoals;
	}

	public int[] getHomeSuccess() {
		return homeSuccess;
	}

	public int getHomeWin() {
		return homeWin;
	}

	public int getAwayWin() {
		return awayWin;
	}

	public int getDraw() {
		return draw;
	}

	public int getMatchNumber() {
		return matchNumber;
	}

	public int[] getResultDetail() {
		return resultDetail;
	}

}
