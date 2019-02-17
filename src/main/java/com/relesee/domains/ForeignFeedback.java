package com.relesee.domains;

public class ForeignFeedback {

    public static final int FOUND_TIME = 0;
    public static final int ACC_NAME = 1;
    public static final int ACC = 2;
    public static final int CURRENCY = 3;
    public static final int FOREIGN_BANK = 4;
    public static final int ROUTING = 5;
    public static final int POST_SCRIPT = 6;
    public static final int NOTE = 7;

    private String foundTime;
    private String accName;
    private String acc;
    private String currency;
    private String foreignBank;
    private String routing;
    private String postScript;
    private String note;

    public String getFoundTime() {
        return foundTime;
    }

    public void setFoundTime(String foundTime) {
        this.foundTime = foundTime;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getForeignBank() {
        return foreignBank;
    }

    public void setForeignBank(String foreignBank) {
        this.foreignBank = foreignBank;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getPostScript() {
        return postScript;
    }

    public void setPostScript(String postScript) {
        this.postScript = postScript;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
