package com.example.maybeclicker

import android.app.Application
import android.content.Context
import android.content.res.Resources

class App : Application() {

    override fun attachBaseContext(base: Context) {
        val config = base.resources.configuration
        if (config.fontScale != 1f) {
            config.fontScale = 1f
            val newBase = base.createConfigurationContext(config)
            super.attachBaseContext(newBase)
        } else {
            super.attachBaseContext(base)
        }
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        val config = res.configuration
        if (config.fontScale != 1f) {
            config.fontScale = 1f
            res.updateConfiguration(config, res.displayMetrics)
        }
        return res
    }
}
