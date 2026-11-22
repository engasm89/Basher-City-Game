package com.example.opencity.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.sin

/**
 * Real-time procedural audio synthesizer using native Android AudioTrack.
 */
class SoundManager {

    private val sampleRate = 22050
    private val audioScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val activeTrackCount = AtomicInteger(0)

    companion object {
        private const val MAX_CONCURRENT_TRACKS = 6
    }

    var isMuted = false
    var volume = 0.8f

    private fun safeClampPcm(raw: Double): Short {
        return raw.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }

    fun playHorn() {
        if (isMuted) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        audioScope.launch {
            val durationSec = 0.45f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)
            val freq1 = 440.0 // A4
            val freq2 = 554.37 // C#5

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val sample1 = sin(2.0 * Math.PI * freq1 * t)
                val sample2 = sin(2.0 * Math.PI * freq2 * t)
                buffer[i] = safeClampPcm((sample1 + sample2) * 0.5 * Short.MAX_VALUE * vol)
            }
            playPcm(buffer)
        }
    }

    fun playSiren() {
        if (isMuted) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        audioScope.launch {
            val durationSec = 1.0f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 550.0 + sin(2.0 * Math.PI * 2.0 * t) * 300.0 // wail between 250Hz and 850Hz
                val sample = sin(2.0 * Math.PI * freq * t)
                buffer[i] = safeClampPcm(sample * Short.MAX_VALUE * vol * 0.7)
            }
            playPcm(buffer)
        }
    }

    fun playChime() {
        if (isMuted) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        audioScope.launch {
            val durationSec = 0.6f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6 arpeggio

            val noteDuration = (numSamples / notes.size).coerceAtLeast(1)
            for (n in notes.indices) {
                val freq = notes[n]
                val start = n * noteDuration
                val end = ((n + 1) * noteDuration).coerceAtMost(numSamples)
                for (i in start until end) {
                    val t = (i - start).toDouble() / sampleRate
                    val env = 1.0 - ((i - start).toDouble() / noteDuration)
                    val sample = sin(2.0 * Math.PI * freq * t) * env
                    buffer[i] = safeClampPcm(sample * Short.MAX_VALUE * vol * 0.8)
                }
            }
            playPcm(buffer)
        }
    }

    fun playFootstep() {
        if (isMuted) return
        // Avoid AudioFlinger congestion when rapid footsteps occur while other SFX play
        if (activeTrackCount.get() >= MAX_CONCURRENT_TRACKS - 2) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        audioScope.launch {
            val durationSec = 0.08f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val env = 1.0 - (i.toDouble() / numSamples)
                val noise = (Math.random() * 2.0 - 1.0) * env * 0.35
                buffer[i] = safeClampPcm(noise * Short.MAX_VALUE * vol)
            }
            playPcm(buffer)
        }
    }

    fun playEngineRev(pitchMultiplier: Float = 1.0f) {
        if (isMuted) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        val safePitch = if (pitchMultiplier.isNaN()) 1.0f else pitchMultiplier.coerceIn(0.4f, 2.5f)
        audioScope.launch {
            val durationSec = 0.35f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)

            val baseFreq = (75.0 * safePitch).coerceIn(40.0, 350.0)
            val deltaFreq = (130.0 * safePitch).coerceIn(60.0, 500.0)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = baseFreq + (t / durationSec) * deltaFreq
                val sample = sin(2.0 * Math.PI * freq * t) * 0.65 + (Math.random() * 2.0 - 1.0) * 0.18
                buffer[i] = safeClampPcm(sample * Short.MAX_VALUE * vol * 0.65)
            }
            playPcm(buffer)
        }
    }

    fun playCarPurchaseChime() {
        if (isMuted) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        audioScope.launch {
            val durationSec = 0.85f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)
            // Grand celebratory fanfare: C5, G5, C6, E6
            val notes = listOf(523.25, 783.99, 1046.50, 1318.51)
            val noteDuration = (numSamples / notes.size).coerceAtLeast(1)
            for (n in notes.indices) {
                val freq = notes[n]
                val start = n * noteDuration
                val end = ((n + 1) * noteDuration).coerceAtMost(numSamples)
                for (i in start until end) {
                    val t = (i - start).toDouble() / sampleRate
                    val env = 1.0 - ((i - start).toDouble() / noteDuration) * 0.7
                    val sample = sin(2.0 * Math.PI * freq * t) * env
                    buffer[i] = safeClampPcm(sample * Short.MAX_VALUE * vol * 0.85)
                }
            }
            playPcm(buffer)
        }
    }

    fun playDoorOpen() {
        if (isMuted) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        audioScope.launch {
            val durationSec = 0.15f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val sample = sin(2.0 * Math.PI * 320.0 * t) * (1.0 - t / durationSec)
                buffer[i] = safeClampPcm(sample * Short.MAX_VALUE * vol * 0.5)
            }
            playPcm(buffer)
        }
    }

    /**
     * Realistic and fun eating / munching / chewing sound effect ("صوت الأكل والمضغ نَم نَم نَم وقرمشة"):
     * Generates a series of crunchy bites followed by a satisfying gulp.
     */
    fun playEatingSound() {
        if (isMuted) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        audioScope.launch {
            val durationSec = 0.85f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)

            val phaseCount = 4
            val phaseSamples = (numSamples / phaseCount).coerceAtLeast(1)

            for (phase in 0 until phaseCount) {
                val start = phase * phaseSamples
                val end = ((phase + 1) * phaseSamples).coerceAtMost(numSamples)
                val len = (end - start).coerceAtLeast(1)
                val isGulp = (phase == 3)

                for (i in start until end) {
                    val localT = (i - start).toDouble() / sampleRate
                    val progress = (i - start).toDouble() / len
                    val envelope = sin(progress * Math.PI)

                    val sample = if (isGulp) {
                        val freq = 340.0 - progress * 180.0
                        val tone = sin(2.0 * Math.PI * freq * localT) * 0.75
                        val noise = (Math.random() * 2.0 - 1.0) * 0.12
                        tone + noise
                    } else {
                        val popFreq = 220.0 + (phase * 50.0)
                        val pop = sin(2.0 * Math.PI * popFreq * localT) * 0.45
                        val crunchNoise = (Math.random() * 2.0 - 1.0) * 0.55
                        pop + crunchNoise
                    }

                    buffer[i] = safeClampPcm(sample * envelope * Short.MAX_VALUE * vol * 0.9)
                }
            }
            playPcm(buffer)
        }
    }

    /**
     * Refreshing drink sipping and gulping sound ("صوت شرب ورشفة عصير منعشة"):
     */
    fun playDrinkingSound() {
        if (isMuted) return
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.001f) return
        audioScope.launch {
            val durationSec = 0.65f
            val numSamples = (durationSec * sampleRate).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples
                val env = sin(progress * Math.PI)
                val freq = if (progress < 0.6) 520.0 + sin(progress * 30.0) * 80.0 else (300.0 - progress * 140.0)
                val tone = sin(2.0 * Math.PI * freq * t) * 0.55
                val bubbleNoise = (Math.random() * 2.0 - 1.0) * (if (progress < 0.6) 0.35 else 0.1)
                buffer[i] = safeClampPcm((tone + bubbleNoise) * env * Short.MAX_VALUE * vol * 0.85)
            }
            playPcm(buffer)
        }
    }

    private suspend fun playPcm(buffer: ShortArray) {
        if (buffer.isEmpty()) return
        if (activeTrackCount.get() >= MAX_CONCURRENT_TRACKS) return

        var track: AudioTrack? = null
        val released = AtomicBoolean(false)

        fun cleanupTrack(t: AudioTrack?) {
            if (released.compareAndSet(false, true)) {
                activeTrackCount.decrementAndGet()
                try {
                    t?.stop()
                } catch (_: Exception) {
                }
                try {
                    t?.release()
                } catch (_: Exception) {
                }
            }
        }

        try {
            activeTrackCount.incrementAndGet()
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
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
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            if (track.state != AudioTrack.STATE_INITIALIZED) {
                cleanupTrack(track)
                return
            }

            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) {
                    cleanupTrack(t)
                }

                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
            track.notificationMarkerPosition = buffer.size
            val written = track.write(buffer, 0, buffer.size)
            if (written < 0) {
                cleanupTrack(track)
                return
            }
            track.play()

            // Guaranteed fallback release in case native notification marker doesn't fire
            val durationMs = ((buffer.size.toLong() * 1000L) / sampleRate).coerceAtLeast(50L)
            delay(durationMs + 180L)
            cleanupTrack(track)
        } catch (_: Exception) {
            cleanupTrack(track)
        }
    }
}
