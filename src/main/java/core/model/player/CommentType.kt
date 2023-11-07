package core.model.player

enum class CommentType(val value: Int) {
    /*
    ScoutCommentTypeID
    Value	Description
    0	Hello
    1	Found player
    2	(not in use)
    3	Talented in one skill
    4	Current skill level
    5	Potential skill level
    6	Average skill level
    7	Sign player to team
    8	(not in use)
    9	Player has speciality
     */
    HELLO(0),
    FOUND_PLAYER(1),
    NOT_USED(2),
    TALENTED_IN_ONE_SKILL(3),
    CURRENT_SKILL_LEVEL(4),
    POTENTIAL_SKILL_LEVEL(5),
    AVERAGE_SKILL_LEVEL(6),
    SIGN_PLAYER_TO_TEAM(7),
    NOT_USED2(8),
    PLAYER_HAS_SPECIALTY(9);

    companion object {
        @JvmStatic
        fun valueOf(id: Int?): CommentType? {
            if (id != null) {
                for (type in entries) {
                    if (type.value == id) {
                        return type
                    }
                }
            }
            return null
        }
    }
}
