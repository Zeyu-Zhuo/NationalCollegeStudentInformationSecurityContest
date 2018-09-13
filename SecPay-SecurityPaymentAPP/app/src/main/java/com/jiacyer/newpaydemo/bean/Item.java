package com.jiacyer.newpaydemo.bean;


public class Item extends Bill {

    public static final int ITEM = 0;
    public static final int SECTION = 1;
    public String groupName;
    public int type;

    public Item(int type, Bill bill) {
        super(bill);
        this.type = type;
    }

    public Item(int type, String groupName) {
        super(null);
        this.type = type;
        this.groupName = groupName;
    }

}
