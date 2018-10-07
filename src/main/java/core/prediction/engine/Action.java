package core.prediction.engine;

import core.model.match.IMatchDetails;

public class Action implements  Comparable<Object> {
    //~ Instance fields ----------------------------------------------------------------------------
    private boolean homeTeam;
    private boolean score;
    private int area;
    private int minute;
    private int type;

    //~ Methods ------------------------------------------------------------------------------------
    public final void setArea(int i) {
        area = i;
    }

    public final int getArea() {
        return area;
    }

    public final void setHomeTeam(boolean b) {
        homeTeam = b;
    }

    public final boolean isHomeTeam() {
        return homeTeam;
    }

    public final void setMinute(int i) {
        minute = i;
    }

    public final int getMinute() {
        return minute;
    }

    public final void setScore(boolean b) {
        score = b;
    }

    public final boolean isScore() {
        return score;
    }

    public final String getShortDesc() {
        final StringBuffer buffer = new StringBuffer();

        if (type == IMatchDetails.TAKTIK_KONTER) {
            buffer.append(core.model.HOVerwaltung.instance().getLanguageString("Counter"));
        } else {
            buffer.append(core.model.HOVerwaltung.instance().getLanguageString("Attack"));
        }

        buffer.append(" ");

        if (area == -1) {
            buffer.append(core.model.HOVerwaltung.instance().getLanguageString("on_the_left"));
        } else if (area == 0) {
            buffer.append(core.model.HOVerwaltung.instance().getLanguageString("on_the_middle"));
        } else {
            buffer.append(core.model.HOVerwaltung.instance().getLanguageString("on_the_right"));
        }

        buffer.append(". ");

        if (score) {
            buffer.append(core.model.HOVerwaltung.instance().getLanguageString("TOR"));
            buffer.append("!");
        }

        return buffer.toString();
    }

    public final void setType(int i) {
        type = i;
    }

    public final int getType() {
        return type;
    }

    public final int compareTo(Object o) {
        if (o instanceof Action) {
            final Action action = (Action) o;

            if (action.getMinute() < this.getMinute()) {
                return -1;
            } else if (action.getMinute() < this.getMinute()) {
                return 1;
            } else {
                return 0;
            }
        }

        return 0;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public final String toString() {
        final StringBuffer buffer = new StringBuffer();

        if (homeTeam) {
            buffer.append("Team1 has a chance ");
        } else {
            buffer.append("Team2 has a chance ");
        }

        if (area == -1) {
            buffer.append(" on the left");
        } else if (area == 0) {
            buffer.append(" on the middle");
        } else {
            buffer.append(" on the right");
        }

        buffer.append(", at minute ");
        buffer.append(minute);

        if (type == IMatchDetails.TAKTIK_KONTER) {
            buffer.append(". It's a counter!!");
        }

        if (score) {
            buffer.append("and it is a goal");
        }

        return buffer.toString();
    }
}
