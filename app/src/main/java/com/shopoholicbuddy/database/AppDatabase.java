package com.shopoholicbuddy.database;

import android.content.ContentValues;

import com.shopoholicbuddy.ShopoholicBuddyApplication;
import com.shopoholicbuddy.firebasechat.models.ChatMessageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to access local db
 */

public class AppDatabase {

    private AppDatabase() {

    }


    /*
    Method to save contacts in database
     */

    public static void saveMediaFilesDetailsInDb(ChatMessageBean messageDetails) {
        DataBaseProvider dataBaseProvider = ShopoholicBuddyApplication.getInstance().getDataBaseProvider();
        ContentValues contentValue = new ContentValues();
        contentValue.put(SqlConstant.MESSAGE_SENDER, messageDetails.getSenderId());
        contentValue.put(SqlConstant.MESSAGE_ID, messageDetails.getMessageId());
        contentValue.put(SqlConstant.MESSAGE_TYPE, messageDetails.getType());
        contentValue.put(SqlConstant.MESSAGE_TEXT, messageDetails.getMessageText());
        contentValue.put(SqlConstant.MESSAGE_URI, messageDetails.getMediaUrl());
        contentValue.put(SqlConstant.MESSAGE_THUMBNAIL, messageDetails.getThumbnail());
        contentValue.put(SqlConstant.MESSAGE_TIMESTAMP, Long.parseLong(messageDetails.getTimestamp().toString()));
        contentValue.put(SqlConstant.MESSAGE_STATUS, messageDetails.getStatus());
        contentValue.put(SqlConstant.MESSAGE_ROOM_ID, messageDetails.getRoomId());
        contentValue.put(SqlConstant.MESSAGE_RECEIVER_ID, messageDetails.getReceiverId());
        contentValue.put(SqlConstant.MESSAGE_LATITUDE, messageDetails.getLatitude());
        contentValue.put(SqlConstant.MESSAGE_LONGITUDE, messageDetails.getLongitude());
        contentValue.put(SqlConstant.MESSAGE_BLOCK, messageDetails.getIsBlock());
        dataBaseProvider.insert(SqlConstant.TABLE_MEDIA_FILES, contentValue);
    }

    /*
    Method to fetch all files details
     */

    public static List<ChatMessageBean> fetchMediaFilesDetails(String roomId) {

        DataBaseProvider dataBaseProvider = ShopoholicBuddyApplication.getInstance().getDataBaseProvider();
        List<ChatMessageBean> fetchContactList = new ArrayList<ChatMessageBean>();
        List<ContentValues> contentValues = dataBaseProvider.select(SqlConstant.TABLE_MEDIA_FILES, null, SqlConstant.MESSAGE_RECEIVER_ID + " = ?",
                new String[]{roomId}, null, null, null);
        if (contentValues != null && contentValues.size() > 0) {
            for (ContentValues contentValue : contentValues) {
                ChatMessageBean messageDetails = new ChatMessageBean();
                messageDetails.setSenderId(contentValue.getAsString(SqlConstant.MESSAGE_SENDER));
                messageDetails.setMessageId(contentValue.getAsString(SqlConstant.MESSAGE_ID));
                messageDetails.setType(contentValue.getAsString(SqlConstant.MESSAGE_TYPE));
                messageDetails.setMessageText(contentValue.getAsString(SqlConstant.MESSAGE_TEXT));
                messageDetails.setMediaUrl(contentValue.getAsString(SqlConstant.MESSAGE_URI));
                messageDetails.setThumbnail(contentValue.getAsString(SqlConstant.MESSAGE_THUMBNAIL));
                messageDetails.setTimestamp(contentValue.getAsLong(SqlConstant.MESSAGE_TIMESTAMP));
                messageDetails.setStatus(contentValue.getAsString(SqlConstant.MESSAGE_STATUS));
                messageDetails.setRoomId(contentValue.getAsString(SqlConstant.MESSAGE_ROOM_ID));
                messageDetails.setReceiverId(contentValue.getAsString(SqlConstant.MESSAGE_RECEIVER_ID));
                messageDetails.setLatitude(contentValue.getAsDouble(SqlConstant.MESSAGE_LATITUDE));
                messageDetails.setLongitude(contentValue.getAsDouble(SqlConstant.MESSAGE_LONGITUDE));
                messageDetails.setIsBlock(contentValue.getAsBoolean(SqlConstant.MESSAGE_BLOCK));

                fetchContactList.add(messageDetails);
            }
        }

        return fetchContactList;

    }

    /*
    Method to fetch single media details
     */

    public static ChatMessageBean fetchSingleMediaFilesDetails(String messageId) {

        DataBaseProvider dataBaseProvider = ShopoholicBuddyApplication.getInstance().getDataBaseProvider();
        List<ContentValues> contentValues = dataBaseProvider.select(SqlConstant.TABLE_MEDIA_FILES, null, SqlConstant.MESSAGE_ID + " = ?",
                new String[]{messageId}, null, null, null);
        ChatMessageBean messageDetails = null;
        if (contentValues != null && contentValues.size() > 0) {
            ContentValues contentValue = contentValues.get(0);
            messageDetails = new ChatMessageBean();
            messageDetails.setSenderId(contentValue.getAsString(SqlConstant.MESSAGE_SENDER));
            messageDetails.setMessageId(contentValue.getAsString(SqlConstant.MESSAGE_ID));
            messageDetails.setType(contentValue.getAsString(SqlConstant.MESSAGE_TYPE));
            messageDetails.setMessageText(contentValue.getAsString(SqlConstant.MESSAGE_TEXT));
            messageDetails.setMediaUrl(contentValue.getAsString(SqlConstant.MESSAGE_URI));
            messageDetails.setThumbnail(contentValue.getAsString(SqlConstant.MESSAGE_THUMBNAIL));
            messageDetails.setTimestamp(contentValue.getAsLong(SqlConstant.MESSAGE_TIMESTAMP));
            messageDetails.setStatus(contentValue.getAsString(SqlConstant.MESSAGE_STATUS));
            messageDetails.setRoomId(contentValue.getAsString(SqlConstant.MESSAGE_ROOM_ID));
            messageDetails.setReceiverId(contentValue.getAsString(SqlConstant.MESSAGE_RECEIVER_ID));
            messageDetails.setLatitude(contentValue.getAsDouble(SqlConstant.MESSAGE_LATITUDE));
            messageDetails.setLongitude(contentValue.getAsDouble(SqlConstant.MESSAGE_LONGITUDE));
            messageDetails.setIsBlock(contentValue.getAsBoolean(SqlConstant.MESSAGE_BLOCK));
        }
        return messageDetails;
    }

    /**
     * method to set deals in db
     * @param dealId
     * @param dealDetails
     * @param isNew
     */
    public static void setDealDetails(String dealId, String dealDetails, boolean isNew){
        DataBaseProvider dataBaseProvider = ShopoholicBuddyApplication.getInstance().getDataBaseProvider();
        ContentValues contentValue = new ContentValues();
        contentValue.put(SqlConstant.DEAL_ID, dealId);
        contentValue.put(SqlConstant.DEAL_DETAILS, dealDetails);
        if (!isNew) {
            dataBaseProvider.update(SqlConstant.TABLE_OFFLINE_DEALS, contentValue, dealId);
        }else {
            dataBaseProvider.insert(SqlConstant.TABLE_OFFLINE_DEALS, contentValue);
        }
    }

    /*
    Method to fetch all files details
     */

    public static List<String> fetchDealDetails() {

        DataBaseProvider dataBaseProvider = ShopoholicBuddyApplication.getInstance().getDataBaseProvider();
        List<String> fetchDealsList = new ArrayList<>();
        List<ContentValues> contentValues = dataBaseProvider.select(SqlConstant.TABLE_OFFLINE_DEALS, null, null,
                null, null, null, null);
        if (contentValues != null && contentValues.size() > 0) {
            for (ContentValues contentValue : contentValues) {
                fetchDealsList.add(contentValue.getAsString(SqlConstant.DEAL_DETAILS));
            }
        }
        return fetchDealsList;
    }

    /*
    Method to remove data from database
     */

    public static void removeDealFromDb(String dealId) {

        DataBaseProvider dataBaseProvider = ShopoholicBuddyApplication.getInstance().getDataBaseProvider();
        dataBaseProvider.delete(SqlConstant.TABLE_OFFLINE_DEALS, SqlConstant.DEAL_ID + " = ?", new String[]{dealId});
    }

    /*
    Method to remove data from database
     */

    public static void removeMediaFileDetailsFromDb(String messageId) {

        DataBaseProvider dataBaseProvider = ShopoholicBuddyApplication.getInstance().getDataBaseProvider();
        dataBaseProvider.delete(SqlConstant.TABLE_MEDIA_FILES, SqlConstant.MESSAGE_ID + " = ?", new String[]{messageId});
    }
    /*
    Method to remove data from database
     */

    public static void clearDatabase() {
        DataBaseProvider dataBaseProvider = ShopoholicBuddyApplication.getInstance().getDataBaseProvider();
        dataBaseProvider.clearDatabase();
    }


}
