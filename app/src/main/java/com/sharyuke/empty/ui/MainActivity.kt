package com.sharyuke.empty.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.sharyuke.empty.R
import com.sharyuke.empty.ui.base.BaseActivity
import com.sharyuke.empty.utils.dialog
import com.sharyuke.empty.utils.onClick

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        netClient.test().sub { findViewById<TextView>(R.id.main_hw).text = toString() }
        findViewById<View>(R.id.main_hw).onClick {
            dialog {

            }
        }
    }
}