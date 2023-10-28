package core.util;

import core.constants.player.PlayerSkill;
import java.util.Map;

public class Htms {


//    Level	Gk	Def	PM	Wg	Ps	Sc	SP

    private static final int[][] htmsPerSkillLevel = {
            {/*20*/ 924, 1791, 1480, 995, 1355, 1547, 287},
            {/*19*/ 797, 1487, 1247, 855, 1148, 1300, 246},
            {/*18*/ 691, 1268, 1070, 741, 988, 1114, 210},
            {/*17*/ 600, 1092, 924, 642, 854, 961, 179},
            {/*16*/ 519, 942, 798, 555, 738, 830, 153},
            {/*15*/ 446, 809, 685, 478, 634, 713, 131},
            {/*14*/ 380, 689, 584, 407, 540, 607, 112},
            {/*13*/ 321, 580, 493, 344, 457, 511, 95},
            {/*12*/ 268, 484, 412, 287, 381, 427, 81},
            {/*11*/ 222, 401, 341, 238, 315, 354, 68},
            {/*10*/ 183, 330, 281, 195, 259, 291, 56},
            {/*9*/ 150, 271, 231, 161, 213, 240, 46},
            {/*8*/ 123, 221, 190, 132, 173, 197, 37},
            {/*7*/ 99, 175, 150, 105, 137, 156, 28},
            {/*6*/ 76, 134, 114, 81, 104, 119, 21},
            {/*5*/ 56, 98, 84, 60, 75, 88, 15},
            {/*4*/ 39, 65, 57, 41, 51, 59, 9},
            {/*3*/ 23, 39, 34, 25, 31, 36, 5},
            {/*2*/ 12, 18, 17, 12, 14, 17, 2},
            {/*1*/ 2, 4, 4, 2, 3, 4, 1}
    };

    private static final int[] HTMS28 = {
            /*17 + 0 */ 1641,
            /*18 + 0 */ 1481,
            /*19 + 0 */ 1323,
            /*20 + 0 */ 1166,
            /*21 + 0 */ 1011,
            /*22 + 0 */ 858,
            /*23 + 0 */ 708,
            /*24 + 0 */ 560,
            /*25 + 0 */ 416,
            /*26 + 0 */ 274,
            /*27 + 0 */ 136,
            /*28 + 0 */ 0,
            /*29 + 0 */ -129,
            /*30 + 0 */ -255,
            /*31 + 0 */ -378,
            /*32 + 0 */ -497,
            /*33 + 0 */ -614,
            /*34 + 0 */ -727,
            // guessed
            /*35 + 0 */ -837,
            /*36 + 0 */ -933,
            /*37 + 0 */ -1030,
            /*38 + 0 */ -1127,
            /*39 + 0 */ -1204,
            /*40 + 0 */ -1301,
            /*41 + 0 */ -1398,
            /*42 + 0 */ -1495,
            /*43 + 0 */ -1592,
            /*44 + 0 */ -1689,
            /*45 + 0 */ -1786,
            /*46 + 0 */ -1883,
            /*47 + 0 */ -1980
    };

    public static int htms(Map<Integer, Integer> skills) {
        var ret = 0;
        for (var skill : skills.entrySet()) {
            var id = skill.getKey();
            var level = skill.getValue();
            if (level <= 0) continue;
            if (level > 20) level = 20;
            int skillIndex = switch (id) {
                case PlayerSkill.DEFENDING -> 1;
                case PlayerSkill.PLAYMAKING -> 2;
                case PlayerSkill.WINGER -> 3;
                case PlayerSkill.PASSING -> 4;
                case PlayerSkill.SCORING -> 5;
                case PlayerSkill.SET_PIECES -> 6;
                default -> 0; // Keeper
            };

            ret += htmsPerSkillLevel[20 - level][skillIndex];
        }
        return ret;
    }

    public static int htms28(Map<Integer, Integer> skills, int ageYears, int ageDays) {
        int index = ageYears - 17;
        if (index < 0) return 0;
        int increment;
        if (index < HTMS28.length) {
            increment = HTMS28[index];
            var next = index + 1;
            if (next < HTMS28.length) {
                var nextVal = HTMS28[next];
                increment += (nextVal - increment) * ageDays / 112;
            }
        } else {
            increment = HTMS28[HTMS28.length - 1];
        }
        return increment + htms(skills);
    }
}
