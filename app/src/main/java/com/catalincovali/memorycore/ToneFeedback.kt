package com.catalincovali.memorycore

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin


// feedback audio
// un tono diverso per ogni bottone
class ToneFeedback {

    private companion object {
        const val SAMPLE_RATE = 44100
        const val DURATION_MS = 200
        val FREQ_FOR_COLOR = mapOf(
            "R" to 261.63, "G" to 293.66, "B" to 329.63,
            "M" to 392.00, "Y" to 440.00, "C" to 523.25,
        )
    }

    // sinusoide
    // ==================================================================
    private fun generateTone(freqHz: Double, durMs: Int): ShortArray {
        val n = SAMPLE_RATE * durMs / 1000
        return ShortArray(n) { i ->
            (sin(2 * PI * i * freqHz / SAMPLE_RATE) * Short.MAX_VALUE)
                .toInt().toShort()
        }
    }
    // ===================================================================

    // calcolo tutti i toni alla prima inizializzazione
    private val buffers: Map<String, ShortArray> =
        FREQ_FOR_COLOR.mapValues { (_, f) -> generateTone(f, DURATION_MS) }

    private val track = AudioTrack.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .setAudioFormat(
            AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
        )
        .setTransferMode(AudioTrack.MODE_STREAM)
        .build()
        .apply { play() }

    fun play(color: String) {
        val buf = buffers[color] ?: return
        track.pause(); track.flush(); track.play()
        track.write(buf, 0, buf.size)
    }

    fun release() {
        track.release()
    }
}
