package com.relesee.domains;

/**
 * 此类作为账户通知书，Pdf生成类的参数之一，
 * 解决表单的多样性，存放统一的信息
 */
public class PdfParameters {
    private String destination;
    private String czbankAcc;
    private String czbankAccName;
    private String ForeignSwiftCode;
    private String ForeignFullName;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCzbankAcc() {
        return czbankAcc;
    }

    public void setCzbankAcc(String czbankAcc) {
        this.czbankAcc = czbankAcc;
    }

    public String getCzbankAccName() {
        return czbankAccName;
    }

    public void setCzbankAccName(String czbankAccName) {
        this.czbankAccName = czbankAccName;
    }

    public String getForeignSwiftCode() {
        return ForeignSwiftCode;
    }

    public void setForeignSwiftCode(String foreignSwiftCode) {
        ForeignSwiftCode = foreignSwiftCode;
    }

    public String getForeignFullName() {
        return ForeignFullName;
    }

    public void setForeignFullName(String foreignFullName) {
        ForeignFullName = foreignFullName;
    }
}
