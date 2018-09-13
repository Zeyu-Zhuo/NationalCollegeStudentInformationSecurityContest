package com.jiacyer.newpaymerchantclient.utils;

import java.math.BigInteger;
import java.util.HashMap;

public class CodeUtil {
    // '闭单引号（0x27） 和 ` 开单号（0x60）未使用
    private static String encodeCharacter = "1234567890-=qwertyuiop[]\\asdfghjkl;zxcvb\"nm,./~@#$%&*()_+QWERTYUIOP{}|ASDFGHJKL:ZXCVBNM<>?!";
//    private static char isNotUsrIdFirst = '`';
    private static char isAppendZero = '^';
    private static int verifyLen = 2;
    private static int payCodeLen = 18;
    private static RSAUtil rsaUtil = new RSAUtil();

    public static String PAY_CODE = "payCode";
    public static String USR_ID = "usrId";
    public static String ERROR_PAY_CODE = "Error";

    public static String encode(String usrId, String payCode, String publicKey) {
        StringBuilder newPayCode = new StringBuilder(payCode);
        StringBuilder mergeString = new StringBuilder();
        StringBuilder encodeString = new StringBuilder();
//        boolean isUsrIdFirst = true;
        boolean isNotAppendZero = true;

        // 添加最多两位验证位，来自公钥base64编码后字符串中的数字
        for (int i=0, count=0; i<publicKey.length() && count < verifyLen; i++) {
            char c = publicKey.charAt(i);
            if (Character.isDigit(c)) {
                newPayCode.append(c);
                count++;
            }
        }
//        System.out.println("添加验证位后的payCode="+newPayCode);
        String encodePayCode = rsaUtil.encode(publicKey, new BigInteger(newPayCode.toString())).toString();
//        System.out.println(Base64.getEncoder().encodeToString(new String("12345:"+encodePayCode).getBytes()));
//        System.out.println("加密后的payCode="+encodePayCode);
//        st = encodePayCode;
//        if (usrId.length() > encodePayCode.length()) {
//            isUsrIdFirst = false;
//        }

        // usrId在前，加密后的付款码在后的顺序，逐个混合，usrId以0作为结束标志位
        for (int i=0; i<usrId.length() && i<encodePayCode.length(); i++) {
//            if (isUsrIdFirst) {
            mergeString.append(usrId.charAt(i));
            mergeString.append(encodePayCode.charAt(i));
//            } else {
//                mergeString.append(encodePayCode.charAt(i));
//                mergeString.append(usrId.charAt(i));
//            }
        }

        // usrId以0作为结束标志位，其后所有位均为加密后的付款码
//        if (isUsrIdFirst) {
        mergeString.append("0");
        mergeString.append(encodePayCode.substring(usrId.length()));
//        } else {
//            mergeString.append(usrId.substring(encodePayCode.length()));
//        }
//        System.out.println("合并后的mergeString="+mergeString.toString());

        // 在两个连续相同数字之间，添加00分割
        for (int i=0; i<mergeString.length()-1; i++) {
            if (isDoubleNum(mergeString.substring(i, i+2))) {
                mergeString.insert(i+1, "00");
                i += 2;
            }
        }
//        System.out.println("添00后的mergeString="+mergeString.toString());

        // 补全为偶数长度
        if (mergeString.length()%2 != 0) {
            mergeString.append("0");
            isNotAppendZero = false;
        }
//        System.out.println("补全为偶数长度后的mergeString="+mergeString.toString());

        // 将每两个数字编码为一个字符
        for (int i=0; i<mergeString.length()/2; i++) {
            int index = Integer.parseInt(mergeString.substring(2*i, 2*(i+1)));
            if (index >= encodeCharacter.length()) {
                index = 11 * (index % 10);
            }
            encodeString.append(encodeCharacter.charAt(index));
        }

//        if (!isUsrIdFirst) {
//            encodeString.append(isNotUsrIdFirst);
//        }
        // 末尾置是否补全偶数长度标志位
        if (!isNotAppendZero) {
            encodeString.append(isAppendZero);
        }
        return encodeString.toString();
    }

    public static HashMap<String, String> decode(String encodeString, String publicKey, String privateKey) {
        HashMap<String, String> retMap = new HashMap<>();
        StringBuilder indexString = new StringBuilder();
        StringBuilder usrId = new StringBuilder();
        StringBuilder encodePayCode = new StringBuilder();
        boolean isNotAppendZero = true;
        int len = encodeString.length();

//        if (encodeString.contains(String.valueOf(isNotUsrIdFirst))) {
//            isUsrIdFirst = false;
//            len--;
//        }
        // 检查是否补全偶数长度标志位
        if (encodeString.contains(String.valueOf(isAppendZero))) {
            isNotAppendZero = false;
            len--;
        }

        // 根据字符信息还原原本的数字信息
        for (int i=0; i<len; i++) {
            char c = encodeString.charAt(i);
            String indexStr;
            int index = encodeCharacter.indexOf(c);
            if (index < 10)
                indexStr = "0" + String.valueOf(index);
            else if (index%10 == index/10)
                indexStr = String.valueOf(90+(index/10));
            else
                indexStr = String.valueOf(index);
            indexString.append(indexStr);
        }

        // 去掉补全偶数的末尾0
        if (!isNotAppendZero) {
            indexString.deleteCharAt(indexString.length()-1);
        }

        // 删除分割连续相同数字的00
        for (int i=0; i<indexString.length()-2; i++) {
            if (isDoubleNum(indexString.substring(i+1, i+3))){
                indexString = indexString.delete(i+1, i+3);
            }
        }

        // 解析UsrId和加密后的付款码
        for (int i=0; i<indexString.length(); i+=2) {
            if (indexString.charAt(i) == '0') {
                indexString.deleteCharAt(i);
                break;
            }
            usrId.append(indexString.charAt(i));
            encodePayCode.append(indexString.charAt(i+1));
        }
        encodePayCode.append(indexString.substring(encodePayCode.length()*2));

        // 解密付款码
        String payCode = rsaUtil.decode(privateKey, new BigInteger(encodePayCode.toString())).toString();

        // 验证付款码中的验证位
        int i = payCodeLen;
        boolean checkSucceed = true;
        for (int j=0, count=0; j<publicKey.length() && i<payCode.length() && count < verifyLen; j++) {
            char c = publicKey.charAt(j);
            if (Character.isDigit(c) && payCode.charAt(i) == c) {
                count++;
                i++;
            } else if (Character.isDigit(c) && payCode.charAt(i) != c) {
                checkSucceed = false;
            }
        }

        if (checkSucceed && i == payCode.length())
            retMap.put(PAY_CODE, payCode.substring(0,payCodeLen));
        else
            retMap.put(PAY_CODE, ERROR_PAY_CODE);

        retMap.put(USR_ID, usrId.toString());

        return retMap;
    }

    private static boolean isDoubleNum(String string) {
        return string.charAt(0)==string.charAt(1);
    }

//    public static void main(String[] args) {
//
//        int succeed = 0;
//        int size = 1;
//        long[] times = new long[size];
//        for (int i=0; i<size; i++) {
//            long st = System.currentTimeMillis();
//            boolean f = test();
//            long et = System.currentTimeMillis();
//            if (f)  succeed++;
//            times[i] = et - st;
//        }
//        System.out.println("成功次数：" + succeed);
//        long totalTime = 0;
//        for (long e : times) {
//            totalTime += e;
//        }
//        System.out.println("平均用时：" + totalTime/size + " ms");
//
//    }
//
//    private static boolean test() {
//        RSAUtil rsaUtil = new RSAUtil();
//        rsaUtil.generateKeyPair();
//        BigInteger M1 = new BigInteger("123456789012345678");
////        do {
////            M1 = RSAUtil.rand(new BigInteger("999999999999999999"));
////        } while (M1.toString().length() != 18);
//
//        System.out.println("公钥"+rsaUtil.getPublicKey());
//        System.out.println("私钥"+rsaUtil.getPrivateKey());
//        String C = CodeUtil.encode("12345", M1.toString(), rsaUtil.getPublicKey());
//        System.out.println("------------原文------------ "+M1);
//        System.out.println("the information encrypted is "+C);
//        HashMap<String, String> M2 = CodeUtil.decode(C, rsaUtil.getPublicKey(), rsaUtil.getPrivateKey());
//        System.out.println("the information decrypted is "+M2.get("payCode"));
//
//        return M1.toString().equals(M2.get("payCode"));
//    }
}
