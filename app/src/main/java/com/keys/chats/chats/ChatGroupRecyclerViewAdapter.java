package com.keys.chats.chats;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import com.keys.MyApplication;
import com.keys.chats.base.RecyclerViewAdapterBase;
import com.keys.chats.base.ViewWrapper;
import com.keys.chats.model.User;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EBean
class ChatGroupRecyclerViewAdapter extends RecyclerViewAdapterBase<User, chatGroupView> implements Filterable {

    @RootContext
    Context context;

    List<User> users;
    private ValueFilter valueFilter;

    private Map<String, Boolean> list = new HashMap<>();

    public Map<String, Boolean> getList() {
        return list;
    }

    public void setList(Map<String, Boolean> list) {
        this.list = list;
    }

    @Override
    public void onBindViewHolder(ViewWrapper<chatGroupView> holder, final int position) {
        final chatGroupView view = holder.getView();
        final User s = items.get(position);
        view.bind(s);
        Log.e("list", MyApplication.gson.toJson(getList()) + "");
        if (getList().containsKey(s.getObjectId()))
            view.add.setChecked(getList().get(s.getObjectId()));

        view.add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getList().put(s.getObjectId(), b);
            }
        });
//        if (view.progressBar.getVisibility() == View.GONE) {
//        }
    }

    @Override
    protected chatGroupView onCreateItemView(ViewGroup parent, int viewType) {
        return chatGroupView_.build(context);
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<User> userList = items;

            if (constraint != null && constraint.length() > 0) {
                List<User> filterList = new ArrayList<>();
                for (int i = 0; i < userList.size(); i++) {
                    if ((userList.get(i).getFullName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(userList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = users.size();
                results.values = users;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items = (List<User>) results.values;
            notifyDataSetChanged();
        }

    }

    //    private final int VIEW_TYPE_ITEM = 0;
//    private final int VIEW_TYPE_LOADING = 1;

    //    private OnLoadMoreListener mOnLoadMoreListener;

//    private boolean isLoading = false;
//    private boolean mBeforeLast = false;

//    public void setmRecyclerView(RecyclerView mRecyclerView) {
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
}
