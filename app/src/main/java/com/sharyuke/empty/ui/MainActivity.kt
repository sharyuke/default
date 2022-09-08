package com.sharyuke.empty.ui

import android.os.Bundle
import android.widget.TextView
import com.sharyuke.empty.R
import com.sharyuke.empty.ui.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        netClient.test().sub { findViewById<TextView>(R.id.main_hw).text = toString() }
    }
}