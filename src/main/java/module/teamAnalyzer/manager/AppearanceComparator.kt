// %4122214879:hoplugins.teamAnalyzer.comparator%
package module.teamAnalyzer.manager;



import module.teamAnalyzer.vo.PlayerAppearance;

import java.util.Comparator;


/**
 * Comparator that orders based on number of appearance
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
class AppearanceComparator implements Comparator<PlayerAppearance> {
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Compare two objects
     *
     * @param o1
     * @param o2
     *
     * @return
     */
    public int compare(PlayerAppearance o1, PlayerAppearance o2) {
        int s1 = o1.getAppearance();
        int s2 = o2.getAppearance();

        if (s1 > s2) {
            return -1;
        }

        if (s2 > s1) {
            return 1;
        }

        return 1;
    }
}
