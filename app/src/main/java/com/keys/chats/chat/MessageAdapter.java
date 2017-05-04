package com.keys.chats.chat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.keys.Utils;
import com.keys.chats.ChattingActivity;
import com.keys.chats.model.Message;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import me.himanshusoni.chatmessageview.ChatMessageView;
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
    public ClickListenerChatFirebase mClickListenerChatFirebase;
    private Context context;
    private MediaPlayer mMediaPlayer;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;
    private MediaPlayer mediaPlayer;
    private static final int TIMER_FREQ = 1000;
    private int lastPosition = -1;
    RecyclerView recyclerView;
    private ProgressDialog pd;

    MessageAdapter(RecyclerView recyclerView, Context context, List<Message> messageList, ClickListenerChatFirebase mClickListenerChatFirebase) {
        this.recyclerView = recyclerView;
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
        } else if (viewType == TYPE_AUDIO ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
            return new viewHolderAudio(itemView);
        } else if (viewType == TYPE_AUDIO_RIGHT ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_right, parent, false);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        viewHolder.setIsRecyclable(false);
        String time = (String) converteTimestamp(String.valueOf((long) messageList.get(position).getCreatedAt()));
        switch (viewHolder.getItemViewType()) {
            case TYPE_MESSAGE:
                final MessageAdapter.viewHolder holder = (MessageAdapter.viewHolder) viewHolder;
                holder.messageTextView.setText(messageList.get(position).getText());
//                String name = messageList.get(position).getSenderName().substring(0,
//                        messageList.get(position).getSenderName().indexOf("@"));
                holder.messengerTextView.setText(Utils.getName(messageList.get(position).getSenderName()));
                holder.messageDate.setText(time);
                break;
            case TYPE_MESSAGE_RIGHT:
                MessageAdapter.viewHolder holderR = (MessageAdapter.viewHolder) viewHolder;
                holderR.messageTextView.setText(messageList.get(position).getText());
                holderR.messengerTextView.setText(Utils.getName(messageList.get(position).getSenderName()));
                holderR.messageDate.setText(time);
                break;
            case TYPE_IMG:
                MessageAdapter.viewHolderImg holderImg = (MessageAdapter.viewHolderImg) viewHolder;
                holderImg.messengerTextView.setText(Utils.getName(messageList.get(position).getSenderName()));
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
                holderVideo.messengerTextView.setText(Utils.getName(messageList.get(position).getSenderName()));
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
                            VideoPlayerActivity_.intent(context).url(messageList.get(position).getVideo()).start();

                        }
                    });
                }
                break;
            case TYPE_IMG_RIGHT:
                MessageAdapter.viewHolderImg holderImgR = (MessageAdapter.viewHolderImg) viewHolder;
                holderImgR.messengerTextView.setText(Utils.getName(messageList.get(position).getSenderName()));
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
                holderVideoR.messengerTextView.setText(Utils.getName(messageList.get(position).getSenderName()));
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
                            VideoPlayerActivity_.intent(context).url(messageList.get(position).getVideo()).start();
//                            Intent i = new Intent(context, VideoPlayerActivity.class);
//                            i.putExtra("url", messageList.get(position).getVideo());
//                            context.startActivity(i);
                        }
                    });
                }
                break;
            case TYPE_AUDIO:
                final MessageAdapter.viewHolderAudio holderAudio = (MessageAdapter.viewHolderAudio) viewHolder;
                if (messageList.get(position).getType().equals(ChatActivity.AUDIO)) {
                    holderAudio.contentMessageChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            AudioWife.getInstance().release();
                            mClickListenerChatFirebase.clickVoiceMessage(holderAudio, position, messageList.get(position).getAudio(),
                                    "" + messageList.get(position).getAudioDuration());
                            //   getMedia(messageList.get(position).getAudio(),""+messageList.get(position).getAudioDuration(),holderAudio);
                        }
                    });
                }
                break;
            case TYPE_AUDIO_RIGHT:
                final MessageAdapter.viewHolderAudio holderAudioR = (MessageAdapter.viewHolderAudio) viewHolder;
                if (messageList.get(position).getType().equals(ChatActivity.AUDIO)) {
                    holderAudioR.contentMessageChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          /*  String url = messageList.get(position).getAudio();
                            int currentPos = position;
                            stopPlaying();
                            for (int pos = 0; pos <messageList.size(); pos++) {
                                // Utils.showCustomToast(context,"lll");
//                                Log.i("/////*",pos+"");
//                                Log.i("/////* messageList",messageList.size()+"");
                                try {
                                    if (messageList.get(pos).getType().equals(ChatActivity.AUDIO)) {
                                        if (pos != position) {
                                            RecyclerView.ViewHolder holder1
                                               = recyclerView.getChildViewHolder(recyclerView.getChildAt(pos));
                                            if (holder1.getItemViewType() == TYPE_AUDIO_RIGHT || holder1.getItemViewType() == TYPE_AUDIO) {
                                                MessageAdapter.viewHolderAudio holder = (MessageAdapter.viewHolderAudio) holder1;
                                                holder.mPlayMedia.setImageResource(R.drawable.ic_play_);
                                                holder.mMediaSeekBar.setProgress(0);
                                                holder.mPlayMedia.setVisibility(View.VISIBLE);
                                                holder.mPauseMedia.setVisibility(View.GONE);
                                                holder.mMediaSeekBar.setVisibility(View.GONE);
                                                Utils.showCustomToast(context,"ssss");
                                            }
                                        } else {
                                            // Utils.showCustomToast(context,"lll 99");
                                            RecyclerView.ViewHolder holder1
                                            = recyclerView.getChildViewHolder(recyclerView.getChildAt(position));
                                        Log.i("////*",holder1.getItemViewType() +"");
                                            if (holder1.getItemViewType() == TYPE_AUDIO_RIGHT || holder1.getItemViewType() == TYPE_AUDIO) {
                                                MessageAdapter.viewHolderAudio holder = (MessageAdapter.viewHolderAudio) holder1;
                                                holder.mMediaSeekBar.setVisibility(View.VISIBLE);
                                                holder.mPlayMedia.setVisibility(View.GONE);
                                                holder.mPauseMedia.setVisibility(View.VISIBLE);
                                                holder.mMediaSeekBar.setProgress(0);
                                                Utils.showCustomToast(context,"aaaa");
                                            }
                                        }
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                            mediaPlayer = new MediaPlayer();
                            pd = new ProgressDialog(context);
                            pd.show();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    return false;
                                }
                            });
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    if (pd.isShowing())
                                    pd.dismiss();
                                    mediaPlayer.start();
                                    holderAudioR.mPlayMedia.setVisibility(View.GONE);
                                    holderAudioR.mPauseMedia.setVisibility(View.VISIBLE);
                                    holderAudioR.mMediaSeekBar.setProgress(0);
                                    holderAudioR.mMediaSeekBar.setMax((int) messageList.get(position).getAudioDuration());
                                    holderAudioR.mTotalTime.setText("" + Utils.milliSecondsToTimer(mediaPlayer.getDuration()));
                                    Timer progressBarAdvancer = new Timer();
                                    progressBarAdvancer.scheduleAtFixedRate(new TimerTask() {
                                        public void run() {
                                            if (mediaPlayer != null) {
                                                try {
                                                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                                    holderAudioR.mMediaSeekBar.setProgress(mCurrentPosition);
                                                }catch (Exception e){
                                                    holderAudioR.mMediaSeekBar.setProgress(0);
                                                }

                                            }
                                        }
                                    }, 0, TIMER_FREQ);
                                }
                            });
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
//                    holder.mPlayMedia.setImageResource(R.drawable.ic_play_);
                                    holderAudioR.mPlayMedia.setVisibility(View.VISIBLE);
                                    holderAudioR.mPauseMedia.setVisibility(View.GONE);
                                    holderAudioR.mMediaSeekBar.setProgress(0);
                                    holderAudioR.mMediaSeekBar.setMax(0);
                                    //killMediaPlayer();
                                }
                            });
                            try {
                                mediaPlayer.setDataSource(url);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.prepareAsync();*/
                          //  AudioWife.getInstance().release();
                            mClickListenerChatFirebase.clickVoiceMessage(holderAudioR, position,messageList.get(position).getAudio(),
                                    ""+messageList.get(position).getAudioDuration());
                        }
                    });
                    holderAudioR.mPauseMedia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            stopPlaying();
//                            if (mediaPlayer != null ) {
//                                mediaPlayer.pause();
                            holderAudioR.mPlayMedia.setVisibility(View.VISIBLE);
                            holderAudioR.mPauseMedia.setVisibility(View.GONE);
                            holderAudioR.mMediaSeekBar.setProgress(0);
//                                holderAudioR.mPauseMedia.setImageResource(R.drawable.ic_pause);
//                                holderAudioR.mPlayMedia.setImageResource(R.drawable.ic_play_);
//                            }
                        }
                    });
                    holderAudioR.mMediaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (mediaPlayer != null && fromUser) {
                                mediaPlayer.seekTo(progress * 1000);
                            }
                        }
                    });
                }

                break;

        }


        // write this message to the on-device index
        FirebaseAppIndex.getInstance().update(getMessageIndexable(messageList.get(position)));

        // log a view action on it
        FirebaseUserActions.getInstance().end(getMessageViewAction(messageList.get(position)));
    }

    private void getMedia(String audio, String duration, viewHolderAudio holderAudioR) {
        new LoadVideoThumbnail(holderAudioR).execute(audio, duration);

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

    public class viewHolderAudio extends RecyclerView.ViewHolder {
        private RelativeLayout audio_play;
        private ChatMessageView contentMessageChat;
        public ImageView mPlayMedia;
        public ImageView mPauseMedia;
        public SeekBar mMediaSeekBar;
        public TextView mRunTime;
        public TextView mTotalTime;

        viewHolderAudio(final View v) {
            super(v);
            contentMessageChat = (ChatMessageView) v.findViewById(R.id.contentMessageChat);
            audio_play = (RelativeLayout) v.findViewById(R.id.audio_play);
            mPlayMedia = (ImageView) v.findViewById(R.id.play_icon);
            mPauseMedia = (ImageView) v.findViewById(R.id.mPauseMedia);
            mMediaSeekBar = (SeekBar) v.findViewById(R.id.media_seekbar);
            mTotalTime = (TextView) v.findViewById(R.id.period_time);
            mPauseMedia.setVisibility(View.GONE);
            mMediaSeekBar.setVisibility(View.GONE);
            mTotalTime.setVisibility(View.GONE);
//            mPlayMedia = v.findViewById(R.id.play);
//            mPauseMedia = v.findViewById(R.id.pause);
//            mMediaSeekBar = (SeekBar) v.findViewById(R.id.media_seekbar);
//            mRunTime = (TextView) v.findViewById(R.id.run_time);
//            mTotalTime = (TextView) v.findViewById(R.id.total_time);
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

    public void playAudio(String audio, final double duration, final viewHolderAudio holder) {
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//                killMediaPlayer();
//                holder.mPlayMedia.setImageResource((R.drawable.ic_play_));
//            }
//        } else {
//            holder.mMediaSeekBar.setProgress(0);
//            holder.mMediaSeekBar.setMax((int)duration);
        mediaPlayer = new MediaPlayer();
//            holder.mPlayMedia.setImageResource(R.drawable.ic_pause);
        pd = new ProgressDialog(context);
        pd.show();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pd.dismiss();
                mediaPlayer.start();
                holder.mPlayMedia.setVisibility(View.GONE);
                holder.mPauseMedia.setVisibility(View.VISIBLE);
//                    holder.mPauseMedia.setImageResource(R.drawable.ic_pause);
//                    holder.mPlayMedia.setImageResource(R.drawable.ic_pause);
                holder.mMediaSeekBar.setProgress(0);
                holder.mMediaSeekBar.setMax((int) duration);
                holder.mTotalTime.setText("" + Utils.milliSecondsToTimer(mediaPlayer.getDuration()));
                Timer progressBarAdvancer = new Timer();
                progressBarAdvancer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        if (mediaPlayer != null) {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            holder.mMediaSeekBar.setProgress(mCurrentPosition);
                        }
//                holder.mMediaSeekBar.setProgress(holder.mMediaSeekBar.getProgress() + TIMER_FREQ);
                    }
                }, 0, TIMER_FREQ);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                    holder.mPlayMedia.setImageResource(R.drawable.ic_play_);
                holder.mPlayMedia.setVisibility(View.VISIBLE);
                holder.mPauseMedia.setVisibility(View.GONE);
                holder.mMediaSeekBar.setProgress(0);
                holder.mMediaSeekBar.setMax(0);
                //killMediaPlayer();
            }
        });
        try {
            mediaPlayer.setDataSource(audio);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();

//        }
    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LoadVideoThumbnail extends AsyncTask<String, String, Boolean> {
        viewHolderAudio holderAudioR;
        String audio = "";
        String duration = "";

        public LoadVideoThumbnail(viewHolderAudio holderAudioR) {
            this.holderAudioR = holderAudioR;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            mMediaPlayer = new MediaPlayer();
            try {
                audio = params[0];
                duration = params[1];
                Log.e("-----", params[0] + " aaa");
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(context, Uri.parse(params[0]));

                if (mMediaPlayer == null) {
                    return false;
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                holderAudioR.mMediaSeekBar.setProgress(0);
                holderAudioR.mMediaSeekBar.setMax((int) (Double.parseDouble(duration)));
                holderAudioR.mTotalTime.setText("" + Utils.milliSecondsToTimer((long) (Double.parseDouble(duration))));
//                playAudio(audio,duration,holderAudioR);
            } else {
                holderAudioR.mTotalTime.setText(duration);
            }
        }

    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
