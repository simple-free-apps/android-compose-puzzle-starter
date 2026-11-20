package com.simplefreeapps.chromacircuit.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.simplefreeapps.chromacircuit.engine.CellKind
import com.simplefreeapps.chromacircuit.engine.Dir
import com.simplefreeapps.chromacircuit.engine.GridCell
import kotlin.math.min

private val BoardBg = Color(0xFF0A1120)
private val TileBase = Color(0xFF131E36)
private val TilePowered = Color(0xFF172B47)
private val TileEmpty = Color(0xFF0D1526)
private val TileBorder = Color(0xFF233554)
private val ConduitDormant = Color(0xFF475569)
private val ConduitCyan = Color(0xFF38BDF8)
private val ConduitCore = Color(0xFFE0F2FE)
private val AmberGold = Color(0xFFFBBF24)
private val AmberGlow = Color(0xFFF59E0B)

@Composable
fun CircuitBoardCanvas(
    size: Int,
    cells: List<GridCell>,
    poweredCoords: Set<Pair<Int, Int>>,
    hintCoord: Pair<Int, Int>?,
    onCellTap: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pulseTransition = rememberInfiniteTransition(label = "circuit_pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 950),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_alpha",
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(size) {
                detectTapGestures { offset ->
                    val boardSide = min(this.size.width, this.size.height).toFloat()
                    val cellSide = boardSide / size
                    val col = (offset.x / cellSide).toInt().coerceIn(0, size - 1)
                    val row = (offset.y / cellSide).toInt().coerceIn(0, size - 1)
                    onCellTap(row, col)
                }
            },
    ) {
        val boardSide = min(this.size.width, this.size.height)
        val cellSide = boardSide / size
        val pad = cellSide * 0.08f
        val tileSide = cellSide - pad * 2f

        // Board outer frame
        drawRoundRect(
            color = BoardBg,
            topLeft = Offset.Zero,
            size = Size(boardSide, boardSide),
            cornerRadius = CornerRadius(24f, 24f),
        )
        drawRoundRect(
            color = TileBorder,
            topLeft = Offset.Zero,
            size = Size(boardSide, boardSide),
            cornerRadius = CornerRadius(24f, 24f),
            style = Stroke(width = 3f),
        )

        for (cell in cells) {
            val left = cell.col * cellSide + pad
            val top = cell.row * cellSide + pad
            val center = Offset(left + tileSide / 2f, top + tileSide / 2f)
            val isPowered = (cell.row to cell.col) in poweredCoords
            val isHinted = hintCoord == (cell.row to cell.col)

            val bg = when {
                cell.kind == CellKind.EMPTY -> TileEmpty
                isPowered -> TilePowered
                else -> TileBase
            }

            drawRoundRect(
                color = bg,
                topLeft = Offset(left, top),
                size = Size(tileSide, tileSide),
                cornerRadius = CornerRadius(tileSide * 0.16f, tileSide * 0.16f),
            )

            if (cell.kind != CellKind.EMPTY) {
                val borderCol = when {
                    isHinted -> AmberGold.copy(alpha = pulse)
                    isPowered -> ConduitCyan.copy(alpha = 0.65f)
                    else -> TileBorder
                }
                drawRoundRect(
                    color = borderCol,
                    topLeft = Offset(left, top),
                    size = Size(tileSide, tileSide),
                    cornerRadius = CornerRadius(tileSide * 0.16f, tileSide * 0.16f),
                    style = Stroke(width = if (isHinted) 5f else 2f),
                )
            }

            drawConduitArms(
                cell = cell,
                center = center,
                halfSpan = cellSide / 2f - pad * 0.25f,
                tileSide = tileSide,
                isPowered = isPowered,
                pulse = pulse,
            )
        }
    }
}

private fun DrawScope.drawConduitArms(
    cell: GridCell,
    center: Offset,
    halfSpan: Float,
    tileSide: Float,
    isPowered: Boolean,
    pulse: Float,
) {
    if (cell.kind == CellKind.EMPTY) return
    val mask = cell.activeMask
    val outerWidth = tileSide * 0.20f
    val innerWidth = tileSide * 0.08f
    val baseColor = if (isPowered) ConduitCyan else ConduitDormant

    val armOffsets = listOf(
        Dir.NORTH to Offset(center.x, center.y - tileSide / 2f),
        Dir.EAST to Offset(center.x + tileSide / 2f, center.y),
        Dir.SOUTH to Offset(center.x, center.y + tileSide / 2f),
        Dir.WEST to Offset(center.x - tileSide / 2f, center.y),
    )

    for ((dir, endPt) in armOffsets) {
        if ((mask and dir) == 0) continue
        if (isPowered) {
            drawLine(
                color = ConduitCyan.copy(alpha = 0.32f * pulse),
                start = center,
                end = endPt,
                strokeWidth = outerWidth * 1.45f,
                cap = StrokeCap.Butt,
            )
        }
        drawLine(
            color = baseColor,
            start = center,
            end = endPt,
            strokeWidth = outerWidth,
            cap = StrokeCap.Butt,
        )
        if (isPowered) {
            drawLine(
                color = ConduitCore,
                start = center,
                end = endPt,
                strokeWidth = innerWidth,
                cap = StrokeCap.Butt,
            )
        }
    }
    if (mask != Dir.NONE) {
        drawCircle(
            color = baseColor,
            radius = outerWidth / 2f,
            center = center,
        )
        if (isPowered) {
            drawCircle(
                color = ConduitCore,
                radius = innerWidth / 2f,
                center = center,
            )
        }
    }

    when (cell.kind) {
        CellKind.SOURCE -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ConduitCore, ConduitCyan, Color.Transparent),
                    center = center,
                    radius = tileSide * 0.42f,
                ),
                radius = tileSide * 0.42f,
                center = center,
            )
            drawCircle(color = ConduitCore, radius = tileSide * 0.16f, center = center)
        }
        CellKind.TARGET -> {
            val beaconColor = if (isPowered) AmberGold else Color(0xFF94A3B8)
            if (isPowered) {
                drawCircle(
                    color = AmberGlow.copy(alpha = 0.35f * pulse),
                    radius = tileSide * 0.38f,
                    center = center,
                )
            }
            drawCircle(color = beaconColor, radius = tileSide * 0.22f, center = center)
            drawCircle(
                color = if (isPowered) Color.White else TileBase,
                radius = tileSide * 0.10f,
                center = center,
            )
        }
        CellKind.PIPE -> {
            drawCircle(
                color = if (isPowered) ConduitCore else ConduitDormant,
                radius = tileSide * 0.10f,
                center = center,
            )
        }
        CellKind.EMPTY -> Unit
    }
}
