// %3002294435:hoplugins.teamAnalyzer.comparator%
package module.teamAnalyzer.manager;

import core.util.HODateTime;
import module.teamAnalyzer.vo.Match;

import java.util.Comparator;
import java.util.Date;


/**
 * Comparator that orders based on match date
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class MatchComparator implements Comparator<Match> {
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Compare two objects
     *
     * @param o1
     * @param o2
     *
     * @return
     */
    @Override
	public int compare(Match o1, Match o2) {
        HODateTime s1 = o1.getMatchDate();
        HODateTime s2 = o2.getMatchDate();

        return s2.compareTo(s1);
    }
}
