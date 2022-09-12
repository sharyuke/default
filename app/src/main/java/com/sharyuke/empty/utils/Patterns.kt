package com.sharyuke.empty.utils

import java.util.regex.Pattern

/**
 * 该文件收藏各种正则表达式
 */

/**
 * IP正则表达式，
 * @see org.apache.http.conn.ssl.AbstractVerifier 出自Android SDK 此类
 */
val IPV4_PATTERN: Pattern = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$")
