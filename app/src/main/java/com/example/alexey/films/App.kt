package com.example.alexey.films

import android.app.Application
import io.realm.Realm

/**
 * Created by Alexey on 22.01.2018.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
    }
}