// %3002294435:hoplugins.teamAnalyzer.comparator%
package module.teamanalyzer.manager;

import core.util.HODateTime;
import module.teamanalyzer.vo.Match;

import java.util.Comparator;


/**
 * Comparator that orders based on match date
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class MatchComparator implements Comparator<Match> {
    //~ Methods ------------------------------------------------------------------------------------

    @Override
	public int compare(Match o1, Match o2) {
        HODateTime s1 = o1.getMatchDate();
        HODateTime s2 = o2.getMatchDate();

        return s2.compareTo(s1);
    }
}
