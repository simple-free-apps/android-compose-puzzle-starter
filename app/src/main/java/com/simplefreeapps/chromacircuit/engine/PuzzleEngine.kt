package com.simplefreeapps.chromacircuit.engine

import androidx.annotation.StringRes

/** Bitmask constants for conduit ports on each cell. */
object Dir {
    const val NONE = 0
    const val NORTH = 1
    const val EAST = 2
    const val SOUTH = 4
    const val WEST = 8
    const val ALL = NORTH or EAST or SOUTH or WEST

    fun rotateCw(mask: Int, steps: Int = 1): Int {
        var m = mask and 0xF
        repeat(((steps % 4) + 4) % 4) {
            m = ((m shl 1) and 0xF) or (m shr 3)
        }
        return m
    }

    fun opposite(dir: Int): Int = when (dir) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        EAST -> WEST
        WEST -> EAST
        else -> NONE
    }
}

enum class CellKind {
    EMPTY,
    SOURCE,
    TARGET,
    PIPE
}

data class GridCell(
    val row: Int,
    val col: Int,
    val kind: CellKind,
    /** Base unrotated connector mask (for PIPE) or fixed mask (for SOURCE/TARGET). */
    val baseMask: Int,
    /** Current clockwise quarter-turn steps (0..3). */
    val rotation: Int = 0,
    /** Canonical solved clockwise steps (0..3) used for Par and Hints. */
    val targetRotation: Int = 0,
) {
    val activeMask: Int
        get() = if (kind == CellKind.PIPE) Dir.rotateCw(baseMask, rotation) else baseMask

    val isRotatable: Boolean
        get() = kind == CellKind.PIPE && baseMask != Dir.ALL && baseMask != Dir.NONE

    fun rotated(): GridCell =
        if (isRotatable) copy(rotation = (rotation + 1) % 4) else this

    fun stepsNeededToMatchTarget(): Int {
        if (!isRotatable) return 0
        val targetMask = Dir.rotateCw(baseMask, targetRotation)
        for (s in 0..3) {
            if (Dir.rotateCw(baseMask, rotation + s) == targetMask) return s
        }
        return 0
    }
}

data class LevelSpec(
    val number: Int,
    @StringRes val nameRes: Int,
    val size: Int,
    val initialCells: List<GridCell>,
) {
    val parMoves: Int
        get() = initialCells.sumOf { it.stepsNeededToMatchTarget() }.coerceAtLeast(1)
}

data class BoardEvaluation(
    val poweredCoords: Set<Pair<Int, Int>>,
    val totalTargets: Int,
    val poweredTargets: Int,
    val isSolved: Boolean,
)

object PuzzleEngine {

    /** Computes live powered cells using BFS across matching neighbor ports. */
    fun evaluate(size: Int, cells: List<GridCell>): BoardEvaluation {
        val byCoord = cells.associateBy { it.row to it.col }
        val powered = HashSet<Pair<Int, Int>>()
        val queue = ArrayDeque<GridCell>()

        for (cell in cells) {
            if (cell.kind == CellKind.SOURCE) {
                val coord = cell.row to cell.col
                powered.add(coord)
                queue.addLast(cell)
            }
        }

        val deltas = listOf(
            Triple(-1, 0, Dir.NORTH),
            Triple(0, 1, Dir.EAST),
            Triple(1, 0, Dir.SOUTH),
            Triple(0, -1, Dir.WEST),
        )

        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            val curMask = cur.activeMask
            for ((dr, dc, dir) in deltas) {
                if ((curMask and dir) == 0) continue
                val nr = cur.row + dr
                val nc = cur.col + dc
                if (nr !in 0 until size || nc !in 0 until size) continue
                val neighbor = byCoord[nr to nc] ?: continue
                if (neighbor.kind == CellKind.EMPTY) continue
                if ((neighbor.activeMask and Dir.opposite(dir)) == 0) continue
                val nCoord = nr to nc
                if (powered.add(nCoord)) {
                    queue.addLast(neighbor)
                }
            }
        }

        val targets = cells.filter { it.kind == CellKind.TARGET }
        val litTargets = targets.count { (it.row to it.col) in powered }
        return BoardEvaluation(
            poweredCoords = powered,
            totalTargets = targets.size,
            poweredTargets = litTargets,
            isSolved = targets.isNotEmpty() && litTargets == targets.size,
        )
    }

    /** Returns the next tile coordinate to rotate for a verified hint. */
    fun findNextHint(size: Int, cells: List<GridCell>): Pair<Int, Int>? {
        val eval = evaluate(size, cells)
        if (eval.isSolved) return null

        // 1. Check if a single 90-degree tap immediately solves the level.
        for (i in cells.indices) {
            val cell = cells[i]
            if (!cell.isRotatable) continue
            val next = cells.toMutableList().apply { this[i] = cell.rotated() }
            if (evaluate(size, next).isSolved) return cell.row to cell.col
        }

        // 2. Otherwise pick the first rotatable conduit still differing from canonical alignment,
        // prioritizing tiles adjacent to currently powered conduits so the hint feels natural.
        val candidates = cells.filter { it.stepsNeededToMatchTarget() > 0 }
        if (candidates.isEmpty()) return null

        val poweredNeighbors = candidates.firstOrNull { c ->
            listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1).any { (dr, dc) ->
                (c.row + dr to c.col + dc) in eval.poweredCoords
            }
        }
        val chosen = poweredNeighbors ?: candidates.first()
        return chosen.row to chosen.col
    }

    fun calculateStars(moves: Int, parMoves: Int): Int = when {
        moves <= parMoves -> 3
        moves <= parMoves + 3 -> 2
        else -> 1
    }
}
