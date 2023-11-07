package core.model.player

enum class TrainerType(val id: Int) {
    /*
    0 = defensive, 1 = offensive, 2 = balanced
     */
    None(-1),
    Defensive(0),
    Offensive(1),
    Balanced(2);

    fun toInt(): Int {
        return id
    }

    companion object {
        @JvmStatic
        fun fromInt(i: Int?): TrainerType {
            return if (i != null) {
                when (i) {
                    0 -> Defensive
                    1 -> Offensive
                    2 -> Balanced
                    else -> None
                }
            } else None
        }

        fun toInt(trainerTyp: TrainerType?): Int {
            return trainerTyp?.toInt() ?: None.id
        }
    }
}
