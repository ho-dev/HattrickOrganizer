package core.model;

import core.db.AbstractTable;

public class WorldDetailLeague  extends AbstractTable.Storable {
	private int leagueId;
	private int countryId;
	private String countryName;
	private int activeUsers;

	public static WorldDetailLeague[] allLeagues = {
			new WorldDetailLeague(1,1,"Sweden"),
			new WorldDetailLeague(2,2,"England"),
			new WorldDetailLeague(3,3,"Germany"),
			new WorldDetailLeague(4,4,"Italy"),
			new WorldDetailLeague(5,5,"France"),
			new WorldDetailLeague(6,6,"Mexico"),
			new WorldDetailLeague(7,7,"Argentina"),
			new WorldDetailLeague(8,8,"USA"),
			new WorldDetailLeague(9,9,"Norway"),
			new WorldDetailLeague(11,10,"Denmark"),
			new WorldDetailLeague(12,11,"Finland"),
			new WorldDetailLeague(14,12,"Netherlands"),
			new WorldDetailLeague(15,13,"Oceania"),
			new WorldDetailLeague(16,22,"Brazil"),
			new WorldDetailLeague(17,14,"Canada"),
			new WorldDetailLeague(18,17,"Chile"),
			new WorldDetailLeague(19,18,"Colombia"),
			new WorldDetailLeague(20,27,"India"),
			new WorldDetailLeague(21,16,"Ireland"),
			new WorldDetailLeague(22,25,"Japan"),
			new WorldDetailLeague(23,21,"Peru"),
			new WorldDetailLeague(24,26,"Poland"),
			new WorldDetailLeague(25,23,"Portugal"),
			new WorldDetailLeague(26,15,"Scotland"),
			new WorldDetailLeague(27,24,"South Africa"),
			new WorldDetailLeague(28,19,"Uruguay"),
			new WorldDetailLeague(29,20,"Venezuela"),
			new WorldDetailLeague(30,29,"South Korea"),
			new WorldDetailLeague(31,30,"Thailand"),
			new WorldDetailLeague(32,31,"Turkey"),
			new WorldDetailLeague(33,32,"Egypt"),
			new WorldDetailLeague(34,28,"People's Republic of China"),
			new WorldDetailLeague(35,34,"Russia"),
			new WorldDetailLeague(36,35,"Spain"),
			new WorldDetailLeague(37,36,"Romania"),
			new WorldDetailLeague(38,37,"Iceland"),
			new WorldDetailLeague(39,33,"Austria"),
			new WorldDetailLeague(44,38,"Belgium"),
			new WorldDetailLeague(45,39,"Malaysia"),
			new WorldDetailLeague(46,40,"Switzerland"),
			new WorldDetailLeague(47,41,"Singapore"),
			new WorldDetailLeague(50,45,"Greece"),
			new WorldDetailLeague(51,44,"Hungary"),
			new WorldDetailLeague(52,46,"Czech Republic"),
			new WorldDetailLeague(53,48,"Latvia"),
			new WorldDetailLeague(54,49,"Indonesia"),
			new WorldDetailLeague(55,50,"Philippines"),
			new WorldDetailLeague(56,47,"Estonia"),
			new WorldDetailLeague(57,43,"Serbia"),
			new WorldDetailLeague(58,42,"Croatia"),
			new WorldDetailLeague(59,53,"Hong Kong"),
			new WorldDetailLeague(60,52,"Chinese Taipei"),
			new WorldDetailLeague(61,56,"Wales"),
			new WorldDetailLeague(62,55,"Bulgaria"),
			new WorldDetailLeague(63,51,"Israel"),
			new WorldDetailLeague(64,57,"Slovenia"),
			new WorldDetailLeague(66,61,"Lithuania"),
			new WorldDetailLeague(67,66,"Slovakia"),
			new WorldDetailLeague(68,62,"Ukraine"),
			new WorldDetailLeague(69,63,"Bosnia and Herzegovina"),
			new WorldDetailLeague(70,65,"Vietnam"),
			new WorldDetailLeague(71,64,"Pakistan"),
			new WorldDetailLeague(72,67,"Paraguay"),
			new WorldDetailLeague(73,68,"Ecuador"),
			new WorldDetailLeague(74,69,"Bolivia"),
			new WorldDetailLeague(75,70,"Nigeria"),
			new WorldDetailLeague(76,71,"Faroe Islands"),
			new WorldDetailLeague(77,72,"Morocco"),
			new WorldDetailLeague(79,75,"Saudi Arabia"),
			new WorldDetailLeague(80,76,"Tunisia"),
			new WorldDetailLeague(81,77,"Costa Rica"),
			new WorldDetailLeague(83,78,"United Arab Emirates"),
			new WorldDetailLeague(84,79,"Luxembourg"),
			new WorldDetailLeague(85,80,"Iran"),
			new WorldDetailLeague(88,83,"Dominican Republic"),
			new WorldDetailLeague(89,82,"Cyprus"),
			new WorldDetailLeague(91,87,"Belarus"),
			new WorldDetailLeague(93,88,"Northern Ireland"),
			new WorldDetailLeague(94,89,"Jamaica"),
			new WorldDetailLeague(95,90,"Kenya"),
			new WorldDetailLeague(96,91,"Panama"),
			new WorldDetailLeague(97,92,"FYR Macedonia"),
			new WorldDetailLeague(98,94,"Albania"),
			new WorldDetailLeague(99,95,"Honduras"),
			new WorldDetailLeague(100,96,"El Salvador"),
			new WorldDetailLeague(101,97,"Malta"),
			new WorldDetailLeague(102,98,"Kyrgyzstan"),
			new WorldDetailLeague(103,99,"Moldova"),
			new WorldDetailLeague(104,100,"Georgia"),
			new WorldDetailLeague(105,101,"Andorra"),
			new WorldDetailLeague(106,103,"Jordan"),
			new WorldDetailLeague(107,102,"Guatemala"),
			new WorldDetailLeague(110,105,"Trinidad & Tobago"),
			new WorldDetailLeague(111,121,"Nicaragua"),
			new WorldDetailLeague(112,122,"Kazakhstan"),
			new WorldDetailLeague(113,123,"Suriname"),
			new WorldDetailLeague(117,125,"Liechtenstein"),
			new WorldDetailLeague(118,126,"Algeria"),
			new WorldDetailLeague(119,127,"Mongolia"),
			new WorldDetailLeague(120,128,"Lebanon"),
			new WorldDetailLeague(121,86,"Senegal"),
			new WorldDetailLeague(122,104,"Armenia"),
			new WorldDetailLeague(123,129,"Bahrain"),
			new WorldDetailLeague(124,130,"Barbados"),
			new WorldDetailLeague(125,131,"Cape Verde"),
			new WorldDetailLeague(126,132,"Côte d’Ivoire"),
			new WorldDetailLeague(127,134,"Kuwait"),
			new WorldDetailLeague(128,135,"Iraq"),
			new WorldDetailLeague(129,133,"Azerbaijan"),
			new WorldDetailLeague(130,137,"Angola"),
			new WorldDetailLeague(131,136,"Montenegro"),
			new WorldDetailLeague(132,138,"Bangladesh"),
			new WorldDetailLeague(133,139,"Yemen"),
			new WorldDetailLeague(134,140,"Oman"),
			new WorldDetailLeague(135,142,"Mozambique"),
			new WorldDetailLeague(136,143,"Brunei"),
			new WorldDetailLeague(137,144,"Ghana"),
			new WorldDetailLeague(138,145,"Cambodia"),
			new WorldDetailLeague(139,147,"Benin"),
			new WorldDetailLeague(140,148,"Syria"),
			new WorldDetailLeague(141,149,"Qatar"),
			new WorldDetailLeague(142,150,"Tanzania"),
			new WorldDetailLeague(143,153,"Uganda"),
			new WorldDetailLeague(144,154,"Maldives"),
			new WorldDetailLeague(145,163,"Uzbekistan"),
			new WorldDetailLeague(146,165,"Cameroon"),
			new WorldDetailLeague(147,93,"Cuba"),
			new WorldDetailLeague(148,166,"Palestine"),
			new WorldDetailLeague(149,177,"Sao Tomé-et-Principe"),
			new WorldDetailLeague(151,180,"Comoros"),
			new WorldDetailLeague(152,175,"Sri Lanka"),
			new WorldDetailLeague(153,178,"Curaçao"),
			new WorldDetailLeague(154,179,"Guam"),
			new WorldDetailLeague(155,181,"DR Congo"),
			new WorldDetailLeague(156,182,"Ethiopia"),
			new WorldDetailLeague(157,184,"Saint Vincent and the Grenadines"),
			new WorldDetailLeague(158,186,"Belize"),
			new WorldDetailLeague(159,183,"Madagascar"),
			new WorldDetailLeague(160,185,"Botswana"),
			new WorldDetailLeague(161,189,"Myanmar"),
			new WorldDetailLeague(162,187,"Zambia"),
			new WorldDetailLeague(163,191,"San Marino"),
			new WorldDetailLeague(164,188,"Haiti"),
			new WorldDetailLeague(165,190,"Puerto Rico"),
			new WorldDetailLeague(166,194,"Grenada"),
			new WorldDetailLeague(167,193,"Burkina Faso"),
			new WorldDetailLeague(168,192,"Nepal"),
			new WorldDetailLeague(169,197,"Guyana"),
			new WorldDetailLeague(170,196,"Tahiti"),
			new WorldDetailLeague(1000,1000,"International")
	};
	
	public WorldDetailLeague(){
		
	}

	public WorldDetailLeague(int leagueId, int countryId, String countryName){
		this.leagueId = leagueId;
		this.countryId = countryId;
		this.countryName = countryName;
	}
	
	public final int getLeagueId() {
		return leagueId;
	}
	public final void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	public final int getCountryId() {
		return countryId;
	}
	public final void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	public final String getCountryName() {
		return countryName;
	}
	public final void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public final int getActiveUsers() {
		return activeUsers;
	}
	public final void setActiveUsers(int activeUsers) {
		this.activeUsers = activeUsers;
	}

	@Override
	public String toString(){
		return getCountryName();
	}
}
