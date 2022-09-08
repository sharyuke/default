package com.sharyuke.empty.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.bucket(capacity: Int, time: Long = Long.MAX_VALUE, onSave: (Int) -> Unit = {}) = flow<List<T>> {
    val current = ArrayList<T>(capacity)
    var job: Job? = null
    coroutineScope {
        collect {
            job?.cancel()
            job = launch {
                delay(time)
                current.clear()
            }
            if (current.size < capacity - 1) {
                current.add(it).also { onSave(current.size) }
            } else {
                emit(current).also { current.clear() }
            }
        }
    }
}

fun <T> Flow<T>.catchWhen(p: (Throwable) -> Boolean, block: suspend () -> Unit) = catch { if (p(it)) block() else throw it }

fun <T> eachOf(list: Array<T>) = flow { list.forEach { emit(it) } }

suspend fun endEach() = currentCoroutineContext().cancel(EndTestException())// 结束掉循环
fun CoroutineScope.closeWithErr(e: Throwable?) = cancel(CancellationException(e?.message, e)) // 结束掉Callback

class EndTestException : CancellationException("We fond useful IP!!")
class NotFoundException : CancellationException("Not found IP!!")
