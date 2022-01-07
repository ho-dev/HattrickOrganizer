package core.model.enums;

import core.model.match.IMatchType;
import core.model.match.SourceSystem;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public enum MatchTypeExtended implements IMatchType {

	EMERALDCUP(1001), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	RUBYCUP(1002), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	SAPPHIRECUP(1003), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	CONSOLANTECUP(1004), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
	DIVISIONBATTLE(1101), // That match type is not part of HT CHPP API. It is created within HO for convenience with existing DB structure
 	GROUP_OFFICIAL(9990); // Supposed to replace constants declared in SpielePanel


	private final int id;


	MatchTypeExtended(int id) {
		this.id = id;
	}


	public int getId() {
		return id;
	}


	public static Stream<MatchTypeExtended> stream() {
		return Stream.of(MatchTypeExtended.values());
	}

	public static MatchTypeExtended getById(int id) {
		for (MatchTypeExtended matchType : MatchTypeExtended.values()) {
			if (matchType.getId() == id) {
				return matchType;
			}
		}
		return null;
	}
	
	@Override
	public int getMatchTypeId() {
		return MatchType.CUP.getId();
	}

	@Override
	public int getIconArrayIndex() {
		return switch (this) {
			case EMERALDCUP -> 4;
			case RUBYCUP -> 5;
			case SAPPHIRECUP -> 6;
			case CONSOLANTECUP -> 12;
			case DIVISIONBATTLE -> 13;
			default -> 11;
		};
	}

	@Override
	public String getName() {
		return switch (this) {
			case EMERALDCUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.emerald_cup");
			case RUBYCUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.ruby_cup");
			case SAPPHIRECUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.sapphire_cup");
			case CONSOLANTECUP -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.consolante_cup");
			case DIVISIONBATTLE -> core.model.HOVerwaltung.instance().getLanguageString("ls.match.matchtype.division_battle");
			default -> "unknown";
		};
	}
}
