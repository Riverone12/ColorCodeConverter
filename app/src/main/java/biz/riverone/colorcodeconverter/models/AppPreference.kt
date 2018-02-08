package biz.riverone.colorcodeconverter.models

import android.content.Context
import net.grandcentrix.tray.AppPreferences

/**
 * AppPreference.kt: このアプリの設定項目
 * Created by kawahara on 2018/01/13.
 * 2018.2.8 J.Kawahara 履歴をJSON形式で保存
 */
object AppPreference {

    private const val PREFERENCE_VERSION = 2

    private const val PREF_KEY_VERSION = "pref_version"
    private const val PREF_KEY_MODE = "pref_mode"
    private const val PREF_KEY_HISTORY_JSON = "pref_history_json"

    // モード
    const val MODE_RGB_TO_HEX = 0
    const val MODE_HEX_TO_RGB = 1

    var mode: Int = MODE_RGB_TO_HEX

    var historyJson: String = ""

    fun initialize(applicationContext: Context) {
        val appPref = AppPreferences(applicationContext)
        val prefVersion = appPref.getInt(PREF_KEY_VERSION, 0)
        if (prefVersion <= 0) {
            saveAll(applicationContext)
            return
        }
        // Tray から設定値を読み込む
        mode = appPref.getInt(PREF_KEY_MODE, MODE_RGB_TO_HEX)
        historyJson = appPref.getString(PREF_KEY_HISTORY_JSON, "") ?: ""

        if (prefVersion < PREFERENCE_VERSION) {
            saveAll(applicationContext)
        }
    }

    fun saveAll(applicationContext: Context) {
        val appPref = AppPreferences(applicationContext)
        appPref.put(PREF_KEY_VERSION, PREFERENCE_VERSION)
        appPref.put(PREF_KEY_MODE, mode)
        appPref.put(PREF_KEY_HISTORY_JSON, historyJson)
    }
}