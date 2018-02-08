package biz.riverone.colorcodeconverter.models

import org.junit.Test

import org.junit.Assert.*

/**
 * ColorCodeTest.kt : ColorCodeのテスト
 * Created by kawahara on 2018/01/13.
 */
class ColorCodeTest {
    @Test
    fun rgbToHex() {
        val r = 255
        val g = 127
        val b = 0

        val result = ColorCode.rgbToHex(r, g, b)
        assertEquals("#ff7f00", result)
    }

    @Test
    fun hexToRgb() {
        val hex = "#ff7f00"

        val result = ColorCode.hexToRgb(hex)
        assertEquals(255127000, result)

        val result3 = ColorCode.hexToRgb("#ccc")
        assertEquals(204204204, result3)
    }

    @Test
    fun intToR() {
        assertEquals(255, ColorCode.intToR(255127000))
    }

    @Test
    fun intToG() {
        assertEquals(127, ColorCode.intToG(255127000))
    }

    @Test
    fun intToB() {
        assertEquals(0, ColorCode.intToB(255127000))
    }

}