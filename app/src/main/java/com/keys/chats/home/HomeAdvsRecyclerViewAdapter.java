package com.keys.chats.home;

import android.content.Context;
import android.view.ViewGroup;

import com.keys.chats.base.RecyclerViewAdapterBase;
import com.keys.chats.base.ViewWrapper;
import com.keys.chats.home.homeAdvsView_;
import com.keys.chats.model.PublicAdvs;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean
public class HomeAdvsRecyclerViewAdapter extends RecyclerViewAdapterBase<PublicAdvs, homeAdvsView> {

    @RootContext
    Context context;

//    private final int VIEW_TYPE_ITEM = 0;
//    private final int VIEW_TYPE_LOADING = 1;

//    private OnLoadMoreListener mOnLoadMoreListener;

//    boolean isLoading = false;
//    boolean mBeforeLast = false;
//    boolean isLastVisibleItemReached = false;

//    private RecyclerView mRecyclerView;

//    public void setmRecyclerView(RecyclerView mRecyclerView) {
//        this.mRecyclerView = mRecyclerView;
//        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(RecyclerView view, int scrollState) {
//                RecyclerView.LayoutManager lm = view.getLayoutManager();
//                int totalItemCount = view.getLayoutManager().getItemCount();
//                int visibleItemCount = lm.getChildCount();
//                int firstVisibleItem = 0;
//                int lastVisibleItem = 0;
//                if (lm instanceof LinearLayoutManager) {
//                    firstVisibleItem = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
//                    lastVisibleItem = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
//                }
//                if (lm instanceof GridLayoutManager) {
//                    firstVisibleItem = ((GridLayoutManager) lm).findFirstVisibleItemPosition();
//                    lastVisibleItem = ((GridLayoutManager) lm).findLastVisibleItemPosition();
//                }
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if (lastInScreen < totalItemCount) {
//                    mBeforeLast = true;
//                }
//
//
//                if (!isLoading && mBeforeLast && (lastInScreen >= totalItemCount)) {
//                    isLoading = true;
//                    mBeforeLast = false;
//                    if (mOnLoadMoreListener != null)
//                        mOnLoadMoreListener.onLoadMore();
//                    isLoading = false;
//                    isLastVisibleItemReached = false;
//                }
//
//            }
//        });
//    }

//    void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
//        this.mOnLoadMoreListener = mOnLoadMoreListener;
//    }

//    @Override
//    public int getItemViewType(int position) {
//        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
//    }

    @Override
    public void onBindViewHolder(ViewWrapper<homeAdvsView> holder, final int position) {
        final homeAdvsView view = holder.getView();
//        if (view.progressBar.getVisibility() == View.GONE) {
        final PublicAdvs s = items.get(position);

        view.bind(s);
//        }
    }

    @Override
    protected homeAdvsView onCreateItemView(ViewGroup parent, int viewType) {
//        homeView servicesView = homeView_.build(context);
//        if (viewType == VIEW_TYPE_LOADING) {
//            servicesView.showProgress();
//        }
//        return servicesView;
        return homeAdvsView_.build(context);
    }
}
