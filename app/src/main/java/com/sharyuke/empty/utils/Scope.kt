package com.sharyuke.empty.utils

import kotlinx.coroutines.*
import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

// 保存 CoroutineScope
private var scopeRef: AtomicReference<Any> = AtomicReference()

// 自定义的 CoroutineScope
val appScope: CoroutineScope
    get() {
        while (true) {
            val existing = scopeRef.get() as CoroutineScope?
            if (existing != null) {
                return existing
            }
            val newScope = SafeCoroutineScope(Dispatchers.Main.immediate)
            if (scopeRef.compareAndSet(null, newScope)) {
                return newScope
            }
        }
    }

val newAppScope get() = SafeCoroutineScope(Dispatchers.Main.immediate)

// 不会崩溃的 CoroutineScope
class SafeCoroutineScope(context: CoroutineContext) : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext = SupervisorJob() + context + UncaughtCoroutineExceptionHandler()
    override fun close() = coroutineContext.cancelChildren()
}

// 自定义 CoroutineExceptionHandler
private class UncaughtCoroutineExceptionHandler : CoroutineExceptionHandler, AbstractCoroutineContextElement(CoroutineExceptionHandler) {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
    }
}
