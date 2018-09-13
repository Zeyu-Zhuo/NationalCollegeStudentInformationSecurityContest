package com.jiacyer.attacker.tools;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 *  Created by Jiacy-PC on 2018/3/5.
 */

public class JsonUtil {
    private static Gson gson = new Gson();

    public class AttackIdResult {
        private int size;
        @SerializedName("attack_id")
        private ArrayList<String> attackIdList;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public ArrayList<String> getAttackIdList() {
            return attackIdList;
        }

        public void setAttackIdList(ArrayList<String> attackIdList) {
            this.attackIdList = attackIdList;
        }
    }

    public class QRCodeResult {
        private int size;
        @SerializedName("qrcode")
        private ArrayList<String> qrcodeList;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public ArrayList<String> getQrcodeList() {
            return qrcodeList;
        }

        public void setQrcodeList(ArrayList<String> qrcodeList) {
            this.qrcodeList = qrcodeList;
        }
    }

    public static AttackIdResult parseToAttackList(String data) {
        return gson.fromJson(data, AttackIdResult.class);
    }

    public static QRCodeResult parseToQRCode(String data) {
        return gson.fromJson(data, QRCodeResult.class);
    }
}
