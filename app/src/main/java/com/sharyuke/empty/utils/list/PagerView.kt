package com.sharyuke.empty.utils.list

import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sharyuke.empty.utils.onClick
import com.sharyuke.empty.utils.setOnPageChange

fun FragmentStateAdapter.withViewPager(viewPager2: ViewPager2, vararg tabs: View?, onSelect: (View, Boolean) -> Unit = { _, _ -> }) = apply {
    viewPager2.adapter = this
    tabs.firstOrNull()?.isSelected = true
    tabs.firstOrNull()?.apply { onSelect(this, true) }
    tabs.forEach { tv ->
        tv?.onClick {
            tabs.filterNotNull().forEach { i -> i.isSelected = (i == this).apply { onSelect(i, this) } }
            viewPager2.setCurrentItem(tabs.indexOf(this), false)
        }
    }
    viewPager2.setOnPageChange { tabs.filterNotNull().forEachIndexed { index, view -> view.isSelected = (index == it).apply { onSelect(tabs[index]!!, this) } } }
}

fun FragmentStateAdapter.withViewPager(viewPager2: ViewPager2, tabLayout: TabLayout, vararg tabs: Pair<Int, String>) = apply {
    viewPager2.adapter = this
    tabLayout.withViewPager(viewPager2, *tabs)
}

fun FragmentStateAdapter.withViewPager(viewPager2: ViewPager2, tabLayout: TabLayout, vararg tabs: String) = apply {
    viewPager2.adapter = this
    tabLayout.withViewPager(viewPager2, *tabs)
}

fun TabLayout.withViewPager(vp: ViewPager2, vararg tabs: Pair<Int, String>) {
    TabLayoutMediator(this, vp) { tab, position ->
        tabs.getOrNull(position)?.apply {
            tab.text = second
            tab.setIcon(first)
        }
    }.attach()
}

fun TabLayout.withViewPager(vp: ViewPager2, vararg tabs: String) {
    TabLayoutMediator(this, vp) { tab, position -> tab.text = tabs.getOrElse(position) { "" } }.attach()
}
