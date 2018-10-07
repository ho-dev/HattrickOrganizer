package module.transfer.test;

public class TransferFee {

	/**
	 * Gebühr für den Spielervermittler
	 * 
	 * @param daysInTeam
	 * @return
	 */
	public static double getFee(int daysInTeam) {
		// 0 Tage 12%
		// 1 Tag 10,45%
		// 2 Tage 9,95%
		// 3 Tage 9,59%
		// 4 Tage 9,30%
		// 5 Tage 9,05%
		// 6 Tage 8,83%
		// 1 Woche 8,62% 7
		// 2 Wochen 7,55% 14
		// 3 Wochen 6,76% 21
		// 4 Wochen 6,12% 28
		// 5 Wochen 5,57% 35
		// 6 Wochen 5,09% 42
		// 7 Wochen 4,65% 49
		// 8 Wochen 4,24% 56
		// 9 Wochen 3,87% 63
		// 10 Wochen 3,52% 70
		// 11 Wochen 3,19% 77
		// 12 Wochen 2,88% 84
		// 13 Wochen 2,58% 91
		// 14 Wochen 2,30% 98
		// 15 Wochen 2,03% 105
		// 16 Wochen 2% 112
		switch (daysInTeam) {
		case 0:
			return 12;
		case 1:
			return 10.45;
		case 2:
			return 9.95;
		case 3:
			return 9.59;
		case 4:
			return 9.3;
		case 5:
			return 9.05;
		case 6:
			return 8.83;
		}

		if (daysInTeam < 14) {
			return 8.62;
		}

		if (daysInTeam < 21) {
			return 7.55;
		}

		if (daysInTeam < 28) {
			return 6.76;
		}
		if (daysInTeam < 35) {
			return 6.12;
		}
		if (daysInTeam < 42) {
			return 5.57;
		}
		if (daysInTeam < 49) {
			return 5.09;
		}
		if (daysInTeam < 56) {
			return 4.65;
		}
		if (daysInTeam < 63) {
			return 4.24;
		}
		if (daysInTeam < 70) {
			return 3.87;
		}
		if (daysInTeam < 77) {
			return 3.52;
		}
		if (daysInTeam < 84) {
			return 3.19;
		}
		if (daysInTeam < 91) {
			return 2.88;
		}
		if (daysInTeam < 98) {
			return 2.58;
		}
		if (daysInTeam < 105) {
			return 2.3;
		}
		if (daysInTeam < 105) {
			return 2.03;
		}
		return 2;
	}

	/**
	 * Prämie für den vorherigen Verein
	 * 
	 * @param 0 none, 1 average, 2 max
	 * @return
	 */
	public static double feePreviousClub(int x) {
		// 0 Spiele 0%
		// 1 Spiel 0,25%
		// 2 Spiele 0,50%
		// 3 Spiele 1,00%
		// 4 Spiele 1,50%
		// 5 Spiele 2,00%
		// 7 Spiele 2,50%
		// 10 Spiele 3,00%
		// 20 Spiele 3,50%
		// 40 Spiele 4,00%
		switch (x) {
		case 0:
			return 0d;
		case 1:
			return 2d;
		case 2:
			return 4d;
		default:
			return 4;
		}
	}	
}
