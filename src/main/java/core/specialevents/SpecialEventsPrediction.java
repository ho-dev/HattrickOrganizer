package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;

import java.util.List;

public class SpecialEventsPrediction {

    private double m_dChanceCreationProbability=0;
    private String m_sName=null;
    private IMatchRoleID m_cResponsiblePosition =null;
    private List<IMatchRoleID> m_cInvolvedPositions=null;

    public SpecialEventsPrediction(IMatchRoleID position, String name, double p) {
        m_cResponsiblePosition = position;
        m_sName = name;
        m_dChanceCreationProbability=p;
    }

    public IMatchRoleID getResponsiblePosition() {
        return m_cResponsiblePosition;
    }

    public void setResponsiblePosition(IMatchRoleID responsiblePosition) {
        this.m_cResponsiblePosition = responsiblePosition;
    }
}
