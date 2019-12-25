
package com.shopoholicbuddy.models.huntdetailresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageArr implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("hunt_id")
    @Expose
    private String huntId;
    @SerializedName("media_type")
    @Expose
    private String mediaType;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("default_image")
    @Expose
    private String defaultImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHuntId() {
        return huntId;
    }

    public void setHuntId(String huntId) {
        this.huntId = huntId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

}
