package com.simplefreeapps.chromacircuit

import android.app.Activity
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.simplefreeapps.chromacircuit.ads.AdsController
import com.simplefreeapps.chromacircuit.audio.SoundEngine
import com.simplefreeapps.chromacircuit.data.ProgressStore
import com.simplefreeapps.chromacircuit.engine.GridCell
import com.simplefreeapps.chromacircuit.engine.LevelSpec
import com.simplefreeapps.chromacircuit.engine.PuzzleEngine
import com.simplefreeapps.chromacircuit.engine.StarterLevels
import com.simplefreeapps.chromacircuit.ui.CircuitBoardCanvas

private val DeepBgTop = Color(0xFF070C18)
private val DeepBgBot = Color(0xFF0F1930)
private val CardBg = Color(0xFF141E36)
private val AccentCyan = Color(0xFF38BDF8)
private val GoldStar = Color(0xFFFBBF24)
private val SoftText = Color(0xFFCBD5E1)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val store = ProgressStore(this)
        SoundEngine.enabled = store.isSoundEnabled()
        AdsController.init(this)

        val savedLang = store.getLanguageTag()
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (currentLocales.isEmpty || currentLocales.toLanguageTags() != savedLang) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(savedLang))
        }

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = AccentCyan,
                    secondary = GoldStar,
                    background = DeepBgTop,
                    surface = CardBg,
                    onBackground = Color.White,
                    onSurface = Color.White,
                )
            ) {
                StarterRoot(
                    store = store,
                    activity = this,
                )
            }
        }
    }
}

@Composable
private fun StarterRoot(store: ProgressStore, activity: Activity) {
    var activeLevelNum by remember { mutableStateOf<Int?>(null) }
    var soundOn by remember { mutableStateOf(store.isSoundEnabled()) }
    var refreshTick by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBgTop, DeepBgBot)))
            .safeDrawingPadding()
    ) {
        val currentLevel = activeLevelNum
        if (currentLevel == null) {
            HomeScreen(
                store = store,
                refreshTick = refreshTick,
                soundOn = soundOn,
                onToggleSound = {
                    soundOn = !soundOn
                    SoundEngine.enabled = soundOn
                    store.setSoundEnabled(soundOn)
                },
                onToggleLanguage = {
                    val next = if (store.getLanguageTag() == "ar") "en" else "ar"
                    store.setLanguageTag(next)
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(next))
                },
                onSelectLevel = { num -> activeLevelNum = num },
            )
        } else {
            PlayScreen(
                spec = StarterLevels.byNumber(currentLevel),
                activity = activity,
                soundOn = soundOn,
                onToggleSound = {
                    soundOn = !soundOn
                    SoundEngine.enabled = soundOn
                    store.setSoundEnabled(soundOn)
                },
                onWin = { stars, moves ->
                    store.recordWin(currentLevel, stars, moves)
                    refreshTick++
                },
                onNextLevel = {
                    if (currentLevel < 5) {
                        AdsController.showInterstitialIfReady(activity) {
                            activeLevelNum = currentLevel + 1
                        }
                    } else {
                        activeLevelNum = null
                    }
                },
                onBack = { activeLevelNum = null },
            )
        }
    }
}

@Composable
private fun HomeScreen(
    store: ProgressStore,
    refreshTick: Int,
    soundOn: Boolean,
    onToggleSound: () -> Unit,
    onToggleLanguage: () -> Unit,
    onSelectLevel: (Int) -> Unit,
) {
    val maxUnlocked = remember(refreshTick) { store.getMaxUnlockedLevel() }
    val levels = remember { StarterLevels.all() }
    val solvedCount = remember(refreshTick) {
        levels.count { store.getLevelProgress(it.number).bestStars > 0 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = AccentCyan.copy(alpha = 0.14f),
                shape = RoundedCornerShape(999.dp),
            ) {
                Text(
                    text = stringResource(R.string.starter_badge),
                    color = AccentCyan,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onToggleLanguage) {
                    Text(stringResource(R.string.lang_label), color = AccentCyan, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onToggleSound) {
                    Icon(
                        imageVector = if (soundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = stringResource(if (soundOn) R.string.cd_sound_on else R.string.cd_sound_off),
                        tint = Color.White,
                    )
                }
            }
        }

        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.app_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = SoftText,
            )
        }

        Button(
            onClick = { onSelectLevel(maxUnlocked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DeepBgTop),
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (maxUnlocked > 1) stringResource(R.string.play_continue, maxUnlocked)
                else stringResource(R.string.play_start),
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = stringResource(R.string.level_select_title, solvedCount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        levels.forEach { lvl ->
            val progress = store.getLevelProgress(lvl.number)
            val unlocked = lvl.number <= maxUnlocked
            LevelRowCard(
                spec = lvl,
                unlocked = unlocked,
                stars = progress.bestStars,
                bestMoves = progress.bestMoves,
                onClick = { if (unlocked) onSelectLevel(lvl.number) },
            )
        }

        Surface(
            color = CardBg,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF233554), RoundedCornerShape(14.dp)),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(
                        if (BuildConfig.ADS_ENABLED) R.string.flavor_badge_withads else R.string.flavor_badge_noads
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentCyan,
                )
                Text(
                    text = stringResource(R.string.audio_badge),
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText,
                )
            }
        }
    }
}

@Composable
private fun LevelRowCard(
    spec: LevelSpec,
    unlocked: Boolean,
    stars: Int,
    bestMoves: Int?,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = unlocked, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = if (unlocked) CardBg else Color(0xFF0E1525)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (stars > 0) GoldStar.copy(alpha = 0.16f) else AccentCyan.copy(alpha = 0.12f),
                            RoundedCornerShape(12.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!unlocked) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF64748B))
                    } else if (stars > 0) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = GoldStar)
                    } else {
                        Text(
                            text = "${spec.number}",
                            color = AccentCyan,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${stringResource(R.string.level_card_title, spec.number)} · ${stringResource(spec.nameRes)}",
                        fontWeight = FontWeight.Bold,
                        color = if (unlocked) Color.White else Color(0xFF64748B),
                    )
                    Text(
                        text = if (!unlocked) stringResource(R.string.level_locked)
                        else if (bestMoves != null) "${stringResource(R.string.level_par, spec.parMoves)} · ${stringResource(R.string.level_best, bestMoves)}"
                        else stringResource(R.string.level_par, spec.parMoves),
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftText,
                    )
                }
            }
            Row {
                repeat(3) { idx ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (idx < stars) GoldStar else Color(0xFF334155),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayScreen(
    spec: LevelSpec,
    activity: Activity,
    soundOn: Boolean,
    onToggleSound: () -> Unit,
    onWin: (stars: Int, moves: Int) -> Unit,
    onNextLevel: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var cells by remember(spec.number) { mutableStateOf(spec.initialCells) }
    val history = remember(spec.number) { mutableStateListOf<List<GridCell>>() }
    var moves by remember(spec.number) { mutableIntStateOf(0) }
    var hintCoord by remember(spec.number) { mutableStateOf<Pair<Int, Int>?>(null) }
    var winRecorded by remember(spec.number) { mutableStateOf(false) }

    val evaluation = remember(cells, spec.size) { PuzzleEngine.evaluate(spec.size, cells) }

    LaunchedEffect(evaluation.isSolved, spec.number) {
        if (evaluation.isSolved && !winRecorded) {
            winRecorded = true
            SoundEngine.playVictory()
            onWin(PuzzleEngine.calculateStars(moves, spec.parMoves), moves)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                        tint = Color.White,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.hud_level, spec.number),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = stringResource(spec.nameRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentCyan,
                    )
                }
                IconButton(onClick = onToggleSound) {
                    Icon(
                        imageVector = if (soundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = stringResource(if (soundOn) R.string.cd_sound_on else R.string.cd_sound_off),
                        tint = Color.White,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatChip(stringResource(R.string.hud_moves, moves))
                StatChip(stringResource(R.string.hud_par, spec.parMoves))
            }

            Text(
                text = stringResource(R.string.board_instruction),
                style = MaterialTheme.typography.bodySmall,
                color = SoftText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        CircuitBoardCanvas(
            size = spec.size,
            cells = cells,
            poweredCoords = evaluation.poweredCoords,
            hintCoord = hintCoord,
            onCellTap = { row, col ->
                if (evaluation.isSolved) return@CircuitBoardCanvas
                val idx = row * spec.size + col
                val target = cells[idx]
                if (!target.isRotatable) return@CircuitBoardCanvas

                history.add(cells)
                val updated = cells.toMutableList().apply { this[idx] = target.rotated() }
                val newEval = PuzzleEngine.evaluate(spec.size, updated)
                cells = updated
                moves++
                hintCoord = null

                if (!newEval.isSolved) {
                    if (newEval.poweredCoords.size > evaluation.poweredCoords.size) {
                        SoundEngine.playConnect()
                    } else {
                        SoundEngine.playRotate()
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        if (history.isNotEmpty() && !evaluation.isSolved) {
                            cells = history.removeAt(history.lastIndex)
                            moves = (moves - 1).coerceAtLeast(0)
                            hintCoord = null
                            SoundEngine.playUndo()
                        }
                    },
                    enabled = history.isNotEmpty() && !evaluation.isSolved,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.action_undo), maxLines = 1, softWrap = false)
                }

                OutlinedButton(
                    onClick = {
                        cells = spec.initialCells
                        history.clear()
                        moves = 0
                        hintCoord = null
                        winRecorded = false
                        SoundEngine.playUndo()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.action_reset), maxLines = 1, softWrap = false)
                }

                Button(
                    onClick = {
                        AdsController.showRewardedHint(activity) {
                            hintCoord = PuzzleEngine.findNextHint(spec.size, cells)
                            SoundEngine.playConnect()
                        }
                    },
                    enabled = !evaluation.isSolved,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldStar, contentColor = DeepBgTop),
                ) {
                    Icon(Icons.Filled.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.action_hint), fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                }
            }

            AdsController.BannerSlot(context = context)
        }
    }

    if (evaluation.isSolved) {
        val stars = PuzzleEngine.calculateStars(moves, spec.parMoves)
        AlertDialog(
            onDismissRequest = {},
            containerColor = CardBg,
            title = {
                Text(stringResource(R.string.victory_title), fontWeight = FontWeight.Bold, color = GoldStar)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.victory_stars, stars), color = Color.White, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.victory_summary, moves, spec.parMoves), color = SoftText)
                    if (spec.number == 5) {
                        Text(stringResource(R.string.victory_all_done), color = AccentCyan)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = DeepBgTop),
                ) {
                    Text(
                        stringResource(if (spec.number < 5) R.string.victory_next else R.string.victory_home),
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        cells = spec.initialCells
                        history.clear()
                        moves = 0
                        hintCoord = null
                        winRecorded = false
                    }
                ) {
                    Text(stringResource(R.string.victory_replay), color = SoftText)
                }
            },
        )
    }
}

@Composable
private fun StatChip(label: String) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.border(1.dp, Color(0xFF233554), RoundedCornerShape(12.dp)),
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}
