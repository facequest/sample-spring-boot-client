package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Photo {

    private String id;
    private String name;
    private String ownedByUser;
    private Object lastDetectedTime;
    private String pictureUrl;
    private String folderId;
    private Object faceEncoding;

    /**
     * No args constructor for use in serialization
     */
    public Photo() {
    }

    /**
     * @param pictureUrl
     * @param name
     * @param id
     * @param ownedByUser
     * @param lastDetectedTime
     * @param folderId
     * @param faceEncoding
     */
    public Photo(String id, String name, String ownedByUser, Object lastDetectedTime, String pictureUrl, String folderId, Object faceEncoding) {
        super();
        this.id = id;
        this.name = name;
        this.ownedByUser = ownedByUser;
        this.lastDetectedTime = lastDetectedTime;
        this.pictureUrl = pictureUrl;
        this.folderId = folderId;
        this.faceEncoding = faceEncoding;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnedByUser() {
        return ownedByUser;
    }

    public void setOwnedByUser(String ownedByUser) {
        this.ownedByUser = ownedByUser;
    }

    public Object getLastDetectedTime() {
        return lastDetectedTime;
    }

    public void setLastDetectedTime(Object lastDetectedTime) {
        this.lastDetectedTime = lastDetectedTime;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public Object getFaceEncoding() {
        return faceEncoding;
    }

    public void setFaceEncoding(Object faceEncoding) {
        this.faceEncoding = faceEncoding;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("ownedByUser", ownedByUser).append("lastDetectedTime", lastDetectedTime).append("pictureUrl", pictureUrl).append("folderId", folderId).append("faceEncoding", faceEncoding).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(pictureUrl).append(name).append(id).append(ownedByUser).append(lastDetectedTime).append(folderId).append(faceEncoding).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Photo) == false) {
            return false;
        }
        Photo rhs = ((Photo) other);
        return new EqualsBuilder().append(pictureUrl, rhs.pictureUrl).append(name, rhs.name).append(id, rhs.id).append(ownedByUser, rhs.ownedByUser).append(lastDetectedTime, rhs.lastDetectedTime).append(folderId, rhs.folderId).append(faceEncoding, rhs.faceEncoding).isEquals();
    }

}