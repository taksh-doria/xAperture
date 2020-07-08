package com.example.xaperture;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;


public class WallpapersModel implements Serializable
{
    StorageReference ref;
    String name;
    String orignal_img_name;
    String thumbnail_img_name;
    String cat;
    transient Timestamp timestamp;
    static WallpapersModel temp;

    public WallpapersModel(){}
    public WallpapersModel(String name, String orignal_img_name, String thumbnail_img_name, String cat, Timestamp timestamp,StorageReference ref) {
        this.ref = ref;
        this.name = name;
        this.orignal_img_name = orignal_img_name;
        this.thumbnail_img_name = thumbnail_img_name;
        this.cat = cat;
        this.timestamp = timestamp;
    }
    public StorageReference getRef() {
        return ref;
    }

    public void setRef(StorageReference ref) {
        this.ref = ref;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail_img_name() {
        return thumbnail_img_name;
    }

    public void setThumbnail_img_name(String thumbnail_img_name) {
        this.thumbnail_img_name = thumbnail_img_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrignal_img_name() {
        return orignal_img_name;
    }

    public void setOrignal_img_name(String orignal_img_name) {
        this.orignal_img_name = orignal_img_name;
    }

    @Override
    public String toString() {
        return "WallpapersModel{" +
                "ref=" + ref +
                ", name='" + name + '\'' +
                ", orignal_img_name='" + orignal_img_name + '\'' +
                ", thumbnail_img_name='" + thumbnail_img_name + '\'' +
                ", cat='" + cat + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
