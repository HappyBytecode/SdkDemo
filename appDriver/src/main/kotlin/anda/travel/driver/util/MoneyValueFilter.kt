package anda.travel.driver.util

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.DigitsKeyListener

class MoneyValueFilter : DigitsKeyListener(false, true) {

    private var digits = 2

    fun setDigits(d: Int): MoneyValueFilter {
        digits = d
        return this
    }

    override fun filter(psource: CharSequence, pstart: Int, pend: Int,
                        dest: Spanned, dstart: Int, dend: Int): CharSequence {
        var source = psource
        var start = pstart
        var end = pend
        val out = super.filter(source, start, end, dest, dstart, dend)

        // if changed, replace the source
        if (out != null) {
            source = out
            start = 0
            end = out.length
        }

        val len = end - start

        // if deleting, source is empty
        // and deleting can't break anything
        if (len == 0) {
            return source
        }

        // 以点开始的时候，自动在前面添加0
        if (source.toString() == "." && dstart == 0) {
            return "0."
        }
        // 如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (source.toString() != "." && dest.toString() == "0") {
            return ""
        }

        val dlen = dest.length

        // Find the position of the decimal .
        for (i in 0 until dstart) {
            if (dest[i] == '.') {
                // being here means, that a number has
                // been inserted after the dot
                // check if the amount of digits is right
                return if (dlen - (i + 1) + len > digits)
                    ""
                else
                    SpannableStringBuilder(source, start, end)
            }
        }

        for (i in start until end) {
            if (source[i] == '.') {
                // being here means, dot has been inserted
                // check if the amount of digits is right
                return if (dlen - dend + (end - (i + 1)) > digits)
                    ""
                else
                    break // return new SpannableStringBuilder(source, start, end);
            }
        }

        // if the dot is after the inserted part,
        // nothing can break
        return SpannableStringBuilder(source, start, end)
    }
}
