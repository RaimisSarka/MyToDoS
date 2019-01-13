package com.ahsas.mytodos;

public class ReminderDataModel {

    public int mId;
    public String mKind;
    public String mTitle;
    public String mStartDate;
    public String mFinishDate;
    public String mComment;
    public String mStatus;

    public ReminderDataModel() {
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
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

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }
}
