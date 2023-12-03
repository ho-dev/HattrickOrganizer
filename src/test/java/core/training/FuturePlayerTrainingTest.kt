package core.training

import core.util.HODateTime
import core.util.HODateTime.HTWeek
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FuturePlayerTrainingTest {

    @Test
    fun testFuturePlayerTrainingTestCut() {
        var futurePlayerTraining = FuturePlayerTraining(
            4711,
            FuturePlayerTraining.Priority.NO_TRAINING,
            HODateTime.fromHTWeek(HTWeek(86, 1)),
            null
        )

        var remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(86, 4)), HODateTime.fromHTWeek(HTWeek(86, 5)))
        Assertions.assertEquals(2, remaining.size)
        var f = remaining.get(0)
        Assertions.assertEquals(4711, f.playerId)
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.priority)
        Assertions.assertEquals(86, f.fromSeason)
        Assertions.assertEquals(1, f.fromWeek)
        Assertions.assertEquals(86, f.toSeason)
        Assertions.assertEquals(3, f.toWeek)
        f = remaining.get(1)
        Assertions.assertEquals(4711, f.playerId)
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.priority)
        Assertions.assertEquals(86, f.fromSeason)
        Assertions.assertEquals(5, f.fromWeek)
        Assertions.assertNull( f.toSeason)
        Assertions.assertNull( f.toWeek)

        remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(86, 4)), null)
        Assertions.assertEquals(1, remaining.size)
        f = remaining.get(0)
        Assertions.assertEquals(4711, f.playerId)
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.priority)
        Assertions.assertEquals(86, f.fromSeason)
        Assertions.assertEquals(1, f.fromWeek)
        Assertions.assertEquals(86, f.toSeason)
        Assertions.assertEquals(3, f.toWeek)

        remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(85, 4)), HODateTime.fromHTWeek(HTWeek(86, 4)))
        Assertions.assertEquals(1, remaining.size)
        f = remaining.get(0)
        Assertions.assertEquals(4711, f.playerId)
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.priority)
        Assertions.assertEquals(86, f.fromSeason)
        Assertions.assertEquals(4, f.fromWeek)
        Assertions.assertNull( f.toSeason)
        Assertions.assertNull( f.toWeek)

        remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(85, 4)), null)
        Assertions.assertEquals(0, remaining.size)

        futurePlayerTraining = FuturePlayerTraining(
            4711,
            FuturePlayerTraining.Priority.NO_TRAINING,
            HODateTime.fromHTWeek(HTWeek(86, 1)),
            HODateTime.fromHTWeek(HTWeek(86, 3)),
        )

        remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(86, 5)), HODateTime.fromHTWeek(HTWeek(86, 5)))
        Assertions.assertEquals(1, remaining.size)
        f = remaining.get(0)
        Assertions.assertEquals(4711, f.playerId)
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.priority)
        Assertions.assertEquals(86, f.fromSeason)
        Assertions.assertEquals(1, f.fromWeek)
        Assertions.assertEquals(86, f.toSeason)
        Assertions.assertEquals(3, f.toWeek)

        remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(86, 5)), null)
        Assertions.assertEquals(1, remaining.size)
        f = remaining.get(0)
        Assertions.assertEquals(4711, f.playerId)
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.priority)
        Assertions.assertEquals(86, f.fromSeason)
        Assertions.assertEquals(1, f.fromWeek)
        Assertions.assertEquals(86, f.toSeason)
        Assertions.assertEquals(3, f.toWeek)

        remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(86, 3)), null)
        Assertions.assertEquals(1, remaining.size)
        f = remaining.get(0)
        Assertions.assertEquals(4711, f.playerId)
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.priority)
        Assertions.assertEquals(86, f.fromSeason)
        Assertions.assertEquals(1, f.fromWeek)
        Assertions.assertEquals(86, f.toSeason)
        Assertions.assertEquals(2, f.toWeek)

        remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(85, 3)), HODateTime.fromHTWeek(HTWeek(86, 2)))
        Assertions.assertEquals(1, remaining.size)
        f = remaining.get(0)
        Assertions.assertEquals(4711, f.playerId)
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.priority)
        Assertions.assertEquals(86, f.fromSeason)
        Assertions.assertEquals(2, f.fromWeek)
        Assertions.assertEquals(86, f.toSeason)
        Assertions.assertEquals(3, f.toWeek)

        remaining =
            futurePlayerTraining.cut(HODateTime.fromHTWeek(HTWeek(85, 3)), HODateTime.fromHTWeek(HTWeek(86, 3)))
        Assertions.assertEquals(0, remaining.size)

    }

}