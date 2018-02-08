package biz.riverone.colorcodeconverter.views

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import biz.riverone.colorcodeconverter.R
import biz.riverone.colorcodeconverter.models.ColorCode
import org.json.JSONArray
import org.json.JSONException

/**
 * ColorListAdapter.kt: リサイクルビュー用カスタムアダプタ
 * Created by kawahara on 2018/02/08.
 */
class ColorListAdapter : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {
    companion object {
        // 履歴を表示する最大件数
        private const val MAX_ITEM_COUNT = 30
    }

    private val colorList = ArrayList<String>()

    var defaultColorId: Int = 0

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val colorSampleView: View = v.findViewById(R.id.colorSampleView)
        val textViewColorCode: TextView = v. findViewById(R.id.textViewColorCode)
        val textViewRGB: TextView = v.findViewById(R.id.textViewRGB)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater
                .from(parent?.context)
                .inflate(R.layout.control_color_sample, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (colorList[position].isEmpty()) {
            holder?.colorSampleView?.setBackgroundColor(defaultColorId)
            holder?.textViewColorCode?.text = ""
            holder?.textViewRGB?.text = ""
            return
        }
        val color = Color.parseColor(colorList[position])
        val bgShape = GradientDrawable()
        bgShape.setColor(color)
        bgShape.setStroke(1, color)
        //bgShape.cornerRadius = 10.0f

        holder?.colorSampleView?.background = bgShape

        // 16進コードを表示する
        holder?.textViewColorCode?.text = colorList[position]

        // RGBを表示する
        val rgb = ColorCode.hexToRgb(colorList[position])
        val r = ColorCode.intToR(rgb)
        val g = ColorCode.intToG(rgb)
        val b = ColorCode.intToB(rgb)
        val rgbCode = "(R $r, G $g, B $b)"
        holder?.textViewRGB?.text = rgbCode
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    fun addItem(position: Int, color: String) {
        while (colorList.size >= MAX_ITEM_COUNT) {
            remove(colorList.size - 1)
        }
        colorList.add(position, color)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        colorList.removeAt(position)
        notifyDataSetChanged()
    }

    // Preference に保存するためのJSON文字列に変換する
    fun toJson(): String {
        val jsonArray = JSONArray()
        for (i in colorList.indices) {
            try {
                jsonArray.put(i, colorList[i])
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return jsonArray.toString()
    }

    // Preference に保存したJSON文字列から要素を復元する
    fun fromJson(jsonString: String) {
        colorList.clear()
        try {
            val array = JSONArray(jsonString)
            (0 until array.length()).mapTo(colorList) { array.optString(it) }
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        notifyDataSetChanged()
    }
}