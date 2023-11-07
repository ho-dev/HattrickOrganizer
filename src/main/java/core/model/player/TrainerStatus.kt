package core.model.player

enum class TrainerStatus(private val id: Int) {
    // 1 = PlayingTrainer, 2 = OnlyTrainer, 3 = HoFTrainer
    PlayingTrainer(1),
    OnlyTrainer(2),
    HoFTrainer(3);

    fun toInt(): Int {
        return id
    }

    companion object {
        fun fromInt(i: Int?): TrainerStatus? {
            return if (i != null) {
                when (i) {
                    1 -> PlayingTrainer
                    2 -> OnlyTrainer
                    3 -> HoFTrainer
                    else -> null
                }
            } else null
        }

        fun toInteger(trainerStatus: TrainerStatus?): Int? {
            return trainerStatus?.toInt()
        }
    }
}
