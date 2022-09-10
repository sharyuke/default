package com.sharyuke.empty.net

// TODO: URL ==> Config this.
/**
 * 项目配置域名
 */
const val URL = "http://xx.xxx.com"

/**
 * okhttp配置，连接超时时间 10s
 */
const val NET_CONFIG_OKHTTP_CONNECT_TIME_OUT = 10L

/**
 * okhttp配置，读取超时时间 10s
 */
const val NET_CONFIG_OKHTTP_READ_TIME_OUT = 10L

/**
 * okhttp配置，写入超时时间 30s
 */
const val NET_CONFIG_OKHTTP_WRITE_TIME_OUT = 30L

/**
 * 是否打印日志，默认release不打印.debug打印。当此配置为false时，则都不打印。
 */
const val NET_CONFIG_OKHTTP_LOG = true
