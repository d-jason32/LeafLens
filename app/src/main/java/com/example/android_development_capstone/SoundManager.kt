package com.example.android_development_capstone


import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.collections.set
import kotlin.let

object SoundManager {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<String, Int>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundMap["choose"] = soundPool!!.load(context, R.raw.choose, 1)






        isInitialized = true
    }

    fun play(name: String) {
        soundMap[name]?.let { id ->
            soundPool?.play(id, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        isInitialized = false
    }
}