package com.sharyuke.empty.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.sharyuke.empty.dm
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*

val uiHandler = Handler(Looper.getMainLooper())

val View.lifecycleScope get() = findViewTreeLifecycleOwner()?.lifecycleScope
fun View.lifecycleScope(block: LifecycleCoroutineScope.() -> Unit) = findViewTreeLifecycleOwner()?.lifecycleScope?.apply(block)

fun <T> Flow<T>.launchIn(scope: CoroutineScope?): Job? = scope?.launch { collect() }

/**
 * 防止双击，优雅的写法
 */
fun <T : View> T.onClick(drop: Boolean = true, scope: LifecycleCoroutineScope? = null, back: T.() -> Unit): T {
    callbackFlow {
        setOnClickListener { trySendBlocking(this@onClick) }
        awaitClose {}
    }.dropIfBusy(drop).onEach { i -> back(i).apply { delay(500) } }.flowOn(Dispatchers.Main).launchIn(scope ?: lifecycleScope ?: GlobalScope)
    return this
}

fun <T : View> T.onClickLong(back: T.() -> Boolean): T {
    setOnLongClickListener { back(this) }// 长按大概率不会重复点击，所以去掉重复点击逻辑
    return this
}

fun ImageView.loadUrl(url: String?) {
    url?.let { Glide.with(context).load(it).into(this) }
}

val Context.imm get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun FragmentActivity.hideSoftInputMethod(view: View) = imm.hideSoftInputFromWindow(view.windowToken, 0)
fun FragmentActivity.showSoftInputMethod(view: View, flag: Int = InputMethodManager.SHOW_FORCED) = imm.hideSoftInputFromWindow(view.windowToken, flag)

val Float.dp: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, dm).toInt()
val Int.dp: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toFloat(), dm).toInt()

val Float.px: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this, dm).toInt()
val Int.px: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, toFloat(), dm).toInt()

fun ViewPager2.setOnPageChange(back: (Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val vs = findViewTreeLifecycleOwner()?.lifecycle
            if (vs != null && vs.currentState.isAtLeast(Lifecycle.State.CREATED)) back(position) else unregisterOnPageChangeCallback(this)
        }
    })
}