package com.simplefreeapps.chromacircuit.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Pure Kotlin procedural PCM AudioTrack synthesizer.
 * Zero bundled audio files (.ogg/.mp3/.wav) — completely license-free for public open starter distribution.
 */
object SoundEngine {
    private const val SAMPLE_RATE = 44100

    @Volatile
    var enabled: Boolean = true

    private val rotateBuffer: ShortArray by lazy {
        synthesizeSweep(durationMs = 55, startHz = 620.0, endHz = 960.0, gain = 0.28)
    }

    private val connectBuffer: ShortArray by lazy {
        synthesizeChord(durationMs = 110, freqs = doubleArrayOf(880.0, 1318.5), gain = 0.22)
    }

    private val undoBuffer: ShortArray by lazy {
        synthesizeSweep(durationMs = 60, startHz = 520.0, endHz = 340.0, gain = 0.22)
    }

    private val victoryBuffer: ShortArray by lazy {
        synthesizeArpeggio(
            freqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50),
            noteMs = 95,
            gain = 0.26,
        )
    }

    fun playRotate() = playAsync(rotateBuffer)
    fun playConnect() = playAsync(connectBuffer)
    fun playUndo() = playAsync(undoBuffer)
    fun playVictory() = playAsync(victoryBuffer)

    private fun playAsync(pcm: ShortArray) {
        if (!enabled || pcm.isEmpty()) return
        thread(isDaemon = true, name = "pcm-sfx") {
            runCatching {
                val byteCount = pcm.size * 2
                val minBuf = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                ).coerceAtLeast(byteCount)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(SAMPLE_RATE)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()
                val sleepMs = (pcm.size * 1000L / SAMPLE_RATE) + 30L
                Thread.sleep(sleepMs)
                track.stop()
                track.release()
            }
        }
    }

    private fun synthesizeSweep(durationMs: Int, startHz: Double, endHz: Double, gain: Double): ShortArray {
        val count = (SAMPLE_RATE * durationMs) / 1000
        return ShortArray(count) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / count
            val freq = startHz + (endHz - startHz) * progress
            val env = sin(PI * progress).coerceAtLeast(0.0) * exp(-2.2 * progress)
            val sample = sin(2.0 * PI * freq * t) * env * gain * Short.MAX_VALUE
            sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    private fun synthesizeChord(durationMs: Int, freqs: DoubleArray, gain: Double): ShortArray {
        val count = (SAMPLE_RATE * durationMs) / 1000
        return ShortArray(count) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / count
            val env = sin(PI * progress).coerceAtLeast(0.0) * exp(-1.8 * progress)
            var sum = 0.0
            for (f in freqs) {
                sum += sin(2.0 * PI * f * t)
            }
            val sample = (sum / freqs.size) * env * gain * Short.MAX_VALUE
            sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    private fun synthesizeArpeggio(freqs: DoubleArray, noteMs: Int, gain: Double): ShortArray {
        val perNote = (SAMPLE_RATE * noteMs) / 1000
        val out = ShortArray(perNote * freqs.size)
        for (n in freqs.indices) {
            val f = freqs[n]
            for (i in 0 until perNote) {
                val t = i.toDouble() / SAMPLE_RATE
                val p = i.toDouble() / perNote
                val env = sin(PI * p).coerceAtLeast(0.0) * exp(-1.2 * p)
                val v = sin(2.0 * PI * f * t) * env * gain * Short.MAX_VALUE
                out[n * perNote + i] = v.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        return out
    }
}
