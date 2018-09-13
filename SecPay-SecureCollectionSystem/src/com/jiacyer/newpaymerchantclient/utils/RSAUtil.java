package com.jiacyer.newpaymerchantclient.utils;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;

public class RSAUtil {
    private BigInteger eVal;
    private BigInteger dVal;
    private BigInteger nVal;

    public static class MSet {
        BigInteger d, x, y;
    }

    public RSAUtil() {

    }

    public void generateKeyPair() {
        do {
            generateKeyAction();
        } while (!isAvailableKey(100));
//        System.out.println("e=" + byte2hex(eVal.toByteArray()) + ";n=" + byte2hex(nVal.toByteArray())+
//            ";d="+byte2hex(dVal.toByteArray()));
    }

    private boolean isAvailableKey(int testTime) {
        BigInteger boundary = new BigInteger("98765432109876543210");
        boolean f = true;
        for (int i = 0; i < testTime; i++) {
            BigInteger testCase = rand(boundary);
            BigInteger tmpCase = encode(getPublicKey(), testCase);
            tmpCase = decode(getPrivateKey(), tmpCase);
            f = f & tmpCase.equals(testCase);
            if (!f) break;
        }
        return f;
    }

    private void generateKeyAction() {
        BigInteger p = null, q = null, eulern;
        BigInteger one = new BigInteger("1");
//        BigInteger infinity = new BigInteger("2305863009213693950");
        BigInteger infinity = new BigInteger("4294967296").multiply(new BigInteger("4"));
        for (int i = 0; i < 200; i++) {
            p = rand(infinity);
            if (p.isProbablePrime(20))
                break;
        }
        for (int j = 0; j < 200; j++) {
            q = rand(infinity);
            if (q.isProbablePrime(20))
                break;
        }
//        System.out.println("p="+byte2hex(p.toByteArray())+"\tq="+byte2hex(q.toByteArray()));
        nVal = p.multiply(q);
        eulern = p.subtract(one).multiply(q.subtract(one));
//        System.out.println("eulern="+byte2hex(eulern.toByteArray()));
        for (int j = 0; j < 200; j++) {
            eVal = rand(eulern);
            if (isCoprime(eVal, eulern))
                break;
        }
        dVal = mod_inverse(eVal, eulern);
    }

    private boolean isCoprime(BigInteger e, BigInteger eulern) {
        BigInteger zero = new BigInteger("0");
        return e.isProbablePrime(20) && !eulern.divide(eVal).equals(zero);
    }

    public String getPublicKey() {
        if (eVal == null || nVal == null)
            return ":";

        String es = Base64.getEncoder().encodeToString(eVal.toByteArray());
        String ns = Base64.getEncoder().encodeToString(nVal.toByteArray());
        return es + ":" + ns;
    }

    public String getPrivateKey() {
        if (dVal == null || nVal == null)
            return ":";

        String ds = Base64.getEncoder().encodeToString(dVal.toByteArray());
        String ns = Base64.getEncoder().encodeToString(nVal.toByteArray());
        return ds + ":" + ns;
    }

    public BigInteger encode(String publicKey, BigInteger msg) {
        return encodeOrDecode(publicKey, msg);
    }

    public BigInteger decode(String privateKey, BigInteger msg) {
        return encodeOrDecode(privateKey, msg);
    }

    private BigInteger encodeOrDecode(String key, BigInteger msg) {
        String x = key.substring(0, key.indexOf(":"));
        String y = key.substring(key.indexOf(":") + 1);
        BigInteger a, n;
        a = new BigInteger(Base64.getDecoder().decode(x));
        n = new BigInteger(Base64.getDecoder().decode(y));
        return qe2(msg, a, n);
    }

    private BigInteger qe2(BigInteger x, BigInteger y, BigInteger z) {// x^y mod z
        BigInteger u;
        BigInteger t, s;
        BigInteger zero = new BigInteger("0");
        BigInteger one = new BigInteger("1");
        s = new BigInteger("1");
        t = x;
        u = y;
        while (!u.equals(zero)) {
            if (!u.and(one).equals(zero))
                s = (s.multiply(t)).mod(z);
            u = u.shiftRight(1);
            t = (t.multiply(t)).mod(z);
        }
        return s;
    }

//    public static boolean rabinmiller(BigInteger n, int cnt) {
//        BigInteger  k = new BigInteger("0");
//        BigInteger zero = new BigInteger("0");
//        BigInteger one = new BigInteger("1");
//        BigInteger two = new BigInteger("2");
//        BigInteger  q = n.subtract(one);
//        BigInteger  r;
//        if (!q.equals(one)&&!q.mod(two).equals(one))
//            return false;
//        for (;!q.mod(two).equals(one);k = k.add(one))
//            q = q.divide(two);
//        for (int i = 0; i<cnt; i++){
//            r = rand().mod(n.subtract(one)).add(one);//从[1,n-1]随机选取一个数
//            if (qe2(r, q, n).equals(one))
//                continue;
//            else{
//                for (BigInteger j = new BigInteger("0"); j.compareTo(k) == -1; j = j.add(one)) {
//                    if (qe2(r, two.pow(j.intValue()).multiply(k), n).equals(n.subtract(one)))
//                        break;
//                    else if (j.equals(k.subtract(one)))
//                        return false;
//                }
//            }
//        }
//        return true;
//    }

    private MSet ext_gcd(BigInteger a, BigInteger b, MSet set) {
        BigInteger zero = new BigInteger("0");
        if (b.equals(zero)) {
            set.d = a;
            set.x = new BigInteger("1");
            set.y = new BigInteger("0");
        } else {
            MSet t = ext_gcd(b, a.mod(b), set);
            set.d = t.d;
            BigInteger tmp = t.y;
            set.y = set.x;
            set.x = tmp;
//            System.out.println("d="+set.d + ",x="+set.x+",y="+set.y);
            set.y = set.y.subtract(set.x.multiply(a.divide(b)));
        }
        return set;
    }

    private BigInteger mod_inverse(BigInteger a, BigInteger m) {
        MSet set = new MSet();
        set.x = new BigInteger("0");
        set.y = new BigInteger("0");
        set.d = new BigInteger("0");
        set = ext_gcd(a, m, set);
        return (m.add(set.x.mod(m))).mod(m);
    }

    public static BigInteger rand(BigInteger n) {
        BigInteger r;
        Random rnd = new Random();
        do {
            r = new BigInteger(n.bitLength(), rnd);
        } while (r.compareTo(n) >= 0);
        return r;
    }

//    public static void main(String[] args) {
//        int succeed = 0;
//        int size = 10;
//        long[] times = new long[size];
//        for (int i = 0; i < size; i++) {
//            long st = System.currentTimeMillis();
//            boolean f = test();
//            long et = System.currentTimeMillis();
//            if (f) succeed++;
//            times[i] = et - st;
//        }
//        System.out.println("成功次数：" + succeed);
//        long totalTime = 0;
//        for (long e : times) {
//            totalTime += e;
//        }
//        System.out.println("平均用时：" + totalTime / size + " ms");
//    }
//
//    private static boolean test() {
//        RSAUtil rsaUtil = new RSAUtil();
//        rsaUtil.generateKeyPair();
//        BigInteger M1 = RSAUtil.rand(new BigInteger("99999999999999999999"));
//        System.out.println(rsaUtil.getPublicKey());
//        BigInteger C = rsaUtil.encode(rsaUtil.getPublicKey(), M1);
//        System.out.println("---------------------------- " + M1);
//        System.out.println("the information encrypted is " + C + "----" + C.bitLength());
//        BigInteger M2 = rsaUtil.decode(rsaUtil.getPrivateKey(), C);
//        System.out.println("the information decrypted is " + M2);
//
//        return M2.equals(M1);
////        return rsaUtil.isAvailableKey(10000);
//    }
//
//    private static String byte2hex(byte[] buffer) {
//        String h = "";
//
//        for (int i = 0; i < buffer.length; i++) {
//            String temp = Integer.toHexString(buffer[i] & 0xFF);
//            if (temp.length() == 1) {
//                temp = "0" + temp;
//            }
//            h = h + " " + temp;
//        }
//
//        return h;
//    }
}
