package core.db;

import core.model.WorldDetailLeague;
import java.sql.Types;
import java.util.List;

class WorldDetailsTable extends AbstractTable {

	final static String TABLENAME = "HT_WORLDDETAILS";
	
	WorldDetailsTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}
	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("LEAGUE_ID").setGetter((p) -> ((WorldDetailLeague) p).getLeagueId()).setSetter((p, v) -> ((WorldDetailLeague) p).setLeagueId((int) v)).setType(Types.INTEGER).isPrimaryKey(true).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COUNTRY_ID").setGetter((p) -> ((WorldDetailLeague) p).getCountryId()).setSetter((p, v) -> ((WorldDetailLeague) p).setCountryId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("COUNTRYNAME").setGetter((p) -> ((WorldDetailLeague) p).getCountryName()).setSetter((p, v) -> ((WorldDetailLeague) p).setCountryName((String) v)).setType(Types.VARCHAR).setLength(128).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("ACTIVE_USER").setGetter((p) -> ((WorldDetailLeague) p).getActiveUsers()).setSetter((p, v) -> ((WorldDetailLeague) p).setActiveUsers((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this, "");
	}
	void insertWorldDetailsLeague(WorldDetailLeague league){
		if(league == null)
			return;
		store(league);
	}

	List<WorldDetailLeague> getAllWorldDetailLeagues(){
		return load(WorldDetailLeague.class);
	}
	
	@Override
	protected void insertDefaultValues(){
		insertWorldDetailsLeague(new WorldDetailLeague(1,1,"Sweden"));
		insertWorldDetailsLeague(new WorldDetailLeague(2,2,"England"));
		insertWorldDetailsLeague(new WorldDetailLeague(3,3,"Germany"));
		insertWorldDetailsLeague(new WorldDetailLeague(4,4,"Italy"));
		insertWorldDetailsLeague(new WorldDetailLeague(5,5,"France"));
		insertWorldDetailsLeague(new WorldDetailLeague(6,6,"Mexico"));
		insertWorldDetailsLeague(new WorldDetailLeague(7,7,"Argentina"));
		insertWorldDetailsLeague(new WorldDetailLeague(8,8,"USA"));
		insertWorldDetailsLeague(new WorldDetailLeague(9,9,"Norway"));
		insertWorldDetailsLeague(new WorldDetailLeague(11,10,"Denmark"));
		insertWorldDetailsLeague(new WorldDetailLeague(12,11,"Finland"));
		insertWorldDetailsLeague(new WorldDetailLeague(14,12,"Netherlands"));
		insertWorldDetailsLeague(new WorldDetailLeague(15,13,"Oceania"));
		insertWorldDetailsLeague(new WorldDetailLeague(16,22,"Brazil"));
		insertWorldDetailsLeague(new WorldDetailLeague(17,14,"Canada"));
		insertWorldDetailsLeague(new WorldDetailLeague(18,17,"Chile"));
		insertWorldDetailsLeague(new WorldDetailLeague(19,18,"Colombia"));
		insertWorldDetailsLeague(new WorldDetailLeague(20,27,"India"));
		insertWorldDetailsLeague(new WorldDetailLeague(21,16,"Ireland"));
		insertWorldDetailsLeague(new WorldDetailLeague(22,25,"Japan"));
		insertWorldDetailsLeague(new WorldDetailLeague(23,21,"Peru"));
		insertWorldDetailsLeague(new WorldDetailLeague(24,26,"Poland"));
		insertWorldDetailsLeague(new WorldDetailLeague(25,23,"Portugal"));
		insertWorldDetailsLeague(new WorldDetailLeague(26,15,"Scotland"));
		insertWorldDetailsLeague(new WorldDetailLeague(27,24,"South Africa"));
		insertWorldDetailsLeague(new WorldDetailLeague(28,19,"Uruguay"));
		insertWorldDetailsLeague(new WorldDetailLeague(29,20,"Venezuela"));
		insertWorldDetailsLeague(new WorldDetailLeague(30,29,"South Korea"));
		insertWorldDetailsLeague(new WorldDetailLeague(31,30,"Thailand"));
		insertWorldDetailsLeague(new WorldDetailLeague(32,31,"Turkey"));
		insertWorldDetailsLeague(new WorldDetailLeague(33,32,"Egypt"));
		insertWorldDetailsLeague(new WorldDetailLeague(34,28,"People's Republic of China"));
		insertWorldDetailsLeague(new WorldDetailLeague(35,34,"Russia"));
		insertWorldDetailsLeague(new WorldDetailLeague(36,35,"Spain"));
		insertWorldDetailsLeague(new WorldDetailLeague(37,36,"Romania"));
		insertWorldDetailsLeague(new WorldDetailLeague(38,37,"Iceland"));
		insertWorldDetailsLeague(new WorldDetailLeague(39,33,"Austria"));
		insertWorldDetailsLeague(new WorldDetailLeague(44,38,"Belgium"));
		insertWorldDetailsLeague(new WorldDetailLeague(45,39,"Malaysia"));
		insertWorldDetailsLeague(new WorldDetailLeague(46,40,"Switzerland"));
		insertWorldDetailsLeague(new WorldDetailLeague(47,41,"Singapore"));
		insertWorldDetailsLeague(new WorldDetailLeague(50,45,"Greece"));
		insertWorldDetailsLeague(new WorldDetailLeague(51,44,"Hungary"));
		insertWorldDetailsLeague(new WorldDetailLeague(52,46,"Czech Republic"));
		insertWorldDetailsLeague(new WorldDetailLeague(53,48,"Latvia"));
		insertWorldDetailsLeague(new WorldDetailLeague(54,49,"Indonesia"));
		insertWorldDetailsLeague(new WorldDetailLeague(55,50,"Philippines"));
		insertWorldDetailsLeague(new WorldDetailLeague(56,47,"Estonia"));
		insertWorldDetailsLeague(new WorldDetailLeague(57,43,"Serbia"));
		insertWorldDetailsLeague(new WorldDetailLeague(58,42,"Croatia"));
		insertWorldDetailsLeague(new WorldDetailLeague(59,53,"Hong Kong"));
		insertWorldDetailsLeague(new WorldDetailLeague(60,52,"Chinese Taipei"));
		insertWorldDetailsLeague(new WorldDetailLeague(61,56,"Wales"));
		insertWorldDetailsLeague(new WorldDetailLeague(62,55,"Bulgaria"));
		insertWorldDetailsLeague(new WorldDetailLeague(63,51,"Israel"));
		insertWorldDetailsLeague(new WorldDetailLeague(64,57,"Slovenia"));
		insertWorldDetailsLeague(new WorldDetailLeague(66,61,"Lithuania"));
		insertWorldDetailsLeague(new WorldDetailLeague(67,66,"Slovakia"));
		insertWorldDetailsLeague(new WorldDetailLeague(68,62,"Ukraine"));
		insertWorldDetailsLeague(new WorldDetailLeague(69,63,"Bosnia and Herzegovina"));
		insertWorldDetailsLeague(new WorldDetailLeague(70,65,"Vietnam"));
		insertWorldDetailsLeague(new WorldDetailLeague(71,64,"Pakistan"));
		insertWorldDetailsLeague(new WorldDetailLeague(72,67,"Paraguay"));
		insertWorldDetailsLeague(new WorldDetailLeague(73,68,"Ecuador"));
		insertWorldDetailsLeague(new WorldDetailLeague(74,69,"Bolivia"));
		insertWorldDetailsLeague(new WorldDetailLeague(75,70,"Nigeria"));
		insertWorldDetailsLeague(new WorldDetailLeague(76,71,"Faroe Islands"));
		insertWorldDetailsLeague(new WorldDetailLeague(77,72,"Morocco"));
		insertWorldDetailsLeague(new WorldDetailLeague(79,75,"Saudi Arabia"));
		insertWorldDetailsLeague(new WorldDetailLeague(80,76,"Tunisia"));
		insertWorldDetailsLeague(new WorldDetailLeague(81,77,"Costa Rica"));
		insertWorldDetailsLeague(new WorldDetailLeague(83,78,"United Arab Emirates"));
		insertWorldDetailsLeague(new WorldDetailLeague(84,79,"Luxembourg"));
		insertWorldDetailsLeague(new WorldDetailLeague(85,80,"Iran"));
		insertWorldDetailsLeague(new WorldDetailLeague(88,83,"Dominican Republic"));
		insertWorldDetailsLeague(new WorldDetailLeague(89,82,"Cyprus"));
		insertWorldDetailsLeague(new WorldDetailLeague(91,87,"Belarus"));
		insertWorldDetailsLeague(new WorldDetailLeague(93,88,"Northern Ireland"));
		insertWorldDetailsLeague(new WorldDetailLeague(94,89,"Jamaica"));
		insertWorldDetailsLeague(new WorldDetailLeague(95,90,"Kenya"));
		insertWorldDetailsLeague(new WorldDetailLeague(96,91,"Panama"));
		insertWorldDetailsLeague(new WorldDetailLeague(97,92,"FYR Macedonia"));
		insertWorldDetailsLeague(new WorldDetailLeague(98,94,"Albania"));
		insertWorldDetailsLeague(new WorldDetailLeague(99,95,"Honduras"));
		insertWorldDetailsLeague(new WorldDetailLeague(100,96,"El Salvador"));
		insertWorldDetailsLeague(new WorldDetailLeague(101,97,"Malta"));
		insertWorldDetailsLeague(new WorldDetailLeague(102,98,"Kyrgyzstan"));
		insertWorldDetailsLeague(new WorldDetailLeague(103,99,"Moldova"));
		insertWorldDetailsLeague(new WorldDetailLeague(104,100,"Georgia"));
		insertWorldDetailsLeague(new WorldDetailLeague(105,101,"Andorra"));
		insertWorldDetailsLeague(new WorldDetailLeague(106,103,"Jordan"));
		insertWorldDetailsLeague(new WorldDetailLeague(107,102,"Guatemala"));
		insertWorldDetailsLeague(new WorldDetailLeague(110,105,"Trinidad & Tobago"));
		insertWorldDetailsLeague(new WorldDetailLeague(111,121,"Nicaragua"));
		insertWorldDetailsLeague(new WorldDetailLeague(112,122,"Kazakhstan"));
		insertWorldDetailsLeague(new WorldDetailLeague(113,123,"Suriname"));
		insertWorldDetailsLeague(new WorldDetailLeague(117,125,"Liechtenstein"));
		insertWorldDetailsLeague(new WorldDetailLeague(118,126,"Algeria"));
		insertWorldDetailsLeague(new WorldDetailLeague(119,127,"Mongolia"));
		insertWorldDetailsLeague(new WorldDetailLeague(120,128,"Lebanon"));
		insertWorldDetailsLeague(new WorldDetailLeague(121,86,"Senegal"));
		insertWorldDetailsLeague(new WorldDetailLeague(122,104,"Armenia"));
		insertWorldDetailsLeague(new WorldDetailLeague(123,129,"Bahrain"));
		insertWorldDetailsLeague(new WorldDetailLeague(124,130,"Barbados"));
		insertWorldDetailsLeague(new WorldDetailLeague(125,131,"Cape Verde"));
		insertWorldDetailsLeague(new WorldDetailLeague(126,132,"Côte d’Ivoire"));
		insertWorldDetailsLeague(new WorldDetailLeague(127,134,"Kuwait"));
		insertWorldDetailsLeague(new WorldDetailLeague(128,135,"Iraq"));
		insertWorldDetailsLeague(new WorldDetailLeague(129,133,"Azerbaijan"));
		insertWorldDetailsLeague(new WorldDetailLeague(130,137,"Angola"));
		insertWorldDetailsLeague(new WorldDetailLeague(131,136,"Montenegro"));
		insertWorldDetailsLeague(new WorldDetailLeague(132,138,"Bangladesh"));
		insertWorldDetailsLeague(new WorldDetailLeague(133,139,"Yemen"));
		insertWorldDetailsLeague(new WorldDetailLeague(134,140,"Oman"));
		insertWorldDetailsLeague(new WorldDetailLeague(135,142,"Mozambique"));
		insertWorldDetailsLeague(new WorldDetailLeague(136,143,"Brunei"));
		insertWorldDetailsLeague(new WorldDetailLeague(137,144,"Ghana"));
		insertWorldDetailsLeague(new WorldDetailLeague(138,145,"Cambodia"));
		insertWorldDetailsLeague(new WorldDetailLeague(139,147,"Benin"));
		insertWorldDetailsLeague(new WorldDetailLeague(140,148,"Syria"));
		insertWorldDetailsLeague(new WorldDetailLeague(141,149,"Qatar"));
		insertWorldDetailsLeague(new WorldDetailLeague(142,150,"Tanzania"));
		insertWorldDetailsLeague(new WorldDetailLeague(143,153,"Uganda"));
		insertWorldDetailsLeague(new WorldDetailLeague(144,154,"Maldives"));
		insertWorldDetailsLeague(new WorldDetailLeague(145,163,"Uzbekistan"));
		insertWorldDetailsLeague(new WorldDetailLeague(146,165,"Cameroon"));
		insertWorldDetailsLeague(new WorldDetailLeague(147,93,"Cuba"));
		insertWorldDetailsLeague(new WorldDetailLeague(148,166,"Palestine"));
		insertWorldDetailsLeague(new WorldDetailLeague(149,177,"Sao Tomé-et-Principe"));
		insertWorldDetailsLeague(new WorldDetailLeague(151,180,"Comoros"));
		insertWorldDetailsLeague(new WorldDetailLeague(152,175,"Sri Lanka"));
		insertWorldDetailsLeague(new WorldDetailLeague(153,178,"Curaçao"));
		insertWorldDetailsLeague(new WorldDetailLeague(154,179,"Guam"));
		insertWorldDetailsLeague(new WorldDetailLeague(155,181,"DR Congo"));
		insertWorldDetailsLeague(new WorldDetailLeague(156,182,"Ethiopia"));
		insertWorldDetailsLeague(new WorldDetailLeague(157,184,"Saint Vincent and the Grenadines"));
		insertWorldDetailsLeague(new WorldDetailLeague(158,186,"Belize"));
		insertWorldDetailsLeague(new WorldDetailLeague(159,183,"Madagascar"));
		insertWorldDetailsLeague(new WorldDetailLeague(160,185,"Botswana"));
		insertWorldDetailsLeague(new WorldDetailLeague(161,189,"Myanmar"));
		insertWorldDetailsLeague(new WorldDetailLeague(162,187,"Zambia"));
		insertWorldDetailsLeague(new WorldDetailLeague(163,191,"San Marino"));
		insertWorldDetailsLeague(new WorldDetailLeague(164,188,"Haiti"));
		insertWorldDetailsLeague(new WorldDetailLeague(165,190,"Puerto Rico"));
		insertWorldDetailsLeague(new WorldDetailLeague(1000,1000,"International"));
	}
}
