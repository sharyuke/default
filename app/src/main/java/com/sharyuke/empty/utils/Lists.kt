package com.sharyuke.empty.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * @see onClick in file Views.kt
 * @see loadUrl in file Views.kt
 */
class EasyHolder<T>(view: View) : BaseViewHolder(view) {
    var item: T? = null
    var adapter: BaseQuickAdapter<T, EasyHolder<T>>? = null
    val context: Context = view.context

    fun setImageUrl(id: Int, url: String?) = getView<ImageView>(id).loadUrl(url)
    fun setClick(id: Int, click: (View) -> Unit?) = getView<View>(id).onClick { click(this) }

    fun onHasItem(block: T.() -> Unit) = item?.block()
    fun onItemClick(itemClick: ItemModel<T>.() -> Unit) = itemView.onClick(scope = (context as FragmentActivity?)?.lifecycleScope) { item?.apply { itemClick(ItemModel(adapterPosition, this, adapter!!)) } }
    fun onItemClickLong(itemClick: ItemModel<T>.() -> Unit) = itemView.onClickLong { item?.apply { itemClick(ItemModel(adapterPosition, this, adapter!!)) }.let { true } }
}

data class ItemModel<T>(val position: Int, val item: T, val adapter: BaseQuickAdapter<T, EasyHolder<T>>)

fun <T> adapterCreate(layout: Int, initData: List<T>? = null, emptyLayout: Int? = null, convert: EasyHolder<T>.() -> Unit): BaseQuickAdapter<T, EasyHolder<T>> = object : BaseQuickAdapter<T, EasyHolder<T>>(layout, initData?.toMutableList()) {
    override fun convert(holder: EasyHolder<T>, item: T) {
        holder.item = item
        holder.adapter = this
        convert(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (emptyLayout != null) setEmptyView(LayoutInflater.from(recyclerView.context).inflate(emptyLayout, recyclerView, false))
    }
}

fun <T> BaseQuickAdapter<T, EasyHolder<T>>.withRecyclerView(recyclerView: RecyclerView) = apply { recyclerView.adapter = this }

fun FragmentActivity.adapterViewPager(list: List<Fragment>) = object : FragmentStateAdapter(this) {
    override fun getItemCount(): Int = list.size
    override fun createFragment(position: Int): Fragment = list[position]
}

fun FragmentStateAdapter.withViewPager(viewPager2: ViewPager2) = apply { viewPager2.adapter = this }

const val PAGE_SIZE = 20
const val PAGE_FIRST = 1

/**
 * 分页适配器
 */
fun <T : Any> adapterPaging(layout: Int, convert: EasyPagingHolder<T>.() -> Unit) = object : PagingDataAdapter<T, EasyPagingHolder<T>>(object : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}) {
    override fun onBindViewHolder(holder: EasyPagingHolder<T>, position: Int) {
        val item = getItem(position)
        holder.item = item
        holder.adapter = this
        convert(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EasyPagingHolder<T> {
        val itemView = LayoutInflater.from(parent.context).inflate(layout, null)
        itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return EasyPagingHolder(itemView)
    }
}


class EasyPagingHolder<T : Any>(view: View) : BaseViewHolder(view) {
    var item: T? = null
    var adapter: PagingDataAdapter<T, EasyPagingHolder<T>>? = null

    fun setImageUrl(id: Int, url: String?) = getView<ImageView>(id).loadUrl(url)
    fun setClick(id: Int, click: (View) -> Unit?) = getView<View>(id).onClick { click(this) }

    fun onHasItem(block: T.() -> Unit) = item?.apply(block)
    fun <R> onHasItem(convert: T.() -> R, block: R.() -> Unit) = item?.apply { block(convert(this)) }

    fun onItemClick(itemClick: ItemPagingModel<T>.() -> Unit) = itemView.onClick { item?.apply { itemClick(ItemPagingModel(adapterPosition, this, adapter!!)) } }
    fun onItemClickLong(itemClick: ItemPagingModel<T>.() -> Unit) = itemView.onClickLong { item?.apply { itemClick(ItemPagingModel(adapterPosition, this, adapter!!)) }.let { false } }
}

data class ItemPagingModel<T : Any>(val position: Int, val item: T, val adapter: PagingDataAdapter<T, EasyPagingHolder<T>>)

fun <T : Any> PagingDataAdapter<T, EasyPagingHolder<T>>.withRecyclerView(recyclerView: RecyclerView, emptyView: View? = null) = apply {
    addLoadStateListener {
        if (emptyView != null && it.source.refresh is LoadState.NotLoading) {
            val empty = it.append.endOfPaginationReached && itemCount < 1
            recyclerView.visibility = if (empty) View.GONE else View.VISIBLE
            emptyView.visibility = if (empty) View.VISIBLE else View.GONE
        }
    }
}.apply { recyclerView.adapter = this }.apply { recyclerView.itemAnimator = null }

fun <T : Any> PagingDataAdapter<T, EasyPagingHolder<T>>.withRefresher(refresh: SwipeRefreshLayout) = apply {
//    refresh.setColorSchemeColors(refresh.context.resources.getColor(R.color.orange))
    addLoadStateListener { refresh.isRefreshing = it.refresh is LoadState.Loading && it.refresh !is LoadState.NotLoading }
    refresh.setOnRefreshListener { refresh() }
}

fun <T : Any> PagingDataAdapter<T, EasyPagingHolder<T>>.pagingStart(lifeScope: LifecycleCoroutineScope, data: suspend (Int) -> List<T>) = Pager(PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PAGE_SIZE, false, initialLoadSize = PAGE_SIZE), initialKey = 0, pagingSourceFactory = {
    object : PagingSource<Int, T>() {
        override fun getRefreshKey(state: PagingState<Int, T>): Int = PAGE_FIRST

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
            val index = params.key ?: PAGE_FIRST
            val list = data(index)
            val next = if (list.size < params.loadSize) null else index + 1
            return LoadResult.Page(list, null, next)
        }
    }
}).flow.onEach { submitData(it) }.launchIn(lifeScope)
