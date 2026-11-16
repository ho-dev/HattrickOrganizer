package module.youth;

import core.constants.player.PlayerSkill;
import core.db.AbstractTable;
import core.util.HODateTime;
import lombok.Getter;
import lombok.Setter;
import module.training.Skills;

/**
 * One of the coach's training comments about a youth player, as youthplayerdetails 1.4 sends it
 * ({@code TrainingReports/TrainingReport}). Hattrick sends them only for the user's own youth
 * players and only for a club with Gold supporter (or higher).
 * <p>
 * CHPP does not document the {@code ReportType} codes, only that 18, 19, 28, 29 and 30 carry a
 * skill level. The type is therefore stored raw and nothing derives skill estimates from it yet:
 * the codes can only be mapped against real reports, and none were available when this was
 * written (see docs/chpp_files.md).
 */
@Getter
@Setter
public class YouthTrainerComment extends AbstractTable.Storable {

    private int youthPlayerId;
    private HODateTime reportDate;
    /** CHPP's ReportType, raw. */
    private int reportType;
    /** ScoutCommentSkillTypeID of the skill the comment names, 0 when it names none. */
    private int skillType;
    /** The skill level named, 0 unless ReportType is 18, 19, 28, 29 or 30. */
    private int skillLevel;
    /** The comment text, in the language of the user who downloaded it. */
    private String text;

    /** The skill the comment names, null when it names none. */
    public PlayerSkill getSkill() {
        final var type = Skills.ScoutCommentSkillTypeID.valueOf(skillType);
        return type == null ? null : type.toHTSkillId();
    }
}
