package core.model.player;

public enum CommentType {
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

    HELLO((int) 0),
    FOUND_PLAYER((int) 1),
    NOT_USED((int) 2),
    TALENTED_IN_ONE_SKILL((int)3),
    CURRENT_SKILL_LEVEL((int) 4),
    POTENTIAL_SKILL_LEVEL((int) 5),
    AVERAGE_SKILL_LEVEL((int) 6),
    SIGN_PLAYER_TO_TEAM((int) 7),
    NOT_USED2((int) 8),
    PLAYER_HAS_SPECIALTY((int) 9);

    private final int value;

    CommentType(int id) {
        this.value = id;
    }

    public int getValue() {
        return value;
    }

    public static CommentType valueOf(Integer id) {
        if ( id != null) {
            for (CommentType type : CommentType.values()) {
                if (type.getValue() == id) {
                    return type;
                }
            }
        }
        return null;
    }

}
