package module.training.ui;

import core.model.UserParameter;

import java.util.Comparator;
import java.util.Vector;

public class TrainingComparator implements Comparator<Vector<String>> {
    //~ Methods ------------------------------------------------------------------------------------
    private int speed;
    private int first;

    TrainingComparator(int speedColumn, int firstColumn) {
        first = firstColumn;
        speed = speedColumn;
    }

    @Override
    public int compare(Vector<String> arg0, Vector<String> arg1) {
        Vector<String> v1 =  arg0;
        Vector<String> v2 =  arg1;

        /* Compare training speed first */
        if (!v1.get(speed).equals(v2.get(speed))) {
            int i1 = Integer.valueOf(v1.get(speed));
            int i2 = Integer.valueOf(v2.get(speed));

            if (i1 > i2)
                return -1;
            else
                return 1;
        }

        for (int i = first; i < (UserParameter.instance().futureWeeks + first); i++) {
            String s1 = "" + v1.get(i); //$NON-NLS-1$
            String s2 = "" + v2.get(i); //$NON-NLS-1$

            if (!s1.equalsIgnoreCase("") && s2.equalsIgnoreCase("")) { //$NON-NLS-1$ //$NON-NLS-2$
                return -1;
            } else if (s1.equalsIgnoreCase("") && !s2.equalsIgnoreCase("")) { //$NON-NLS-1$ //$NON-NLS-2$
                return 1;
            }
        }

        return 0;
    }
}
