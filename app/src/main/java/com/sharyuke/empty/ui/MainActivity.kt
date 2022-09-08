package com.sharyuke.empty.ui

import android.os.Bundle
import com.sharyuke.empty.R
import com.sharyuke.empty.ui.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}