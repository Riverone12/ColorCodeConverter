package biz.riverone.colorcodeconverter.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import biz.riverone.colorcodeconverter.R
import biz.riverone.colorcodeconverter.models.ColorCode

/**
 * SampleController.kt: 色見本を表示
 * Created by kawahara on 2018/01/13.
 */
class SampleController(
        context: Context,
        private val colorSampleView: View,
        private val textViewColorCode: TextView,
        private val textViewRGB: TextView
) {

    private val defaultColorId: Int = ContextCompat.getColor(context, R.color.mainBackground)
    private var currentCode: String = ""

    val currentColorCode: String
        get() { return currentCode }

    init {
        clear()
    }

    private fun clear() {
        colorSampleView.setBackgroundColor(defaultColorId)
        textViewColorCode.text = ""
        textViewRGB.text = ""
    }

    fun setColor(hexColorCode: String) {
        if (hexColorCode.isEmpty()) {
            clear()
            return
        }
        Log.d("SampleController", hexColorCode + " ---------------------------------")
        // 背景色を変更する
        val color = Color.parseColor(hexColorCode)
        val bgShape = GradientDrawable()
        bgShape.setColor(color)
        bgShape.setStroke(1, color)
        bgShape.cornerRadius = 8.0f

        colorSampleView.background = bgShape

        // 16進コードを表示する
        textViewColorCode.text = hexColorCode.toString()

        // RGBを表示する
        val rgb = ColorCode.hexToRgb(hexColorCode)
        val r = ColorCode.intToR(rgb)
        val g = ColorCode.intToG(rgb)
        val b = ColorCode.intToB(rgb)
        val rgbCode = "(R $r, G $g, B $b)"
        textViewRGB.text = rgbCode

        // 現在表示中の色コードを保持しておく
        currentCode = hexColorCode
    }

}