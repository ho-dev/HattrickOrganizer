package module.matchesanalyzer.data;

import core.model.HOVerwaltung;

import java.awt.Point;
import java.util.Comparator;


public class MatchesAnalyzerPlayer {

	public enum Position {
		KEEPER	(0, 0),
		DEFENDER(3, 1),
		WINGBACK(4, 2),
		MIDFIELD(7, 1),
		WINGER	(8, 2),
		FORWARD	(11, 1);

		private final int xOffset;
		private final int yFactor;

		private Position(int xOffset, int yFactor) {
			this.xOffset = xOffset;
			this.yFactor = yFactor;
		}

		public int getxOffset() {
			return xOffset;
		}

		public int getyFactor() {
			return yFactor;
		}
	}

	public enum Side {
		RIGHT	(1, "Rechts"),
		MIDDLE	(0, "Mitte"),
		LEFT	(-1, "Links");

		private final int yOffset;
		private final String languageString;

		private Side(int yOffset, String lang) {
			this.yOffset = yOffset;
			this.languageString = HOVerwaltung.instance().getLanguageString(lang);
		}

		public int getyOffset() {
			return yOffset;
		}

		public String getLanguageString() {
			return languageString;
		}
	}

	public enum Behavior {
		NORMAL,
		OFFENSIVE,
		DEFENSIVE,
		TOWARDS_MIDDLE,
		TOWARDS_WING,
		OLD_EXTRA_FORWARD,
		OLD_EXTRA_MIDFIELD,
		OLD_EXTRA_DEFENDER,
		OLD_EXTRA_DEFENSIVE_FORWARD;
	}

	public enum Role {
		GK(Position.KEEPER, Side.MIDDLE),
		RWB(Position.WINGBACK, Side.RIGHT),
		RD(Position.DEFENDER, Side.RIGHT),
		CD(Position.DEFENDER, Side.MIDDLE),
		LD(Position.DEFENDER, Side.LEFT),
		LWB(Position.WINGBACK, Side.LEFT),
		RW(Position.WINGER, Side.RIGHT),
		RM(Position.MIDFIELD, Side.RIGHT),
		CM(Position.MIDFIELD, Side.MIDDLE),
		LM(Position.MIDFIELD, Side.LEFT),
		LW(Position.WINGER, Side.LEFT),
		RFW(Position.FORWARD, Side.RIGHT),
		CFW(Position.FORWARD, Side.MIDDLE),
		LFW(Position.FORWARD, Side.LEFT);

		private final Position position;
		private final Side side;

		private Role(Position position, Side side) {
			this.position = position;
			this.side = side;
		}

		public Position getPosition() {
			return position;
		}

		public Side getSide() {
			return side;
		}
	}

	private final int id;
	private final String name;
	private final Role role;
	private final Behavior behavior;
	private final double stars;

	public MatchesAnalyzerPlayer(int id, String name, Role role, Behavior behavior, double stars) {
		this.id = id;
		this.name = name;
		this.role = role;
		this.behavior = behavior;
		this.stars = stars;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Role getRole() {
		return role;
	}

	public Behavior getBehavior() {
		return behavior;
	}
	
	public double getStars() {
		return stars;
	}

	public Point getPoint() {
		Point point = new Point();
		Position position = role.getPosition();
		point.x = position.getxOffset();
		point.y = position.getyFactor() * role.getSide().getyOffset() + 3;
		return point;
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof MatchesAnalyzerPlayer)) return false;

		MatchesAnalyzerPlayer player = (MatchesAnalyzerPlayer)obj;

		return(player.getId() == getId() && player.getName().compareTo(player.getName()) == 0);
	}

	public static Comparator<MatchesAnalyzerPlayer> comparator() {
		return new Comparator<MatchesAnalyzerPlayer>() {
			@Override
			public int compare(MatchesAnalyzerPlayer a, MatchesAnalyzerPlayer b) {
				return a.getRole().ordinal() - b.getRole().ordinal();
			}
		};
	}

	public static Comparator<MatchesAnalyzerPlayer> reverse_comparator() {
		return new Comparator<MatchesAnalyzerPlayer>() {
			@Override
			public int compare(MatchesAnalyzerPlayer a, MatchesAnalyzerPlayer b) {
				return b.getRole().ordinal() - a.getRole().ordinal();
			}
		};
	}
}
