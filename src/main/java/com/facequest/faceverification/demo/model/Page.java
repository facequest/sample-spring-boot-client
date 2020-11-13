package com.facequest.faceverification.demo.model;

public class Page {

    private long totalElements;

    public Page() {
    }

    /**
     * @param totalElements
     */
    public Page(long totalElements) {
        super();
        this.totalElements = totalElements;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}