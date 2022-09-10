package com.sharyuke.empty.utils.permission

data class Permission(
  val permission: String,
  val isGranted: Boolean,
  val shouldShowRational: Boolean = false
)
