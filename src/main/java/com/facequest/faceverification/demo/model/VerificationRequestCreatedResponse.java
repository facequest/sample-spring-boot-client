package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VerificationRequestCreatedResponse {

    private String verificationRequestId;

    /**
     * No args constructor for use in serialization
     */
    public VerificationRequestCreatedResponse() {
    }

    /**
     * @param verificationRequestId
     */
    public VerificationRequestCreatedResponse(String verificationRequestId) {
        super();
        this.verificationRequestId = verificationRequestId;
    }

    public String getVerificationRequestId() {
        return verificationRequestId;
    }

    public void setVerificationRequestId(String verificationRequestId) {
        this.verificationRequestId = verificationRequestId;
    }

    @Override
    public String toString() {
        //return new ToStringBuilder(this).append("verificationRequestId", verificationRequestId).toString();
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}