package com.dnitinverma.amazons3library.model;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

import java.io.Serializable;

/**
 * Created by appinventiv on 7/9/17.
 */

public class ImageBean implements Serializable{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public TransferObserver getmObserver() {
        return mObserver;
    }

    public void setmObserver(TransferObserver mObserver) {
        this.mObserver = mObserver;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getIsSucess() {
        return isSucess;
    }

    public void setIsSucess(String isSucess) {
        this.isSucess = isSucess;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String name;
    private int progress = 0;
    private TransferObserver mObserver;
    private String serverUrl="";
    private String isSucess = "0";
    private String imagePath;
    private boolean isLoading;
    private String messageId;
    private int fileType = 1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
}
