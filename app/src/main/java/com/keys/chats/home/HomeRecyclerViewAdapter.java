package com.keys.chats.home;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.keys.chats.base.RecyclerViewAdapterBase;
import com.keys.chats.base.ViewWrapper;
import com.keys.chats.home.homeView_;
import com.keys.chats.utilities.OnLoadMoreListener;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean
public class HomeRecyclerViewAdapter extends RecyclerViewAdapterBase<String, homeView> {

    @RootContext
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;

    boolean isLoading = false;
    boolean mBeforeLast = false;
    boolean isLastVisibleItemReached = false;

    private RecyclerView mRecyclerView;

    public void setmRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                RecyclerView.LayoutManager lm = view.getLayoutManager();
                int totalItemCount = view.getLayoutManager().getItemCount();
                int visibleItemCount = lm.getChildCount();
                int firstVisibleItem = 0;
                int lastVisibleItem = 0;
                if (lm instanceof LinearLayoutManager) {
                    firstVisibleItem = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
                    lastVisibleItem = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
                }
                if (lm instanceof GridLayoutManager) {
                    firstVisibleItem = ((GridLayoutManager) lm).findFirstVisibleItemPosition();
                    lastVisibleItem = ((GridLayoutManager) lm).findLastVisibleItemPosition();
                }
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen < totalItemCount) {
                    mBeforeLast = true;
                }


                if (!isLoading && mBeforeLast && (lastInScreen >= totalItemCount)) {
                    isLoading = true;
                    mBeforeLast = false;
                    if (mOnLoadMoreListener != null)
                        mOnLoadMoreListener.onLoadMore();
                    isLoading = false;
                    isLastVisibleItemReached = false;
                }

            }
        });
    }

    void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

//    @Override
//    public int getItemViewType(int position) {
//        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
//    }

    @Override
    public void onBindViewHolder(ViewWrapper<homeView> holder, final int position) {
        final homeView view = holder.getView();
//        if (view.progressBar.getVisibility() == View.GONE) {
            final String s = items.get(position);

            view.bind(s);
//        }
    }

    @Override
    protected homeView onCreateItemView(ViewGroup parent, int viewType) {
//        homeView servicesView = homeView_.build(context);
//        if (viewType == VIEW_TYPE_LOADING) {
//            servicesView.showProgress();
//        }
//        return servicesView;
        return homeView_.build(context);
    }
}
