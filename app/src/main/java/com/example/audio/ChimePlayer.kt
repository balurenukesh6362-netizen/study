package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object ChimePlayer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun playChime(type: String = "order", enabled: Boolean = true) {
        if (!enabled) return
        scope.launch {
            runCatching {
                val freqs = if (type == "order") {
                    doubleArrayOf(587.33, 880.0, 1174.66)
                } else {
                    doubleArrayOf(523.25, 659.25, 783.99, 1046.5)
                }
                val sampleRate = 22050
                val noteDurationSec = 0.14
                val samplesPerNote = (sampleRate * noteDurationSec).toInt()
                val totalSamples = samplesPerNote * freqs.size
                val pcm = ShortArray(totalSamples)

                freqs.forEachIndexed { idx, freq ->
                    val offset = idx * samplesPerNote
                    for (i in 0 until samplesPerNote) {
                        val env = 1.0 - (i.toDouble() / samplesPerNote)
                        val sample = sin(2.0 * PI * freq * i / sampleRate) * env * 0.32
                        pcm[offset + i] = (sample * Short.MAX_VALUE).toInt().toShort()
                    }
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcm.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()
                Thread.sleep((freqs.size * noteDurationSec * 1000).toLong() + 120L)
                track.stop()
                track.release()
            }
        }
    }
}
