package com.keys.chats.chats;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.chats.ChattingActivity;
import com.keys.chats.base.RecyclerViewAdapterBase;
import com.keys.chats.base.ViewWrapper;
import com.keys.chats.chat.ChatActivity_;
import com.keys.chats.model.Group;
import com.keys.chats.model.RecentRealm;
import com.keys.chats.utilities.Constant;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import io.realm.Realm;
import io.realm.RealmResults;

@EBean
class ChatsRecyclerViewAdapter extends RecyclerViewAdapterBase<Group, chatView> {

    @RootContext
    Context context;

    private Realm realm;

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @Override
    public void onBindViewHolder(ViewWrapper<chatView> holder, final int position) {
        final chatView view = holder.getView();
//        if (view.progressBar.getVisibility() == View.GONE) {
        final Group s = items.get(position);

        RealmResults<RecentRealm> results = getRealm().where(RecentRealm.class).equalTo("groupId", s.getObjectId()).findAll();
        if (!results.isEmpty()) {
            for (RecentRealm recent : results) {
                view.recent.setText(recent.getLastMessage());
            }
        } else
            view.recent.setText("");
        getUnseen(s.getObjectId(), view.unseen);
        view.bind(s);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity_.intent(context).group(s).start();
            }
        });
//        }
    }

    @Override
    protected chatView onCreateItemView(ViewGroup parent, int viewType) {
//        chatView chatView = chatView_.build(context);
//        if (viewType == VIEW_TYPE_LOADING) {
//            servicesView.showProgress();
//        }
//        return chatView;
        return chatView_.build(context);
    }

    private void getUnseen(String groupId, final TextView unseen) {
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_UNSEEN)
                .child(groupId).child(ChattingActivity.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String num = "" + dataSnapshot.child("counter").getValue();
                        if (!num.equals("0") && !num.equals("null")) {
                            unseen.setVisibility(View.VISIBLE);
                            unseen.setText(num);
                        } else {
                            unseen.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
