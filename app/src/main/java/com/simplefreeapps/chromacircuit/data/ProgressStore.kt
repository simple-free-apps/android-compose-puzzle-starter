package com.simplefreeapps.chromacircuit.data

import android.content.Context

data class LevelProgress(
    val bestStars: Int,
    val bestMoves: Int?,
)

class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("chroma_circuit_prefs", Context.MODE_PRIVATE)

    fun isSoundEnabled(): Boolean = prefs.getBoolean("sound_enabled", true)

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }

    fun getLanguageTag(): String = prefs.getString("lang_tag", "en") ?: "en"

    fun setLanguageTag(tag: String) {
        prefs.edit().putString("lang_tag", tag).apply()
    }

    fun getMaxUnlockedLevel(): Int = prefs.getInt("max_unlocked", 1).coerceIn(1, 5)

    fun getLevelProgress(level: Int): LevelProgress {
        val stars = prefs.getInt("stars_$level", 0)
        val moves = prefs.getInt("best_moves_$level", -1).takeIf { it > 0 }
        return LevelProgress(stars, moves)
    }

    fun recordWin(level: Int, stars: Int, moves: Int) {
        val prev = getLevelProgress(level)
        val editor = prefs.edit()
        if (stars > prev.bestStars) editor.putInt("stars_$level", stars)
        if (prev.bestMoves == null || moves < prev.bestMoves) editor.putInt("best_moves_$level", moves)
        val nextMax = maxOf(getMaxUnlockedLevel(), (level + 1).coerceAtMost(5))
        editor.putInt("max_unlocked", nextMax)
        editor.apply()
    }
}
