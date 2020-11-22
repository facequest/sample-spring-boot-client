package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PhotoToFolderResponse {

    private String id;
    private String status;

    /**
     * No args constructor for use in serialization
     */
    public PhotoToFolderResponse() {
    }

    /**
     * @param id
     * @param status
     */
    public PhotoToFolderResponse(String id, String status) {
        super();
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("status", status).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(status).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PhotoToFolderResponse) == false) {
            return false;
        }
        PhotoToFolderResponse rhs = ((PhotoToFolderResponse) other);
        return new EqualsBuilder().append(id, rhs.id).append(status, rhs.status).isEquals();
    }

}