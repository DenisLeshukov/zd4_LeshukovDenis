package com.example.pr19_1_Leshukov

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.util.Date
import java.util.UUID
import android.view.LayoutInflater
import android.view.ViewGroup

class MainFragment(crime: Crime): Fragment()
{
    var mainFragm = crime
    private lateinit var ViewMain: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        ViewMain = view
        return view


    }
    fun ClickFirst(view: View) {
        Snackbar.make(view,getString(R.string.checkbox_solved), Snackbar.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        if(mainFragm.isSolved == true)
        {
            ClickFirst(ViewMain)
        }
    }

}


