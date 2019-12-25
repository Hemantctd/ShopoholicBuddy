
package com.shopoholicbuddy.firebasechat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cameraandgallery.activities.CameraGalleryActivity;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.multiple_media_picker.ImagesGallery;
import com.multiple_media_picker.VideosGallery;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.WebViewActivity;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.database.AppDatabase;
import com.shopoholicbuddy.dialogs.CustomDialogForMakeOffer;
import com.shopoholicbuddy.firebasechat.adapters.ChatListAdapter;
import com.shopoholicbuddy.firebasechat.adapters.LocationBean;
import com.shopoholicbuddy.firebasechat.dialogs.CustomDialogForFiles;
import com.shopoholicbuddy.firebasechat.interfaces.FilePickerDialogCallback;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseBlockStatusListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseMessageListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseProductResponseListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseRoomResponseListener;
import com.shopoholicbuddy.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholicbuddy.firebasechat.models.ChatMessageBean;
import com.shopoholicbuddy.firebasechat.models.ChatRoomBean;
import com.shopoholicbuddy.firebasechat.models.HuntDeal;
import com.shopoholicbuddy.firebasechat.models.OfferModel;
import com.shopoholicbuddy.firebasechat.models.ProductBean;
import com.shopoholicbuddy.firebasechat.models.UserBean;
import com.shopoholicbuddy.firebasechat.utils.FirebaseChatUtils;
import com.shopoholicbuddy.firebasechat.utils.FirebaseConstants;
import com.shopoholicbuddy.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholicbuddy.firebasechat.utils.FirebaseEventListeners;
import com.shopoholicbuddy.interfaces.MakeOfferDialogCallback;
import com.shopoholicbuddy.interfaces.PopupItemDialogCallback;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Class used for one to one chat
 */

public class SingleChatActivity extends AppCompatActivity implements Comparator<ChatMessageBean> {

    private final long OFFER_TIMESTAMP = 7200000;
    private final int REQUEST_STORAGE_LOCATION_PERMISSION = 101;
    private final int REQUEST_CAMERA = 1001, CROPPER_REQUEST_CODE = 1002, REQUEST_AUDIO = 1003, REQUEST_FILE = 1004,
            MULTIPLE_IMAGE_INTENT = 1005, MULTIPLE_VIDEO_INTENT = 1006;
    public boolean isResume = false;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.iv_product_pic)
    ImageView ivProductImage;
    @BindView(R.id.tv_product_name)
    CustomTextView tvProductName;
    @BindView(R.id.tv_product_price)
    CustomTextView tvProductPrice;
    @BindView(R.id.tv_product_quantity)
    CustomTextView tvProductQuantity;
    @BindView(R.id.rl_product)
    RelativeLayout rlProduct;
    @BindView(R.id.rv_chat)
    RecyclerView rvChatMessages;
    @BindView(R.id.layout_no_data_found)
    CustomTextView tvNoChat;
    @BindView(R.id.ll_product_deal)
    LinearLayout llProductDeal;
    @BindView(R.id.et_write_message)
    EditText etWriteMessage;
    @BindView(R.id.iv_attachments)
    ImageView ivAttachments;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;
    @BindView(R.id.iv_send_messages)
    ImageView ivSendMessages;
    @BindView(R.id.ll_bottom_bar)
    LinearLayout llBottomBar;
    @BindView(R.id.tv_leave_status)
    AppCompatTextView tvLeaveStatus;
    @BindView(R.id.iv_user_image)
    CircleImageView ivUserImage;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_typing)
    CustomTextView tvTyping;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.iv_more)
    ImageView ivMore;

    private boolean isOtherUserBlock;
    private String loginUserId;
    private String loginUserName;
    private String otherUserId;
    private String roomId;
    public UserBean otherUser;
    private ChatRoomBean chatRoom;
    private FirebaseEventListeners messageListener, typingListener, blockListener, onlineStatusListener, userStatusListener, offerListener;
    private List<ChatMessageBean> chatMessagesList;
    private ChatListAdapter chatListAdapter;
    private LinearLayoutManager layoutManager;
    private HashMap<String, Object> memberDetails;
    private boolean isLoadingMessage;
    private Uri outputUri;
    private int maxImageSelect = 5, maxVideoSelect = 1;
    private boolean status;
    private boolean isMeBlock;
    private List<UserBean> users;
    public List<String> selectMessagesId;
    public List<String> selectMsgUserId;
    private ProductBean productDetails;
    private int permissionType;
    private String productId = "";
    private Uri mCapturedImageURI;
    private ArrayList<String> imagesList;
    private boolean openPlacePicker;
    private String currency = "";
    private TextView labelInterested;
    private TextView labelOffer;
    private TextView labelClose;
    private String offerPrice = "";
    private int userStatus;
    private HuntDeal huntDeal;
    private String huntId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initVariables();
        setListener();
        setAdapters();
        addBottomViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        chatListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        isResume = false;
        super.onPause();
    }

    /**
     * method to add bottom views
     */
    private void addBottomViews() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        View viewInterested = LayoutInflater.from(this).inflate(R.layout.item_chat_labels, null, false);
        labelInterested = viewInterested.findViewById(R.id.tv_chat_label);
        labelInterested.setLayoutParams(params);
        View viewOffer = LayoutInflater.from(this).inflate(R.layout.item_chat_labels, null, false);
        labelOffer = viewOffer.findViewById(R.id.tv_chat_label);
        labelOffer.setLayoutParams(params);
        View viewClose = LayoutInflater.from(this).inflate(R.layout.item_chat_labels, null, false);
        labelClose = viewClose.findViewById(R.id.tv_chat_label);
        labelClose.setLayoutParams(params);
        labelInterested.setText(getString(R.string.accept_offer));
        labelOffer.setText(getString(R.string.counter_offer));
        labelClose.setText(getString(R.string.reject_offer));
        labelInterested.setOnClickListener(view -> {
            if (!isOtherUserBlock) {
//                    String currency = getString(productDetails.getCurrency().equals("2") ? R.string.rupees : productDetails.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
                String currency = productDetails == null ? huntDeal.getCurrencySymbol() : productDetails.getCurrency();
                new AlertDialog.Builder(SingleChatActivity.this, R.style.DatePickerTheme).setTitle(getString(R.string.accept_offer))
                        .setMessage(getString(R.string.sure_want_to_accept_offer) + " " + currency + offerPrice)
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            FirebaseDatabaseQueries.getInstance().acceptOffer(roomId);
                            String message = getString(R.string.offer_accepted);
                            ChatMessageBean chatMessage = new ChatMessageBean();
                            chatMessage.setMessageText(message);
                            chatMessage.setType(FirebaseConstants.TEXT);
                            chatMessage.setMediaUrl("");
                            chatMessage.setThumbnail("");
                            createMessage(chatMessage);

                        })
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                            // do nothing
                        })
                        .show();
            } else {
                FirebaseChatUtils.getInstance().showToast(SingleChatActivity.this, getString(R.string.unblock) + " " + otherUser.getFirstName() + " " + getString(R.string.to_send_msg));
            }
        });
        labelOffer.setOnClickListener(view -> {
            if (!isOtherUserBlock) {
                makeOffer();
            } else {
                FirebaseChatUtils.getInstance().showToast(SingleChatActivity.this, getString(R.string.unblock) + " " + otherUser.getFirstName() + " " + getString(R.string.to_send_msg));
            }
        });
        labelClose.setOnClickListener(view -> {
            if (!isOtherUserBlock) {
                new AlertDialog.Builder(SingleChatActivity.this, R.style.DatePickerTheme).setTitle(getString(R.string.reject_offer))
                        .setMessage(getString(R.string.sure_want_to_reject_offer))
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            FirebaseDatabaseQueries.getInstance().rejectOffer(roomId);
                            String message = getString(R.string.offer_rejected);
                            ChatMessageBean chatMessage = new ChatMessageBean();
                            chatMessage.setMessageText(message);
                            chatMessage.setType(FirebaseConstants.TEXT);
                            chatMessage.setMediaUrl("");
                            chatMessage.setThumbnail("");
                            createMessage(chatMessage);

                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            } else {
                FirebaseChatUtils.getInstance().showToast(SingleChatActivity.this, getString(R.string.unblock) + " " + otherUser.getFirstName() + " " + getString(R.string.to_send_msg));
            }
        });
        llProductDeal.addView(labelInterested);
        llProductDeal.addView(labelOffer);
        llProductDeal.addView(labelClose);

        labelInterested.setClickable(false);
        labelOffer.setClickable(false);
        labelClose.setClickable(false);
        changeLabelColor();
    }

    /**
     * function to change the label colors
     */
    private void changeLabelColor() {
        if (labelInterested.isClickable()) {
            labelInterested.setBackgroundResource(R.drawable.round_courner_chat_item_gradient);
        }else {
            labelInterested.setBackgroundResource(R.drawable.color_disable_button);
        }
        if (labelOffer.isClickable()) {
            labelOffer.setBackgroundResource(R.drawable.round_courner_chat_item_gradient);
        }else {
            labelOffer.setBackgroundResource(R.drawable.color_disable_button);
        }
        if (labelClose.isClickable()) {
            labelClose.setBackgroundResource(R.drawable.round_courner_chat_item_gradient);
        }else {
            labelClose.setBackgroundResource(R.drawable.color_disable_button);
        }
    }


    /**
     * method to make offer
     */
    private void makeOffer() {
        CustomDialogForMakeOffer customDialogForMakeOffer = new CustomDialogForMakeOffer(SingleChatActivity.this, 1, currency, "", "", new MakeOfferDialogCallback() {
            @Override
            public void onSelect(String status, String currency, String price, int type) {
                if (!isOtherUserBlock) {
                    ChatMessageBean chatMessage = new ChatMessageBean();
                    chatMessage.setMessageText(status);
                    chatMessage.setType(FirebaseConstants.TEXT);
                    chatMessage.setMediaUrl("");
                    chatMessage.setThumbnail("");
                    createMessage(chatMessage);
                    FirebaseDatabaseQueries.getInstance().makeAnOffer(roomId, status, currency, price);
                } else {
                    FirebaseChatUtils.getInstance().showToast(SingleChatActivity.this, getString(R.string.unblock) + " " + otherUser.getFirstName() + " " + getString(R.string.to_send_msg));
                }
            }
        });
        customDialogForMakeOffer.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (roomId != null) {
            AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.CURRENT_CHAT_ROOM, roomId);
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_LAST_UPDATES)
                    .child(loginUserId).setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (roomId != null) {
            FirebaseDatabaseQueries.getInstance().updateUnreadCount(this, roomId);
            AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.CURRENT_CHAT_ROOM, "");
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_LAST_UPDATES)
                    .child(loginUserId).setValue(ServerValue.TIMESTAMP);
        }
    }


    /**
     * method to initialize the variables
     */
    private void initVariables() {
        chatMessagesList = new ArrayList<>();
        selectMessagesId = new ArrayList<>();
        selectMsgUserId = new ArrayList<>();
        imagesList = new ArrayList<>();
        ivAdd.setVisibility(View.GONE);
        UserBean loginUser = FirebaseDatabaseQueries.getInstance().getCurrentUser(this);
        loginUserId = loginUser.getUserId();
        loginUserName = loginUser.getFirstName() + loginUser.getLastName();
        //get data from intent
        if (getIntent() != null && getIntent().getExtras() != null) {
            otherUser = (UserBean) getIntent().getExtras().getSerializable(FirebaseConstants.OTHER_USER);
            roomId = getIntent().getExtras().getString(FirebaseConstants.ROOM_ID, null);
            otherUserId = otherUser != null ? otherUser.getUserId() : null;
            productDetails = (ProductBean) getIntent().getExtras().getSerializable(FirebaseConstants.CHAT_ROOM_PRODUCT);
            huntDeal = (HuntDeal) getIntent().getExtras().getSerializable(FirebaseConstants.CHAT_ROOM_HUNT);
            if (productDetails != null) {
                tvProductName.setText(productDetails.getName());
                currency = productDetails.getCurrency();
//                    String currency = getString(productDetails.getCurrency().equals("2") ? R.string.rupees : productDetails.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
                tvProductPrice.setText(TextUtils.concat(currency + productDetails.getPrice()));
                tvProductQuantity.setText(TextUtils.concat(getString(R.string.quantity) + ": " + productDetails.getQuantity()));
                AppUtils.getInstance().setImages(this, productDetails.getImage(), ivProductImage, 0, R.drawable.ic_placeholder);
                productId = productDetails.getId();
//                    FirebaseDatabaseQueries.getInstance().createProduct(productDetails);
            } else if (huntDeal != null) {
                ivAdd.setVisibility(View.GONE);
                tvProductName.setText(TextUtils.concat(huntDeal.getSubCategoryName() + " " + getString(R.string.in) + " " + huntDeal.getCategoryName()));
                currency = huntDeal.getCurrencySymbol();
                if (currency == null || huntDeal.getPrice() == null) tvProductPrice.setVisibility(View.GONE);
                else tvProductPrice.setText(TextUtils.concat(currency + huntDeal.getPrice()));
                tvProductQuantity.setVisibility(View.GONE);
                huntId = huntDeal.getId();
                if (huntDeal != null && !huntDeal.getUserId().equals("") && !huntDeal.getUserId().equals("0")
                        && !huntDeal.getUserId().equals(loginUserId)) {
                    llProductDeal.setVisibility(View.GONE);
                    tvLeaveStatus.setVisibility(View.VISIBLE);
                    tvLeaveStatus.setText(getString(R.string.hunt_closed));
                }
                try {
                    AppUtils.getInstance().setImages(this, huntDeal.getHuntImage().split(",")[0], ivProductImage, 0, R.drawable.ic_placeholder);
                } catch (Exception ignored) {}
            } else {
                rlProduct.setVisibility(View.GONE);
                llProductDeal.setVisibility(View.GONE);
                ivAdd.setVisibility(View.GONE);
            }
        }
        //check other user exists or not
        if (otherUserId == null) {
            FirebaseChatUtils.getInstance().showToast(this, getString(R.string.user_not_exists));
            finish();
        } else {
            tvTitle.setText(TextUtils.concat(otherUser.getFirstName() + " " + otherUser.getLastName()));
            ivUserImage.setVisibility(View.VISIBLE);
            AppUtils.getInstance().setCircularImages(this, otherUser.getUserImage(), ivUserImage, R.drawable.ic_side_menu_user_placeholder);
        }

        // create list of users
        users = new ArrayList<>();
        users.add(otherUser);
        users.add(loginUser);

        etWriteMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        //check room id exists or not
        if (roomId != null) {
            getChatRoomDetails(roomId);
        } else {
            getRoomId();
        }

        //set layout manager and adapter
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        chatListAdapter = new ChatListAdapter(this, chatMessagesList, users, new RecycleViewCallBack() {
            @Override
            public void onClick(int position, View clickedView) {
                int id = clickedView.getId();
                if (id == R.id.iv_play_video) {
                    Uri uri = Uri.parse(chatMessagesList.get(position).getMediaUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/*");
                    startActivity(intent);
                    updateViews();
                } else if (id == R.id.ll_message) {
                    if (chatMessagesList.get(position).getType().equals(FirebaseConstants.LOCATION)) {
                        double latitude = chatMessagesList.get(position).getLatitude();
                        double longitude = chatMessagesList.get(position).getLongitude();
                        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Location)");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                    if (chatMessagesList.get(position).getType().equals(FirebaseConstants.FILE) ||
                            chatMessagesList.get(position).getType().equals(FirebaseConstants.PDF) ||
                            chatMessagesList.get(position).getType().equals(FirebaseConstants.SHEET)) {
                        Intent containerIntent = new Intent(SingleChatActivity.this, WebViewActivity.class);
                        containerIntent.putExtra(chatMessagesList.get(position).getType(), chatMessagesList.get(position).getMediaUrl());
                        startActivity(containerIntent);
                    }
                    if (chatMessagesList.get(position).getType().equals(FirebaseConstants.IMAGE)) {
                        showImages(chatMessagesList.get(position));
                    }
                    updateViews();
                } else if (id == R.id.tv_resend) {
                    chatMessagesList.get(position).setStatus(FirebaseConstants.PENDING);
                    startUpload(chatMessagesList.get(position));
                    chatListAdapter.notifyItemChanged(position);
                    updateViews();
                }
            }

            @Override
            public void onLongClick(int position, View clickedView) {
                switch (clickedView.getId()) {
                    case R.id.ll_message_row:
                        /*if (chatMessagesList.get(position).getSenderId().equals(loginUserId)) {
                            showDeleteDialog(chatMessagesList.get(position));
                        }*/
                        break;
                }
            }
        });

        //call to check other user online and block status
        getOnlineAndBlockStatus();
    }


    /**
     * method to show the list dialog
     */
    private void showDeleteDialog(final ChatMessageBean chatMessageBean) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_launcher);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.add(getString(R.string.delete_message));

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        String messageId = chatMessageBean.getMessageId();
                        FirebaseDatabaseQueries.getInstance().setMessagesDeleteStatus(roomId, messageId);
                        if (chatMessagesList.get(0).getMessageId().equals(messageId)) {
                            if (chatMessagesList.size() > 1) {
                                ChatMessageBean message = chatMessagesList.get(0);
                                if (!chatMessagesList.get(1).getType().equals(FirebaseConstants.CHAT_TIME)) {
                                    message = chatMessagesList.get(1);
                                } else if (chatMessagesList.size() > 2) {
                                    message = chatMessagesList.get(2);
                                }
                                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId)
                                        .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).setValue(message);
                            } else {
//                                FirebaseDatabaseQueries.getInstance().deleteRoom(roomId, FirebaseConstants.SINGLE_CHAT, otherUserId, productId);
//                                setResult(RESULT_OK);
//                                finish();
                            }
                        }
                        break;
                }
            }
        });
        builderSingle.show();
    }



    /**
     * method to show the images
     *
     * @param chatMessage
     */
    public void showImages(ChatMessageBean chatMessage) {
        Intent intent = new Intent(this, FullScreenImageSliderActivity.class);
        intent.putExtra("imagelist", imagesList);
        intent.putExtra("from", otherUser.getFirstName());
        for (int i = 0; i < imagesList.size(); i++) {
            if (imagesList.get(i).equalsIgnoreCase(chatMessage.getMediaUrl())) {
                intent.putExtra("pos", i);
            }
        }
        startActivity(intent);
    }

    /**
     * method to get online status of other person
     * and to get that current user block other user or not
     * and get that if login user block or not
     */
    private void getOnlineAndBlockStatus() {
        //online status of other person
        onlineStatusListener = new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    tvTyping.setVisibility(isMeBlock ? View.GONE : View.VISIBLE);
                    status = dataSnapshot.getValue() instanceof Boolean ? dataSnapshot.getValue(Boolean.class) : false;
                    FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(otherUserId)
                            .child(FirebaseConstants.STATUS_NODE).removeEventListener((ValueEventListener) userStatusListener);
                    if (status) {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(otherUserId)
                                .child(FirebaseConstants.STATUS_NODE).addValueEventListener(userStatusListener);
                    }else {
                        tvTyping.setVisibility(View.GONE);
                    }
                } else {
                    tvTyping.setVisibility(View.GONE);
                }
//                setUserStatus();

            }
        };
        userStatusListener = new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userStatus = Integer.parseInt(dataSnapshot.getValue().toString());
                } else {
                    tvTyping.setVisibility(View.GONE);
                }
                setUserStatus();

            }
        };

        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(otherUserId).child(FirebaseConstants.ONLINE_STATUS_NODE).addValueEventListener(onlineStatusListener);

        // get that current user block other user or not

        FirebaseDatabaseQueries.getInstance().getBlockStatus(otherUserId, new FirebaseBlockStatusListener() {
            @Override
            public void isUserBlock(boolean isBlock) {
                isOtherUserBlock = isBlock;
            }
        });

        // block status of login user
        blockListener = new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    isMeBlock = true;
                    tvTyping.setVisibility(View.GONE);
                } else {
                    isMeBlock = false;
                    tvTyping.setVisibility(View.VISIBLE);
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.BLOCK_NODE).child(otherUserId).child(loginUserId).addValueEventListener(blockListener);


    }

    /**
     * Method to set the user online status
     */
    private void setUserStatus() {
        if (status) {
            switch (userStatus) {
                case 0:
                case 1:
                    tvTyping.setText(getString(R.string.active));
                    break;
                case 2:
                    tvTyping.setText(getString(R.string.away));
                    break;
                case 3:
                    tvTyping.setText(getString(R.string.do_not_disturb));
                    break;
                default:
                    tvTyping.setText(getString(R.string.offline));
            }
        } else {
            tvTyping.setVisibility(View.GONE);
        }
    }

    /**
     * method to set listener on views
     */
    private void setListener() {
        etWriteMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (roomId != null) {
                    if (etWriteMessage.getText().toString().length() == 0) {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(loginUserId).setValue(false);
                        ivAttachments.setVisibility(View.VISIBLE);
                        ivCamera.setVisibility(View.VISIBLE);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(loginUserId).setValue(true);
                        ivAttachments.setVisibility(View.GONE);
                        ivCamera.setVisibility(View.GONE);
                    }
                }
            }
        });

        rvChatMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (chatRoom != null && !isLoadingMessage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoadingMessage = true;
                    memberDetails = (HashMap<String, Object>) chatRoom.getChatRoomMembers().get(loginUserId);
                    FirebaseDatabaseQueries.getInstance().getPreviousMessages(roomId, Long.parseLong(memberDetails.get(FirebaseConstants.MEMBER_DELETE_NODE).toString()), Long.parseLong(chatMessagesList.get(chatMessagesList.size() - 1).getTimestamp().toString()) - 1, new FirebaseMessageListener() {
                        @Override
                        public void getMessages(ChatMessageBean message) {
                        }

                        @Override
                        public void getMessagesList(List<ChatMessageBean> messagesList) {
                            if (messagesList.size() > 0) {
                                chatMessagesList.remove(chatMessagesList.size() - 1);
                                int previousPosition = chatMessagesList.size() - 1;
                                for (int i = 0; i <= messagesList.size(); i++) {
                                    ChatMessageBean time = null;
                                    if (i == 0) {
                                        time = FirebaseChatUtils.getInstance().getDateStamp(SingleChatActivity.this, chatMessagesList.get(chatMessagesList.size() - 1), messagesList.get(i));
                                    } else if (i == messagesList.size()) {
                                        time = FirebaseChatUtils.getInstance().getDateStamp(SingleChatActivity.this, messagesList.get(i - 1), null);
                                    } else {
                                        time = FirebaseChatUtils.getInstance().getDateStamp(SingleChatActivity.this, messagesList.get(i - 1), messagesList.get(i));
                                    }
                                    if (time != null) {
                                        messagesList.add(i, time);
                                        i++;
                                    }
                                }
                                chatMessagesList.addAll(messagesList);
                                chatListAdapter.notifyItemRangeChanged(previousPosition, chatMessagesList.size() - 1);
                                isLoadingMessage = false;
                                updateViews();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(chatListAdapter);

        List<ChatMessageBean> previousMessages = (List<ChatMessageBean>) AppDatabase.fetchMediaFilesDetails(otherUser.getUserId());
        if (previousMessages != null && previousMessages.size() > 0) {
            tvNoChat.setVisibility(View.GONE);
            for (int i = 0; i < previousMessages.size(); i++) {
                previousMessages.get(i).setStatus(FirebaseConstants.FAILED);
                setNewChatMessage(previousMessages.get(i));
                if (previousMessages.get(i) != null && previousMessages.get(i).getType().equals(FirebaseConstants.IMAGE))
                    imagesList.add(previousMessages.get(i).getMediaUrl());
            }
        }
    }


    /**
     * Method to get the room id
     */
    private void getRoomId() {
//        tvNoChat.setVisibility(View.VISIBLE);
        String productHuntId = productId.equals("") ? (huntId.equals("") ? "" : "h" + huntId) : productId;
        FirebaseDatabaseQueries.getInstance().getRoomId(loginUserId, otherUserId, productHuntId, new FirebaseRoomResponseListener() {
            @Override
            public void getRoomId(String roomId) {
                if (SingleChatActivity.this.roomId == null) {
                    tvNoChat.setVisibility(View.GONE);
                    SingleChatActivity.this.roomId = roomId;
                    getChatRoomDetails(roomId);
                }
            }

            @Override
            public void getRoomDetails(ChatRoomBean chatRoomBean) {
            }
        });
        FirebaseDatabaseQueries.getInstance().getRoomId(otherUserId, loginUserId, productHuntId, new FirebaseRoomResponseListener() {
            @Override
            public void getRoomId(String roomId) {
                if (SingleChatActivity.this.roomId == null) {
                    tvNoChat.setVisibility(View.GONE);
                    SingleChatActivity.this.roomId = roomId;
                    getChatRoomDetails(roomId);
                }
            }

            @Override
            public void getRoomDetails(ChatRoomBean chatRoomBean) {
            }
        });
    }

    /**
     * Method to get the chat room details
     *
     * @param roomId
     */
    private void getChatRoomDetails(String roomId) {
        FirebaseDatabaseQueries.getInstance().updateUnreadCount(this, roomId);
        FirebaseDatabaseQueries.getInstance().getRoomDetails(roomId, new FirebaseRoomResponseListener() {
            @Override
            public void getRoomId(String roomId) {
            }

            @Override
            public void getRoomDetails(ChatRoomBean chatRoomBean) {
                SingleChatActivity.this.chatRoom = chatRoomBean;
                if (chatRoomBean.getProductId() != null && !chatRoomBean.getProductId().equals("")) {
                    getProductDetails();
                } else if (chatRoomBean.getHuntId() != null && !chatRoomBean.getHuntId().equals("")) {
                    getHuntDetails();
                }else {
                    getOfferStatus();
                    getChatMessages();
                    setTypingListener();
                }
            }
        });
    }


    /**
     * get product details
     */
    private void getProductDetails() {
        FirebaseDatabaseQueries.getInstance().getProductDetails(chatRoom.getProductId(), new FirebaseProductResponseListener() {
            @Override
            public void getProductDetails(ProductBean productBean) {
                if (productBean.getProductType().equals("2")) productBean.setPrice(chatRoom.getPrice());
                chatRoom.setProduct(productBean);
                getOfferStatus();
                getChatMessages();
                setTypingListener();
            }
        });
    }


    /**
     * get hunt details
     */
    private void getHuntDetails() {
        FirebaseDatabaseQueries.getInstance().getHuntDetails(chatRoom.getHuntId(), huntDeal -> {
            if (huntDeal.getProductType().equals("2")) huntDeal.setPrice(chatRoom.getPrice());
            chatRoom.setHuntDeal(huntDeal);
            if (!huntDeal.getUserId().equals("") && !huntDeal.getUserId().equals("0")
                    && !huntDeal.getUserId().equals(loginUserId)) {
                llProductDeal.setVisibility(View.GONE);
                tvLeaveStatus.setVisibility(View.VISIBLE);
                tvLeaveStatus.setText(getString(R.string.hunt_closed));
            }
            getOfferStatus();
            getChatMessages();
            setTypingListener();
        });
    }


    /**
     * method to get offer status
     */
    private void getOfferStatus() {
        if (offerListener != null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.OFFER).child(roomId).removeEventListener((ValueEventListener) offerListener);
        }
        offerListener = new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final OfferModel offer = dataSnapshot.getValue(OfferModel.class);
                    if (offer != null) {
                        offerPrice = offer.getPrice();
                        switch (offer.getStatus()) {
                            case "1":
                                labelInterested.setClickable(false);
                                labelOffer.setClickable(false);
                                labelClose.setClickable(false);
                                break;
                            case "2":
                                labelInterested.setClickable(true);
                                labelOffer.setClickable(true);
                                labelClose.setClickable(true);
                                break;
                            case "3":
                                labelInterested.setClickable(false);
                                labelOffer.setClickable(false);
                                labelClose.setClickable(false);
                                break;
                            default:
                                labelInterested.setClickable(false);
                                labelOffer.setClickable(false);
                                labelClose.setClickable(false);
                                break;
                        }
                        if (offer.getStatus().equals("1") || offer.getStatus().equals("3")){
                            if ((offer.getTimestamp() + (6 * 60 * 60 * 1000)) < Calendar.getInstance().getTimeInMillis()) {  //21600000 millisec
                                FirebaseDatabaseQueries.getInstance().changeOfferStatus(roomId, "0");
                            }
                        }
                    }
                } else {
                    labelInterested.setClickable(false);
                    labelOffer.setClickable(false);
                    labelClose.setClickable(false);
                }
                changeLabelColor();
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.OFFER).child(roomId)
                .addValueEventListener(offerListener);
    }

    /**
     * Method to get all chat messages
     */
    private void getChatMessages() {
        memberDetails = (HashMap<String, Object>) chatRoom.getChatRoomMembers().get(loginUserId);
        if (memberDetails == null){
            FirebaseChatUtils.getInstance().showToast(this, getString(R.string.user_not_exists));
            finish();
            return;
        }
        messageListener = new FirebaseEventListeners() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    ChatMessageBean chatMessage = dataSnapshot.getValue(ChatMessageBean.class);
                    if (chatMessage != null && !chatMessage.getIsDeleted()) {
                        setChatMessageInList(chatMessage);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    ChatMessageBean chatMessage = dataSnapshot.getValue(ChatMessageBean.class);
                    if (chatMessage != null && chatMessage.getIsDeleted()) {
                        for (int i = 0; i < chatMessagesList.size(); i++) {
                            if (chatMessagesList.get(i).getMessageId() != null && chatMessagesList.get(i).getMessageId().equals(chatMessage.getMessageId())) {
                                if (chatMessagesList.size() > (i + 1) && (i == 0
                                        || chatMessagesList.get(i - 1).getType().equals(FirebaseConstants.CHAT_TIME))
                                        && chatMessagesList.get(i + 1).getType().equals(FirebaseConstants.CHAT_TIME)) {
                                    chatMessagesList.remove(i + 1);
                                    chatListAdapter.notifyItemRemoved(i + 1);
                                }
                                chatMessagesList.remove(i);
                                chatListAdapter.notifyItemRemoved(i);
                                chatListAdapter.notifyItemRangeChanged(i, chatMessagesList.size());
                                updateViews();
                                break;
                            }
                        }
                    } else {
                        if (chatMessage != null && chatMessage.getType().equals(FirebaseConstants.TEXT)) {
                            for (int i = 0; i < chatMessagesList.size(); i++) {
                                if (chatMessagesList.get(i).getMessageId() != null && chatMessagesList.get(i).getMessageId().equals(chatMessage.getMessageId())) {
                                    chatMessagesList.set(i, chatMessage);
                                    chatListAdapter.notifyItemChanged(i);
                                    updateViews();
                                    break;
                                }
                            }
                        } else if (chatMessage != null) {
                            setChatMessageInList(chatMessage);
                        }
                    }
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.MESSAGES_NODE).child(roomId).orderByChild(FirebaseConstants.TIME_STAMP).limitToLast(50)
                .startAt(Long.parseLong(memberDetails.get(FirebaseConstants.MEMBER_DELETE_NODE).toString())).addChildEventListener(messageListener);

        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.MESSAGES_NODE).child(roomId).orderByChild(FirebaseConstants.TIME_STAMP).limitToLast(50)
                .startAt(Long.parseLong(memberDetails.get(FirebaseConstants.MEMBER_DELETE_NODE).toString())).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    tvNoChat.setVisibility(View.GONE);
                } else {
//                    tvNoChat.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * method to set chat message in list
     *
     * @param chatMessage
     */
    private void setChatMessageInList(ChatMessageBean chatMessage) {
        boolean newMessage = true;
        if (chatMessage == null || !chatMessage.getIsBlock() || !chatMessage.getReceiverId().equals(loginUserId)) {
            if (chatMessage != null && !chatMessage.getType().equals(FirebaseConstants.TEXT)) {
                for (int i = 0; i < chatMessagesList.size(); i++) {
                    if (!chatMessagesList.get(i).getType().equals(FirebaseConstants.CHAT_TIME) && chatMessagesList.get(i).getMessageId().equals(chatMessage.getMessageId())) {
                        ChatMessageBean previousMessage = AppDatabase.fetchSingleMediaFilesDetails(chatMessage.getMessageId());
                        if (previousMessage == null) previousMessage = chatMessagesList.get(i);
                        if (!chatMessage.getType().equals(FirebaseConstants.FILE))
                            chatMessage.setMediaUrl(previousMessage.getMediaUrl());
                        chatMessage.setThumbnail(previousMessage.getThumbnail());
                        chatMessage.setTimestamp(previousMessage.getTimestamp());
                        chatMessagesList.set(i, chatMessage);
                        chatListAdapter.notifyItemChanged(i);
                        newMessage = false;
                        AppDatabase.removeMediaFileDetailsFromDb(chatMessage.getMessageId());
                        break;
                    }
                }
            }
            if (chatMessage != null && (chatMessage.getSenderId().equals(loginUserId) || !chatMessage.getIsBlock())) {
                if (newMessage) {
                    setNewChatMessage(chatMessage);
                    if (chatMessage.getType().equals(FirebaseConstants.IMAGE))
                        imagesList.add(chatMessage.getMediaUrl());
                }
            }
        }
    }

    /**
     * Method to get the typing status of other person
     */
    private void setTypingListener() {
        if (otherUserId != null) {
            typingListener = new FirebaseEventListeners() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.getValue() instanceof Boolean && dataSnapshot.getValue(Boolean.class)) {
                            tvTyping.setVisibility(isMeBlock ? View.GONE : View.VISIBLE);
                            tvTyping.setText(R.string.typing);
                        } else {
                            setUserStatus();
                        }
                    }
                }
            };
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(otherUserId)
                    .addValueEventListener(typingListener);
        }
    }

    /**
     * Method to set new message in message list
     *
     * @param chatMessage
     */
    private void setNewChatMessage(ChatMessageBean chatMessage) {
        if (chatMessagesList.size() > 0 && chatMessagesList.get(chatMessagesList.size() - 1).getType().equals(FirebaseConstants.CHAT_TIME)) {
            chatMessagesList.remove(chatMessagesList.size() - 1);
        }
        chatMessagesList.add(chatMessage);
        Collections.sort(chatMessagesList, this);

        ChatMessageBean time = null;
        if (chatMessagesList.size() == 1) {
            time = FirebaseChatUtils.getInstance().getDateStamp(SingleChatActivity.this, chatMessage, null);
            if (time != null) {
                chatMessagesList.add(time);
            }
        } else {
            for (int i = 1; i < chatMessagesList.size(); i++) {
                if (!chatMessagesList.get(i - 1).getType().equals(FirebaseConstants.CHAT_TIME) && !chatMessagesList.get(i).getType().equals(FirebaseConstants.CHAT_TIME)) {
                    time = FirebaseChatUtils.getInstance().getDateStamp(SingleChatActivity.this, chatMessagesList.get(i - 1), chatMessagesList.get(i));
                    if (time != null) {
                        chatMessagesList.add(i, time);
                        i++;
                    }
                }
            }
            if (chatMessagesList.size() > 0) {
                time = FirebaseChatUtils.getInstance().getDateStamp(SingleChatActivity.this, chatMessagesList.get(chatMessagesList.size() - 1), null);
                if (time != null) {
                    chatMessagesList.add(time);
                }
            }
        }

        chatListAdapter.notifyDataSetChanged();
        updateViews();
    }


    @Override
    public int compare(ChatMessageBean lhs, ChatMessageBean rhs) {
        if (lhs.getTimestamp() != null && rhs.getTimestamp() != null) {
            long lhsTime = Long.parseLong(lhs.getTimestamp().toString());
            long rhsTime = Long.parseLong(rhs.getTimestamp().toString());
            if (lhsTime < rhsTime) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_add, R.id.iv_more, R.id.iv_attachments, R.id.iv_camera, R.id.iv_send_messages})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_add:
                break;
            case R.id.iv_more:
                showMorePopUp(findViewById(R.id.iv_more));
                break;
            case R.id.iv_attachments:
                if (!isOtherUserBlock) {
                    permissionType = Constants.IntentConstant.REQUEST_GALLERY;
                    checkStorageLocationPermission();
                } else {
                    FirebaseChatUtils.getInstance().showToast(this, getString(R.string.unblock) + " " + otherUser.getFirstName() + " " + getString(R.string.to_send_msg));
                }
                break;
            case R.id.iv_camera:
                if (!isOtherUserBlock) {
                    permissionType = Constants.IntentConstant.REQUEST_CAMERA;
                    checkStorageLocationPermission();
                } else {
                    FirebaseChatUtils.getInstance().showToast(this, getString(R.string.unblock) + " " + otherUser.getFirstName() + " " + getString(R.string.to_send_msg));
                }
                break;
            case R.id.iv_send_messages:
                if (etWriteMessage.getText().toString().trim().length() != 0) {
                    if (!isOtherUserBlock) {
                        String message = etWriteMessage.getText().toString().trim();
                        etWriteMessage.setText("");
                        ChatMessageBean chatMessage = new ChatMessageBean();
                        chatMessage.setMessageText(message);
                        chatMessage.setType(FirebaseConstants.TEXT);
                        chatMessage.setMediaUrl("");
                        chatMessage.setThumbnail("");
                        createMessage(chatMessage);
                    } else {
                        FirebaseChatUtils.getInstance().showToast(this, getString(R.string.unblock) + " " + otherUser.getFirstName() + " " + getString(R.string.to_send_msg));
                    }
                }
                break;
        }
    }

    /**
     * method to show more popup
     * @param view
     */
    private void showMorePopUp(View view) {
        AppUtils.getInstance().showMorePopUp(this, view, !isOtherUserBlock ? getString(R.string.block) : getString(R.string.unblock),
                "", "", 2, new PopupItemDialogCallback() {
            @Override
            public void onItemOneClick() {
                if (!isOtherUserBlock) {
                    showBlockDialog();
                } else {
                    FirebaseDatabaseQueries.getInstance().blockUser(roomId, false, otherUser.getUserId());
                    isOtherUserBlock = false;
                    if (roomId != null) {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(otherUserId)
                                .addValueEventListener(typingListener);
                    }
                }
            }
            @Override
            public void onItemTwoClick() {}
            @Override
            public void onItemThreeClick() {}
        });
    }


    /**
     * Method to create local message
     *
     * @param chatMessage
     */
    private void createMessage(ChatMessageBean chatMessage) {
        String productHuntId = productId.equals("") ? (huntId.equals("") ? "" : "h" + huntId) : productId;
        if (roomId == null) {
//            roomId = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).push().getKey();
            roomId = loginUserId + "_" + productHuntId + "_" + otherUserId;
            FirebaseDatabaseQueries.getInstance().createChatRoom(users, FirebaseConstants.SINGLE_CHAT, roomId, "", "");
            getChatRoomDetails(roomId);
            AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.CURRENT_CHAT_ROOM, roomId);
        }
        chatMessage.setIsBlock(isMeBlock);
        chatMessage.setReceiverId(otherUserId);
        chatMessage.setSenderId(loginUserId);
        chatMessage.setRoomId(roomId);
        FirebaseDatabaseQueries.getInstance().sendChatMessage(chatMessage, users, FirebaseConstants.SINGLE_CHAT, "", productHuntId);
    }


    /**
     * Method to get the current location of user
     *
     * @param location
     */
    private void getCurrentLocation(LatLng location) {
        if (location != null) {
            double longitude = location.longitude;
            double latitude = location.latitude;
            LocationBean locationBean = new LocationBean();
            locationBean.setLatitude(latitude);
            locationBean.setLongitude(longitude);
            locationBean.setLocationUri(FirebaseChatUtils.getInstance().getStaticMapImage(this, latitude, longitude));
            sendLocation(locationBean);
        } else {
            FirebaseChatUtils.getInstance().showToast(SingleChatActivity.this, getString(R.string.unable_to_fetch_location));
        }
    }


    /**
     * Checks permission to Write external storage in Marshmallow and above devices
     */
    private void checkStorageLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here

            if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_STORAGE_LOCATION_PERMISSION);
            } else {
                // permission already granted
                if (permissionType == Constants.IntentConstant.REQUEST_CAMERA) {
//                    File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name));
//                    if (!myDir.exists()) myDir.mkdir();
//                    String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
//                    File file = new File(myDir, fileName);
//                    outputUri = Uri.fromFile(file);
//                    ImageCropper.startCaptureImageActivity(SingleChatActivity.this, REQUEST_CAMERA, CROPPER_REQUEST_CODE);

//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, "shopoholic_" + System.currentTimeMillis() + ".jpeg");
//                    mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//                    takePictureIntent.putExtra("return-data", true);
//                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);

                    startActivityForResult(new Intent(this, CameraGalleryActivity.class)
                                    .putExtra("maxSelection", 5)
                            , Constants.IntentConstant.REQUEST_GALLERY);
                } else {
                    showDialog();
                }
            }
        } else {
            //before marshmallow
            if (permissionType == Constants.IntentConstant.REQUEST_CAMERA) {
//                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name));
//                if (!myDir.exists()) myDir.mkdir();
//                String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
//                File file = new File(myDir, fileName);
//                outputUri = Uri.fromFile(file);
//                ImageCropper.startCaptureImageActivity(SingleChatActivity.this, REQUEST_CAMERA, CROPPER_REQUEST_CODE);

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.TITLE, "shopoholic_" + System.currentTimeMillis() + ".jpeg");
//                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//                takePictureIntent.putExtra("return-data", true);
//                startActivityForResult(takePictureIntent, REQUEST_CAMERA);

                startActivityForResult(new Intent(this, CameraGalleryActivity.class)
                                .putExtra("maxSelection", 5)
                        , Constants.IntentConstant.REQUEST_GALLERY);
            } else {
                showDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE_LOCATION_PERMISSION:
                boolean isRationalGalleryStorage = false;
                for (String permission : permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission) &&
                            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        isRationalGalleryStorage = true;
                    }
                }
                boolean permissionsGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        permissionsGranted = false;
                    }
                }
                if (grantResults.length > 0 && permissionsGranted) {
                    checkStorageLocationPermission();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_storage_permission));
                }
                break;

        }

    }

    /**
     * Show Location dialog
     *
     * @param location
     */
    private void sendLocation(LocationBean location) {
        if (!isOtherUserBlock) {
            ChatMessageBean chatMessage = new ChatMessageBean();
            chatMessage.setMessageText(FirebaseConstants.LOCATION);
            chatMessage.setType(FirebaseConstants.LOCATION);
            chatMessage.setMediaUrl(location.getLocationUri());
            chatMessage.setThumbnail("");
            chatMessage.setLatitude(location.getLatitude());
            chatMessage.setLongitude(location.getLongitude());
            createMessage(chatMessage);
        } else {
            FirebaseChatUtils.getInstance().showToast(this, getString(R.string.unblock) + " " + otherUser.getFirstName() + " " + getString(R.string.to_send_msg));
        }
    }

    /**
     * Show Image picker dialog at bottom
     */
    private void showDialog() {
        final CustomDialogForFiles dialog = new CustomDialogForFiles(this, new FilePickerDialogCallback() {
            @Override
            public void onFilesSelection() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
//                intent.setType("text/*");
                startActivityForResult(intent, REQUEST_FILE);
            }

            @Override
            public void onPhotosSelection() {
                Intent intent = new Intent(SingleChatActivity.this, ImagesGallery.class);
                intent.putExtra("selectedList", new ArrayList<String>());
                // Set the title
                intent.putExtra("title", getString(R.string.select_image));
                intent.putExtra("maxSelection", maxImageSelect); // Optional
                startActivityForResult(intent, MULTIPLE_IMAGE_INTENT);
            }

            @Override
            public void onVideosSelection() {
                Intent intent = new Intent(SingleChatActivity.this, VideosGallery.class);
                intent.putExtra("selectedList", new ArrayList<String>());
                // Set the title
                intent.putExtra("title", getString(R.string.select_video));
                intent.putExtra("maxSelection", maxVideoSelect); // Optional
                startActivityForResult(intent, MULTIPLE_VIDEO_INTENT);
            }

            @Override
            public void onLocationSelection() {
                if (!openPlacePicker) {
                    try {
                        openPlacePicker = true;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(SingleChatActivity.this), Constants.IntentConstant.REQUEST_PLACE_PICKER);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        openPlacePicker = false;
                        e.printStackTrace();
                    }
                }
            }

        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        openPlacePicker = false;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
//                ImageCropper.activity(ImageCropper.getCapturedImageURI()).setGuidelines(CropImageView.Guidelines.OFF).
//                        setCropShape(CropImageView.CropShape.RECTANGLE).setBorderLineColor(Color.WHITE).setBorderCornerColor(Color.TRANSPARENT)
//                        .setAspectRatio(80, 80).setBorderLineThickness(5)
//                        .setOutputUri(outputUri).setActionbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                        .setAutoZoomEnabled(true).start(this);

                if (mCapturedImageURI != null && !mCapturedImageURI.toString().equals("")) {
                    createMediaMessage(mCapturedImageURI, FirebaseConstants.IMAGE, null, "");
                }else {
                    AppUtils.getInstance().showToast(this, getString(R.string.file_not_found));
                }
            } else if (requestCode == CROPPER_REQUEST_CODE) {
//                Bitmap thumbnailImage = FirebaseChatUtils.getInstance().compressBitmap(BitmapFactory.decodeFile(
//                        FirebaseChatUtils.getInstance().getImagePathFromUri(SingleChatActivity.this, outputUri), new BitmapFactory.Options()));
//                Log.d("Size : ", String.valueOf(thumbnailImage.getByteCount()));
//                createMediaMessage(outputUri, FirebaseConstants.IMAGE, thumbnailImage, "");
                createMediaMessage(outputUri, FirebaseConstants.IMAGE, null, "");
            } else if (requestCode == MULTIPLE_IMAGE_INTENT && data != null) {
                if (data.getStringArrayListExtra("result") != null) {
                    ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                    for (String filePath : selectionResult) {
//                        Bitmap thumbnailImage = FirebaseChatUtils.getInstance().compressBitmap(BitmapFactory.decodeFile(filePath, new BitmapFactory.Options()));
//                        Log.d("Size : ", String.valueOf(thumbnailImage.getByteCount()));
//                        createMediaMessage(Uri.fromFile(new File(filePath)), FirebaseConstants.IMAGE, thumbnailImage, "");
                        createMediaMessage(Uri.fromFile(new File(filePath)), FirebaseConstants.IMAGE, null, "");
                    }
                }
            }  else if (requestCode == Constants.IntentConstant.REQUEST_GALLERY && data != null && data.getExtras() != null) {
                ArrayList<String> selectionResult = data.getExtras().getStringArrayList("result");
                if (selectionResult != null) {
                    for (String filePath : selectionResult) {
                        createMediaMessage(Uri.fromFile(new File(filePath)), FirebaseConstants.IMAGE, null, "");
                    }
                }
            }  else if (requestCode == MULTIPLE_VIDEO_INTENT && data != null) {
                if (data.getStringArrayListExtra("result") != null) {
                    ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                    for (String filePath : selectionResult) {
                        Bitmap thumbnailImage = FirebaseChatUtils.getInstance().compressBitmap(ThumbnailUtils.createVideoThumbnail
                                (filePath, MediaStore.Images.Thumbnails.MINI_KIND));
                        Log.d("Size : ", String.valueOf(thumbnailImage.getByteCount()));
                        createMediaMessage(Uri.fromFile(new File(filePath)), FirebaseConstants.VIDEO, thumbnailImage, "");
                    }
                }
            } else if (requestCode == REQUEST_FILE && data != null) {
                try {
                    Uri pdfUri = data.getData();
                    if (pdfUri != null) {
                        if (pdfUri.toString().startsWith("content://com.google") || pdfUri.toString().startsWith("content://com.dropbox")) {
                            AppUtils.getInstance().showToast(this, getString(R.string.sharing_from_cloud_not_supported));
                        } else {
                            createMediaMessage(pdfUri, FirebaseConstants.PDF, null, "");
                        }
                    }
                } catch (Exception e) {
                    AppUtils.getInstance().showToast(this, getString(R.string.uable_to_select_pdf_please_try_again_later));
                }

            } else if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
                Place place = PlacePicker.getPlace(this, data);
                getCurrentLocation(place.getLatLng());
            }
        }
    }

    /**
     * method to create local media message
     *
     * @param outputUri
     * @param fileType
     * @param thumbnailImage
     * @param duration
     */
    private void createMediaMessage(final Uri outputUri, String fileType, Bitmap thumbnailImage, String duration) {
        Uri thumbnailUri = null;
        if (thumbnailImage != null)
            thumbnailUri = FirebaseChatUtils.getInstance().getImageUri(this, thumbnailImage);
        final String messageId = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.MESSAGES_NODE).push().getKey();
        final ChatMessageBean chatMessage = new ChatMessageBean();
        chatMessage.setMessageId(messageId);
        chatMessage.setMessageText(fileType);
        chatMessage.setType(fileType);
        chatMessage.setMediaUrl(FirebaseChatUtils.getInstance().getPathFromUri(this, outputUri));
        chatMessage.setThumbnail(thumbnailUri == null ? "" : FirebaseChatUtils.getInstance().getImagePathFromUri(this, thumbnailUri));
        chatMessage.setTimestamp(Calendar.getInstance().getTimeInMillis());
        chatMessage.setStatus(FirebaseConstants.PENDING);
        chatMessage.setIsBlock(isMeBlock);
        chatMessage.setReceiverId(otherUserId);
        chatMessage.setSenderId(loginUserId);
        chatMessage.setRoomId(roomId);
        setNewChatMessage(chatMessage);
        AppDatabase.saveMediaFilesDetailsInDb(chatMessage);
        if (chatMessage.getType().equals(FirebaseConstants.IMAGE))
            imagesList.add(chatMessage.getMediaUrl());
        startUpload(chatMessage);
    }


    /**
     * dialog to block user
     */
    private void showBlockDialog() {
        new AlertDialog.Builder(this, R.style.DatePickerTheme).setTitle(getString(R.string.block) + " " + otherUser.getFirstName())
                .setMessage(getString(R.string.sure_to_block) + " " + otherUser.getFirstName() + "?")
                .setPositiveButton(getString(R.string.block), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabaseQueries.getInstance().blockUser(roomId, true, otherUser.getUserId());
                        isOtherUserBlock = true;
                        if (roomId != null) {
                            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(otherUserId)
                                    .removeEventListener((ValueEventListener) typingListener);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();

    }

    /**
     * dialog to delete messages
     */
    private void showDeleteMessageDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.delete_msg)
                .setMessage(getString(R.string.sure_to_delete_msg))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for (String messageId : selectMessagesId) {
                            FirebaseDatabaseQueries.getInstance().setMessagesDeleteStatus(roomId, messageId);
                        }
                        for (int i = 0; i < chatMessagesList.size(); i++) {
                            if (selectMessagesId.contains(chatMessagesList.get(i).getMessageId())) {
                                chatMessagesList.get(i).setIsDeleted(true);
                                selectMessagesId.remove(chatMessagesList.get(i).getMessageId());
                                selectMsgUserId.remove(chatMessagesList.get(i).getSenderId());
                                chatListAdapter.notifyItemChanged(i);
                                updateViews();
                            }
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();

    }

    /**
     * method to updates the views
     */
    private void updateViews() {
        if (layoutManager.findFirstVisibleItemPosition() == 0) {
            rvChatMessages.smoothScrollToPosition(0);
        }
        if (chatMessagesList.size() == 0) {
//            tvNoChat.setVisibility(View.VISIBLE);
            String productHuntId = productId.equals("") ? (huntId.equals("") ? "" : "h" + huntId) : productId;
            FirebaseDatabaseQueries.getInstance().deleteRoom(roomId, FirebaseConstants.SINGLE_CHAT, otherUserId, productHuntId);
        } else {
            tvNoChat.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        removeAllListeners();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        removeAllListeners();
        super.onDestroy();
    }

    /**
     * method used to remove all event listeners
     */
    private void removeAllListeners() {
        if (roomId != null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(loginUserId).setValue(false);
            if (messageListener != null) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.MESSAGES_NODE).child(roomId).removeEventListener((ChildEventListener) messageListener);
            }
            if (offerListener != null) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.OFFER).child(roomId).removeEventListener((ValueEventListener) offerListener);
            }
            if (otherUserId != null && typingListener != null) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING)
                        .child(otherUserId).removeEventListener((ValueEventListener) typingListener);
            }
        }
        if (otherUserId != null && onlineStatusListener != null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(otherUserId)
                    .child(FirebaseConstants.ONLINE_STATUS_NODE).removeEventListener((ValueEventListener) onlineStatusListener);
        }
        if (otherUserId != null && userStatusListener != null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(otherUserId)
                    .child(FirebaseConstants.STATUS_NODE).removeEventListener((ValueEventListener) userStatusListener);
        }
        if (otherUserId != null && blockListener != null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.BLOCK_NODE).child(otherUserId).child(loginUserId).removeEventListener((ValueEventListener) blockListener);

        }
        if (chatListAdapter != null && chatListAdapter.playerModel != null && chatListAdapter.playerModel.getMediaPlayer() != null) {
            try {
                if (chatListAdapter.playerModel.getMediaPlayer().isPlaying()) {
                    chatListAdapter.playerModel.getMediaPlayer().pause();
                    chatListAdapter.playerModel.getMediaPlayer().stop();
                    chatListAdapter.playerModel.getMediaPlayer().release();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * upload file in S3
     *
     * @param chatMessage
     */
    private void startUpload(final ChatMessageBean chatMessage) {
        AmazonS3 mAmazonS3 = new AmazonS3();
        mAmazonS3.setVariables(Constants.UrlConstant.AMAZON_POOLID, Constants.UrlConstant.BUCKET, Constants.UrlConstant.AMAZON_SERVER_URL, Constants.UrlConstant.END_POINT, Constants.UrlConstant.REGION);
        mAmazonS3.setActivity(this);
        if (!chatMessage.getThumbnail().equals("")) {
            ImageBean bean = addDataInBean(chatMessage.getThumbnail());
            mAmazonS3.setCallback(new AmazonCallback() {
                @Override
                public void uploadSuccess(ImageBean bean) {
                    String filePath = bean.getServerUrl();
                    chatMessage.setThumbnail(filePath);
                    Uri outputUri = Uri.fromFile(new File(chatMessage.getMediaUrl()));
                    uploadMediaToS3(chatMessage, outputUri);
                }

                @Override
                public void uploadFailed(ImageBean bean) {
                    chatMessage.setStatus(FirebaseConstants.FAILED);
                    chatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void uploadProgress(ImageBean bean) {
                    AppUtils.getInstance().printLogMessage(FirebaseConstants.TAG, "Uploaded " + bean.getProgress() + " %");
                }

                @Override
                public void uploadError(Exception e, ImageBean imageBean) {
                    chatMessage.setStatus(FirebaseConstants.FAILED);
                    chatListAdapter.notifyDataSetChanged();
                }
            });
            mAmazonS3.uploadImage(bean);
        } else {
            if (chatMessage.getMediaUrl() != null) {
                Uri outputUri = Uri.fromFile(new File(chatMessage.getMediaUrl()));
                uploadMediaToS3(chatMessage, outputUri);
            }else {
                FirebaseChatUtils.getInstance().showToast(SingleChatActivity.this, getString(R.string.file_not_found_for_send));
            }
        }
    }

    /**
     * methos to upload media file to s3
     * @param chatMessage
     * @param outputUri
     */
    private void uploadMediaToS3(final ChatMessageBean chatMessage, Uri outputUri) {
        AmazonS3 mAmazonS3 = new AmazonS3();
        mAmazonS3.setVariables(Constants.UrlConstant.AMAZON_POOLID, Constants.UrlConstant.BUCKET, Constants.UrlConstant.AMAZON_SERVER_URL, Constants.UrlConstant.END_POINT, Constants.UrlConstant.REGION);
        mAmazonS3.setActivity(this);
        if (chatMessage.getMediaUrl() != null) {
            ImageBean bean = addDataInBean(chatMessage.getMediaUrl());
            bean.setFileType(chatMessage.getType().equals(FirebaseConstants.VIDEO) ? 2 : chatMessage.getType().equals(FirebaseConstants.FILE) ? 3 : 1);
            mAmazonS3.setCallback(new AmazonCallback() {
                @Override
                public void uploadSuccess(ImageBean bean) {
                    String filePath = bean.getServerUrl();
                    chatMessage.setMediaUrl(filePath);
                    chatMessage.setStatus(FirebaseConstants.SEND);
                    createMessage(chatMessage);
                }

                @Override
                public void uploadFailed(ImageBean bean) {
                    chatMessage.setStatus(FirebaseConstants.FAILED);
                    chatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void uploadProgress(ImageBean bean) {
                    AppUtils.getInstance().printLogMessage(FirebaseConstants.TAG, "Uploaded " + bean.getProgress() + " %");
                }

                @Override
                public void uploadError(Exception e, ImageBean imageBean) {
                    chatMessage.setStatus(FirebaseConstants.FAILED);
                    chatListAdapter.notifyDataSetChanged();
                }
            });
            mAmazonS3.uploadImage(bean);
        }else {
            FirebaseChatUtils.getInstance().showToast(SingleChatActivity.this, getString(R.string.file_not_found_for_send));
        }
    }

    /**
     * create image bean object
     *
     * @param path
     * @return
     */
    private ImageBean addDataInBean(String path) {
        ImageBean bean = new ImageBean();
        bean.setId("1");
        bean.setName("sample");
        bean.setImagePath(path);
        return bean;
    }

}
