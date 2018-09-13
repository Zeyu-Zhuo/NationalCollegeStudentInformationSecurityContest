package com.jiacyer.newpaydemo.bean;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.jiacyer.newpaydemo.adapter.BooleanTypeAdapter;

import java.util.Date;

/**
 *  Created by Jiacy-PC on 2018/5/19.
 */

public class MsgContent {
    @SerializedName("msg_id")
    private int msgId;
    private String msg;
    private String loc;
    @SerializedName("push_time")
    private Date pushDate;
    @JsonAdapter(BooleanTypeAdapter.class)
    @SerializedName("is_checked")
    private boolean isChecked;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Date getPushDate() {
        return pushDate;
    }

    public void setPushDate(Date pushDate) {
        this.pushDate = pushDate;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
