package com.keys.chats.chat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.CameraVideoPicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.keys.MyApplication;
import com.keys.R;
import com.keys.activity.LoginActivity;
import com.keys.chats.ChattingActivity;
import com.keys.chats.model.Group;
import com.keys.chats.model.GroupRealm;
import com.keys.chats.model.Members;
import com.keys.chats.model.Message;
import com.keys.chats.model.Recent;
import com.keys.chats.model.RecentRealm;
import com.keys.chats.model.User;
import com.keys.chats.utilities.Constant;
import com.onesignal.OneSignal;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import vn.tungdx.mediapicker.MediaItem;
import vn.tungdx.mediapicker.MediaOptions;
import vn.tungdx.mediapicker.activities.MediaPickerActivity;

@EActivity(R.layout.activity_chat)
public class ChatActivity extends AppCompatActivity implements ImagePickerCallback, VideoPickerCallback {

    private static final String TAG = "ChattingActivity";
    public static final String URL_STORAGE_REFERENCE = "gs://keysapp-e17cf.appspot.com/";
    public static final String FOLDER_STORAGE_IMG = "images";
    public static final String FOLDER_STORAGE_VIDEO = "videos";
    public static final String FOLDER_STORAGE_RECORD = "audio";
    public static final String AUDIO = "audio";
    public static final String IMG = "img";
    public static final String TEXT = "text";
    public static final String VIDEO = "video";
    public static final String MAP = "map";

    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private final static int EXTERNAL_STORAGE_PERMISSION_REQUEST = 100;
    private static final int REQUEST_MEDIA = 200;
    private static final int CAPTURE_MEDIA = 368;
    private static final int REQUEST_RECORD_AUDIO = 0;
    private static final int REQUEST_INVITE = 1;
    private static final int PLACE_PICKER_REQUEST = 3;

    @ViewById(R.id.toolbar)
    LinearLayout toolbar;
    @ViewById(R.id.title_toolbar)
    TextView title_toolbar;
    @ViewById(R.id.typing_toolbar)
    TextView typing_toolbar;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;
    @ViewById(R.id.messageRecyclerView)
    RecyclerView mMessageRecyclerView;
    @ViewById(R.id.messageEditText)
    EditText mMessageEditText;
    @ViewById(R.id.sendButton)
    ImageView mSendButton;

    ListPopupWindow popupWindowList;
    private ProgressDialog pDialog;

    String path = "null", pickerPath, pickerPathV, groupId, recentId;
    boolean userScrolled;
    int page;

    @Extra
    User user;

    @Extra
    Group group;

    private ImagePicker imagePicker;
    private CameraImagePicker cameraPicker;
    private VideoPicker videoPicker;
    private CameraVideoPicker cameraPickerV;

    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter adapter;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseStorage storage;

    private Realm realm;
    private List<Message> result = new ArrayList<>();
    private boolean isExist=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        realm = Realm.getDefaultInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            RealmResults<GroupRealm> results = realm.where(GroupRealm.class)
                    .equalTo("members.memberId", user.getObjectId())
                    .equalTo("members.memberId", ChattingActivity.getUid())
                    .findAll();
            if (results.isEmpty())
                writeNewGroup();
            else
                for (GroupRealm group : results) {
                    if (group.getMembers().size() == 2) {
                        groupId = group.getObjectId();
                    }
                }
        } else if (group != null) {
            groupId = group.getObjectId();
            for (int i = 0; i < group.getMembers().size(); i++) {
                getAllUsersFromFirebase(group.getMembers().get(i));
            }
        }
        mFirebaseDatabaseReference.child(Constant.TABLE_UNSEEN).child(groupId)
                .child(ChattingActivity.getUid()).child("counter").setValue(0);
        mFirebaseDatabaseReference.child(Constant.TABLE_ACTIVE).child(groupId)
                .child(ChattingActivity.getUid()).setValue(true);
    }

    @AfterViews
    void afterView() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        inintDialog();
        if (firebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        typing_toolbar.setVisibility(View.GONE);
        if (user != null)
            title_toolbar.setText(user.getFullName());
        else
            title_toolbar.setText(group.getName());

        askForPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CAMERA_PERMISSIONS);
        requestExternalStoragePermission();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        adapter = new MessageAdapter(ChatActivity.this, result, new ClickListenerChatFirebase() {
            @Override
            public void clickImageChat(View view, int position, String nameUser,
                                       String urlPhotoUser, String urlPhotoClick) {
                FullScreenImageActivity_.intent(ChatActivity.this)
                        .nameUser(nameUser).urlPhotoUser(urlPhotoClick).start();
            }

            @Override
            public void clickImageMapChat(View view, int position, String latitude, String longitude) {
//                String uriBegin = "geo:" + latitude + "," + longitude;
//                String query = latitude + "," + longitude;
//                String encodedQuery = Uri.encode(query);
//                String uri = uriBegin + "?q=" + encodedQuery + "&z=16";
//                String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Float.parseFloat(latitude), Float.parseFloat(longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        };
        adapter.registerAdapterDataObserver(observer);
        mMessageRecyclerView.setAdapter(adapter);

        getAllMessageFromFirebase();


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = mMessageEditText.getText().toString();
                recentId = getRecentID();
                String messageId = getMessageID();
                long time = System.currentTimeMillis();
                Message message = new Message(groupId, ChattingActivity.getUid(), MyApplication.myPrefs.user().get(),
                        messageText, time, messageId, TEXT);
                mMessageEditText.setText("");
                postSendNotification(getString(R.string.you_have_text_messsage));
                addMessageObj(message, messageId);
                addRecentMessage(messageText, TEXT);
            }
        });

        typingListener();

        mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                userScrolled = newState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ?
                        0 : recyclerView.getChildAt(0).getTop();
                assert recyclerView != null;
                LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItem = linearLayoutManager1.findFirstVisibleItemPosition();
                if (userScrolled && firstVisibleItem == 0 && topRowVerticalPosition >= 0) {
                    RealmResults<Message> resultMessages = realm.where(Message.class).equalTo("groupId", groupId).findAll();
                    if (resultMessages.size() > 10)
                        getItems();
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    public void getAllMessageFromFirebase() {
        final List<String> groupIDList = new ArrayList<>();
        Query query = mFirebaseDatabaseReference.child(Constant.TABLE_MESSAGE).child(groupId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        realm.where(Message.class).equalTo("groupId", groupId).findAll().deleteAllFromRealm();
//                    }
//                });
//                result.clear();
                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                    try {
                        JSONObject jUserData = new JSONObject((Map) dataSnapshotChild.getValue());
                        String groupId = jUserData.optString("groupId");
                        String objectId = jUserData.optString("objectId");
                        String senderName = jUserData.optString("senderName");
                        String userId = jUserData.optString("senderId");
                        String text = jUserData.optString("text");
                        String type = jUserData.optString("type");
                        String picture = jUserData.optString("picture");
                        String video = jUserData.optString("video");
                        String audio = jUserData.optString("audio");
                        double createdAt = jUserData.optDouble("createdAt");
                        double latitude = jUserData.optDouble("latitude");
                        double longitude = jUserData.optDouble("longitude");
                        double picture_height = jUserData.optDouble("pictureHeight");
                        double picture_width = jUserData.optDouble("pictureWidth");
                        double video_duration = jUserData.optDouble("videoDuration");
                        double audio_duration = jUserData.optDouble("audioDuration");

                        Message msg = null;
                        switch (type) {
                            case TEXT:
                                msg = new Message(groupId, userId, senderName, text, createdAt, objectId, type);
                                break;
                            case IMG:
                                msg = new Message(picture, picture_height, picture_width, userId, senderName,
                                        type, createdAt, groupId, objectId);
                                break;
                            case MAP:
                                msg = new Message(groupId, userId, senderName, latitude, longitude, createdAt, objectId, type);
                                break;
                            case AUDIO:
                                msg = new Message(audio, audio_duration, createdAt, groupId, objectId, userId, senderName, type);
                                break;
                            case VIDEO:
                                msg = new Message(createdAt, groupId, objectId, userId, senderName, type, video, video_duration);
                                break;
                        }

                        RealmResults<Message> resultMessages = realm.where(Message.class).equalTo("groupId", groupId).findAll();
                        for (int i = 0; i < resultMessages.size(); i++) {
                            groupIDList.add(resultMessages.get(i).getObjectId());
                        }
                        if (!groupIDList.contains(objectId)) {
                            Log.e("realm.isInTransaction()", "on " + realm.isInTransaction());
                            if (!realm.isInTransaction())
                                realm.beginTransaction();
                            Message message = realm.createObject(Message.class);
                            message.setGroupId(groupId);
                            message.setObjectId(objectId);
                            message.setSenderId(userId);
                            message.setSenderName(senderName);
                            message.setCreatedAt(createdAt);
                            message.setType(type);
                            message.setText(text);
                            message.setPicture(picture);
                            message.setPictureHeight(picture_height);
                            message.setPictureWidth(picture_width);
                            message.setVideoDuration(video_duration);
                            message.setVideo(video);
                            message.setLatitude(latitude);
                            message.setLongitude(longitude);
                            message.setAudio(audio);
                            message.setAudioDuration(audio_duration);
                            if (msg != null) {
                                message.setDeleted(msg.getDeleted());
                                message.setSenderInitials(msg.getSenderInitials());
                                message.setStatus(msg.getStatus());
                                message.setUpdatedAt(msg.getUpdatedAt());
                            }
                            realm.commitTransaction();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                }
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                page = 1;
                getItems();
                mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    private void getItems() {
        RealmResults<Message> resultMessages = realm.where(Message.class).equalTo("groupId", groupId)
                .findAllSorted("createdAt", Sort.DESCENDING);
//        result.clear();
        int limit = 10;
        int size = page * limit;

        if (size > resultMessages.size())
            size = resultMessages.size();
        for (int i = 0; i < size; i++) {
            if (!result.contains(resultMessages.get(i)))
                result.add(resultMessages.get(i));
        }

        if (page <= countPages())
            ++page;
        Collections.sort(result, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return Double.compare(o1.getCreatedAt(), o2.getCreatedAt());
            }
        });

        if (adapter != null) {
            adapter.notifyDataSetChanged();
            mMessageRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    public int countPages() {
        RealmResults<Message> resultMessages = realm.where(Message.class).equalTo("groupId", groupId)
                .findAllSorted("createdAt", Sort.ASCENDING);
        int limit = 10;
        int total = resultMessages.size();
        int pages = total / limit;
        if ((total % limit) > 0)
            pages += 1;
        return pages;
    }

    private void typingListener() {
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                    mFirebaseDatabaseReference.child(Constant.TABLE_TYPING).child(groupId).child(ChattingActivity.getUid()).setValue(true);

                } else {
                    mSendButton.setEnabled(false);
                    mFirebaseDatabaseReference.child(Constant.TABLE_TYPING).child(groupId).child(ChattingActivity.getUid()).setValue(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mFirebaseDatabaseReference.child(Constant.TABLE_TYPING).child(groupId).addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        if (user != null) {
                            if (dataSnapshot.getKey().equals(user.getObjectId())) {
                                boolean status = (boolean) dataSnapshot.getValue();
                                if (status) {
                                    typing_toolbar.setVisibility(View.VISIBLE);
                                } else
                                    typing_toolbar.setVisibility(View.GONE);
                            }
                        } else {
                            String userId = getUserId();
                            if (dataSnapshot.getKey().equals(userId)) {
                                boolean status = (boolean) dataSnapshot.getValue();
                                if (status) {
                                    typing_toolbar.setVisibility(View.VISIBLE);
                                } else
                                    typing_toolbar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addMessageObj(Message message, String messageId) {
        mFirebaseDatabaseReference.child(Constant.TABLE_MESSAGE).child(groupId).child(messageId).setValue(message);
        addUnseenMessage();
        if (!realm.isInTransaction())
            realm.beginTransaction();
        Message messageRealm = realm.createObject(Message.class);
        messageRealm.setGroupId(message.getGroupId());
        messageRealm.setObjectId(message.getObjectId());
        messageRealm.setSenderId(message.getSenderId());
        messageRealm.setSenderName(message.getSenderName());
        messageRealm.setCreatedAt(message.getCreatedAt());
        messageRealm.setType(message.getType());
        messageRealm.setText(message.getText());
        messageRealm.setPicture(message.getPicture());
        messageRealm.setPictureHeight(message.getPictureHeight());
        messageRealm.setPictureWidth(message.getPictureWidth());
        messageRealm.setVideoDuration(message.getVideoDuration());
        messageRealm.setVideo(message.getVideo());
        messageRealm.setLatitude(message.getLatitude());
        messageRealm.setLongitude(message.getLongitude());
        messageRealm.setAudio(message.getAudio());
        messageRealm.setAudioDuration(message.getAudioDuration());
        messageRealm.setDeleted(message.getDeleted());
        messageRealm.setSenderInitials(message.getSenderInitials());
        messageRealm.setStatus(message.getStatus());
        messageRealm.setUpdatedAt(message.getUpdatedAt());
        realm.commitTransaction();
        getItems();
    }

    private void addRecentMessage(String text, String type) {
        long time = System.currentTimeMillis();
        recentId = getRecentID();
        List<String> members = new ArrayList<>();
        if (user != null) {
            members.add(user.getObjectId());
            members.add(ChattingActivity.getUid());
        } else
            members.addAll(group.getMembers());

        if (user != null) {
            RealmResults<RecentRealm> results = realm.where(RecentRealm.class)
                    .equalTo("members.memberId", user.getObjectId())
                    .equalTo("members.memberId", ChattingActivity.getUid())
                    .findAll();
            if (!results.isEmpty()) {
                for (RecentRealm recent : results) {
                    recentId = recent.getObjectId();
                }
            }
        } else {
            RealmResults<RecentRealm> results = realm.where(RecentRealm.class)
                    .equalTo("groupId", group.getObjectId())
                    .findAll();
            if (!results.isEmpty()) {
                for (RecentRealm recent : results) {
                    recentId = recent.getObjectId();
                }
            }
        }
        String pic;
        if (user != null) {
            pic = user.getPicture();
        } else {
            pic = group.getPicture();
        }
        Recent rcnt = new Recent(members, groupId, text, recentId, pic, type, ChattingActivity.getUid(), time);
        mFirebaseDatabaseReference.child(Constant.TABLE_RECENT).child(recentId).setValue(rcnt);
        if (!realm.isInTransaction())
            realm.beginTransaction();
        RecentRealm recentRealm = realm.createObject(RecentRealm.class);
        recentRealm.setGroupId(rcnt.getGroupId());
        recentRealm.setLastMessage(rcnt.getLastMessage());
        recentRealm.setObjectId(rcnt.getObjectId());
        recentRealm.setPicture(rcnt.getPicture());
        recentRealm.setType(rcnt.getType());
        recentRealm.setUserId(rcnt.getUserId());
        recentRealm.setLastMessageDate(rcnt.getLastMessageDate());
        recentRealm.setUpdatedAt(rcnt.getUpdatedAt());
        recentRealm.setArchived(rcnt.getArchived());
        recentRealm.setDeleted(rcnt.getDeleted());
        recentRealm.setDescription(rcnt.getDescription());
        recentRealm.setInitials(rcnt.getInitials());
        recentRealm.setCounter(rcnt.getCounter());
        for (int i = 0; i < members.size(); i++) {
            Members memb = realm.createObject(Members.class);
            memb.setMemberId(members.get(i));
            recentRealm.getMembers().add(memb);
        }
        realm.commitTransaction();
    }

    private void addUnseenMessage() {
        mFirebaseDatabaseReference.child(Constant.TABLE_UNSEEN).child(groupId)
                .child(ChattingActivity.getUid()).child("counter").setValue(0);
        if (user != null)
            mFirebaseDatabaseReference.child(Constant.TABLE_UNSEEN).child(groupId).child(user.getObjectId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int counter = 1;
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.getKey().equals(user.getObjectId())) {
                                    counter = Integer.parseInt(dataSnapshot.child("counter").getValue() + "") + 1;
                                }
                            }
                            mFirebaseDatabaseReference.child(Constant.TABLE_UNSEEN)
                                    .child(groupId).child(user.getObjectId()).child("counter").setValue((counter));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        else {
            for (int i = 0; i < group.getMembers().size(); i++) {
                final String member = group.getMembers().get(i);
                mFirebaseDatabaseReference.child(Constant.TABLE_UNSEEN).child(groupId).child(member)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int counter = 1;
                                if (dataSnapshot.exists()) {
                                    if (!dataSnapshot.getKey().equals(ChattingActivity.getUid())) {
                                        counter = Integer.parseInt(dataSnapshot.child("counter").getValue() + "") + 1;
                                    }
                                }
                                if (!dataSnapshot.getKey().equals(ChattingActivity.getUid()))
                                    mFirebaseDatabaseReference.child(Constant.TABLE_UNSEEN)
                                            .child(groupId).child(member)
                                            .child("counter").setValue(counter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    private String getUserId() {
        String userId = "";
        if (!group.getMembers().isEmpty())
            for (int i = 0; i < group.getMembers().size(); i++) {
                if (!ChattingActivity.getUid().equals(group.getMembers().get(i))) {
                    userId = group.getMembers().get(i);
                }
            }
        return userId;
    }


    private void locationPlacesIntent() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void recordAudio() {
        String name = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString();
        path = Environment.getExternalStorageDirectory().getPath() + "/" + name + "_audio.wav";
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(path)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setRequestCode(REQUEST_RECORD_AUDIO)
                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(false)
                .setKeepDisplayOn(true)
                // Start recording
                .record();
    }

    private void openGallery() {
        MediaOptions.Builder builder = new MediaOptions.Builder();
        MediaOptions options = builder.canSelectBothPhotoVideo()
                .canSelectMultiPhoto(true).canSelectMultiVideo(true).build();
        if (options != null) {
            MediaPickerActivity.open(this, REQUEST_MEDIA, options);
        }
    }

    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        StorageReference storageRef = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE).child(FOLDER_STORAGE_IMG);
        for (ChosenImage chosen : images) {
            sendFileFirebase(storageRef, Uri.fromFile(new File(chosen.getThumbnailPath())));
        }
    }

    @Override
    public void onVideosChosen(List<ChosenVideo> files) {
        StorageReference storageRef = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE).child(FOLDER_STORAGE_VIDEO);
        for (ChosenVideo chosen : files) {
            sendFileVideoFirebase(storageRef, Uri.fromFile(new File(chosen.getOriginalPath())), chosen.getDuration());
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void requestExternalStoragePermission() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                    EXTERNAL_STORAGE_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void enableCamera() {
        AnncaConfiguration.Builder builder = new AnncaConfiguration.Builder(ChatActivity.this, CAPTURE_MEDIA);
        new Annca(builder.build()).launchCamera();
    }

    protected final void askForPermissions(String[] permissions, int requestCode) {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[permissionsToRequest.size()]), requestCode);
        }
    }

    void postSendNotification(final String contents) {
        try {
            String deviceToken = "";
            if (user != null) {
                deviceToken = user.getDeviceToken();
            } else {
                for (int i = 0; i < group.getMembers().size(); i++) {
                    if (!group.getMembers().get(i).equals(ChattingActivity.getUid())) {
                        RealmResults<User> realmResultsUser =
                                realm.where(User.class).equalTo("objectId", group.getMembers().get(i)).findAll();
                        if (i != (group.getMembers().size() - 1)) {
                           if (!checkIfExist(group.getMembers().get(i)))
                            deviceToken = realmResultsUser.get(i).getDeviceToken() + ",";
                        } else {
                            if (!checkIfExist(group.getMembers().get(i)))
                            deviceToken = realmResultsUser.get(i).getDeviceToken();
                        }
                    }
                }
            }
            OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + contents + "'}, 'include_player_ids': ['"
                            + deviceToken + "']}"),
                    new OneSignal.PostNotificationResponseHandler() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.e("OneSignalExample", "postNotification Success: " + response.toString());
                        }

                        @Override
                        public void onFailure(JSONObject response) {
                            Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfExist(final String memberId) {
        mFirebaseDatabaseReference.child(Constant.TABLE_ACTIVE).child(groupId)
                .child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("///",dataSnapshot.getValue()+"");
                Log.i("///",memberId+"");
//                if ((Boolean) dataSnapshot.getValue()){
//                    isExist=true;
//                    return;
//                }else  isExist=false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isExist=false;
            }
        });
        return isExist;
    }

    private void writeNewGroup() {
        List<String> members = new ArrayList<>();
        members.add(user.getObjectId());
        members.add(ChattingActivity.getUid());
        long time = System.currentTimeMillis();
        groupId = getGroupID();
        Group group = new Group(user.getFullName(), groupId, user.getPicture(), ChattingActivity.getUid(), time, time, members);
        mFirebaseDatabaseReference.child(Constant.TABLE_GROUP).child(groupId).setValue(group);
        if (!realm.isInTransaction())
            realm.beginTransaction();
        GroupRealm groupm = realm.createObject(GroupRealm.class);
        groupm.setName(user.getFullName());
        groupm.setObjectId(groupId);
        groupm.setUserId(ChattingActivity.getUid());
        for (int i = 0; i < members.size(); i++) {
            Members memb = realm.createObject(Members.class);
            memb.setMemberId(members.get(i));
            groupm.getMembers().add(memb);
        }
        realm.commitTransaction();
    }

    public String getGroupID() {
        int length = 24;
        Random random = new Random(System.currentTimeMillis());
        String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890-_";
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }

    public String getMessageID() {
        int length = 24;
        Random random = new Random(System.currentTimeMillis());
        String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890-_";
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }

    public String getRecentID() {
        int length = 24;
        Random random = new Random(System.currentTimeMillis());
        String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890-_";
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }

    public void sendFileFirebase(StorageReference storageReference, final Uri file) {
        showpDialog();
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_pic");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidepDialog();
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hidepDialog();
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    assert downloadUrl != null;
                    String messageId = getMessageID();
                    long time = System.currentTimeMillis();
                    String senderName;
                    if (user != null)
                        senderName = user.getFullName();
                    else
                        senderName = group.getName();
                    String senderId = ChattingActivity.getUid();
                    double picture_width = 100.0;
                    double picture_height = 100.0;
                    String picture = downloadUrl.toString();
                    Message message = new Message(picture, picture_height, picture_width, senderId, senderName,
                            IMG, time, groupId, messageId);
                    postSendNotification(getString(R.string.you_have_messsage));
                    addMessageObj(message, messageId);
                    addRecentMessage(getString(R.string.image_message), IMG);
                }
            });
        }
    }

    public void sendFileVideoFirebase(StorageReference storageReference, final Uri file, final double duration) {
        showpDialog();
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_video");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidepDialog();
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hidepDialog();
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    assert downloadUrl != null;
                    String messageId = getMessageID();
                    long time = System.currentTimeMillis();
                    String senderName;
                    if (user != null)
                        senderName = user.getFullName();
                    else
                        senderName = group.getName();
                    String senderId = ChattingActivity.getUid();
                    String video = downloadUrl.toString();
                    Message message = new Message(time, groupId, messageId, senderId, senderName, VIDEO, video, duration);
                    postSendNotification(getString(R.string.you_have_messsage));
                    addMessageObj(message, messageId);
                    addRecentMessage(getString(R.string.video_message), VIDEO);
                }
            });
        }
    }

    public void sendFileRecordFirebase(StorageReference storageReference, final File file) {
        showpDialog();
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString();
            StorageReference audioGalleryRef = storageReference.child(name + "_audio");
            UploadTask uploadTask = audioGalleryRef.putFile(Uri.fromFile(file));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    assert downloadUrl != null;
                    String messageId = getMessageID();
                    long time = System.currentTimeMillis();
                    String senderName;
                    if (user != null)
                        senderName = user.getFullName();
                    else
                        senderName = group.getName();
                    String senderId = ChattingActivity.getUid();

                    MediaPlayer mp = MediaPlayer.create(ChatActivity.this, Uri.fromFile(file));
                    long duration = mp.getDuration();
                    String seconds = String.valueOf((duration % 60000) / 1000);
                    Log.e("time", file.getPath() + "  " + seconds + "  " + duration);
                    mp.release();

                    Message message = new Message(downloadUrl.toString(), Double.parseDouble(seconds), time, groupId,
                            messageId, senderId, senderName, AUDIO);
                    postSendNotification(getString(R.string.you_have_record_messsage));
                    addMessageObj(message, messageId);
                    addRecentMessage(getString(R.string.audio_message), AUDIO);
                }
            });
        }
        hidepDialog();
    }

    @Click
    void back() {
        finish();
    }

    @Click
    void menu(View view) {
        openMenu(view);
    }

    private void openMenu(View view) {
        popupWindowList = new ListPopupWindow(ChatActivity.this);
        popupWindowList.setAnchorView(view);
        popupWindowList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        String[] list = {getString(R.string.camera), getString(R.string.gallery),
                getString(R.string.sendLocation)/*, getString(R.string.record)*/};
        ArrayAdapter<String> popAdapter = new ArrayAdapter<>(ChatActivity.this, R.layout.setting, list);
        popupWindowList.setAdapter(popAdapter);
        popupWindowList.setContentWidth(measureContentWidth(popAdapter));

        popupWindowList.setModal(true);
        popupWindowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        enableCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                    case 2:
                        locationPlacesIntent();
                        break;
//                    case 3:
//                        recordAudio();
//                        break;
                }
                popupWindowList.dismiss();
            }
        });
        popupWindowList.show();
    }

    private int measureContentWidth(ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(ChatActivity.this);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_INVITE:
                    Bundle payload = new Bundle();
                    payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");
                    break;
                case Picker.PICK_IMAGE_DEVICE:
                    if (imagePicker == null) {
                        imagePicker = new ImagePicker(this);
                        imagePicker.allowMultiple();
                        imagePicker.pickImage();
                        imagePicker.setImagePickerCallback(this);
                    }
                    imagePicker.submit(data);
                    break;
                case Picker.PICK_IMAGE_CAMERA:
                    if (cameraPicker == null) {
                        cameraPicker = new CameraImagePicker(this);
                        cameraPicker.setImagePickerCallback(this);
                        cameraPicker.reinitialize(pickerPath);
                    }
                    cameraPicker.submit(data);
                    break;
                case REQUEST_RECORD_AUDIO:
                    StorageReference storageRefR
                            = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE).child(FOLDER_STORAGE_RECORD);
                    File file = new File(path);
                    if (file.exists())
                        sendFileRecordFirebase(storageRefR, file);
                    break;
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    if (place != null) {
                        LatLng latLng = place.getLatLng();
                        String messageId = getMessageID();
                        long time = System.currentTimeMillis();
                        Message message = new Message(groupId, ChattingActivity.getUid(),MyApplication.myPrefs.user().get(),
                                latLng.latitude, latLng.longitude, time, messageId, MAP);
                        postSendNotification(getString(R.string.you_have_messsage));
                        addMessageObj(message, messageId);
                        addRecentMessage(getString(R.string.location_message), MAP);
                    }
                    break;
                case Picker.PICK_VIDEO_DEVICE:
                    if (videoPicker == null) {
                        videoPicker = new VideoPicker(this);
                        videoPicker.setVideoPickerCallback(this);
                    }
                    videoPicker.submit(data);
                    break;
                case Picker.PICK_VIDEO_CAMERA:
                    if (cameraPickerV == null) {
                        cameraPickerV = new CameraVideoPicker(this, pickerPathV);
                    }
                    cameraPickerV.submit(data);
                    break;
                case REQUEST_MEDIA:
                    List<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
                    StorageReference storageRef = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE).child(FOLDER_STORAGE_IMG);
                    if (mMediaSelectedList != null) {
                        showpDialog();
                        for (MediaItem mediaItem : mMediaSelectedList) {
                            if (mediaItem.getPathOrigin(ChatActivity.this).endsWith(".png")
                                    || mediaItem.getPathOrigin(ChatActivity.this).endsWith(".jpeg")
                                    || mediaItem.getPathOrigin(ChatActivity.this).endsWith(".jpg")) {
                                sendFileFirebase(storageRef, Uri.fromFile(new File(mediaItem.getPathOrigin(ChatActivity.this))));
                            } else {
                                MediaPlayer mp = MediaPlayer.create(ChatActivity.this, mediaItem.getUriOrigin());
                                long duration = mp.getDuration();
                                String seconds = String.valueOf((duration % 60000) / 1000);
                                mp.release();
                                sendFileVideoFirebase(storageRef,
                                        Uri.fromFile(new File(mediaItem.getPathOrigin(ChatActivity.this))),
                                        Double.parseDouble(seconds));
                            }
                        }
                        hidepDialog();
                    }
                    break;
                case CAPTURE_MEDIA:
                    if (data.hasExtra(AnncaConfiguration.Arguments.FILE_PATH)) {
                        showpDialog();
                        StorageReference storageRef2 = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE).child(FOLDER_STORAGE_IMG);
                        String path = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
                        if (path.endsWith(".jpg"))
                            sendFileFirebase(storageRef2, Uri.fromFile(new File(path)));
                        else if (path.endsWith(".mp4")) {
                            MediaPlayer mp = MediaPlayer.create(ChatActivity.this, Uri.fromFile(new File(path)));
                            long duration = mp.getDuration();
                            String seconds = String.valueOf((duration % 60000) / 1000);
                            mp.release();
                            hidepDialog();
                            sendFileVideoFirebase(storageRef2,
                                    Uri.fromFile(new File(path)),
                                    Double.parseDouble(seconds));
                        }
                        hidepDialog();
                    }
                    break;
            }
        } else {
            if (requestCode == REQUEST_INVITE) {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (result.isEmpty()) {
            mFirebaseDatabaseReference.child(Constant.TABLE_MESSAGE).child(groupId).removeValue();
            mFirebaseDatabaseReference.child(Constant.TABLE_TYPING).child(groupId).removeValue();

            typing_toolbar.setVisibility(View.GONE);
            if (!realm.isInTransaction()) {
                realm.beginTransaction();
            }
            RealmResults<RecentRealm> results = realm.where(RecentRealm.class)
                    .equalTo("groupId", groupId)
                    .findAll();
            if (!results.isEmpty()) {
                for (RecentRealm recent : results) {
                    mFirebaseDatabaseReference.child(Constant.TABLE_RECENT).child(recent.getObjectId()).removeValue();
                    recent.deleteFromRealm();
                }
            }
            RealmResults<GroupRealm> groupRealm = realm.where(GroupRealm.class)
                    .equalTo("objectId", String.valueOf(groupId))
                    .findAll();
            if (!groupRealm.isEmpty()) {


                for (GroupRealm recent : groupRealm) {
                    mFirebaseDatabaseReference.child(Constant.TABLE_GROUP).child(recent.getObjectId()).removeValue();
                    recent.deleteFromRealm();
                }

                realm.commitTransaction();
            }

        } else {
            mFirebaseDatabaseReference.child(Constant.TABLE_TYPING).child(groupId).child(ChattingActivity.getUid()).setValue(false);
            mFirebaseDatabaseReference.child(Constant.TABLE_ACTIVE).child(groupId)
                    .child(ChattingActivity.getUid()).setValue(false);
            typing_toolbar.setVisibility(View.GONE);
        }
    }

    public void getAllUsersFromFirebase(String memberID) {
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_USER).child(memberID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        JSONObject jUserData = new JSONObject((Map) dataSnapshot.getValue());
                        try {
                            String country = jUserData.optString("country");
                            String displayStatus = jUserData.optString("displayStatus");
                            String deviceToken = jUserData.optString("deviceToken");
                            String email = jUserData.optString("email");
                            String fullName = jUserData.optString("fullName");
                            String latitude = jUserData.optString("latitude");
                            String longitude = jUserData.optString("longitude");
                            String mobileNo = jUserData.optString("mobileNo");
                            String objectId = jUserData.optString("objectId");
                            String os = jUserData.optString("os");
                            String picture = jUserData.optString("picture");
//                            String userQRCode = jUserData.optString("userQRCode");
                            double updatedAt = jUserData.optDouble("updatedAt");
                            double createdAt = jUserData.optDouble("createdAt");
                            String statusId = jUserData.optString("statusId");
                            if (!realm.isInTransaction())
                                realm.beginTransaction();
                            User person = realm.createObject(User.class);
                            person.setCountry(country);
                            person.setDisplayStatus(displayStatus);
                            person.setDeviceToken(deviceToken);
                            person.setEmail(email);
                            person.setFullName(fullName);
                            person.setLatitude(latitude);
                            person.setLongitude(longitude);
                            person.setMobileNo(mobileNo);
                            person.setObjectId(objectId);
                            person.setOs(os);
                            person.setPicture(picture);
                            person.setUserQRCode("userQRCode");
                            person.setUpdatedAt(updatedAt);
                            person.setCreatedAt(createdAt);
                            person.setStatusId(statusId);
                            realm.commitTransaction();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void inintDialog() {
        pDialog = new ProgressDialog(ChatActivity.this);
        pDialog.setMessage(getString(R.string.pleaseWait));
    }

    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //            } else {
//                RealmResults<RecentRealm> results = realm.where(RecentRealm.class)
//                        .equalTo("groupId", groupId)
//                        .findAll();
//                if (!results.isEmpty()) {
//                    for (RecentRealm recent : results) {
//                        mFirebaseDatabaseReference.child(Constant.TABLE_RECENT).child(recent.getObjectId()).removeValue();
//                    }
//                }
//            }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.chat_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.camera:
//                enableCamera();
//                return true;
//            case R.id.gallery:
//                openGallery();
//                return true;
//            case R.id.sendLocation:
//                locationPlacesIntent();
//                return true;
//            case R.id.record:
//                recordAudio();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseDatabaseReference.child(Constant.TABLE_ACTIVE).child(groupId)
                .child(ChattingActivity.getUid()).setValue(false);
    }

}
