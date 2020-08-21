package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Data {

    private ReferencePhoto referencePhoto;
    private PhotoToBeVerified photoToBeVerified;

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    /**
     * @param referencePhoto
     * @param photoToBeVerified
     */
    public Data(ReferencePhoto referencePhoto, PhotoToBeVerified photoToBeVerified) {
        super();
        this.referencePhoto = referencePhoto;
        this.photoToBeVerified = photoToBeVerified;
    }

    public ReferencePhoto getReferencePhoto() {
        return referencePhoto;
    }

    public void setReferencePhoto(ReferencePhoto referencePhoto) {
        this.referencePhoto = referencePhoto;
    }

    public PhotoToBeVerified getPhotoToBeVerified() {
        return photoToBeVerified;
    }

    public void setPhotoToBeVerified(PhotoToBeVerified photoToBeVerified) {
        this.photoToBeVerified = photoToBeVerified;
    }

    @Override
    public String toString() {
        //return new ToStringBuilder(this).append("referencePhoto", referencePhoto).append("photoToBeVerified", photoToBeVerified).toString();
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
