package com.sahilkumar.dfms.core.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sahilkumar.dfms.core.network.Page

/**
 * Generic Paging 3 source over any Spring `Page<T>` endpoint.
 * [loader] receives a zero-based page index and the requested size.
 */
class PagePagingSource<T : Any>(
    private val loader: suspend (page: Int, size: Int) -> Page<T>,
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 0
        return try {
            val result = loader(page, params.loadSize)
            LoadResult.Page(
                data = result.content,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (result.last || result.content.isEmpty()) null else page + 1,
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
}
