package com.sharyuke.empty.utils

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flow

/**
 * 动态权限，改自github的一位小哥的代码，目前找不到出处，感谢他的分享。
 * 利用动态添加Fragment的方式拉起权限请求。同时解决了原创者的几个小bug。目前使用一切正常。
 * 支持不在询问的回调。shouldShowRational表示是否还能再次请求，以便弹出权限请求框。
 * 当用户拒绝并且勾选不在询问或者同意之后，shouldShowRational均为false，也就是再次请求权限，不会再弹出权限请求框。
 */
internal object PermissionFlow {
    private val FRAGMENT_TAG = PermissionFragment::class.java.simpleName

    internal fun request(fragment: Fragment, vararg permissionsToRequest: String) =
        request(fragment.childFragmentManager, *permissionsToRequest)

    internal fun request(activity: FragmentActivity, vararg permissionsToRequest: String) =
        request(activity.supportFragmentManager, *permissionsToRequest)

    internal fun requestEach(fragment: Fragment, vararg permissionsToRequest: String) =
        requestEach(fragment.childFragmentManager, *permissionsToRequest)

    internal fun requestEach(activity: FragmentActivity, vararg permissionsToRequest: String) =
        requestEach(activity.supportFragmentManager, *permissionsToRequest)

    private fun request(fragmentManager: FragmentManager, vararg permissionsToRequest: String) = flow {
        createFragment(fragmentManager).takeIf { permissionsToRequest.isNotEmpty() }?.run {
            request(*permissionsToRequest)
            val results = completableDeferred.await()
            if (results.isNotEmpty()) {
                emit(results)
            }
        }
    }

    private fun requestEach(fragmentManager: FragmentManager, vararg permissionsToRequest: String) = flow {
        createFragment(fragmentManager).takeIf { permissionsToRequest.isNotEmpty() }?.run {
            request(*permissionsToRequest)
            val results = completableDeferred.await()
            results.forEach { emit(it) }
        }
    }

    private fun createFragment(fragmentManager: FragmentManager): PermissionFragment {
        val fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)?.let { it as PermissionFragment } ?: PermissionFragment.newInstance()
        if (!fragment.isAdded) fragmentManager.beginTransaction().add(fragment, FRAGMENT_TAG).commitNow()
        return fragment
    }
}

class PermissionFragment : Fragment() {
    var completableDeferred: CompletableDeferred<List<Permission>> = CompletableDeferred()

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        completableDeferred.complete(permissions.map { Permission(permission = it.key, isGranted = it.value, shouldShowRational = showRequestPermissionRationale(it.key)) })
        completableDeferred = CompletableDeferred()
    }

    fun request(vararg permissions: String) {
        permissionRequest.launch(permissions)
    }

    private fun showRequestPermissionRationale(permission: String) =
        activity?.let { !isPermissionGranted(permission) && ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } ?: false

    private fun isPermissionGranted(permission: String): Boolean =
        activity?.let { PermissionChecker.checkSelfPermission(it, permission) == PermissionChecker.PERMISSION_GRANTED } ?: false

    override fun onDestroy() {
        super.onDestroy()
        if (completableDeferred.isActive) completableDeferred.cancel()
        completableDeferred = CompletableDeferred()
    }

    companion object {
        fun newInstance() = PermissionFragment()
    }
}

data class Permission(
    val permission: String,
    val isGranted: Boolean,
    val shouldShowRational: Boolean = false
)

// Extensions
fun FragmentActivity.permissionsRequest(vararg permissionsToRequest: String) = PermissionFlow.request(this, *permissionsToRequest)
fun FragmentActivity.permissionsRequestEach(vararg permissionsToRequest: String) = PermissionFlow.requestEach(this, *permissionsToRequest)
fun Fragment.permissionsRequest(vararg permissionsToRequest: String) = PermissionFlow.request(this, *permissionsToRequest)
fun Fragment.permissionsRequestEach(vararg permissionsToRequest: String) = PermissionFlow.requestEach(this, *permissionsToRequest)
