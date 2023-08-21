package core.db;

import core.model.Team;
import java.sql.Types;

final class TeamTable extends AbstractTable {
	public final static String TABLENAME = "TEAM";
	
	TeamTable(JDBCAdapter adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((p) -> ((Team) p).getHrfId()).setSetter((p, v) -> ((Team) p).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).isPrimaryKey(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TrainingsIntensitaet").setGetter((p) -> ((Team) p).getTrainingslevel()).setSetter((p, v) -> ((Team) p).setTrainingslevel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("TrainingsArt").setGetter((p) -> ((Team) p).getTrainingsArtAsInt()).setSetter((p, v) -> ((Team) p).setTrainingsArtAsInt((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iStimmung").setGetter((p) -> ((Team) p).getTeamSpiritLevel()).setSetter((p, v) -> ((Team) p).setTeamSpiritLevel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iSelbstvertrauen").setGetter((p) -> ((Team) p).getConfidence()).setSetter((p, v) -> ((Team) p).setConfidence((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung541").setGetter((p) -> ((Team) p).getFormationExperience541()).setSetter((p, v) -> ((Team) p).setFormationExperience541((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung433").setGetter((p) -> ((Team) p).getFormationExperience433()).setSetter((p, v) -> ((Team) p).setFormationExperience433((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung352").setGetter((p) -> ((Team) p).getFormationExperience352()).setSetter((p, v) -> ((Team) p).setFormationExperience352((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung451").setGetter((p) -> ((Team) p).getFormationExperience451()).setSetter((p, v) -> ((Team) p).setFormationExperience451((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung532").setGetter((p) -> ((Team) p).getFormationExperience532()).setSetter((p, v) -> ((Team) p).setFormationExperience532((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung343").setGetter((p) -> ((Team) p).getFormationExperience343()).setSetter((p, v) -> ((Team) p).setFormationExperience343((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung442").setGetter((p) -> ((Team) p).getFormationExperience442()).setSetter((p, v) -> ((Team) p).setFormationExperience442((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung523").setGetter((p) -> ((Team) p).getFormationExperience523()).setSetter((p, v) -> ((Team) p).setFormationExperience523((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung550").setGetter((p) -> ((Team) p).getFormationExperience550()).setSetter((p, v) -> ((Team) p).setFormationExperience550((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("iErfahrung253").setGetter((p) -> ((Team) p).getFormationExperience253()).setSetter((p, v) -> ((Team) p).setFormationExperience253((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("StaminaTrainingPart").setGetter((p) -> ((Team) p).getStaminaTrainingPart()).setSetter((p, v) -> ((Team) p).setStaminaTrainingPart((int) v)).setType(Types.INTEGER).isNullable(false).build()

		};
	}

	/**
	 * Save the team data for the given HRF id.
	 */
	void saveTeam(int hrfId, Team team) {
		if (team != null) {
			team.setHrfId(hrfId);
			team.setIsStored(isStored(hrfId));
			store(team);
		}
	}

//	/**
//	 * Gibt die Teamstimmung und das Selbstvertrauen für ein HRFID zurück [0] = Stimmung [1] =
//	 * Selbstvertrauen
//	 */
//	String[] getStimmmungSelbstvertrauen(int hrfid) {
//		final int[] intvalue = new int[2];
//		final String[] returnvalue = new String[2];
//		try {
//			final ResultSet rs = executePreparedSelect(hrfid);
//
//			if (rs.next()) {
//				intvalue[0] = rs.getInt("iStimmung");
//				intvalue[1] = rs.getInt("iSelbstvertrauen");
//
//				//Keine Sinnvollen Werte in der DB -> Strings holen
//				if ((intvalue[0] <= 0) && (intvalue[1] <= 0)) {
//					returnvalue[0] = rs.getString("sStimmung");
//					returnvalue[1] = rs.getString("sSelbstvertrauen");
//				} else {
//					returnvalue[0] = TeamSpirit.toString(intvalue[0]);
//					returnvalue[1] = TeamConfidence.toString(intvalue[1]);
//				}
//			}
//		} catch (Exception e) {
//			HOLogger.instance().log(getClass(),"DatenbankZugriff.getStimmmungSelbstvertrauen : " + e);
//		}
//
//		return returnvalue;
//	}
//
//	/**
//	 * Gibt die Teamstimmung und das Selbstvertrauen für ein HRFID zurück [0] = Stimmung [1] =
//	 * Selbstvertrauen
//	 */
//	int[] getStimmmungSelbstvertrauenValues(int hrfid) {
//		final int[] intvalue = new int[2];
//		try {
//			final ResultSet rs = executePreparedSelect(hrfid);
//
//			if (rs.next()) {
//				intvalue[0] = rs.getInt("iStimmung");
//				intvalue[1] = rs.getInt("iSelbstvertrauen");
//			}
//		} catch (Exception e) {
//			HOLogger.instance().log(getClass(),"DatenbankZugriff.getStimmmungSelbstvertrauenValues : " + e);
//		}
//		return intvalue;
//	}
	
	/**
	 * load the team data for the given HRF id
	 */
	Team getTeam(int hrfID) {
		var ret = loadOne(Team.class, hrfID);
		if ( ret == null) ret = new Team();
		return ret;
	}
}