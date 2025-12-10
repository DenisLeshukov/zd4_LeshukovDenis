package com.example.pr19_1_Leshukov.Database

import android.app.Application

class CriminalIntentApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}