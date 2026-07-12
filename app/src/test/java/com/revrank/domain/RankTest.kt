package com.revrank.domain

import com.revrank.domain.model.Rank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RankTest {

    @Test
    fun `fromXp returns Learner at zero`() {
        assertEquals(Rank.LEARNER, Rank.fromXp(0))
    }

    @Test
    fun `fromXp respects tier boundaries`() {
        assertEquals(Rank.LEARNER, Rank.fromXp(499))
        assertEquals(Rank.CRUISER, Rank.fromXp(500))
        assertEquals(Rank.CRUISER, Rank.fromXp(1499))
        assertEquals(Rank.ROAD_CAPTAIN, Rank.fromXp(1500))
        assertEquals(Rank.LEGEND, Rank.fromXp(30000))
        assertEquals(Rank.LEGEND, Rank.fromXp(999999))
    }

    @Test
    fun `progressToNext is zero at tier start and near one before next tier`() {
        // Exactly at Cruiser start (500) → 0% toward Road Captain (1500)
        assertEquals(0f, Rank.progressToNext(500), 0.001f)
        // Halfway between 500 and 1500 = 1000 → 50%
        assertEquals(0.5f, Rank.progressToNext(1000), 0.001f)
    }

    @Test
    fun `progressToNext caps at one for max rank`() {
        assertEquals(1f, Rank.progressToNext(30000), 0.001f)
        assertEquals(1f, Rank.progressToNext(50000), 0.001f)
    }

    @Test
    fun `ranks are ordered by xp requirement`() {
        val reqs = Rank.entries.map { it.xpRequired }
        assertTrue(reqs == reqs.sorted())
    }
}
