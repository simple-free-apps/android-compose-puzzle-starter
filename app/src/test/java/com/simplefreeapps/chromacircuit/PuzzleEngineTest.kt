package com.simplefreeapps.chromacircuit

import com.simplefreeapps.chromacircuit.engine.Dir
import com.simplefreeapps.chromacircuit.engine.PuzzleEngine
import com.simplefreeapps.chromacircuit.engine.StarterLevels
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleEngineTest {

    @Test
    fun rotationMaskWrapsClockwiseAcrossAllFourTurns() {
        val m = Dir.NORTH or Dir.EAST
        assertEquals(Dir.EAST or Dir.SOUTH, Dir.rotateCw(m, 1))
        assertEquals(Dir.SOUTH or Dir.WEST, Dir.rotateCw(m, 2))
        assertEquals(Dir.WEST or Dir.NORTH, Dir.rotateCw(m, 3))
        assertEquals(m, Dir.rotateCw(m, 4))
    }

    @Test
    fun allFiveStarterLevelsStartUnsolvedAndSolveViaHintSolverWithinPar() {
        val levels = StarterLevels.all()
        assertEquals(5, levels.size)

        for (lvl in levels) {
            var cells = lvl.initialCells
            val startEval = PuzzleEngine.evaluate(lvl.size, cells)
            assertFalse("Level ${lvl.number} must not start solved", startEval.isSolved)
            assertTrue("Level ${lvl.number} par must be positive", lvl.parMoves > 0)

            var steps = 0
            val maxBudget = lvl.parMoves + 2
            while (!PuzzleEngine.evaluate(lvl.size, cells).isSolved && steps <= maxBudget) {
                val hint = PuzzleEngine.findNextHint(lvl.size, cells)
                assertNotNull("Level ${lvl.number} must provide a valid hint step $steps", hint)
                val (r, c) = hint!!
                val idx = r * lvl.size + c
                cells = cells.toMutableList().apply { this[idx] = this[idx].rotated() }
                steps++
            }
            assertTrue("Level ${lvl.number} must be solved cleanly", PuzzleEngine.evaluate(lvl.size, cells).isSolved)
            assertTrue("Level ${lvl.number} solved in $steps <= par ${lvl.parMoves}", steps <= lvl.parMoves)
        }
    }

    @Test
    fun starCalculationFollowsParThresholds() {
        assertEquals(3, PuzzleEngine.calculateStars(moves = 6, parMoves = 6))
        assertEquals(2, PuzzleEngine.calculateStars(moves = 9, parMoves = 6))
        assertEquals(1, PuzzleEngine.calculateStars(moves = 12, parMoves = 6))
    }
}
