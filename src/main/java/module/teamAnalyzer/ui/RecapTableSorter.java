package module.teamAnalyzer.ui;

import core.constants.player.PlayerAbility;
import core.module.config.ModuleConfig;
import module.teamAnalyzer.SystemManager;
import module.transfer.ui.sorter.AbstractTableSorter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.TableModel;



public class RecapTableSorter extends AbstractTableSorter {
    //~ Instance fields ----------------------------------------------------------------------------

    protected final class NaturalNumericComparator implements
			Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			return parseToInt(o1) - parseToInt(o2);
		}

		private int parseToInt(String o1) {
			try {
				return Integer.parseInt(o1);
			} catch (NumberFormatException e) {
				return Integer.MAX_VALUE;
			}
		}
	}

	private static final long serialVersionUID = -3606200720032237171L;
	private List<String> skills;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new RecapTableSorter object.
     */
    public RecapTableSorter(TableModel tableModel) {
        super(tableModel);
        skills = new ArrayList<>();

        for (int i = 1; i < 23; i++) {
            skills.add(PlayerAbility.getNameForSkill(i, false, false));
        }
    }

    @Override
	public Comparator<String> getCustomComparator(int column) {
    	if (column == 3) {
    		return new NaturalNumericComparator();
    	}
        if ((column > 4) && (column < 12)) {
            return new Comparator<>() {
                @Override
                public boolean equals(Object arg0) {
                    return false;
                }

                @Override
                public int compare(String arg0, String arg1) {
                    try {
                        double d1 = RatingUtil.getRating(arg0 + "",
                                ModuleConfig.instance().getBoolean(SystemManager.ISNUMERICRATING),
                                ModuleConfig.instance().getBoolean(SystemManager.ISDESCRIPTIONRATING),
                                skills);
                        double d2 = RatingUtil.getRating(arg1 + "",
                                ModuleConfig.instance().getBoolean(SystemManager.ISNUMERICRATING),
                                ModuleConfig.instance().getBoolean(SystemManager.ISDESCRIPTIONRATING),
                                skills);

                        if (d1 > d2) {
                            return 1;
                        }

                        if (d1 < d2) {
                            return -1;
                        }
                    } catch (Exception ignored) {
                    }

                    return 0;
                }
            };
        }

        if ((column > 11) && (column < 16)) {
            return new Comparator<>() {
                private DecimalFormat df = new DecimalFormat("###.#");

                @Override
                public boolean equals(Object arg0) {
                    return false;
                }

                @Override
                public int compare(String arg0, String arg1) {
                    try {
                        double d1 = df.parse(arg0 + "").doubleValue();
                        double d2 = df.parse(arg1 + "").doubleValue();

                        if (d1 > d2) {
                            return 1;
                        }

                        if (d1 < d2) {
                            return -1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return 0;
                }
            };
        }

        if (column == 16) {
            return new Comparator<>() {
                private DecimalFormat df = new DecimalFormat("###.##");

                @Override
                public boolean equals(Object arg0) {
                    return false;
                }

                @Override
                public int compare(String arg0, String arg1) {
                    try {
                        double d1 = df.parse(arg0 + "").doubleValue();
                        double d2 = df.parse(arg1 + "").doubleValue();

                        if (d1 > d2) {
                            return 1;
                        }

                        if (d1 < d2) {
                            return -1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return 0;
                }
            };
        }

        if (column == 18) {
            return new Comparator<>() {
                @Override
                public boolean equals(Object arg0) {
                    return false;
                }

                @Override
                public int compare(String arg0, String arg1) {
                    try {
                        double d1 = RatingUtil.getRating(arg0 + "", false, true, skills);
                        double d2 = RatingUtil.getRating(arg1 + "", false, true, skills);

                        if (d1 > d2) {
                            return 1;
                        }

                        if (d1 < d2) {
                            return -1;
                        }
                    } catch (Exception ignored) {
                    }

                    return 0;
                }
            };
        }

        return null;
    }

    @Override
	public boolean hasHeaderLine() {
        return true;
    }

    @Override
	public int minSortableColumn() {
        return 3;
    }
}
