package biz.riverone.colorcodeconverter

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import biz.riverone.colorcodeconverter.models.AppPreference
import biz.riverone.colorcodeconverter.models.ColorCode
import biz.riverone.colorcodeconverter.views.SampleController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private var mode: Int = AppPreference.MODE_RGB_TO_HEX

    private val radioGroupMode by lazy { findViewById<RadioGroup>(R.id.radioGroupMode) }

    private val editTextR by lazy { findViewById<EditText>(R.id.editTextR) }
    private val editTextG by lazy { findViewById<EditText>(R.id.editTextG) }
    private val editTextB by lazy { findViewById<EditText>(R.id.editTextB) }
    private val editTextHex by lazy { findViewById<EditText>(R.id.editTextHex) }
    private val textViewResult by lazy { findViewById<TextView>(R.id.textViewResult) }

    private val controlDecToHex: View by lazy { findViewById<View>(R.id.controlDecToHex) }
    private val controlHexToDec: View by lazy { findViewById<View>(R.id.controlHexToDec) }

    private val sampleControllerList = ArrayList<SampleController>()

    private lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 画面をポートレートに固定する
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        AppPreference.initialize(applicationContext)
        initializeControls()

        // 前回終了時の履歴を表示する
        if (AppPreference.mode == AppPreference.MODE_RGB_TO_HEX) {
            val radio10To16 = findViewById<RadioButton>(R.id.radio10To16)
            radio10To16.isChecked = true
        } else {
            val radio16To10 = findViewById<RadioButton>(R.id.radio16To10)
            radio16To10.isChecked = true
        }
        // switchMode(AppPreference.mode)

        displayColor(AppPreference.colorHistory3)
        displayColor(AppPreference.colorHistory2)
        displayColor(AppPreference.colorHistory1)
        displayColor(AppPreference.colorHistory0)

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


        // 色サンプルコントロールの準備
        sampleControllerList.clear()

        val colorSample0 = findViewById<View>(R.id.colorSample0)
        val controller0 = SampleController(
                this,
                colorSample0.findViewById<View>(R.id.colorSampleView),
                colorSample0.findViewById(R.id.textViewColorCode),
                colorSample0.findViewById(R.id.textViewRGB))

        sampleControllerList.add(controller0)

        val colorSample1 = findViewById<View>(R.id.colorSample1)
        val controller1 = SampleController(
                this,
                colorSample1.findViewById<View>(R.id.colorSampleView),
                colorSample1.findViewById(R.id.textViewColorCode),
                colorSample1.findViewById(R.id.textViewRGB))
        sampleControllerList.add(controller1)

        val colorSample2 = findViewById<View>(R.id.colorSample2)
        val controller2 = SampleController(
                this,
                colorSample2.findViewById<View>(R.id.colorSampleView),
                colorSample2.findViewById(R.id.textViewColorCode),
                colorSample2.findViewById(R.id.textViewRGB))
        sampleControllerList.add(controller2)

        val colorSample3 = findViewById<View>(R.id.colorSample3)
        val controller3 = SampleController(
                this,
                colorSample3.findViewById<View>(R.id.colorSampleView),
                colorSample3.findViewById(R.id.textViewColorCode),
                colorSample3.findViewById(R.id.textViewRGB))
        sampleControllerList.add(controller3)

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
    }

    override fun onPause() {
        super.onPause()
        AppPreference.mode = mode
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
        putColorHistory()
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
        putColorHistory()
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

        var i = sampleControllerList.size - 2
        while (i >= 0) {
            sampleControllerList[i + 1].setColor(sampleControllerList[i].currentColorCode)
            i -= 1
        }
        if (sampleControllerList.size > 0) {
            sampleControllerList[0].setColor(strColor)
        }
    }

    private fun putColorHistory() {
        // 履歴を保存する
        if (sampleControllerList.size >= 4) {
            AppPreference.colorHistory0 = sampleControllerList[0].currentColorCode
            AppPreference.colorHistory1 = sampleControllerList[1].currentColorCode
            AppPreference.colorHistory2 = sampleControllerList[2].currentColorCode
            AppPreference.colorHistory3 = sampleControllerList[3].currentColorCode
        }
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
}
