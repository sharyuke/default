package com.sharyuke.empty.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.sharyuke.empty.R

fun Context.dialog(title: String = "提示", msg: String? = "", cancelText: String = "取消", submitText: String = "确定", onCancel: () -> Unit = {}, onSubmit: DialogLifeCycle.() -> Unit) {
    dialogCustom(R.layout.dialog_confirm, width = 250.dp) {
        findViewById<TextView>(R.id.dialog_confirm_title).text = title
        findViewById<TextView>(R.id.dialog_confirm_msg).apply {
            text = msg
            isVisible = msg?.isNotBlank() ?: true
        }
        findViewById<TextView>(R.id.dialog_confirm_submit).apply {
            text = submitText
            onClick { onSubmit().also { dismiss() } }
        }
        findViewById<TextView>(R.id.dialog_confirm_cancel).apply {
            text = cancelText
            onClick { onCancel().also { dismiss() } }
        }
    }
}

fun Context.dialogCustom(layout: Int, gravity: Int = Gravity.CENTER, width: Int = ViewGroup.LayoutParams.WRAP_CONTENT, height: Int = ViewGroup.LayoutParams.WRAP_CONTENT, onInit: DialogLifeCycle.() -> Unit) {
    val d = DialogLifeCycle(this, android.R.style.Theme_Dialog)
    d.setContentView(layout)
    d.window?.let {
        it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        it.setGravity(gravity)
        it.setLayout(width, height)
    }
    d.show()
    d.onInit()
}

class DialogLifeCycle(ctx: Context, style: Int) : Dialog(ctx, style), LifecycleOwner {
    var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = mLifecycleRegistry
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mLifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onDetachedFromWindow() {
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        super.onDetachedFromWindow()
    }
}
