package com.sharyuke.empty.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

/**
 * 带过期计时器的缓冲器，
 * 拦截一定数据的数据，达到指定数量，一次性传递给下游。
 * 此操作符使用场景：双击back退出app
 *
 * @param capacity 缓存容量
 * @param expire 过期时间，达到指定时间，容量不满则抛弃
 * @param onSave 每次存入回调
 */
fun <T> Flow<T>.bucket(capacity: Int, expire: Long = Long.MAX_VALUE, onSave: (Int) -> Unit = {}) = flow<List<T>> {
    val current = ArrayList<T>(capacity)
    var job: Job? = null
    coroutineScope {
        collect {
            job?.cancel()
            job = launch {
                delay(expire)
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

/**
 * 当下游拥堵，丢弃上游数据，相当于木桶的短板
 * @param drop 是否丢弃。
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.dropIfBusy(drop: Boolean): Flow<T> = flow { coroutineScope { produce(capacity = if (drop) Channel.RENDEZVOUS else Channel.UNLIMITED) { collect { trySend(it) } }.consumeEach { emit(it) } } }

fun LongProgression.delayBy(step: Long) = asFlow().onEach { delay(step) }

fun Long.countDown(delay: Long = 1000) = count(-1, delay)

fun Long.count(step: Int = 1, delay: Long = 1000) = flow {
    var start: Long = this@count
    while (true) emit(start).also { delay(delay) }.also { start += step }
}
