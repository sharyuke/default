package com.sharyuke.empty.utils

inline fun <T> T.applyIf(pr: Boolean, block: T.() -> Unit): T = apply { if (pr) block() }

inline fun <T, R : Any> T.applyIt(it: R?, block: T.(R) -> Unit): T = apply { if (it != null) block(it) }

fun Boolean.ifTrue(block: () -> Unit) = apply { if (this) block() }

fun Boolean.ifFalse(block: () -> Unit) = apply { if (!this) block() }
