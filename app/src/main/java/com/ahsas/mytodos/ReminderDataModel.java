package com.ahsas.mytodos;

public class ReminderDataModel {

    public String mKind;
    public String mTitle;
    public String mStartDate;
    public String mFinishDate;
    public String mComment;

    public ReminderDataModel() {
    }

    public String getKind() {
        return mKind;
    }

    public void setKind(String mKind) {
        this.mKind = mKind;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String mStartDate) {
        this.mStartDate = mStartDate;
    }

    public String getFinishDate() {
        return mFinishDate;
    }

    public void setFinishDate(String mFinishDate) {
        this.mFinishDate = mFinishDate;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
    }
}
