package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VerificationRequest {

    private String title;
    private String notes;
    private String referencePhotoFilePath;
    private String photoToBeVerifiedFilePath;
    private String useStoredPhotoForReference = "false";

    /**
     * No args constructor for use in serialization
     */
    public VerificationRequest() {
    }

    /**
     * @param referencePhotoFilePath
     * @param photoToBeVerifiedFilePath
     * @param notes
     * @param title
     */
    public VerificationRequest(String title, String notes, String referencePhotoFilePath, String photoToBeVerifiedFilePath) {
        super();
        this.title = title;
        this.notes = notes;
        this.referencePhotoFilePath = referencePhotoFilePath;
        this.photoToBeVerifiedFilePath = photoToBeVerifiedFilePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReferencePhotoFilePath() {
        return referencePhotoFilePath;
    }

    public void setReferencePhotoFilePath(String referencePhotoFilePath) {
        this.referencePhotoFilePath = referencePhotoFilePath;
    }

    public String getPhotoToBeVerifiedFilePath() {
        return photoToBeVerifiedFilePath;
    }

    public void setPhotoToBeVerifiedFilePath(String photoToBeVerifiedFilePath) {
        this.photoToBeVerifiedFilePath = photoToBeVerifiedFilePath;
    }

    public String getUseStoredPhotoForReference() {
        return useStoredPhotoForReference;
    }

    public void setUseStoredPhotoForReference(String useStoredPhotoForReference) {
        this.useStoredPhotoForReference = useStoredPhotoForReference;
    }

    @Override
    public String toString() {
        //return new ToStringBuilder(this).append("title", title).append("notes", notes).append("referencePhotoFilePath", referencePhotoFilePath).append("photoToBeVerifiedFilePath", photoToBeVerifiedFilePath).toString();
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

    }
}