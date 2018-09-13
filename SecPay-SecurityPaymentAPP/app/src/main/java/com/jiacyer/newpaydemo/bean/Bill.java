package com.jiacyer.newpaydemo.bean;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * 账单概括实体类
 */
public class Bill {
    @SerializedName("pay_id")
    private String payId;
    @SerializedName("pay_content")
    private String payContent;
    @SerializedName("pay_amount")
    private float payAmount;
    @SerializedName("pay_time")
    private Date payTime;
    @SerializedName("pay_class")
    private String payClass;

    public Bill(Bill bill) {
        if (bill == null) return;
        this.payId = bill.payId;
        this.payContent = bill.payContent;
        this.payAmount = bill.payAmount;
        this.payTime = bill.payTime;
        this.payClass = bill.payClass;
    }

    public Bill(String payId, String payContent, String payClass, float payAmount, Date payTime) {
        super();
        this.payId = payId;
        this.payContent = payContent;
        this.payAmount = payAmount;
        this.payTime = payTime;
        this.payClass = payClass;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getPayContent() {
        return payContent;
    }

    public void setPayContent(String payContent) {
        this.payContent = payContent;
    }

    public float getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(float payAmount) {
        this.payAmount = payAmount;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getPayClass() {
        return payClass;
    }

    public void setPayClass(String payClass) {
        this.payClass = payClass;
    }
}
