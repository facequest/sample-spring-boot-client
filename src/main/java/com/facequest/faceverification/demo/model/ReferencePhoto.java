package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ReferencePhoto {

    private String uploadUrl;
    private String filePath;

    /**
     * No args constructor for use in serialization
     */
    public ReferencePhoto() {
    }

    /**
     * @param uploadUrl
     * @param filePath
     */
    public ReferencePhoto(String uploadUrl, String filePath) {
        super();
        this.uploadUrl = uploadUrl;
        this.filePath = filePath;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        //return new ToStringBuilder(this).append("uploadUrl", uploadUrl).append("filePath", filePath).toString();
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
