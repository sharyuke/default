package com.sharyuke.empty.net.ext

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 响应适配器
 */
internal class FlowCallAdapter<R>(
    private val responseType: Type,
    private val isAsync: Boolean,
    private val isBody: Boolean,
) : CallAdapter<R, Flow<Any?>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): Flow<Any?> {
        return callFlow(call, isAsync)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun callFlow(call: Call<R>, isAsync: Boolean): Flow<Any> {
        val started = AtomicBoolean(false)
        return callbackFlow {
            if (started.compareAndSet(false, true)) {
                if (isAsync) callEnqueueFlow(call, isBody) else callExecuteFlow(call, isBody)
                awaitClose { call.cancel() }
            }
        }
    }
}

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<Any>.callEnqueueFlow(call: Call<R>, isBody: Boolean) {
    call.enqueue(object : Callback<R> {
        override fun onResponse(call: Call<R>, response: Response<R>) {
            processing(response, isBody)
        }

        override fun onFailure(call: Call<R>, throwable: Throwable) {
            cancel(CancellationException(throwable.localizedMessage, throwable))
        }
    })
}

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<Any>.callExecuteFlow(call: Call<R>, isBody: Boolean) {
    try {
        processing(call.execute(), isBody)
    } catch (throwable: Throwable) {
        cancel(CancellationException(throwable.localizedMessage, throwable))
    }
}

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<Any>.processing(response: Response<R>, isBody: Boolean) {
    if (response.isSuccessful) {
        val body = response.body()
        if (body == null || response.code() == 204) {
            cancel(CancellationException("HTTP status code: ${response.code()}"))
        } else {
            val channelResult = if (isBody) trySendBlocking(body) else trySendBlocking(response)
            channelResult
                .onSuccess { close() }
                .onClosed { throwable -> cancel(CancellationException(throwable?.localizedMessage, throwable)) }
                .onFailure { throwable -> cancel(CancellationException(throwable?.localizedMessage, throwable)) }
        }
    } else {
        val msg = response.errorBody()?.string()
        cancel(CancellationException(if (msg.isNullOrEmpty()) response.message() else msg))
    }
}
