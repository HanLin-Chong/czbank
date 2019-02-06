package com.relesee.domains;

import java.io.Serializable;

public class NraFile implements Serializable {
    private String id;
    private int queueNo;
    private String fileName;
    private String uploadTime;
    private String uploader;
    private String userName;
    private String restorePath;
    private String note;
    private boolean isQualityCustomer;
    private int statusCode;
    private boolean isPriority;
    private int priorityStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(int queueNo) {
        this.queueNo = queueNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRestorePath() {
        return restorePath;
    }

    public void setRestorePath(String restorePath) {
        this.restorePath = restorePath;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isQualityCustomer() {
        return isQualityCustomer;
    }

    public void setQualityCustomer(boolean qualityCustomer) {
        isQualityCustomer = qualityCustomer;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void setPriority(boolean priority) {
        isPriority = priority;
    }

    public int getPriorityStatus() {
        return priorityStatus;
    }

    public void setPriorityStatus(int priorityStatus) {
        this.priorityStatus = priorityStatus;
    }
}
