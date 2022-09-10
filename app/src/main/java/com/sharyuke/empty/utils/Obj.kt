package com.sharyuke.empty.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val GSON = Gson()

/**
 * 快速转换对象
 * 替代繁琐的 TypeToken<T>写法。感谢kotlin的inline和reified，Java是无法实现这种代码简化的。
 */
inline fun <reified T> String.toObject(): T = GSON.fromJson(this, object : TypeToken<T>() {}.type)