package core.db;

import core.util.HODateTime;
import module.transfer.scout.ScoutEintrag;
import java.sql.Types;
import java.util.List;
import java.util.Vector;


final class ScoutTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "SCOUT";
	
	ScoutTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("PlayerID").setGetter((o) -> ((ScoutEintrag) o).getPlayerID()).setSetter((o, v) -> ((ScoutEintrag) o).setPlayerID((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Name").setGetter((o) -> ((ScoutEintrag) o).getName()).setSetter((o, v) -> ((ScoutEintrag) o).setName((String) v)).setType(Types.VARCHAR).setLength(127).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Info").setGetter((o) -> ((ScoutEintrag) o).getInfo()).setSetter((o, v) -> ((ScoutEintrag) o).setInfo((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Age").setGetter((o) -> ((ScoutEintrag) o).getAlter()).setSetter((o, v) -> ((ScoutEintrag) o).setAlter((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Marktwert").setGetter((o) -> ((ScoutEintrag) o).getTSI()).setSetter((o, v) -> ((ScoutEintrag) o).setTSI((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Speciality").setGetter((o) -> ((ScoutEintrag) o).getSpeciality()).setSetter((o, v) -> ((ScoutEintrag) o).setSpeciality((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Kondition").setGetter((o) -> ((ScoutEintrag) o).getKondition()).setSetter((o, v) -> ((ScoutEintrag) o).setKondition((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Erfahrung").setGetter((o) -> ((ScoutEintrag) o).getErfahrung()).setSetter((o, v) -> ((ScoutEintrag) o).setErfahrung((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Form").setGetter((o) -> ((ScoutEintrag) o).getForm()).setSetter((o, v) -> ((ScoutEintrag) o).setForm((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Torwart").setGetter((o) -> ((ScoutEintrag) o).getTorwart()).setSetter((o, v) -> ((ScoutEintrag) o).setTorwart((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Verteidigung").setGetter((o) -> ((ScoutEintrag) o).getVerteidigung()).setSetter((o, v) -> ((ScoutEintrag) o).setVerteidigung((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Spielaufbau").setGetter((o) -> ((ScoutEintrag) o).getSpielaufbau()).setSetter((o, v) -> ((ScoutEintrag) o).setSpielaufbau((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Fluegel").setGetter((o) -> ((ScoutEintrag) o).getFluegelspiel()).setSetter((o, v) -> ((ScoutEintrag) o).setFluegelspiel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Torschuss").setGetter((o) -> ((ScoutEintrag) o).getTorschuss()).setSetter((o, v) -> ((ScoutEintrag) o).setTorschuss((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Passpiel").setGetter((o) -> ((ScoutEintrag) o).getPasspiel()).setSetter((o, v) -> ((ScoutEintrag) o).setPasspiel((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Standards").setGetter((o) -> ((ScoutEintrag) o).getStandards()).setSetter((o, v) -> ((ScoutEintrag) o).setStandards((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Price").setGetter((o) -> ((ScoutEintrag) o).getPrice()).setSetter((o, v) -> ((ScoutEintrag) o).setPrice((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Deadline").setGetter((o) -> ((ScoutEintrag) o).getDeadline()).setSetter((o, v) -> ((ScoutEintrag) o).setDeadline(((HODateTime) v).toDbTimestamp())).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Wecker").setGetter((o) -> ((ScoutEintrag) o).isWecker()).setSetter((o, v) -> ((ScoutEintrag) o).setWecker((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("AgeDays").setGetter((o) -> ((ScoutEintrag) o).getAgeDays()).setSetter((o, v) -> ((ScoutEintrag) o).setAgeDays((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Agreeability").setGetter((o) -> ((ScoutEintrag) o).getAgreeability()).setSetter((o, v) -> ((ScoutEintrag) o).setAgreeability((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("baseWage").setGetter((o) -> ((ScoutEintrag) o).getbaseWage()).setSetter((o, v) -> ((ScoutEintrag) o).setbaseWage((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Nationality").setGetter((o) -> ((ScoutEintrag) o).getNationality()).setSetter((o, v) -> ((ScoutEintrag) o).setNationality((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Leadership").setGetter((o) -> ((ScoutEintrag) o).getLeadership()).setSetter((o, v) -> ((ScoutEintrag) o).setLeadership((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Loyalty").setGetter((o) -> ((ScoutEintrag) o).getLoyalty()).setSetter((o, v) -> ((ScoutEintrag) o).setLoyalty((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("MotherClub").setGetter((o) -> ((ScoutEintrag) o).isHomegrown()).setSetter((o, v) -> ((ScoutEintrag) o).setHomegrown((boolean) v)).setType(Types.BOOLEAN).isNullable(false).build()
		};
	}

	/**
	 * Save players from TransferScout
	 */
	void saveScoutList(Vector<ScoutEintrag> list) {
		executePreparedDelete();
		if (list != null) {
			for (var scout : list) {
				scout.setIsStored(false);
				store(scout);
			}
		}
	}

	@Override
	protected PreparedSelectStatementBuilder createPreparedSelectStatementBuilder(){
		return new PreparedSelectStatementBuilder(this, "");
	}

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this, "");
	}

	/**
	 * Load player list for insertion into TransferScout
	 */
	List<ScoutEintrag> getScoutList() {
		return load(ScoutEintrag.class);
	}
	
}
