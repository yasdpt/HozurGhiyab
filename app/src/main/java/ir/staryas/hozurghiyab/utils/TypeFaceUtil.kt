package ir.staryas.hozurghiyab.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import java.lang.reflect.AccessibleObject.setAccessible

object TypefaceUtil {

    fun setDefaultFont(
        context: Context,
        staticTypefaceFieldName: String, fontAssetName: String
    ) {
        val regular = Typeface.createFromAsset(
            context.assets,
            fontAssetName
        )
        replaceFont(staticTypefaceFieldName, regular)
    }

    private fun replaceFont(
        staticTypefaceFieldName: String,
        newTypeface: Typeface
    ) {
        try {
            val staticField = Typeface::class.java
                .getDeclaredField(staticTypefaceFieldName)
            staticField.isAccessible = true
            staticField.set(null, newTypeface)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }
}