package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;

import java.util.List;
import java.util.Vector;

public class SpecialEventsPrediction {

    private double m_dChanceCreationProbability = 0;
    private ISpecialEventPredictionAnalyzer.SpecialEventType m_eEventType;
    private IMatchRoleID m_cResponsiblePosition;
    private List<IMatchRoleID> m_cInvolvedPositions;

    public SpecialEventsPrediction(IMatchRoleID position, ISpecialEventPredictionAnalyzer.SpecialEventType type, double p) {
        m_cResponsiblePosition = position;
        m_eEventType = type;
        m_dChanceCreationProbability = p;
    }

    public IMatchRoleID getResponsiblePosition() {
        return m_cResponsiblePosition;
    }

    public void setResponsiblePosition(IMatchRoleID responsiblePosition) {
        this.m_cResponsiblePosition = responsiblePosition;
    }

    public List<IMatchRoleID> getInvolvedPositions() {
        return m_cInvolvedPositions;
    }

    public void setInvolvedPositions(List<IMatchRoleID> m_cInvolvedPositions) {
        this.m_cInvolvedPositions = m_cInvolvedPositions;
    }

    public void setInvolvedPosition(MatchRoleID mid) {
        if (this.m_cInvolvedPositions == null) {
            this.m_cInvolvedPositions = new Vector<IMatchRoleID>();
        } else {
            this.m_cInvolvedPositions.clear();
        }
        this.m_cInvolvedPositions.add(mid);
    }

    static public SpecialEventsPrediction createIfInRange(IMatchRoleID position,
                                                          ISpecialEventPredictionAnalyzer.SpecialEventType eventName,
                                                          double maxProbability,
                                                          double valueAtMaxProbability,
                                                          double valueAtNullProbability,
                                                          double value) {
        if (valueAtMaxProbability > valueAtNullProbability) {
            if (value <= valueAtNullProbability || value > valueAtMaxProbability) return null;
        } else {
            if (value >= valueAtNullProbability || value < valueAtMaxProbability) return null;
        }
        double f = maxProbability - maxProbability / (valueAtNullProbability - valueAtMaxProbability) * (value - valueAtMaxProbability);  // linear fit
        return new SpecialEventsPrediction(position, eventName, f);
    }
}
