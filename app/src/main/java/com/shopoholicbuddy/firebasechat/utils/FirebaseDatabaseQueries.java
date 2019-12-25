package com.shopoholicbuddy.firebasechat.utils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseAuthListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseBlockStatusListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseHuntResponseListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseMessageListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseOfferListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseProductResponseListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseRoomResponseListener;
import com.shopoholicbuddy.firebasechat.interfaces.FirebaseUserListener;
import com.shopoholicbuddy.firebasechat.models.ChatMessageBean;
import com.shopoholicbuddy.firebasechat.models.ChatRoomBean;
import com.shopoholicbuddy.firebasechat.models.GroupDetailBean;
import com.shopoholicbuddy.firebasechat.models.HuntDeal;
import com.shopoholicbuddy.firebasechat.models.NotificationBean;
import com.shopoholicbuddy.firebasechat.models.OfferModel;
import com.shopoholicbuddy.firebasechat.models.ProductBean;
import com.shopoholicbuddy.firebasechat.models.UserBean;
import com.shopoholicbuddy.models.productservicedetailsresponse.Result;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

/**
 * Class contains queries of firebase database
 */

public class FirebaseDatabaseQueries {

    private static FirebaseDatabaseQueries instance;
    private static DatabaseReference firebaseDatabaseRef;
    private static FirebaseAuth mAuth;
    private static Random randomNumber;
    private static String loginUserId;
    private static String loginUserName;
    private UserBean currentUser;

    private FirebaseDatabaseQueries() {
        if (firebaseDatabaseRef == null) {
            firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            randomNumber = new Random();
        }
    }

    /**
     * Method ro grt instance of class
     * @return
     */
    public static FirebaseDatabaseQueries getInstance() {
        if (instance == null)
            instance = new FirebaseDatabaseQueries();
        return instance;
    }

    public void updateUserData(Context mContext) {
        loginUserId = AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID);
        loginUserName = AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.FIRST_NAME) + " " +
                AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.LAST_NAME);
        FirebaseStorageQueries.getInstance().updateUserData(mContext);
        getCurrentUser(mContext);
    }


    /**
     * Method to get the current user
     */
    public UserBean getCurrentUser(Context mContext) {
        UserBean loginUser = new UserBean();
        loginUser.setUserId(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID));
        loginUser.setFirstName(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.FIRST_NAME));
        loginUser.setLastName(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.LAST_NAME));
        loginUser.setUserImage(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_IMAGE));
        loginUser.setEmail(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.EMAIL));
        loginUser.setDeviceToken(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.DEVICE_TOKEN));
        currentUser = loginUser;
        return loginUser;
    }

    /**
     * sign in firebase with email password
     *
     * @param userId
     * @param emailAddress  user email address
     * @param password      user password
     * @param authListener  interface for handle result
     */
    public void signInFirebaseDatabase(String userId, String emailAddress, final String password, final FirebaseAuthListener authListener) {
        if (emailAddress == null || emailAddress.equals("")) emailAddress = userId + "@shopoholic.com";
        final String finalEmailAddress = emailAddress;
        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    authListener.onAuthSuccess(task, user);
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.getException());
//                    authListener.onAuthError(task);
                    createUserInFirebaseDatabase(finalEmailAddress, password, authListener);
                }
            }
        });
    }

    /**
     * create user in firebase with email password
     *
     * @param emailAddress  user email address
     * @param password      user password
     * @param authListener  interface for handle result
     */
    private void createUserInFirebaseDatabase(final String emailAddress, final String password, final FirebaseAuthListener authListener) {
        mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    authListener.onAuthSuccess(task, user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    authListener.onAuthError(task);
                }
            }
        });
    }

    /**
     * Method to create user node in firebase database
     *
     * @param mContext
     */
    public void createUser(Context mContext) {
        HashMap<String, Object> userBean = new HashMap<>();
        userBean.put(FirebaseConstants.USER_ID, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID));
        userBean.put(FirebaseConstants.FIRST_NAME, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.FIRST_NAME));
        userBean.put(FirebaseConstants.LAST_NAME, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.LAST_NAME));
        userBean.put(FirebaseConstants.EMAIL, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.EMAIL));
        userBean.put(FirebaseConstants.USER_IMAGE, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_IMAGE));
        userBean.put(FirebaseConstants.COUNTRY_CODE, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.COUNTRY_CODE));
        userBean.put(FirebaseConstants.MOBILE_NO, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.PHONE_NO));
        userBean.put(FirebaseConstants.DEVICE_TOKEN, AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.DEVICE_TOKEN));
        userBean.put(FirebaseConstants.DEVICE_TYPE, "1");
        userBean.put(FirebaseConstants.ONLINE_STATUS, true);
        userBean.put(FirebaseConstants.STATUS, AppSharedPreference.getInstance().getInt(mContext, AppSharedPreference.PREF_KEY.USER_ONLINE_STATUS));
        updateUserData(mContext);
        firebaseDatabaseRef.child(FirebaseConstants.USERS_NODE).child(userBean.get(FirebaseConstants.USER_ID).toString()).setValue(userBean);
    }

    /**
     * Method to get login user details from firebase database
     *
     * @param emailAddress   email of login User
     */
    public void getLoginUser(String emailAddress, final FirebaseUserListener userListener) {
        firebaseDatabaseRef.child(FirebaseConstants.USERS_NODE).orderByChild(FirebaseConstants.EMAIL).equalTo(emailAddress)
                .addListenerForSingleValueEvent(new FirebaseEventListeners() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                UserBean userBean = snapshot.getValue(UserBean.class);
                                userListener.getUser(userBean);
                                break;
                            }
                        }
                    }
                });
    }

    /**
     * Method to get login user details from firebase database
     *
     * @param userId   userId of User
     */
    public void getUser(String userId, final FirebaseUserListener userListener) {
        firebaseDatabaseRef.child(FirebaseConstants.USERS_NODE).child(userId)
                .addListenerForSingleValueEvent(new FirebaseEventListeners() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            UserBean userBean = dataSnapshot.getValue(UserBean.class);
                            userListener.getUser(userBean);
                        }
                    }
                });
    }

    /**
     * Method to create and send text message
     * @param chatMessage
     * @param users
     * @param chatType
     */
    public void sendChatMessage(ChatMessageBean chatMessage, List<UserBean> users, String chatType, String groupName, String productId) {
        firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(chatMessage.getRoomId()).child(FirebaseConstants.CHAT_LAST_UPDATE)
                .setValue(ServerValue.TIMESTAMP);

        chatMessage.setStatus(FirebaseConstants.SEND);
        chatMessage.setTimestamp(ServerValue.TIMESTAMP);

        createInboxNode(chatMessage, users , chatType, productId);
        createMessageNode(chatMessage, users, chatType, groupName);
    }

    /**
     * Method to create message node in firebase database
     * @param chatMessage
     * @param users
     * @param groupName
     */
    private void createMessageNode(ChatMessageBean chatMessage, List<UserBean> users, String chatType, String groupName) {
        chatMessage.setIsDeleted(false);
        if (chatMessage.getMessageId() == null || chatMessage.getMessageId().equals("")) {
            String messageId = firebaseDatabaseRef.child(FirebaseConstants.MESSAGES_NODE).child(chatMessage.getRoomId()).push().getKey();
            chatMessage.setMessageId(messageId);
        }

        firebaseDatabaseRef.child(FirebaseConstants.MESSAGES_NODE).child(chatMessage.getRoomId()).child(chatMessage.getMessageId()).setValue(chatMessage);

        firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(chatMessage.getRoomId()).child(loginUserId).removeValue();
        firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(chatMessage.getRoomId()).child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).setValue(chatMessage);

        if (!chatMessage.getIsBlock()){
            firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(chatMessage.getRoomId()).child(chatMessage.getReceiverId()).removeValue();
            if (users != null && !chatMessage.getType().equals(FirebaseConstants.ACTION)) {
                sendNotification(chatMessage, users, chatType, groupName);
                for (UserBean user : users) {
                    if (user.getUserId() != null && !user.getUserId().equals(loginUserId))
                        setUnreadCount(user.getUserId(), chatMessage.getRoomId());
                }
            }
        }
    }

    /**
     * methed to create chat room
     * @param users
     * @param chatType
     */

    public void createChatRoom(List<UserBean> users, String chatType, String roomId, String chatTitle, String chatPic) {
        HashMap<String, Object> chatRoom = new HashMap<>();
        chatRoom.put(FirebaseConstants.CHAT_ROOM_ID, roomId);
        chatRoom.put(FirebaseConstants.CHAT_ROOM_TITLE, chatTitle);
        chatRoom.put(FirebaseConstants.CHAT_ROOM_TYPE, chatType);
        chatRoom.put(FirebaseConstants.CHAT_ROOM_PIC, chatPic);
        chatRoom.put(FirebaseConstants.CHAT_LAST_UPDATE, ServerValue.TIMESTAMP);
        HashMap<String, Boolean> isTyping = new HashMap<>();
        isTyping.put(loginUserId, false);
        if (chatType.equals(FirebaseConstants.SINGLE)){
            isTyping.put(users.get(0).getUserId().equals(loginUserId) ? users.get(1).getUserId() : users.get(0).getUserId(), false);
        }
        chatRoom.put(FirebaseConstants.CHAT_ROOM_IS_TYPING, isTyping);
        HashMap<String, Object> lastUpdates = new HashMap<>();
        HashMap<String, Object> roomMembers = new HashMap<>();
        for (UserBean user : users) {
            lastUpdates.put(user.getUserId(), user.getUserId().equals(loginUserId) ? ServerValue.TIMESTAMP : 0);
            HashMap<String, Object> memberStatus = new HashMap<>();
            memberStatus.put(FirebaseConstants.MEMBER_JOIN_NODE, ServerValue.TIMESTAMP);
            memberStatus.put(FirebaseConstants.MEMBER_DELETE_NODE, ServerValue.TIMESTAMP);
            memberStatus.put(FirebaseConstants.MEMBER_LEAVE_NODE, 0);
            roomMembers.put(user.getUserId(), memberStatus);

        }
        chatRoom.put(FirebaseConstants.CHAT_LAST_UPDATES, lastUpdates);
        chatRoom.put(FirebaseConstants.CHAT_ROOM_MEMBERS, roomMembers);
        firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).setValue(chatRoom);
    }




    /**
     * method to create product
     * @param productDetails
     */
    public void createProductNode(Result productDetails) {
        if (productDetails != null) {
            HashMap<String, Object> product = new HashMap<>();
            product.put(FirebaseConstants.PRODUCT_ID, productDetails.getId());
            product.put(FirebaseConstants.PRODUCT_NAME, productDetails.getName());
            product.put(FirebaseConstants.PRODUCT_PRICE, productDetails.getSellingPrice());
            product.put(FirebaseConstants.PRODUCT_CURRENCY, productDetails.getCurrencySymbol());
            product.put(FirebaseConstants.PRODUCT_QUANTITY, productDetails.getQuantity());

            String image = productDetails.getDealImages().split(",")[0];
            product.put(FirebaseConstants.PRODUCT_IMAGE, image != null && !image.equals("") ? image : "");

            product.put(FirebaseConstants.HOME_DELIVERY, productDetails.getHomeDelivery());//home delivery
            product.put(FirebaseConstants.PAYMENT_MODE, productDetails.getPaymentMethod());//paymode
            product.put(FirebaseConstants.USER_TYPE, productDetails.getUserType());//usertype
            product.put(FirebaseConstants.BUDDY_ID, "");//buddyid
            product.put(FirebaseConstants.PRODUCT_TYPE, productDetails.getProductType());//pro type
            product.put(FirebaseConstants.SLOT_ID, "");//slotid
            product.put(FirebaseConstants.DELIVERY_CHARGES, productDetails.getDileveryCharge());//delivery charges
            product.put(FirebaseConstants.MERCHANT_FIRST_NAME, productDetails.getFirstName());//merchantName
            product.put(FirebaseConstants.MERCHANT_LAST_NAME, productDetails.getLastName());//merchantName

            product.put(FirebaseConstants.ORIGINAL_PRICE, productDetails.getOrignalPrice());
            product.put(FirebaseConstants.DISCOUNT, productDetails.getDiscount());

            String dealStartTime, dealEndTime;
            dealStartTime = AppUtils.getInstance().formatDate(productDetails.getDealStartTime(), SERVICE_DATE_FORMAT, DATE_FORMAT);
            dealEndTime = AppUtils.getInstance().formatDate(productDetails.getDealEndTime(), SERVICE_DATE_FORMAT, DATE_FORMAT);
            product.put(FirebaseConstants.DEAL_START_TIME, dealStartTime);
            product.put(FirebaseConstants.DEAL_END_TIME, dealEndTime);
            firebaseDatabaseRef.child(FirebaseConstants.CHAT_ROOM_PRODUCT).child(productDetails.getId()).setValue(product);
        }
    }

    /**
     * Method to create inbox node
     * @param chatMessage
     * @param users
     * @param chatType
     */
    private void createInboxNode(final ChatMessageBean chatMessage, List<UserBean> users, String chatType, String productId) {
        if (users != null) {
            for (UserBean user : users){
                if (user.getUserId().equals(loginUserId)) {
                    if (chatType.equals(FirebaseConstants.SINGLE)){
                        if (!chatMessage.getIsBlock()) {
                            String userId = !productId.equals("") ? chatMessage.getReceiverId() + "_" + productId : chatMessage.getReceiverId() + "_single";
                            firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(loginUserId).child(userId).setValue(chatMessage.getRoomId());
                        }
                    }else {
                        firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(loginUserId).child(chatMessage.getReceiverId()).setValue(chatMessage.getRoomId());
                    }
                }else {
                    if (chatType.equals(FirebaseConstants.SINGLE)){
                        if (!chatMessage.getIsBlock()) {
                            String userId = !productId.equals("") ? loginUserId + "_" + productId : loginUserId + "_single";
                            firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(user.getUserId()).child(userId).setValue(chatMessage.getRoomId());

                        }
                    }else {
                        firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(user.getUserId()).child(chatMessage.getRoomId()).setValue(chatMessage.getRoomId());
                    }
                }


            }
        }
    }

    /**
     * Method used to cerate other user inbox entry node
     * @param otherUserId
     * @param loginUserOrRoomId
     * @param roomId
     */
    private void setOtherUserInbox(final String otherUserId, final String loginUserOrRoomId, String roomId) {
        firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(otherUserId).child(loginUserOrRoomId).setValue(roomId);
    }

    /**
     * Method to get the room id between two users
     * @param firstUserId
     * @param secondUserId
     * @param productId
     * @param firebaseRoomResponseListener
     */
    public void getRoomId(String firstUserId, String secondUserId, String productId, final FirebaseRoomResponseListener firebaseRoomResponseListener) {
        firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(firstUserId + "_" + productId + "_" + secondUserId).child(FirebaseConstants.CHAT_ROOM_ID)
                .addListenerForSingleValueEvent(new FirebaseEventListeners() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            firebaseRoomResponseListener.getRoomId(dataSnapshot.getValue().toString());
                        }
                    }
                });
    }

    /**
     * Method to get the room details
     * @param roomId
     * @param firebaseRoomResponseListener
     */
    public void getRoomDetails(String roomId, final FirebaseRoomResponseListener firebaseRoomResponseListener) {
        firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        ChatRoomBean roomBean = dataSnapshot.getValue(ChatRoomBean.class);
                        firebaseRoomResponseListener.getRoomDetails(roomBean);
                    } catch (Exception e) {
                        ChatRoomBean roomBean = getRoomInfo(dataSnapshot);
                        firebaseRoomResponseListener.getRoomDetails(roomBean);   }
                }
            }
        });

    }


    /**
     * get room info
     * @param dataSnapshot
     */
    public ChatRoomBean getRoomInfo(DataSnapshot dataSnapshot) {
        ChatRoomBean roomBean = null;
        try {
            roomBean = new ChatRoomBean();
            roomBean.setPrice(dataSnapshot.child(FirebaseConstants.PRICE).getValue().toString());
            roomBean.setChatRoomTitle(dataSnapshot.child(FirebaseConstants.CHAT_ROOM_TITLE).getValue().toString());
            roomBean.setBuddyId(dataSnapshot.child(FirebaseConstants.BUDDY_ID).getValue().toString());
            roomBean.setChatRoomType(dataSnapshot.child(FirebaseConstants.CHAT_ROOM_TYPE).getValue().toString());
            roomBean.setProductId(dataSnapshot.child(FirebaseConstants.CHAT_ROOM_PRODUCT_ID).getValue().toString());
            roomBean.setChatLastUpdate(dataSnapshot.child(FirebaseConstants.CHAT_LAST_UPDATE).getValue().toString());
            roomBean.setChatRoomId(dataSnapshot.child(FirebaseConstants.CHAT_ROOM_ID).getValue().toString());
            roomBean.setChatRoomPic(dataSnapshot.child(FirebaseConstants.CHAT_ROOM_PIC).getValue().toString());

            ArrayList<Objects> updateArray = (ArrayList<Objects>) dataSnapshot.child(FirebaseConstants.CHAT_LAST_UPDATES).getValue();
            ArrayList<Objects> memberArray = (ArrayList<Objects>) dataSnapshot.child(FirebaseConstants.CHAT_ROOM_MEMBERS).getValue();
            ArrayList<Boolean> typingArray = (ArrayList<Boolean>) dataSnapshot.child(FirebaseConstants.CHAT_ROOM_IS_TYPING).getValue();
            HashMap<String, Object> update = new HashMap<>();
            HashMap<String, Object> member = new HashMap<>();
            HashMap<String, Boolean> typing = new HashMap<>();
            for (int i=0; i<updateArray.size(); i++) {
                Object lastUpdate = updateArray.get(i);
                if (lastUpdate != null) {
                    update.put(String.valueOf(i), lastUpdate);
                }
            }
            for (int i=0; i<memberArray.size(); i++) {
                Object ob = memberArray.get(i);
                if (ob != null) {
                    member.put(String.valueOf(i), ob);
                }
            }
            for (int i=0; i<typingArray.size(); i++) {
                Boolean ob = typingArray.get(i);
                if (ob != null) {
                    typing.put(String.valueOf(i), ob);
                }
            }
            roomBean.setChatLastUpdates(update);
            roomBean.setChatRoomMembers(member);
            roomBean.setChatRoomIsTyping(typing);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return roomBean;
    }


    /**
     * Method to get the room details
     * @param productId
     * @param firebaseProductResponseListener
     */
    public void getProductDetails(String productId, final FirebaseProductResponseListener firebaseProductResponseListener) {
        firebaseDatabaseRef.child(FirebaseConstants.CHAT_ROOM_PRODUCT).child(productId).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ProductBean productBean = dataSnapshot.getValue(ProductBean.class);
                    firebaseProductResponseListener.getProductDetails(productBean);
                }
            }
        });

    }



    /**
     * Method to get the hunt details
     * @param  huntId
     * @param firebaseHuntResponseListener
     */
    public void getHuntDetails(String huntId, final FirebaseHuntResponseListener firebaseHuntResponseListener) {
        firebaseDatabaseRef.child(FirebaseConstants.CHAT_ROOM_HUNT).child(huntId).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HuntDeal huntData = dataSnapshot.getValue(HuntDeal.class);
                    firebaseHuntResponseListener.getHuntDetails(huntData);
                }
            }
        });

    }


    /**
     * Method to get the block status
     * @param otherUserId
     * @param firebaseBlockStatusListener
     */
    public void getBlockStatus(String otherUserId, final FirebaseBlockStatusListener firebaseBlockStatusListener) {
        firebaseDatabaseRef.child(FirebaseConstants.BLOCK_NODE).child(loginUserId).child(otherUserId)
                .addListenerForSingleValueEvent(new FirebaseEventListeners() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            firebaseBlockStatusListener.isUserBlock(true);
                        } else {
                            firebaseBlockStatusListener.isUserBlock(false);
                        }
                    }
                });
    }

    /**
     * Method to get all the room messages
     * @param roomId
     * @param deleteStamp
     * @param timeStamp
     * @param listener
     */
    public void getPreviousMessages(String roomId, Long deleteStamp, Long timeStamp, final FirebaseMessageListener listener) {
        firebaseDatabaseRef.child(FirebaseConstants.MESSAGES_NODE).child(roomId).orderByChild(FirebaseConstants.TIME_STAMP).limitToLast(10)
                .startAt(deleteStamp).endAt(timeStamp).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    List<ChatMessageBean> chatMessageList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ChatMessageBean chatMessage = snapshot.getValue(ChatMessageBean.class);
                            if (chatMessage != null && !chatMessage.getIsBlock() && !chatMessage.getIsDeleted()){
                                chatMessageList.add(chatMessage);
                        }
                    }
                    Collections.reverse(chatMessageList);
                    listener.getMessagesList(chatMessageList);
                }
            }
        });
    }

    /**
     * Method to change the message status to read
     * @param roomId
     * @param messageId
     */
    public void changeMessageStatus(String roomId, String messageId) {
        firebaseDatabaseRef.child(FirebaseConstants.MESSAGES_NODE).child(roomId).child(messageId).child(FirebaseConstants.MESSAGE_STATUS).setValue(FirebaseConstants.READ);
    }


    /**
     * Method to change the user status
     * @param mContext
     * @param status
     */
    public void changeUserStatus(Context mContext, boolean status) {
        if (!AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID).equals("")) {
            firebaseDatabaseRef.child(FirebaseConstants.USERS_NODE)
                    .child(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID))
                    .child(FirebaseConstants.ONLINE_STATUS_NODE).setValue(status);
        }
    }


    /**
     * Method to change the user status
     * @param mContext
     * @param status
     */
    public void setUserStatus(Context mContext, int status) {
        if (!AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID).equals("")) {
            firebaseDatabaseRef.child(FirebaseConstants.USERS_NODE)
                    .child(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID))
                    .child(FirebaseConstants.STATUS_NODE).setValue(status);
        }
    }


    /**
     * Method used to block user
     * @param roomId
     * @param isBlock
     * @param userId
     */
    public void blockUser(final String roomId, boolean isBlock, String userId) {
        if (isBlock){
            firebaseDatabaseRef.child(FirebaseConstants.BLOCK_NODE).child(loginUserId).child(userId).setValue(ServerValue.TIMESTAMP);
            if (roomId != null) {
                firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId).child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).addListenerForSingleValueEvent(new FirebaseEventListeners() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId).child(loginUserId)
                                    .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).setValue(dataSnapshot.getValue());
                        }
                    }
                });
            }
        }
        else {
            firebaseDatabaseRef.child(FirebaseConstants.BLOCK_NODE).child(loginUserId).child(userId).removeValue();
        }
    }

    /**
     * Method to create group
     * @param mContext
     * @param groupDetailBean
     */
    public void createGroup(Context mContext, GroupDetailBean groupDetailBean) {
        groupDetailBean.getGroupMembers().add(0, getCurrentUser(mContext));
        FirebaseDatabaseQueries.getInstance().createChatRoom(groupDetailBean.getGroupMembers(), FirebaseConstants.GROUP_CHAT, groupDetailBean.getGroupRoomId(),
                groupDetailBean.getGroupName(), groupDetailBean.getGroupImage());


        for (UserBean user : groupDetailBean.getGroupMembers()) {
            ChatMessageBean messageBean = new ChatMessageBean();
            messageBean.setIsBlock(false);
            messageBean.setMediaUrl("");
            messageBean.setMessageId("");
            messageBean.setRoomId(groupDetailBean.getGroupRoomId());
            messageBean.setStatus(FirebaseConstants.SEND);
            messageBean.setTimestamp(ServerValue.TIMESTAMP);
            messageBean.setType(FirebaseConstants.ACTION);
            messageBean.setReceiverId(groupDetailBean.getGroupRoomId());
            messageBean.setSenderId(loginUserId);
            messageBean.setThumbnail("");
            if (user.getUserId().equals(loginUserId)) {
                createInboxNode(messageBean, groupDetailBean.getGroupMembers(), FirebaseConstants.GROUP_CHAT, "");
                messageBean.setMessageText(user.getFirstName() + " " + mContext.getString(R.string.created_group) + " \"" + groupDetailBean.getGroupName() + "\"" );
            }else {
                messageBean.setMessageText(getCurrentUser(mContext).getFirstName() + " " + mContext.getString(R.string.added) + " " + user.getFirstName());
            }
            createMessageNode(messageBean, null, FirebaseConstants.GROUP_CHAT, "");
        }
    }

    /**
     * Method to add new members in group
     * @param mContext
     * @param roomId
     * @param userList
     */
    public void addNewMembers(Context mContext, String roomId, final List<UserBean> userList) {
        for (UserBean member : userList) {
            final ChatMessageBean messageBean = new ChatMessageBean();
            messageBean.setIsBlock(false);
            messageBean.setMediaUrl("");
            messageBean.setMessageId("");
            messageBean.setRoomId(roomId);
            messageBean.setStatus(FirebaseConstants.SEND);
            messageBean.setTimestamp(ServerValue.TIMESTAMP);
            messageBean.setType(FirebaseConstants.ACTION);
            messageBean.setReceiverId(roomId);
            messageBean.setSenderId(loginUserId);
            messageBean.setThumbnail("");
            messageBean.setMessageText(getCurrentUser(mContext).getFirstName() + " " + mContext.getString(R.string.added) + " " + member.getFirstName());
            setOtherUserInbox(member.getUserId(), roomId, roomId);
            HashMap<String, Object> memberStatus = new HashMap<>();
            memberStatus.put(FirebaseConstants.MEMBER_JOIN_NODE, ServerValue.TIMESTAMP);
            memberStatus.put(FirebaseConstants.MEMBER_DELETE_NODE, ServerValue.TIMESTAMP);
            memberStatus.put(FirebaseConstants.MEMBER_LEAVE_NODE, 0);
            firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_LAST_UPDATES_NODE)
                    .child(member.getUserId()).setValue(0);
            firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_MEMBERS_NODE)
                    .child(member.getUserId()).setValue(memberStatus);
            firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId).child(member.getUserId()).removeValue();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createMessageNode(messageBean, null, FirebaseConstants.GROUP_CHAT, "");

                }
            },1000);
        }
    }

    /**
     * Method called when user leave group chat
     * @param mContext
     * @param roomId
     */
    public void leaveGroup(Context mContext, final String roomId) {
       if (roomId != null) {
           final ChatMessageBean messageBean = new ChatMessageBean();
           messageBean.setIsBlock(false);
           messageBean.setMediaUrl("");
           messageBean.setMessageId("");
           messageBean.setRoomId(roomId);
           messageBean.setStatus(FirebaseConstants.SEND);
           messageBean.setTimestamp(ServerValue.TIMESTAMP);
           messageBean.setType(FirebaseConstants.ACTION);
           messageBean.setReceiverId(roomId);
           messageBean.setSenderId(loginUserId);
           messageBean.setThumbnail("");
           messageBean.setMessageText(getCurrentUser(mContext).getFirstName() + " " + mContext.getString(R.string.left_group));
           createMessageNode(messageBean, null, FirebaseConstants.GROUP_CHAT, "");
           firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId).child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).addListenerForSingleValueEvent(new FirebaseEventListeners() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId).child(loginUserId)
                                .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).setValue(dataSnapshot.getValue());
                    }
                }
            });

           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_MEMBERS_NODE)
                           .child(loginUserId).child(FirebaseConstants.MEMBER_LEAVE_NODE).setValue(ServerValue.TIMESTAMP);
               }
           },1000);
        }
    }

    /**
     * Method to send notification
     */
    private void sendNotification(final ChatMessageBean chatMessageBean, List<UserBean> users, final String chatType, final String groupName) {
        for (UserBean user : users) {
            if (user.getUserId() != null && !user.getUserId().equals(loginUserId)) {
                firebaseDatabaseRef.child(FirebaseConstants.USERS_NODE).child(user.getUserId())
                        .addListenerForSingleValueEvent(new FirebaseEventListeners() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    UserBean userBean = dataSnapshot.getValue(UserBean.class);
                                    if (userBean != null) {

                                        try {
                                            HashMap<String, String> headerParams = new HashMap<>();
                                            headerParams.put(FirebaseConstants.PARAM_CONTENT_TYPE, FirebaseConstants.APPLICATION_JSON);
                                            headerParams.put(FirebaseConstants.PARAM_AUTHORIZATION, FirebaseConstants.FIREBASE_SERVER_KEY);

                                            JSONObject finalMap = new JSONObject();
                                            JSONObject jsonData = new JSONObject();
                                            JSONObject jsonNotification = new JSONObject();
                                            NotificationBean notificationBean = new NotificationBean();
//                                            notificationBean.setMessageId(chatMessageBean.getMessageId());
                                            notificationBean.setMessageText(chatMessageBean.getMessageText());
                                            notificationBean.setRoomId(chatMessageBean.getRoomId());
                                            notificationBean.setRoomName(groupName);
                                            notificationBean.setRoomType(chatType);
                                            notificationBean.setImageUrl(chatMessageBean.getMediaUrl());
                                            notificationBean.setSenderId(currentUser.getUserId());
                                            notificationBean.setSenderName(currentUser.getFirstName());
                                            notificationBean.setType(10);
                                            chatMessageBean.setTimestamp(Calendar.getInstance().getTimeInMillis());

                                            finalMap.put("to", userBean.getDeviceToken());

                                            if (userBean.getDeviceType().equals("1")) {
                                                jsonData.put("alert", new Gson().toJson(notificationBean));
                                                finalMap.put("data", jsonData);
                                                finalMap.put("sound", "default");
                                                finalMap.put("priority", "High");
                                                finalMap.put("forceStart", "1");
                                                finalMap.put("forceShow", true);
                                                finalMap.put("collapse_key", "Updates Available");
                                                finalMap.put("content_available", true);
                                            } else {
                                                jsonNotification.put("aps", new Gson().toJson(notificationBean));
                                                jsonNotification.put("sound", "default");
                                                jsonNotification.put("priority", "High");
                                                jsonNotification.put("forceStart", "1");
                                                jsonNotification.put("forceShow", true);
                                                jsonNotification.put("collapse_key", "Updates Available");
                                                jsonNotification.put("content_available", true);
                                                jsonNotification.put("title", currentUser.getFirstName() + (chatType.equals(FirebaseConstants.SINGLE) ? "" : "@" + groupName));
                                                jsonNotification.put("body", chatMessageBean.getMessageText());
                                                finalMap.put("notification", jsonNotification);
                                            }

                                            AndroidNetworking.post(FirebaseConstants.SEND_PUSH_NOTIFICATION)
                                                    .addHeaders(headerParams)
                                                    .addJSONObjectBody(finalMap)
                                                    .setTag(TAG)
                                                    .setPriority(Priority.HIGH)
                                                    .build()
                                                    .getAsString(new StringRequestListener() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Log.e("AFN Stop Time", System.currentTimeMillis() + "");
                                                        }

                                                        @Override
                                                        public void onError(ANError anError) {
                                                            Log.e("error", anError.getErrorDetail());
                                                        }
                                                    });

                                        }catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
            }
        }
    }

    /**
     * Method to set delete status of message
     * @param roomId
     * @param messageId
     */
    public void setMessagesDeleteStatus(String roomId, String messageId) {
        firebaseDatabaseRef.child(FirebaseConstants.MESSAGES_NODE).child(roomId).child(messageId).child(FirebaseConstants.IS_DELETE).setValue(true);
    }


    /**
     * Method to delete room
     * @param roomId
     * @param productId
     */
    public void deleteRoom(String roomId, String roomType, String otherUserId, String productId) {
        firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_MEMBERS_NODE).child(loginUserId)
                .child(FirebaseConstants.MEMBER_DELETE_NODE).setValue(ServerValue.TIMESTAMP);
        if (roomType.equalsIgnoreCase(FirebaseConstants.SINGLE)) {
            if (!productId.equals("")) otherUserId = otherUserId + "_" + productId;
            else otherUserId = otherUserId + "_single";
            firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(loginUserId).child(otherUserId).removeValue();
        } else {
            firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(loginUserId).child(roomId).removeValue();
        }

    }



    /**
     * method to make an offer
     */

    public void makeAnOffer(String roomId, String message, String currency, String price) {
        HashMap<String, Object> offerModel = new HashMap<>();
        offerModel.put(FirebaseConstants.STATUS, "3");
//        offerModel.put(FirebaseConstants.CURRENCY, currency);
        offerModel.put(FirebaseConstants.PRICE, price);
//        offerModel.put(FirebaseConstants.MESSAGE, message);
        offerModel.put(FirebaseConstants.TIME_STAMP, ServerValue.TIMESTAMP);
        firebaseDatabaseRef.child(FirebaseConstants.OFFER).child(roomId).setValue(offerModel);
    }
    /**
     * method to get the order status
     * @param mContext
     * @param roomId
     * @param firebaseOfferListener
     */
    public void getOfferStatus(Context mContext, String roomId, final FirebaseOfferListener firebaseOfferListener) {
        firebaseDatabaseRef.child(FirebaseConstants.OFFER).child(roomId)
                .addListenerForSingleValueEvent(new FirebaseEventListeners(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null){
                            firebaseOfferListener.getOffer(dataSnapshot.getValue(OfferModel.class));
                        }
                    }
                });
    }

    /**
     * method call when accept order
     * @param roomId
     */
    public void acceptOffer(String roomId) {
        firebaseDatabaseRef.child(FirebaseConstants.OFFER).child(roomId).child(FirebaseConstants.STATUS).setValue("1");
        firebaseDatabaseRef.child(FirebaseConstants.OFFER).child(roomId).child(FirebaseConstants.TIME_STAMP).setValue(ServerValue.TIMESTAMP);
    }

    /**
     * method call when reject offer
     * @param roomId
     */
    public void rejectOffer(String roomId) {
        firebaseDatabaseRef.child(FirebaseConstants.OFFER).child(roomId).child(FirebaseConstants.STATUS).setValue("0");
        firebaseDatabaseRef.child(FirebaseConstants.OFFER).child(roomId).child(FirebaseConstants.TIME_STAMP).setValue(ServerValue.TIMESTAMP);
    }

    /**
     * method call when accept order
     * @param roomId
     * @param status
     */
    public void changeOfferStatus(String roomId, String status) {
        firebaseDatabaseRef.child(FirebaseConstants.OFFER).child(roomId).child(FirebaseConstants.STATUS).setValue(status);
        firebaseDatabaseRef.child(FirebaseConstants.OFFER).child(roomId).child(FirebaseConstants.TIME_STAMP).setValue(ServerValue.TIMESTAMP);

    }

    /**
     * function to update unread count
     * @param context
     * @param roomId
     */
    public void updateUnreadCount(Context context, String roomId) {
        firebaseDatabaseRef.child(FirebaseConstants.UNREAD_COUNT)
                .child(AppSharedPreference.getInstance().getString(context, AppSharedPreference.PREF_KEY.USER_ID))
                .child(roomId).setValue(0);
    }

    /**
     * function to update unread count
     * @param roomId
     */
    public void setUnreadCount(String userId, String roomId) {
        firebaseDatabaseRef.child(FirebaseConstants.UNREAD_COUNT).child(userId).child(roomId).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                   try {
                       long count = (long) dataSnapshot.getValue();
                       firebaseDatabaseRef.child(FirebaseConstants.UNREAD_COUNT).child(userId).child(roomId).setValue(++count);
                   }catch (Exception e) {
                       e.printStackTrace();
                   }
                }else {
                    firebaseDatabaseRef.child(FirebaseConstants.UNREAD_COUNT).child(userId).child(roomId).setValue(1);
                }
            }
        });
    }
}
