package core.db;

import core.constants.player.PlayerSkill;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.model.player.Skillup;
import core.util.HODateTime;

import java.sql.*;
import java.util.*;

final class SkillupTable extends AbstractTable {

	/**
	 * tablename
	 **/
	final static String TABLENAME = "SPIELERSKILLUP";
//	private static Map<String, Vector<Object[]>> playerSkillup = null;

	SkillupTable(JDBCAdapter adapter) {
		super(TABLENAME, adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("SpielerID").setGetter((o) -> ((Skillup) o).getPlayerId()).setSetter((o, v) -> ((Skillup) o).setPlayerId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Skill").setGetter((o) -> ((Skillup) o).getSkill()).setSetter((o, v) -> ((Skillup) o).setSkill(PlayerSkill.fromInteger((Integer) v))).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID").setGetter((o) -> ((Skillup) o).getHrfId()).setSetter((o, v) -> ((Skillup) o).setHrfId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Datum").setGetter((o) -> ((Skillup) o).getDate().toDbTimestamp()).setSetter((o, v) -> ((Skillup) o).setDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Value").setGetter((o) -> ((Skillup) o).getValue()).setSetter((o, v) -> ((Skillup) o).setValue((int) v)).setType(Types.INTEGER).isNullable(false).build()
		};
	}

	@Override
	protected String[] getCreateIndexStatement() {
		return new String[]{
				"CREATE INDEX iSkillup_1 ON " + getTableName() + "(" + columns[2].getColumnName() + ")",
				"CREATE INDEX iSkillup_2 ON " + getTableName() + "(" + columns[2].getColumnName() + "," + columns[3].getColumnName() + ")"};
	}

	@Override
	protected PreparedDeleteStatementBuilder createPreparedDeleteStatementBuilder(){
		return new PreparedDeleteStatementBuilder(this, "WHERE HRF_ID=?");
	}

	private void storeSkillup(Skillup skillup) {
		store(skillup);
	}

	private final PreparedSelectStatementBuilder loadLastLevelUpStatementBuilder = new PreparedSelectStatementBuilder(this,
			"WHERE SPIELERID=? AND SKILL = ? ORDER BY Datum DESC LIMIT 1");
	Skillup getLastLevelUp(PlayerSkill skillCode, int spielerId) {
		return loadOne(Skillup.class, this.adapter.executePreparedQuery(loadLastLevelUpStatementBuilder.getStatement(), spielerId, PlayerSkill.toInteger(skillCode)));
	}

	List<Skillup> getAllLevelUp(PlayerSkill skillCode, int spielerId) {
		return load(Skillup.class, spielerId, PlayerSkill.toInteger(skillCode));
	}


	void importNewSkillup(HOModel homodel) {
		List<Player> players = homodel.getCurrentPlayers();
		for (Player nPlayer : players) {
			Player oPlayer = HOVerwaltung.instance().getModel().getCurrentPlayer(nPlayer.getPlayerId());
			if (oPlayer != null) {
				checkNewSkillup(nPlayer, nPlayer.getGoalkeeperSkill(), oPlayer.getGoalkeeperSkill(), PlayerSkill.KEEPER, homodel.getHrfId());
				checkNewSkillup(nPlayer, nPlayer.getPlaymakingSkill(), oPlayer.getPlaymakingSkill(), PlayerSkill.PLAYMAKING, homodel.getHrfId());
				checkNewSkillup(nPlayer, nPlayer.getPassingSkill(), oPlayer.getPassingSkill(), PlayerSkill.PASSING, homodel.getHrfId());
				checkNewSkillup(nPlayer, nPlayer.getWingerSkill(), oPlayer.getWingerSkill(), PlayerSkill.WINGER, homodel.getHrfId());
				checkNewSkillup(nPlayer, nPlayer.getDefendingSkill(), oPlayer.getDefendingSkill(), PlayerSkill.DEFENDING, homodel.getHrfId());
				checkNewSkillup(nPlayer, nPlayer.getScoringSkill(), oPlayer.getScoringSkill(), PlayerSkill.SCORING, homodel.getHrfId());
				checkNewSkillup(nPlayer, nPlayer.getSetPiecesSkill(), oPlayer.getSetPiecesSkill(), PlayerSkill.SETPIECES, homodel.getHrfId());
				checkNewSkillup(nPlayer, nPlayer.getStamina(), oPlayer.getStamina(), PlayerSkill.STAMINA, homodel.getHrfId());
				checkNewSkillup(nPlayer, nPlayer.getExperience(), oPlayer.getExperience(), PlayerSkill.EXPERIENCE, homodel.getHrfId());
			}
		}
	}

	private void checkNewSkillup(Player nPlayer, int newValue, int oldValue, PlayerSkill skill, int hrf) {
		if (newValue > oldValue) {
			var skillup = new Skillup();
			skillup.setHrfId(hrf);
			skillup.setDate(nPlayer.getHrfDate());
			skillup.setSkill(skill);
			skillup.setPlayerId(nPlayer.getPlayerId());
			skillup.setValue(newValue);
			storeSkillup(skillup);
			nPlayer.resetSkillUpInformation();
		}
	}
}