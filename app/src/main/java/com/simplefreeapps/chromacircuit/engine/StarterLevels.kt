package com.simplefreeapps.chromacircuit.engine

import com.simplefreeapps.chromacircuit.R

/**
 * Curated 5-level Starter campaign.
 * Full commercial expansions live separately in the paid Puzzle Pack 5-in-1 bundle.
 */
object StarterLevels {

    private fun emptyGrid(size: Int): MutableList<GridCell> =
        MutableList(size * size) { idx ->
            GridCell(row = idx / size, col = idx % size, kind = CellKind.EMPTY, baseMask = Dir.NONE)
        }

    private fun MutableList<GridCell>.setCell(
        size: Int,
        row: Int,
        col: Int,
        kind: CellKind,
        solvedMask: Int,
        scrambleClicks: Int = 0,
    ) {
        // Compute targetRotation = 0 at solvedMask, and back-rotate by (4 - scramble)
        // so rotating forward by `scrambleClicks` restores `solvedMask`.
        val normalizedScramble = ((scrambleClicks % 4) + 4) % 4
        val initRot = if (kind == CellKind.PIPE) (4 - normalizedScramble) % 4 else 0
        this[row * size + col] = GridCell(
            row = row,
            col = col,
            kind = kind,
            baseMask = solvedMask,
            rotation = initRot,
            targetRotation = 0,
        )
    }

    private val N = Dir.NORTH
    private val E = Dir.EAST
    private val S = Dir.SOUTH
    private val W = Dir.WEST

    fun all(): List<LevelSpec> = listOf(
        level1(),
        level2(),
        level3(),
        level4(),
        level5(),
    )

    fun byNumber(num: Int): LevelSpec = all().first { it.number == num }

    private fun level1(): LevelSpec {
        val sz = 3
        val g = emptyGrid(sz)
        g.setCell(sz, 0, 0, CellKind.SOURCE, E)
        g.setCell(sz, 0, 1, CellKind.PIPE, E or W, scrambleClicks = 1)
        g.setCell(sz, 0, 2, CellKind.PIPE, S or W, scrambleClicks = 2)
        g.setCell(sz, 1, 2, CellKind.PIPE, N or S, scrambleClicks = 1)
        g.setCell(sz, 2, 2, CellKind.TARGET, N)
        return LevelSpec(1, R.string.level_1_name, sz, g)
    }

    private fun level2(): LevelSpec {
        val sz = 4
        val g = emptyGrid(sz)
        g.setCell(sz, 1, 0, CellKind.SOURCE, E)
        g.setCell(sz, 1, 1, CellKind.PIPE, N or S or W, scrambleClicks = 2)
        // Upper branch -> (0,3)
        g.setCell(sz, 0, 1, CellKind.PIPE, E or S, scrambleClicks = 1)
        g.setCell(sz, 0, 2, CellKind.PIPE, E or W, scrambleClicks = 1)
        g.setCell(sz, 0, 3, CellKind.TARGET, W)
        // Lower branch -> (3,3)
        g.setCell(sz, 2, 1, CellKind.PIPE, N or E, scrambleClicks = 3)
        g.setCell(sz, 2, 2, CellKind.PIPE, S or W, scrambleClicks = 1)
        g.setCell(sz, 3, 2, CellKind.PIPE, N or E, scrambleClicks = 2)
        g.setCell(sz, 3, 3, CellKind.TARGET, W)
        return LevelSpec(2, R.string.level_2_name, sz, g)
    }

    private fun level3(): LevelSpec {
        val sz = 4
        val g = emptyGrid(sz)
        g.setCell(sz, 0, 0, CellKind.SOURCE, S)
        g.setCell(sz, 1, 0, CellKind.PIPE, N or E, scrambleClicks = 1)
        g.setCell(sz, 1, 1, CellKind.PIPE, N or E or W, scrambleClicks = 2)
        g.setCell(sz, 0, 1, CellKind.TARGET, S)
        g.setCell(sz, 1, 2, CellKind.PIPE, E or W, scrambleClicks = 1)
        g.setCell(sz, 1, 3, CellKind.PIPE, S or W, scrambleClicks = 3)
        g.setCell(sz, 2, 3, CellKind.PIPE, N or S or W, scrambleClicks = 1)
        g.setCell(sz, 2, 2, CellKind.TARGET, E)
        g.setCell(sz, 3, 3, CellKind.PIPE, N or W, scrambleClicks = 2)
        g.setCell(sz, 3, 2, CellKind.PIPE, E or W, scrambleClicks = 1)
        g.setCell(sz, 3, 1, CellKind.TARGET, E)
        return LevelSpec(3, R.string.level_3_name, sz, g)
    }

    private fun level4(): LevelSpec {
        val sz = 5
        val g = emptyGrid(sz)
        g.setCell(sz, 2, 0, CellKind.SOURCE, E)
        g.setCell(sz, 2, 1, CellKind.PIPE, W or N or S, scrambleClicks = 2)
        // North branch
        g.setCell(sz, 1, 1, CellKind.PIPE, S or N, scrambleClicks = 1)
        g.setCell(sz, 0, 1, CellKind.PIPE, S or E, scrambleClicks = 3)
        g.setCell(sz, 0, 2, CellKind.PIPE, W or E, scrambleClicks = 1)
        g.setCell(sz, 0, 3, CellKind.PIPE, W or S, scrambleClicks = 2)
        g.setCell(sz, 1, 3, CellKind.TARGET, N)
        // South branch splitting to (4,2) & (2,4)
        g.setCell(sz, 3, 1, CellKind.PIPE, N or E, scrambleClicks = 1)
        g.setCell(sz, 3, 2, CellKind.PIPE, W or E or S, scrambleClicks = 2)
        g.setCell(sz, 4, 2, CellKind.TARGET, N)
        g.setCell(sz, 3, 3, CellKind.PIPE, W or N or E, scrambleClicks = 1)
        g.setCell(sz, 3, 4, CellKind.PIPE, W or N, scrambleClicks = 2)
        g.setCell(sz, 2, 4, CellKind.TARGET, S)
        return LevelSpec(4, R.string.level_4_name, sz, g)
    }

    private fun level5(): LevelSpec {
        val sz = 5
        val g = emptyGrid(sz)
        g.setCell(sz, 2, 2, CellKind.SOURCE, Dir.ALL)
        // North -> (0,0) and (0,4)
        g.setCell(sz, 1, 2, CellKind.PIPE, S or N, scrambleClicks = 1)
        g.setCell(sz, 0, 2, CellKind.PIPE, S or W or E, scrambleClicks = 2)
        g.setCell(sz, 0, 1, CellKind.PIPE, E or W, scrambleClicks = 1)
        g.setCell(sz, 0, 0, CellKind.TARGET, E)
        g.setCell(sz, 0, 3, CellKind.PIPE, W or E, scrambleClicks = 1)
        g.setCell(sz, 0, 4, CellKind.TARGET, W)
        // West -> (2,0) -> (4,0)
        g.setCell(sz, 2, 1, CellKind.PIPE, E or W, scrambleClicks = 1)
        g.setCell(sz, 2, 0, CellKind.PIPE, E or S, scrambleClicks = 3)
        g.setCell(sz, 3, 0, CellKind.PIPE, N or S, scrambleClicks = 1)
        g.setCell(sz, 4, 0, CellKind.TARGET, N)
        // East -> (2,3) -> (2,4) -> (4,4)
        g.setCell(sz, 2, 3, CellKind.PIPE, W or E, scrambleClicks = 1)
        g.setCell(sz, 2, 4, CellKind.PIPE, W or S, scrambleClicks = 2)
        g.setCell(sz, 3, 4, CellKind.PIPE, N or S, scrambleClicks = 1)
        g.setCell(sz, 4, 4, CellKind.TARGET, N)
        // South path to (4,2) -> (4,1)
        g.setCell(sz, 3, 2, CellKind.PIPE, N or S, scrambleClicks = 1)
        g.setCell(sz, 4, 2, CellKind.PIPE, N or W, scrambleClicks = 1)
        g.setCell(sz, 4, 1, CellKind.TARGET, E)
        return LevelSpec(5, R.string.level_5_name, sz, g)
    }
}
