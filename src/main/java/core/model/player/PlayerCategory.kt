package core.model.player

import core.model.HOVerwaltung

enum class PlayerCategory(@JvmField val id: Int) {
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

    private fun _toString(): String {
        return super.toString()
    }

    override fun toString(): String {
        return StringValueOf(this)
    }

    companion object {
        fun StringValueOf(value: PlayerCategory?): String {
            val hov = HOVerwaltung.instance()
            return if (value == null || value == NoCategorySet) hov.getLanguageString("ls.player.category.undefined") else hov.getLanguageString(
                "ls.player.category." + value._toString()
            )
        }

        @JvmStatic
        fun valueOf(id: Int?): PlayerCategory? {
            if (id != null) {
                for (category in entries) {
                    if (category.id == id) {
                        return category
                    }
                }
            }
            return null
        }

        fun idOf(category: PlayerCategory?): Int {
            return category?.id ?: NoCategorySet.id
        }
    }
}
