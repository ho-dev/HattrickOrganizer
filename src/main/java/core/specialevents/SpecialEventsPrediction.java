package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

/** REQUIREMENTS
 *
 * copy of https://devblog.hattrick.org/2017/12/a-new-match-engine-odyssey/

 A new match engine is live since Week 16 with a new Special Events framework. After the release, we received every kind
 of critic in the feedback scale of Hattrick Universe. From “This is the end of Hattrick” to “This is the best change
 ever”. In this blog post, I will try to summarize the changes we did since the previous editorial release.

Why a new Special Events framework?

 “Why, why, why? Why did you change it? Why didn’t just tweak this and that, and increase the other”. That was one of
 the comments we received in the Global forum. The reason is simple. The old code had zero flexibility. We couldn’t just
 tweak a few parts. We couldn’t easily spot bugs or make improvements. Every time was like:

 So, we took the decision to dive into and rewrite the Special Events. We don’t regret it. It was a great experience and
 I want to thank a group of Hattrick users who helped a lot with the implementation.

 Bugs during the first release

 That’s true. Even the greatest release will suffer from that :P. But thanks to your immediate feedback, we were able to
 act fast before the official league matches.

 The majority of those were just presentational issues. Several of you encountered a corner where the set piecer
 received the ball. Or a quick player who stopped by his own teammate. As you know, our live texts are dynamic and we
 need to pass to them the players that participated in that event. Since all our previous events were written upon to
 the previous special events framework, we had to either rewrite most of them or adapt the new one on the current texts.
 What you need to know? The event was calculated correctly, but sometimes the wrong attributes were passing on the event
 texts.

 Of course, we had some real bugs too. The nastiest one was an unpredictable own goal that counted for the opposite team
 instead of yours. Lucky you 😉

 Future feedback

 You might encounter in the future more bugs or texts that don’t make a lot of sense with the new type of events. In
 that case, please contact with your local staff (LAs if it is a text issue or GMs if it is a bug) so we can fix it as
 soon as possible.

 How we generate a new Special Event

 Each special event has its own initial probabilities. However, those numbers are dynamic and updated during the match
 based on your lineup. For example, if you and your opponent don’t have any quick player, the probability of all quick
 players will be reduced to zero and the same amount will be added to the rest. Same as if you have a lot of
 Unpredictable players, those events will have a higher probability to get triggered.

 If the event is one of the Corner, Experienced Forward or Inexperienced Defender, the team with the higher midfield has
 more chances to get it. For the rest of the events, midfield rating doesn’t matter, but the number of your specialties
 does. So, if you have 3 quick players and your opponent has only 2, you have more chances to get a quick event.

 We also made it possible that the same Special Event can happen more than once. But every next time, it will be harder
 to trigger it. Plus, all the negative events have a bigger decrease ratio than the rest. So, it will be rarer to get a
 second inexperience event compared to a second corner.

 Weather Events DONE: see ratings prediction

 In case of rainy and sunny weather, the match will start with a new event that will inform you about the effects. In
 specific on rainy weather, technical and quick players will suffer 5% of ALL their skills while powerful players will
 gain 5% on them. Also, in sunny weather, quick and powerful players will suffer and technical will get a boost of the
 same amount as before. The effect might seem minimal compared to the old enormous effect of weather events, but this
 one will be active during the whole match and affect every single player with the related specialty. Our simulations
 showed us that the effect of a wrong player during bad weather could be a decrease of 1.5-2% on your winning chances.
 This is not a rule since a different player has a different effect, but we advise you to keep it in mind.

 Now, you know in advance that if you add a specific player, he will not contribute the same no matter what.

 We have some nice ideas to improve weather events in the future, but they have to wait for version 2.

 TODO: Playing Creatively

 From now on, Playing Creatively tactic has a tactic level just like the rest. Experience and passing are the important
 skills, with the latter 4x times more than experience. Unpredictable players contribute twice to the tactic compared to
 the rest of the players.

 Play Creatively will increase the maximum number of Special Events by 1 (if both teams use it, then by 2), the chance
 to create an event and will also increase the probability just for the team that gets it. A high tactic skill can even
 double the chances to receive the event instead of your opponent.

 Quick Scores DONE: QuickEventPredictionAnalyzer
 Any Winger, Inner Midfielder or Forward can start a quick rush to score. If the exact opposite player is also quick,
 then he has 100% chance to stop the attack. Otherwise, nearby quick players might stop it instead, but they have a much
 smaller chance of doing so.

 Example: Your FW has 9 scoring. The opposite defender has 14 defending and the opponent keeper has 15 goalkeeping. Then
 the success rate to score is ~39% and if the same player was a winger, it would be 58% since it needs less scoring
 compared to IM and FW players.

 Quick Passes DONE: QuickEventPredictionAnalyzer
 Again, Wingers, Inner Midfielders, and Forwards can start a quick rush with potential for a subsequent pass. Any Winger
 and Forward can be the ball receiver.

 Example: You have a Winger with passing 10 and the average of the Defenders’ defending skill of the same side has 14
 defending. The ball receiver has 12 Scoring and the Keeper has 15 goalkeeping. Then the success ratio is 54% for a FW
 and 71% for a Winger.

 TODO: Boost on Counter Attacks
 You get a boost on your counter attacks tactic level for every quick Winger, Inner Midfielder, and Forward. The
 opponent can still nullify this effect with quick Wing Backs, Inner Midfielders and Defenders. So, a single extra quick
 player will increase your tactic level by 5% while the best case scenario with 8 extra quick players will increase it
 by 14%. Important note. The opponent cannot reduce your initial tactic level. Just reduce the extra boost you get from
 it.

 Technical Goes around a Head Player DONE: TechnicalEventPredictionAnalyzer
 This is known as a tech vs head special event. Any technical Winger, Inner Midfielder and Forward can take advantage of
 it against any Defender, Wing Back and Inner Midfielder. It doesn’t have to be in the exact opposite slot. This is a
 great improvement for the technical players, especially if your opponent is a headers oriented team.

 Example: Your technical guy has 12 scoring and 5 experience. Your opponent defender has 14 defending and 6 experience,
 while the keeper has 15 goalkeeping. Then the chance to score is 38% for FW/IM and 48% for Wingers.

 TODO: Create a Non-Tactical Counter Attack
 Every technical defender and wing back will give you a small chance to create a
 non-tactical counter attack from a missed normal chance of your opponent. Your chances to trigger an event vary from
 1.7 to 3% based on how many technical back players you have.

 Unpredictable Long Pass DONE: UnpredictableEventPredictionAnalyzer
 The keeper, wing backs and central defenders can initiate this event. The passing skill of those need to win against
 the defense of the Inner Midfielders and again the pass receiver (winger or forward) needs some scoring.

 Example: Your defender has 8 passing and the sum of the Inner Midfielders defending is 35. The forward has 13 scoring
 and the keeper is 15. The success rate is 40% (42% for the winger). But if the passing was 13 instead, the same chance
 for the forward would be 65%.

 Unpredictable Scores on His Own DONE: UnpredictableEventPredictionAnalyzer
 One on one with the goalkeeper. This event is for Wingers, Inner Midfielders and Forwards. Keep in mind. A lost chance
 can create a counter attack for your opponent.

 Example: Let’s say your Forward has 10 scoring and the opponent keeper 15 goalkeeping. The success ratio will be 78%
 (86% for wingers)

 Unpredictable Special Action DONE: UnpredictableEventPredictionAnalyzer
 Any unpredictable player (except keepers) may read their opponents, dribble, and find an opening pass. The player then
 receiving the pass would be a Winger or Forward. Same as before, a lost chance can lead to a counter attack for your
 opponent.

 Example: Your Wing back with 9 passing and 7 experience against the average of defending and experience of the
 defenders from the opposite site. 13 and 8 for the example. And the ball receiver as a Forward with 12 scoring and
 keeper with 15 goalkeeping. The success rate will be 62%.

 Unpredictable Mistake DONE: UnpredictableEventPredictionAnalyzer
 Wing Backs, Defenders and Inner Midfielders can have a bad day and make a fatal mistake. An opposing Winger or Forward
 can take advantage of this.

 Example: Your unpredictable defender has 13 Defending and 5 experience, while your opponent forward has 15 scoring and
 8 experience. The chance to score is ~7% (8% for a Winger)

 Unpredictable Own Goal DONE: UnpredictableEventPredictionAnalyzer
 A new event where your wingers and forwards may suffer. If they have a low passing skill level, they can make a wrong
 move towards to your defense and your keeper might not be able to stop it.

 Example: If your Winger has 8 passing and your Keeper has 15 goalkeeping then the chances to score an own goal are
 about 31% but if you increase your passing to 15 then you drop it to 18%

 Power Forward (“Powerful Normal Forward”) DONE: PowerfulEventPredictionAnalyzer
 Size matters. If you have a big forward playing Normal, they can put their bodies in the right place, forcing a second
 chance to shoot and score after a normal chance for your team has been missed. Keep in mind that this physical play
 could result in yellow cards! For that reason, if your player has already been carded, he will tread more carefully in
 these situations. One PNF has 10% probs to create a second chance. Two of them have 16% and three 20%.

 Sitting Midfielder (“Powerful Defensive Inner Midfielder”) DONE: PowerfulEventPredictionAnalyzer
 Lets your powerful midfielder play defensively so that he can concentrate on breaking up attacks and winning the ball.
 If he succeeds in pressing, the opponent’s attack breaks down. Same as before, the percentages to trigger this event is
 10%, 16% and 20%.

 Example: Your stamina needs to be close or higher than your opponent. If you have 13 defending and your opponent 17
 scoring, you have 41% chance to press it.

 Winger to Anyone DONE: WingerEventPredictionAnalyzer
 If your winger manages to break through the defense on his side of the pitch, he might pass the ball to the other
 Winger, Forward, or an Inner Midfielder.

 Example: Your Winger has 13 winger skill level and the average defending of the Defenders on the same side is 15. The
 ball receiver has 13 scoring and the keeper has 15 goalkeeping. The chances to score are 69%. But if you increase your
 Winger skill to 16, your chances increase also to 73%

 Winger to Head DONE: WingerEventPredictionAnalyzer
 Same as the previous event, but if the receiver has a head spec, then it will be far easier for him to score.

 Example: Same skills as before. The difference is that the ball receiver is easier to score now since he is a header.
 The chances are 80% and 84% respectively.

 TODO: Corner to Anyone
 You need a good set pieces taker and your offensive indirect set pieces will be tested against the defensive indirect
 set pieces of your opponent. If the ball receiver has good enough scoring skills to pass the keeper, then it’s a goal.

 Example: Your set piecer needs to collaborate with the rest of your team. This means if your set piecer is not good
 enough, it can reduce your indirect free set pieces during corners or increase it. For example, if your set piecer has
 8 set pieces and your indirect set pieces of your team is at 10, you will have a ~6% drop during the corner. But if he
 is 12 at set pieces, you will have ~4.5% boost on them.

 An example, let’s say your set piecer is 12 and your indirect attacking set pieces at 10. The indirect defending set
 pieces of your opponent is at 11. The ball receiver has 13 scoring and the keeper has 15 goalkeeping. Then you have
 76% chances to score. But if your set piecer is just 6 on skill level, your chances drop to 69%.

 TODO: Corner to Head
 Same as the previous one, but instead of a good scoring player, you just need to win the fight between the headers of
 the two teams.

 Example: The penalty/boost on indirect attacking set pieces of your kicker is the same as the Corner to Anyone.
 However, in this event, it doesn’t matter if the ball receiver is good at scoring, but the total number of the headers
 in your team against your opponent’s headers. For example, if you have a 12 set piecer with ind. attacking set pieces
 at 10 vs 11 of your opponent, and you have just 1 header against 4, your chances to score is 4%. But if you increase
 your headers to 6 vs 4, your chances are 61%

 Info: Both events might lead to a yellow card for the offensive team, but if your player is already carded he will be
 extra careful so the risk of a second yellow is lower than it would otherwise have been.

 TODO: Experienced Forward Scores

 If your forward has great experience compared to the average experience of the opponents, and also high enough scoring
 to outwit the keeper, you might end up with a goal.

 Example: If your forward has 10 experience against an average of 8 and his scoring is 13 against a 15 goalkeeper, your
 chances are 33% to score. But if the experience was at 15, your chances increase to 38%.

 TODO: Inexperienced Defender

 If your defender has low experience compared to the average experience of the opposing team, an opposing player may be
 able to win the ball and then get a crack at scoring.

 Example: A defender with 10 experience and 15 defending against a forward with 13 scoring (against a 15 keeper) and an
 average experience of 12, will give the opponent 20% to score. But if you increase your defender experience to 12, you
 will drop this to 16%.

 TODO: Tired Defender Mistake

 During the second half only, if one of your defenders have far lower stamina than an opponent player, he can take the
 chance to pass you and go 1-1 with the goalkeeper.

 Example: If the opponent player is Forward with 13 scoring against your keeper with 15 goalkeeping, the chance to score
 is 41% (57% if same player is a Winger instead)

 TODO Resilient

 If they get injured, their injuries will be less severe than they would otherwise have been, and they heal faster. They
 will also have their skill drop kick in at a slightly higher age than other players. To reflect the wider scope of
 their vigor and durability, this specialty will be renamed “Resilient.”

 Overcrowding Penalty for Unique Players

 With the new specialties, we have 3 unique combinations of specialties and positions: Technical Defensive Forwards
 (TDF), Powerful Normal Forwards (PNF) and Powerful Defensive Inner Midfielders (PDIM). If you field two of the same
 type, you will suffer a penalty on their contribution (4% each). If you field three, the penalty will be bigger
 (7.5% each).

 TODO: Support Player

 We started giving birth to this specialty earlier last season. The effect is quite simple. If the Support Player
 Special Events is triggered, he has 20% boost in total to share with the nearby players. But there is a chance to drop
 your team’s organization instead. So be careful how you use this player.

 Define nearby players:
 Middle Forward -> 10% to left Forward & 10% to right forward
 Right/Left Forward -> 10% to Middle Forward & 10% to right/left Winger
 Middle IM -> 10% to Middle CD & 10% to middle FW
 Right/Left IM -> 10% to right/left Winger & 5% to right/left CD & 5% to right/left FW
 Winger-> 10% on left/right Forward & 10% on left/right Wingback
 Middle Defender -> 10% to left Defender & 10% to right Defender
 Right/Left Defender -> 10% to middle Defender & 10% to right/left Wingback
 Wingback -> 10% to left/right Defender & 10% to right/left Winger
 Goalkeeper -> 10% to middle Defender & 5% to left Defender & 5% to right Defender

 TODO: General Comments

    Form, stamina, experience, health, loyalty, mother club and weather events affect all the above formulas

 */

public class SpecialEventsPrediction {

    private double chanceCreationProbability = 0;
    private double goalProbability = 0;
    private ISpecialEventPredictionAnalyzer.SpecialEventType eventType;
    private IMatchRoleID responsiblePosition;
    private HashSet<IMatchRoleID> involvedPositions;
    private HashSet<IMatchRoleID> involvedOpponentPositions;

    public SpecialEventsPrediction(IMatchRoleID position, ISpecialEventPredictionAnalyzer.SpecialEventType type, double p) {
        responsiblePosition = position;
        eventType = type;
        chanceCreationProbability = p;
    }

    public SpecialEventsPrediction(SpecialEventsPrediction se) {
        this.chanceCreationProbability = se.chanceCreationProbability;
        this.eventType = se.eventType;
        this.goalProbability = se.goalProbability;
        if (se.involvedOpponentPositions != null) {
            this.involvedOpponentPositions = new HashSet<>(se.involvedOpponentPositions);
        }
        if (se.involvedPositions != null) {
            this.involvedPositions = new HashSet<>(se.involvedPositions);
        }
        this.responsiblePosition = se.responsiblePosition;
    }

    public IMatchRoleID getResponsiblePosition() {
        return responsiblePosition;
    }

    public void setResponsiblePosition(IMatchRoleID responsiblePosition) {
        this.responsiblePosition = responsiblePosition;
    }

    public HashSet<IMatchRoleID> getInvolvedPositions() {
        return involvedPositions;
    }

    public void setInvolvedPositions(HashSet<IMatchRoleID> m_cInvolvedPositions) {
        if (this.involvedPositions == null) {
            this.involvedPositions = new HashSet<IMatchRoleID>();

        } else {
            this.involvedPositions.clear();
        }

        this.involvedPositions.addAll(m_cInvolvedPositions);
    }

    public void setInvolvedPosition(MatchRoleID mid) {
        if (this.involvedPositions == null) {
            this.involvedPositions = new HashSet<IMatchRoleID>();
        } else {
            this.involvedPositions.clear();
        }
        this.involvedPositions.add(mid);
    }

    public void addInvolvedPosition(MatchRoleID mid) {
        if (this.involvedPositions == null) {
            this.involvedPositions = new HashSet<IMatchRoleID>();
        }
        this.involvedPositions.add(mid);
    }

    public HashSet<IMatchRoleID> getInvolvedOpponentPositions() {
        return involvedOpponentPositions;
    }

    public void setInvolvedOpponentPositions(HashSet<IMatchRoleID> m_cInvolvedPositions) {
        if (this.involvedOpponentPositions == null) {
            this.involvedOpponentPositions = new HashSet<IMatchRoleID>();

        } else {
            this.involvedOpponentPositions.clear();
        }

        this.involvedOpponentPositions.addAll(involvedOpponentPositions);
    }

    public void setInvolvedOpponentPosition(MatchRoleID mid) {
        if (this.involvedOpponentPositions == null) {
            this.involvedOpponentPositions = new HashSet<IMatchRoleID>();
        } else {
            this.involvedOpponentPositions.clear();
        }
        this.involvedOpponentPositions.add(mid);
    }

    public void addInvolvedOpponentPosition(MatchRoleID mid) {
        if (this.involvedOpponentPositions == null) {
            this.involvedOpponentPositions = new HashSet<IMatchRoleID>();
        }
        this.involvedOpponentPositions.add(mid);
    }

    static public SpecialEventsPrediction createIfInRange(IMatchRoleID position,
                                                          ISpecialEventPredictionAnalyzer.SpecialEventType eventName,
                                                          double maxProbability,
                                                          double valueAtMaxProbability,
                                                          double valueAtNullProbability,
                                                          double value) {
        if (valueAtMaxProbability > valueAtNullProbability) {
            if (value <= valueAtNullProbability) return null;
            if (value > valueAtMaxProbability)
                return new SpecialEventsPrediction(position, eventName, maxProbability);
        } else {
            if (value >= valueAtNullProbability) return null;
            if (value < valueAtMaxProbability)
                return new SpecialEventsPrediction(position, eventName, maxProbability);
        }
        double f = maxProbability - maxProbability / (valueAtNullProbability - valueAtMaxProbability) * (value - valueAtMaxProbability);  // linear fit
        return new SpecialEventsPrediction(position, eventName, f);
    }

    public void setChanceCreationProbability(double p) {
        this.chanceCreationProbability = p;
    }

    public double getChanceCreationProbability() {
        return chanceCreationProbability;
    }

    public double getGoalProbability() {
        return goalProbability;
    }

    public void setGoalProbability(double goalProbability) {
        this.goalProbability = goalProbability;
    }

    public String getEventTypeAsString() {
        return this.eventType.toString();
    }

    public ISpecialEventPredictionAnalyzer.SpecialEventType getEventType() {
        return this.eventType;
    }
}