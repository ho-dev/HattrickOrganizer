// %583496668:hoplugins.teamAnalyzer.comparator%
package module.teamAnalyzer.manager;

import module.teamAnalyzer.report.TacticReport;

import java.util.Comparator;


/**
 * Comparator that orders based on number of performanceand rating as secondary parameter
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class PerformanceComparator implements Comparator<TacticReport> {
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Compare two objects
     *
     * @param o1
     * @param o2
     *
     * @return
     */
    public int compare(TacticReport o1, TacticReport o2) {
        int s1 = o1.getAppearance();
        int s2 = o2.getAppearance();

        if (s1 > s2) {
            return -1;
        }

        if (s2 > s1) {
            return 1;
        }

        double n1 = o1.getRating();
        double n2 = o2.getRating();

        if (n1 > n2) {
            return -1;
        }

        if (n2 > n1) {
            return 1;
        }

        return 1;
    }
}
