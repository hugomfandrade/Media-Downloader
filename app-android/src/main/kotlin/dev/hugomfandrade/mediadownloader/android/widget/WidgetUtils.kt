package dev.hugomfandrade.mediadownloader.android.widget

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

class WidgetUtils
/**
 * Ensure this class is only used as a utility.
 */
private constructor() {


    init {
        throw AssertionError()
    }


    companion object {

        /*
         * Logging tag.
         */
        private const val TAG = "WidgetUtils"


        fun getThemePrimaryDarkColor(context: Context): Int {
            val colorAttr: Int = android.R.attr.colorPrimaryDark
            val outValue = TypedValue()
            context.theme.resolveAttribute(colorAttr, outValue, true)
            return outValue.data
        }

        fun convertDpToPixel(dp: Float, context: Context): Float {
            return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}