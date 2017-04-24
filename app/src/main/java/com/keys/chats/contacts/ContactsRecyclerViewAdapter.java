package com.keys.chats.contacts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.keys.chats.base.RecyclerViewAdapterBase;
import com.keys.chats.base.ViewWrapper;
import com.keys.chats.model.Contact;
import com.keys.chats.model.User;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

@EBean
public class ContactsRecyclerViewAdapter extends RecyclerViewAdapterBase<Contact, contactView> implements Filterable {

    @RootContext
    Context context;

    private boolean canChat = false;
    List<Contact> users;
    private ValueFilter valueFilter;

    private boolean isCanChat() {
        return canChat;
    }

    public void setCanChat(boolean canChat) {
        this.canChat = canChat;
    }

    @Override
    public void onBindViewHolder(ViewWrapper<contactView> holder, final int position) {
        final contactView view = holder.getView();
        final Contact s = items.get(position);

        view.bind(s);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCanChat())
                    ContactDetailsActivity_.intent(context).contact(s).start();
            }
        });

//        if (view.progressBar.getVisibility() == View.GONE) {
//        }
    }

    @Override
    protected contactView onCreateItemView(ViewGroup parent, int viewType) {
        return contactView_.build(context);
//        contactView contactView = contactView_.build(context);
//        if (viewType == VIEW_TYPE_LOADING) {
//            servicesView.showProgress();
//        }
//        return contactView;
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
            List<Contact> userList = items;

            if (constraint != null && constraint.length() > 0) {
                List<Contact> filterList = new ArrayList<>();
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
            items = (List<Contact>) results.values;
            notifyDataSetChanged();
        }

    }

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
}
