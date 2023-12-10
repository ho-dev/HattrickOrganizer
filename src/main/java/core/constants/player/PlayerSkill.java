package core.constants.player;

import core.model.HOVerwaltung;


public enum  PlayerSkill {

//	private static final String[] languageKeys = {
//			"ls.player.skill.keeper",
//			"ls.player.skill.defending",
//			"ls.player.skill.winger",
//			"ls.player.skill.playmaking",
//			"ls.player.skill.scoring",
//			"ls.player.skill.passing",
//			"ls.player.skill.stamina",
//			"ls.player.form",
//			"ls.player.skill.setpieces",
//			"ls.player.experience",
//			"ls.player.leadership",
//			"ls.player.loyalty"
//	};

	KEEPER(0),
	DEFENDING(1),
	WINGER(2),
	PLAYMAKING(3),
	SCORING(4),
	PASSING(5),
	STAMINA(6),
	FORM(7),
	SETPIECES(8),
	EXPERIENCE(9),
	LEADERSHIP(10),
	LOYALTY(11);

	private final int id;

	PlayerSkill(int id) {
		this.id = id;
	}

	public int toInt() {
		return id;
	}

	public String getLanguageString() {
		var b = new StringBuilder("ls.player.");
		switch (this) {
			case KEEPER, DEFENDING, WINGER, PLAYMAKING, SCORING, PASSING, STAMINA, SETPIECES -> b.append("skill.");
		}
		b.append(super.toString().toLowerCase());
		return HOVerwaltung.instance().getLanguageString(b.toString());
	}

	public static PlayerSkill fromInteger(Integer i) {
		if (i != null) {
			for (var s : PlayerSkill.values()) {
				if (s.id == i) {
					return s;
				}
			}
		}
		return null;
	}

	public String getXMLElementName(){
		return switch (this){
			case KEEPER -> "Keeper";
			case DEFENDING -> "Defender";
			case PLAYMAKING -> "Playmaker";
			case WINGER -> "Winger";
			case PASSING -> "Passing";
			case SCORING -> "Scorer";
			case SETPIECES -> "SetPieces";
			default -> "unknown";
		};
	}
}
