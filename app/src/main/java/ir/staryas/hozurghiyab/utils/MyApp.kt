package ir.staryas.hozurghiyab.utils

import android.app.Application

class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()
        TypefaceUtil.setDefaultFont(this, "SANS_SERIF", "fonts/estedadlight.ttf")
    }
}