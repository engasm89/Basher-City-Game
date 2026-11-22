package com.example.opencity.engine

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Custom GLSurfaceView that routes touch gestures to camera look and view angles.
 */
class CityGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    prebuiltWorld: CityWorldInitData? = null
) : GLSurfaceView(context, attrs) {

    val renderer = CityRenderer(context, prebuiltWorld)

    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
        isFocusable = false
        isFocusableInTouchMode = false
    }

    fun setGraphicsQuality(quality: String) {
        renderer.graphicsQuality = quality.uppercase()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Camera mouse-drag/scroll & desktop HUD zones are unified in Compose (GameScreen)
        // so camera orbit, zoom, and UI clicks never conflict.
        return false
    }
}
