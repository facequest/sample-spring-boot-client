package com.facequest.faceverification.demo.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class FoldersResponse {

    private Page page;
    private List<Folder> _embedded = null;

    /**
     * No args constructor for use in serialization
     */
    public FoldersResponse() {
    }

    /**
     * @param page
     * @param _embedded
     */
    public FoldersResponse(Page page, List<Folder> _embedded) {
        super();
        this.page = page;
        this._embedded = _embedded;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Folder> get_embedded() {
        return _embedded;
    }

    public void set_embedded(List<Folder> _embedded) {
        this._embedded = _embedded;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("page", page).append("_embedded", _embedded).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(page).append(_embedded).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof FoldersResponse) == false) {
            return false;
        }
        FoldersResponse rhs = ((FoldersResponse) other);
        return new EqualsBuilder().append(page, rhs.page).append(_embedded, rhs._embedded).isEquals();
    }

}