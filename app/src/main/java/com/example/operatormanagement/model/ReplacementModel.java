package com.example.operatormanagement.model;

public class ReplacementModel {
    private int replaceId;
    private int replaceStatus;
    private int replaceType;
    private String statusStr;
    private String replaceDate;
    private String replaceShiftName;
    private String replaceOperatorName;
    private String replaceOperatorNameChange;

    public String getReplaceOperatorNameChange() {
        return replaceOperatorNameChange;
    }

    public void setReplaceOperatorNameChange(String replaceOperatorNameChange) {
        this.replaceOperatorNameChange = replaceOperatorNameChange;
    }

    public int getReplaceType() {
        return replaceType;
    }

    public void setReplaceType(int replaceType) {
        this.replaceType = replaceType;
    }

    public String getReplaceDate() {
        return replaceDate;
    }

    public void setReplaceDate(String replaceDate) {
        this.replaceDate = replaceDate;
    }

    public String getReplaceShiftName() {
        return replaceShiftName;
    }

    public void setReplaceShiftName(String replaceShiftName) {
        this.replaceShiftName = replaceShiftName;
    }

    public String getReplaceOperatorName() {
        return replaceOperatorName;
    }

    public void setReplaceOperatorName(String replaceOperatorName) {
        this.replaceOperatorName = replaceOperatorName;
    }

    public int getReplaceId() {
        return replaceId;
    }

    public void setReplaceId(int replaceId) {
        this.replaceId = replaceId;
    }

    public int getReplaceStatus() {
        return replaceStatus;
    }

    public void setReplaceStatus(int replaceStatus) {
        this.replaceStatus = replaceStatus;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}
