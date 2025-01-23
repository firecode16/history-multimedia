package com.brain.history.multimedia.response;

/**
 *
 * @author firecode16
 */
public class MultimediaResponse {
    private String result;
    private int page;
    private Object data;
    private int totalItems;
    private int size;

    public MultimediaResponse(String result, Object data) {
        this.result = result;
        this.data = data;
    }

    public MultimediaResponse(String result, int page, Object data, int totalItems, int size) {
        this.result = result;
        this.page = page;
        this.data = data;
        this.totalItems = totalItems;
        this.size = size;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
