package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Folder {

    private String id;
    private String userId;
    private String name;
    private Object parentFolder;
    private String created;
    private String lastUpdated;

    /**
     * No args constructor for use in serialization
     */
    public Folder() {
    }

    /**
     * @param lastUpdated
     * @param parentFolder
     * @param created
     * @param name
     * @param id
     * @param userId
     */
    public Folder(String id, String userId, String name, Object parentFolder, String created, String lastUpdated) {
        super();
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.parentFolder = parentFolder;
        this.created = created;
        this.lastUpdated = lastUpdated;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Object parentFolder) {
        this.parentFolder = parentFolder;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("userId", userId).append("name", name).append("parentFolder", parentFolder).append("created", created).append("lastUpdated", lastUpdated).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(lastUpdated).append(parentFolder).append(created).append(name).append(id).append(userId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Folder) == false) {
            return false;
        }
        Folder rhs = ((Folder) other);
        return new EqualsBuilder().append(lastUpdated, rhs.lastUpdated).append(parentFolder, rhs.parentFolder).append(created, rhs.created).append(name, rhs.name).append(id, rhs.id).append(userId, rhs.userId).isEquals();
    }

}