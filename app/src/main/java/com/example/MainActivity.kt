package com.example

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.opencity.audio.SoundManager
import com.example.opencity.data.GameSaveManager
import com.example.opencity.engine.Weather
import com.example.opencity.ui.GameScreen
import com.example.opencity.ui.SettingsDialog
import com.example.opencity.ui.StartScreen
import com.example.ui.theme.MyApplicationTheme

enum class ScreenState {
    START_MENU,
    IN_GAME
}

class MainActivity : ComponentActivity() {

    private lateinit var saveManager: GameSaveManager
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        // Keep screen awake while playing
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Extend into all display notches / cutouts for 100% edge-to-edge full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        saveManager = GameSaveManager(this)
        soundManager = SoundManager()
        soundManager.isMuted = !saveManager.isSoundEnabled

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0E1A)
                ) {
                    var currentScreen by remember { mutableStateOf(ScreenState.IN_GAME) }
                    var playerName by remember { mutableStateOf(saveManager.playerName) }
                    var characterMotto by remember { mutableStateOf(saveManager.characterMotto) }
                    var showHelpDialog by remember { mutableStateOf(false) }
                    var showSettingsDialog by remember { mutableStateOf(false) }
                    var showNameEditDialog by remember { mutableStateOf(false) }
                    var graphicsQuality by remember { mutableStateOf(saveManager.graphicsQuality) }

                    when (currentScreen) {
                        ScreenState.START_MENU -> {
                            StartScreen(
                                playerName = playerName,
                                onEditName = { showNameEditDialog = true },
                                onStartGame = {
                                    hideSystemBars()
                                    currentScreen = ScreenState.IN_GAME
                                },
                                onOpenSettings = { showSettingsDialog = true },
                                onOpenHelp = { showHelpDialog = true }
                            )

                            if (showNameEditDialog) {
                                com.example.opencity.ui.NameEditDialog(
                                    currentName = playerName,
                                    currentMotto = characterMotto,
                                    onSaveName = { newName ->
                                        playerName = newName
                                        saveManager.playerName = newName
                                    },
                                    onSaveMotto = { newMotto ->
                                        characterMotto = newMotto
                                        saveManager.characterMotto = newMotto
                                    },
                                    onDismiss = { showNameEditDialog = false }
                                )
                            }

                            if (showSettingsDialog || showHelpDialog) {
                                SettingsDialog(
                                    isSoundEnabled = saveManager.isSoundEnabled,
                                    onToggleSound = {
                                        saveManager.isSoundEnabled = it
                                        soundManager.isMuted = !it
                                    },
                                    soundVolume = soundManager.volume,
                                    onChangeVolume = { soundManager.volume = it },
                                    currentTimeHours = 22.5f,
                                    onSetTime = { },
                                    isNightLocked = true,
                                    currentWeather = Weather.CLEAR,
                                    onSetWeather = { },
                                    graphicsQuality = graphicsQuality,
                                    onSetGraphics = {
                                        graphicsQuality = it
                                        saveManager.graphicsQuality = it
                                    },
                                    onResetProgress = { saveManager.resetProgress() },
                                    onDismiss = {
                                        showSettingsDialog = false
                                        showHelpDialog = false
                                        hideSystemBars()
                                    }
                                )
                            }
                        }

                        ScreenState.IN_GAME -> {
                            GameScreen(
                                saveManager = saveManager,
                                soundManager = soundManager,
                                onBackToMenu = { currentScreen = ScreenState.START_MENU }
                            )
                        }
                    }
                }
            }
        }
        window.decorView.post {
            hideSystemBars(force = true)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars(force = true)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.post {
                hideSystemBars(force = true)
            }
        }
    }

    private fun hideSystemBars(force: Boolean = true) {
        if (force) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}
