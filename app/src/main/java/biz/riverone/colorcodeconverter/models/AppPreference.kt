package biz.riverone.colorcodeconverter.models

import android.content.Context
import net.grandcentrix.tray.AppPreferences

/**
 * AppPreference.kt: このアプリの設定項目
 * Created by kawahara on 2018/01/13.
 */
object AppPreference {

    private const val PREFERENCE_VERSION = 1
    private const val PREF_KEY_VERSION = "pref_version"
    private const val PREF_KEY_MODE = "pref_mode"
    private const val PREF_KEY_HISTORY_0 = "pref_history_0"
    private const val PREF_KEY_HISTORY_1 = "pref_history_1"
    private const val PREF_KEY_HISTORY_2 = "pref_history_2"
    private const val PREF_KEY_HISTORY_3 = "pref_history_3"

    // モード
    const val MODE_RGB_TO_HEX = 0
    const val MODE_HEX_TO_RGB = 1

    var mode: Int = MODE_RGB_TO_HEX

    var colorHistory0 = ""
    var colorHistory1 = ""
    var colorHistory2 = ""
    var colorHistory3 = ""

    fun initialize(applicationContext: Context) {
        val appPref = AppPreferences(applicationContext)
        val prefVersion = appPref.getInt(PREF_KEY_VERSION, 0)
        if (prefVersion <= 0) {
            saveAll(applicationContext)
            return
        }
        // Tray から設定値を読み込む
        mode = appPref.getInt(PREF_KEY_MODE, MODE_RGB_TO_HEX)
        colorHistory0 = appPref.getString(PREF_KEY_HISTORY_0, "") ?: ""
        colorHistory1 = appPref.getString(PREF_KEY_HISTORY_1, "") ?: ""
        colorHistory2 = appPref.getString(PREF_KEY_HISTORY_2, "") ?: ""
        colorHistory3 = appPref.getString(PREF_KEY_HISTORY_3, "") ?: ""

        if (prefVersion < PREFERENCE_VERSION) {
            saveAll(applicationContext)
        }
    }

    fun saveAll(applicationContext: Context) {
        val appPref = AppPreferences(applicationContext)
        appPref.put(PREF_KEY_VERSION, PREFERENCE_VERSION)
        appPref.put(PREF_KEY_MODE, mode)
        appPref.put(PREF_KEY_HISTORY_0, colorHistory0)
        appPref.put(PREF_KEY_HISTORY_1, colorHistory1)
        appPref.put(PREF_KEY_HISTORY_2, colorHistory2)
        appPref.put(PREF_KEY_HISTORY_3, colorHistory3)
    }
}