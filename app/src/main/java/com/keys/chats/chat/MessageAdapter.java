package com.keys.chats.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.keys.MyApplication;
import com.keys.R;
import com.keys.chats.ChattingActivity;
import com.keys.chats.model.Message;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.changer.audiowife.AudioWife;

class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messageList;
    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_MESSAGE_RIGHT = 4;
    private static final int TYPE_IMG = 1;
    private static final int TYPE_IMG_RIGHT = 5;
    private static final int TYPE_AUDIO = 2;
    private static final int TYPE_AUDIO_RIGHT = 6;
    private static final int TYPE_VIDEO = 3;
    private static final int TYPE_VIDEO_RIGHT = 7;
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    private ClickListenerChatFirebase mClickListenerChatFirebase;
    private Context context;

    MessageAdapter(Context context, List<Message> messageList, ClickListenerChatFirebase mClickListenerChatFirebase) {
        this.messageList = messageList;
//        this.mUsername = mUsername;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
        this.context = context;
    }

//    public void getTopList(RecyclerView recyclerView){
//        int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ?
//                0 : recyclerView.getChildAt(0).getTop();
//        LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();
//        int firstVisibleItem = linearLayoutManager1.findFirstVisibleItemPosition();
//        if (userScrolled && !loading && firstVisibleItem == 0 && topRowVerticalPosition >= 0) {
//            //Is this the place where top position of first item is reached ?
//            if (mChatRecyclerAdapter.getItemCount() > 10) {
//                loadMore.setVisibility(View.VISIBLE);
//                loading = true;
//                mChatPresenter = new ChatPresenter(ChatActivity.this);
//                mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                        receiverId, MORE_ITEM + mChatRecyclerAdapter.getItemCount(), 1);
//
//            }}
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_IMG) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img, parent, false);
            return new viewHolderImg(itemView);
        } else if (viewType == TYPE_VIDEO) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img, parent, false);
            return new viewHolderImg(itemView);
        } else if (viewType == TYPE_AUDIO || viewType == TYPE_AUDIO_RIGHT) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
            return new viewHolderAudio(itemView);
        } else if (viewType == TYPE_MESSAGE_RIGHT) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new viewHolder(itemView);
        } else if (viewType == TYPE_IMG_RIGHT) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img_right, parent, false);
            return new viewHolderImg(itemView);
        } else if (viewType == TYPE_VIDEO_RIGHT) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img_right, parent, false);
            return new viewHolderImg(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new viewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        viewHolder.setIsRecyclable(false);
        String time = (String) converteTimestamp(String.valueOf((long) messageList.get(position).getCreatedAt()));
        switch (viewHolder.getItemViewType()) {
            case TYPE_MESSAGE:
                MessageAdapter.viewHolder holder = (MessageAdapter.viewHolder) viewHolder;
                holder.messageTextView.setText(messageList.get(position).getText());
//                String name = messageList.get(position).getSenderName().substring(0,
//                        messageList.get(position).getSenderName().indexOf("@"));
                holder.messengerTextView.setText(messageList.get(position).getSenderName());
                holder.messageDate.setText(time);
                break;
            case TYPE_MESSAGE_RIGHT:
                MessageAdapter.viewHolder holderR = (MessageAdapter.viewHolder) viewHolder;
                holderR.messageTextView.setText(messageList.get(position).getText());
                holderR.messengerTextView.setText(messageList.get(position).getSenderName());
                holderR.messageDate.setText(time);
                break;
            case TYPE_IMG:
                MessageAdapter.viewHolderImg holderImg = (MessageAdapter.viewHolderImg) viewHolder;
                holderImg.messengerTextView.setText(messageList.get(position).getSenderName());
                holderImg.messageDate.setText(time);
                if (messageList.get(position).getType().equals(ChatActivity.IMG)) {
                    holderImg.tvIsLocation(View.GONE);
                    holderImg.setIvChatPhoto(messageList.get(position).getPicture());
                } else if (messageList.get(position).getType().equals(ChatActivity.MAP)) {
                    holderImg.tvIsLocation(View.VISIBLE);
                    String loc = local(messageList.get(position).getLatitude(), messageList.get(position).getLongitude());
                    holderImg.setIvChatPhoto(loc);
                }
                break;
            case TYPE_VIDEO:
                MessageAdapter.viewHolderImg holderVideo = (MessageAdapter.viewHolderImg) viewHolder;
                holderVideo.messengerTextView.setText(messageList.get(position).getSenderName());
                holderVideo.messageDate.setText(time);
                if (messageList.get(position).getType().equals(ChatActivity.VIDEO)) {
                    holderVideo.tvIsLocation(View.GONE);
                    Glide.with(holderVideo.img_chat.getContext()).load(messageList.get(position).getPicture())
                            .override(100, 100)
                            .fitCenter()
                            .into(holderVideo.img_chat);
                    holderVideo.img_chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, VideoPlayerActivity.class);
                            i.putExtra("url", messageList.get(position).getVideo());
                            context.startActivity(i);
                        }
                    });
                }
                break;
            case TYPE_IMG_RIGHT:
                MessageAdapter.viewHolderImg holderImgR = (MessageAdapter.viewHolderImg) viewHolder;
                holderImgR.messengerTextView.setText(messageList.get(position).getSenderName());
                holderImgR.messageDate.setText(time);
                if (messageList.get(position).getType().equals(ChatActivity.IMG)) {
                    holderImgR.tvIsLocation(View.GONE);
                    holderImgR.setIvChatPhoto(messageList.get(position).getPicture());
                } else if (messageList.get(position).getType().equals(ChatActivity.MAP)) {
                    holderImgR.tvIsLocation(View.VISIBLE);
                    String loc = local(messageList.get(position).getLatitude(), messageList.get(position).getLongitude());
                    holderImgR.setIvChatPhoto(loc);
                }
                break;
            case TYPE_VIDEO_RIGHT:
                MessageAdapter.viewHolderImg holderVideoR = (MessageAdapter.viewHolderImg) viewHolder;
                holderVideoR.messengerTextView.setText(messageList.get(position).getSenderName());
                holderVideoR.messageDate.setText(time);
                if (messageList.get(position).getType().equals(ChatActivity.VIDEO)) {
                    holderVideoR.tvIsLocation(View.GONE);
                    Glide.with(holderVideoR.img_chat.getContext()).load(messageList.get(position).getPicture())
                            .override(100, 100)
                            .fitCenter()
                            .into(holderVideoR.img_chat);
                    holderVideoR.img_chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, VideoPlayerActivity.class);
                            i.putExtra("url", messageList.get(position).getVideo());
                            context.startActivity(i);
                        }
                    });
                }
                break;
            case TYPE_AUDIO:
                MessageAdapter.viewHolderAudio holderAudio = (MessageAdapter.viewHolderAudio) viewHolder;
                if (messageList.get(position).getType().equals(ChatActivity.AUDIO)) {
                    // AudioWife takes care of click
                    // handler for play/pause button
                    AudioWife.getInstance()
                            .init(context, Uri.parse(messageList.get(position).getAudio()))
                            .setPlayView(holderAudio.mPlayMedia)
                            .setPauseView(holderAudio.mPauseMedia)
                            .setSeekBar(holderAudio.mMediaSeekBar)
                            .setRuntimeView(holderAudio.mRunTime)
                            .setTotalTimeView(holderAudio.mTotalTime);
                }
                break;

        }


        // write this message to the on-device index
        FirebaseAppIndex.getInstance().update(getMessageIndexable(messageList.get(position)));

        // log a view action on it
        FirebaseUserActions.getInstance().end(getMessageViewAction(messageList.get(position)));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    private static class viewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messengerTextView;
        TextView messageDate;
        CircleImageView messengerImageView;
        LinearLayout root;

        viewHolder(View v) {
            super(v);
            root = (LinearLayout) v.findViewById(R.id.root);
            messageTextView = (TextView) v.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) v.findViewById(R.id.messengerTextView);
            messageDate = (TextView) v.findViewById(R.id.messageDate);
            messengerImageView = (CircleImageView) v.findViewById(R.id.messengerImageView);
        }
    }

    private static class viewHolderAudio extends RecyclerView.ViewHolder {
        private View mPlayMedia;
        private View mPauseMedia;
        private SeekBar mMediaSeekBar;
        private TextView mRunTime;
        private TextView mTotalTime;

        viewHolderAudio(View v) {
            super(v);
            mPlayMedia = v.findViewById(R.id.play);
            mPauseMedia = v.findViewById(R.id.pause);
            mMediaSeekBar = (SeekBar) v.findViewById(R.id.media_seekbar);
            mRunTime = (TextView) v.findViewById(R.id.run_time);
            mTotalTime = (TextView) v.findViewById(R.id.total_time);
        }
    }

    private class viewHolderImg extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_chat;
        TextView tvLocation;
        TextView messengerTextView;
        TextView messageDate;

        viewHolderImg(View v) {
            super(v);
            img_chat = (ImageView) v.findViewById(R.id.img_chat);
            tvLocation = (TextView) v.findViewById(R.id.tvLocation);
            messengerTextView = (TextView) v.findViewById(R.id.messengerTextView);
            messageDate = (TextView) v.findViewById(R.id.messageDate);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Message model = messageList.get(position);
            if (model.getType().equals(ChatActivity.MAP)) {
                mClickListenerChatFirebase.clickImageMapChat(view, position, String.valueOf(model.getLatitude()),
                        String.valueOf(model.getLongitude()));
            } else if (model.getType().equals(ChatActivity.IMG)) {
                mClickListenerChatFirebase.clickImageChat(view, position, model.getSenderName(),
                        model.getPicture(), model.getPicture());
            }
        }

        void setIvChatPhoto(String url) {
            if (img_chat == null) return;
            Glide.clear(img_chat);
            Glide.with(img_chat.getContext()).load(url)
                    .override(100, 100)
                    .fitCenter()
                    .into(img_chat);
            img_chat.setOnClickListener(this);
        }


        void tvIsLocation(int visible) {
            if (tvLocation == null) return;
            tvLocation.setVisibility(visible);
        }
    }

    private String local(double latitudeFinal, double longitudeFinal) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitudeFinal + ","
                + longitudeFinal + "&zoom=18&size=280x280&markers=color:red|" + latitudeFinal + "," + longitudeFinal;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(ChattingActivity.getUid()))
            switch (message.getType()) {
            case ChatActivity.IMG:
                return TYPE_IMG_RIGHT;
            case ChatActivity.MAP:
                return TYPE_IMG_RIGHT;
            case ChatActivity.AUDIO:
                return TYPE_AUDIO_RIGHT;
            case ChatActivity.VIDEO:
                return TYPE_VIDEO_RIGHT;
            case ChatActivity.TEXT:
                return TYPE_MESSAGE_RIGHT;
            default:
                return TYPE_MESSAGE_RIGHT;
        }
        else
            switch (message.getType()) {
                case ChatActivity.IMG:
                    return TYPE_IMG;
                case ChatActivity.MAP:
                    return TYPE_IMG;
                case ChatActivity.AUDIO:
                    return TYPE_AUDIO;
                case ChatActivity.VIDEO:
                    return TYPE_VIDEO;
                case ChatActivity.TEXT:
                    return TYPE_MESSAGE;
                default:
                    return TYPE_MESSAGE;
            }
    }


    private Action getMessageViewAction(Message message) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(message.getSenderName(), MESSAGE_URL.concat(message.getSenderId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(Message message) {
        String mUsername = MyApplication.myPrefs.user().get();
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername.equals(message.getSenderName()))
                .setName(message.getSenderName())
                .setUrl(MESSAGE_URL.concat(message.getSenderId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(message.getSenderId() + "/recipient"));

        return Indexables.messageBuilder()
                .setName(message.getText())
                .setUrl(MESSAGE_URL.concat(message.getSenderId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();
    }

    private static CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }
}
