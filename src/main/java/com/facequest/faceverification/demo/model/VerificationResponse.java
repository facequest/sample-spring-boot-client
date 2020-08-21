package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VerificationResponse {

    private String id;
    private String userId;
    private String title;
    private String notes;
    private String result;
    private String errorMessage;
    private String created;
    private Float matchPercentage;

    /**
     * No args constructor for use in serialization
     */
    public VerificationResponse() {
    }

    /**
     * @param result
     * @param matchPercentage
     * @param notes
     * @param created
     * @param errorMessage
     * @param id
     * @param title
     * @param userId
     */
    public VerificationResponse(String id, String userId, String title, String notes, String result, String errorMessage, String created, Float matchPercentage) {
        super();
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.notes = notes;
        this.result = result;
        this.errorMessage = errorMessage;
        this.created = created;
        this.matchPercentage = matchPercentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Float getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Float matchPercentage) {
        this.matchPercentage = matchPercentage;
    }


    @Override
    public String toString() {
        //return new ToStringBuilder(this).append("id", id).append("userId", userId).append("title", title).append("notes", notes).append("result", result).append("errorMessage", errorMessage).append("created", created).append("matchPercentage", matchPercentage).toString();
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}