package biz.riverone.colorcodeconverter

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import biz.riverone.colorcodeconverter.models.AppPreference
import biz.riverone.colorcodeconverter.models.ColorCode
import biz.riverone.colorcodeconverter.views.ColorListAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

/**
 * ColorCodeConvertor
 * Copyright (C) 2018 J.Kawahara
 * 2018.1.12 J.Kawahara 新規作成
 * 2018.1.13 J.Kawahara ver.1.01 初版公開
 * 2018.2.8  J.Kawahara ver.1.02 履歴をRecyclerView で30件まで表示
 */

class MainActivity : AppCompatActivity() {

    private var mode: Int = AppPreference.MODE_RGB_TO_HEX

    private val radioGroupMode by lazy { findViewById<RadioGroup>(R.id.radioGroupMode) }

    private val editTextR by lazy { findViewById<EditText>(R.id.editTextR) }
    private val editTextG by lazy { findViewById<EditText>(R.id.editTextG) }
    private val editTextB by lazy { findViewById<EditText>(R.id.editTextB) }
    private val editTextHex by lazy { findViewById<EditText>(R.id.editTextHex) }
    private val textViewResult by lazy { findViewById<TextView>(R.id.textViewResult) }

    private val controlDecToHex by lazy { findViewById<View>(R.id.controlDecToHex) }
    private val controlHexToDec by lazy { findViewById<View>(R.id.controlHexToDec) }

    private val colorListView by lazy { findViewById<RecyclerView>(R.id.colorListView) }
    private lateinit var colorListAdapter: ColorListAdapter

    private lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 画面をポートレートに固定する
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        AppPreference.initialize(applicationContext)
        initializeControls()

        // 前回終了時の状態を復元する
        if (AppPreference.mode == AppPreference.MODE_RGB_TO_HEX) {
            val radio10To16 = findViewById<RadioButton>(R.id.radio10To16)
            radio10To16.isChecked = true
        } else {
            val radio16To10 = findViewById<RadioButton>(R.id.radio16To10)
            radio16To10.isChecked = true
        }

        // AdMob
        MobileAds.initialize(this, "ca-app-pub-1882812461462801~5078378587")
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun initializeControls() {
        // ラジオボタンの準備
        val radio10To16 = findViewById<RadioButton>(R.id.radio10To16)
        radio10To16.tag = AppPreference.MODE_RGB_TO_HEX

        val radio16To10 = findViewById<RadioButton>(R.id.radio16To10)
        radio16To10.tag = AppPreference.MODE_HEX_TO_RGB

        radioGroupMode.setOnCheckedChangeListener {
            _, i ->
            val radioButton = findViewById<RadioButton>(i)
            val mode = Integer.parseInt(radioButton.tag.toString())
            switchMode(mode)

            textViewResult.text = ""
            editTextR.text.clear()
            editTextG.text.clear()
            editTextB.text.clear()
            editTextHex.text.clear()
        }

        // RGB 入力欄の準備
        // フォーカスが当たったら、テキストを全選択する
        editTextR?.setSelectAllOnFocus(true)
        editTextG?.setSelectAllOnFocus(true)
        editTextB?.setSelectAllOnFocus(true)

        // 16進数入力欄の準備
        // フォーカスが当たったら、テキストを全選択する
        editTextHex.setSelectAllOnFocus(true)

        // フィルタを適用
        val filters = Array<InputFilter>(1) { MyFilter() }
        editTextHex.filters = filters

        // 変換ボタンの準備
        val buttonConvert = findViewById<Button>(R.id.buttonDecToHex)
        buttonConvert.setOnClickListener {
            if (mode == AppPreference.MODE_RGB_TO_HEX) {
                execDecToHex()
            } else {
                execHexToDec()
            }
            textViewResult.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textViewResult.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        // 色履歴表示コントロールの準備
        colorListView.layoutManager = LinearLayoutManager(this)

        colorListAdapter = ColorListAdapter()
        colorListAdapter.defaultColorId = ContextCompat.getColor(this, R.color.mainBackground)
        colorListView.adapter = colorListAdapter

        // スワイプで要素を削除する準備
        val touchHelper = ItemTouchHelper(swipeListCallback)
        touchHelper.attachToRecyclerView(colorListView)
    }

    override fun onResume() {
        super.onResume()

        // 前回終了時の情報を取得する
        AppPreference.initialize(this)
        colorListAdapter.fromJson(AppPreference.historyJson)
    }

    override fun onPause() {
        super.onPause()

        // 終了時の情報を保存する
        AppPreference.mode = mode
        AppPreference.historyJson = colorListAdapter.toJson()

        AppPreference.saveAll(applicationContext)
    }

    // 10進RGB → 16進コード
    private fun execDecToHex() {
        val r = editTextValueToInt(editTextR)
        val g = editTextValueToInt(editTextG)
        val b = editTextValueToInt(editTextB)
        val result = ColorCode.rgbToHex(r, g, b)

        textViewResult.text = result

        displayColor(result)
    }

    // 16進コード → 10進RGB
    private fun execHexToDec() {
        val hexValue = editTextHex.text.toString()
        val rgb = ColorCode.hexToRgb(hexValue)
        val r = ColorCode.intToR(rgb)
        val g = ColorCode.intToG(rgb)
        val b = ColorCode.intToB(rgb)

        val result = "R $r, G $g, B $b"
        textViewResult.text = result

        displayColor(ColorCode.rgbToHex(r, g, b))
    }

    private fun switchMode(mode: Int) {
        this.mode = mode
        if (mode == AppPreference.MODE_RGB_TO_HEX) {
            // RGB → Hex
            controlDecToHex.visibility = View.VISIBLE
            controlHexToDec.visibility = View.GONE
        } else {
            controlDecToHex.visibility = View.GONE
            controlHexToDec.visibility = View.VISIBLE
        }
    }

    private fun editTextValueToInt(editText: EditText, maxValue: Int = 255): Int {
        var r = try {
            Integer.parseInt(editText.text.toString())
        } catch (ex: NumberFormatException) {
            editText.setText("0")
            0
        }

        if (r > maxValue) {
            r = maxValue
            editText.setText(r.toString())
        }
        return r
    }

    private fun displayColor(strColor: String) {
        colorListAdapter.addItem(0, strColor)
        colorListView.scrollToPosition(0)
    }

    private inner class MyFilter : InputFilter {
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
            val reg = Regex("^[a-fA-F0-9]+$")
            if (source.toString().matches(reg)) {
                return source!!
            }
            return ""
        }
    }

    // スワイプでリサイクラービューの要素を削除する
    private val swipeListCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            // 横にスワイプされたら要素を削除する
            if (viewHolder != null) {
                val swipedPosition = viewHolder.adapterPosition
                colorListAdapter.remove(swipedPosition)
            }
        }
    }
}
