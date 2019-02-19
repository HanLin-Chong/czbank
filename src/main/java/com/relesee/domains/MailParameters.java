package com.relesee.domains;

public class MailParameters {

    private String senderAddress;
    private String recipientAddress;
    private String senderAccount;
    private String senderPassword;
    private String mailSubject;
    private String mailContent;
    private String fileAbsolutePosition;

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getSenderPassword() {
        return senderPassword;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public String getFileAbsolutePosition() {
        return fileAbsolutePosition;
    }

    public void setFileAbsolutePosition(String fileAbsolutePosition) {
        this.fileAbsolutePosition = fileAbsolutePosition;
    }
}
