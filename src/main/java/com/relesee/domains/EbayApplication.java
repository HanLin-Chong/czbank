package com.relesee.domains;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


public class EbayApplication {
    private String id;
    private String paypalId;
    private String businessName;
    private String shopUrl;
    private String shopName;
    private String applicantName;
    private String applicantId;
    private String recipientAcc;
    private String recipientAccName;
    private String recipientId;
    private String address;
    private String managerName;
    private String managerDepartment;
    private String managerId;
    private String uploadTime;
    private String transactionRecordName;
    private String applicationFileName;
    private int status;
    private String auditor;

    private MultipartFile transactionRecord;
    private MultipartFile applicationFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaypalId() {
        return paypalId;
    }

    public void setPaypalId(String paypalId) {
        this.paypalId = paypalId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getRecipientAcc() {
        return recipientAcc;
    }

    public void setRecipientAcc(String recipientAcc) {
        this.recipientAcc = recipientAcc;
    }

    public String getRecipientAccName() {
        return recipientAccName;
    }

    public void setRecipientAccName(String recipientAccName) {
        this.recipientAccName = recipientAccName;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerDepartment() {
        return managerDepartment;
    }

    public void setManagerDepartment(String managerDepartment) {
        this.managerDepartment = managerDepartment;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getTransactionRecordName() {
        return transactionRecordName;
    }

    public void setTransactionRecordName(String transactionRecordName) {
        this.transactionRecordName = transactionRecordName;
    }

    public String getApplicationFileName() {
        return applicationFileName;
    }

    public void setApplicationFileName(String applicationFileName) {
        this.applicationFileName = applicationFileName;
    }

    public MultipartFile getTransactionRecord() {
        return transactionRecord;
    }

    public void setTransactionRecord(MultipartFile transactionRecord) {
        this.transactionRecord = transactionRecord;
    }

    public MultipartFile getApplicationFile() {
        return applicationFile;
    }

    public void setApplicationFile(MultipartFile applicationFile) {
        this.applicationFile = applicationFile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }
}
