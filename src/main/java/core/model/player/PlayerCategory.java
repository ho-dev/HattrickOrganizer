package core.model.player;

import core.model.HOVerwaltung;
import module.youth.YouthTrainingType;

public enum PlayerCategory {

    //    PlayerCategoryID
    //    Value	Description
    //1	Keeper
    //2	Wing back
    //3	Central defender
    //4	Winger
    //5	Inner midfielder
    //6	Forward
    //7	Substitute
    //8	Reserve
    //9	Extra 1
    //            10	Extra 2
    //            0	No category set

    NoCategorySet(0),
    Keeper(1),
    WingBack(2),
    CentralDefender(3),
    Winger(4),
    InnerMidfield(5),
    Forward(6),
    Substitute(7),
    Reserve(8),
    Extra1(9),
    Extra2(10);

    private final int id;

    PlayerCategory(int id) {
        this.id = id;
    }

    public static String StringValueOf(PlayerCategory value) {
        var hov = HOVerwaltung.instance();
        if ( value == null) return hov.getLanguageString("ls.player.category.undefined");
        return hov.getLanguageString("ls.player.category."+value._toString());
    }

    private String _toString() {
        return super.toString();
    }

    @Override
    public String toString() {
        return StringValueOf(this);
    }

    public int getId() {
        return id;
    }

    public static PlayerCategory valueOf(Integer id) {
        if ( id != null) {
            for (var category : PlayerCategory.values()) {
                if (category.getId() == id) {
                    return category;
                }
            }
        }
        return null;
    }

}
