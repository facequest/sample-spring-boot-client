package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UploadUrlResponse {

    private Data data;
    private String errorMessage;

    /**
     * No args constructor for use in serialization
     */
    public UploadUrlResponse() {
    }

    /**
     * @param data
     * @param errorMessage
     */
    public UploadUrlResponse(Data data, String errorMessage) {
        super();
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public String toString() {
        //return new ToStringBuilder(this).append("data", data).append("errorMessage", errorMessage).toString();
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}


