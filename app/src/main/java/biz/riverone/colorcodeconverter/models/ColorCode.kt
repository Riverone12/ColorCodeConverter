package biz.riverone.colorcodeconverter.models

/**
 * ColorCode.kt: 色コード関連
 * Created by kawahara on 2018/01/13.
 */
object ColorCode {

    private fun decToHex(dec: Int): String {
        val d =
                when {
                    dec < 0 -> 0
                    dec > 255 -> 255
                    else -> dec
                }

        var r = Integer.toHexString(d)
        if (r.length < 2) {
            r = "0" + r
        }
        return r
    }

    private fun hexToDec(hex: String): Int {
        val d = try {
            Integer.parseInt(hex, 16)
        } catch (ex: NumberFormatException) {
            0
        }
        return when {
            d < 0 -> 0
            d > 255 -> 255
            else -> d
        }
    }

    fun rgbToHex(r: Int, g: Int, b: Int): String {
        val hexR = decToHex(r)
        val hexG = decToHex(g)
        val hexB = decToHex(b)

        return "#" + hexR + hexG + hexB
    }

    fun hexToRgb(hex: String): Int {
        if (hex.isEmpty()) {
            return 0
        }
        var h = hex
        if (hex[0] == '#') {
            h = hex.substring(1)
        }

        if (h.length == 3) {
            val hexR = h[0].toString() + h[0].toString()
            val hexG = h[1].toString() + h[1].toString()
            val hexB = h[2].toString() + h[2].toString()
            h = hexR + hexG + hexB
        }

        while (h.length < 6) {
            h = "0" + h
        }
        val hexR = h.substring(0, 2)
        val hexG = h.substring(2, 4)
        val hexB = h.substring(4, 6)

        val r = hexToDec(hexR)
        val g = hexToDec(hexG)
        val b = hexToDec(hexB)

        return r * 1000000 + g * 1000 + b
    }

    fun intToR(value: Int): Int {
        return value / 1000000
    }

    fun intToG(value: Int): Int {
        return (value % 1000000) / 1000
    }

    fun intToB(value: Int): Int {
        return value % 1000
    }
}