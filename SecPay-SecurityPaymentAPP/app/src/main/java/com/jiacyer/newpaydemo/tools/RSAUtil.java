package com.jiacyer.newpaydemo.tools;

import android.util.Base64;

import java.math.BigInteger;
import java.util.Random;

public class RSAUtil {
    private BigInteger eVal;
    private BigInteger dVal;
    private BigInteger nVal;

    public static class MSet {
        BigInteger d,x,y;
    }

    public RSAUtil() {

    }

    public void generateKeyPair() {
        do {
            generateKeyAction();
        } while (!isAvailableKey(100));
    }

    private boolean isAvailableKey(int testTime) {
        BigInteger boundary = new BigInteger("98765432109876543210");
        boolean f = true;
        for (int i=0; i<testTime; i++) {
            BigInteger testCase = rand(boundary);
            BigInteger tmpCase = encode(getPublicKey(), testCase);
            tmpCase = decode(getPrivateKey(), tmpCase);
            f = f & tmpCase.equals(testCase);
            if (!f) break;
        }
        return f;
    }

    private void generateKeyAction() {
        BigInteger p = null,q = null, eulern;
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
        nVal = p.multiply(q);
        eulern = p.subtract(one).multiply(q.subtract(new BigInteger("1")));
        for (int j = 0; j < 200; j++) {
            eVal = rand(eulern);
            if (eVal.isProbablePrime(20))
                break;
        }
        dVal = mod_inverse(eVal,eulern);
    }

    public String getPublicKey() {
        if (eVal == null || nVal == null)
            return ":";

        String es = Base64.encodeToString(eVal.toByteArray(), Base64.DEFAULT);
        String ns = Base64.encodeToString(nVal.toByteArray(), Base64.DEFAULT);
        return es + ":" + ns;
    }

    public String getPrivateKey() {
        if (dVal == null || nVal == null)
            return ":";

        String ds = Base64.encodeToString(dVal.toByteArray(), Base64.DEFAULT);
        String ns = Base64.encodeToString(nVal.toByteArray(), Base64.DEFAULT);
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
        String y = key.substring(key.indexOf(":")+1);
        BigInteger a, n;
        a = new BigInteger(Base64.decode(x, Base64.DEFAULT));
        n = new BigInteger(Base64.decode(y, Base64.DEFAULT));
        return qe2(msg, a, n);
    }

    private BigInteger qe2(BigInteger x, BigInteger y, BigInteger z) {// x^y mod z
        BigInteger u;
        BigInteger t,s;
        BigInteger zero = new BigInteger("0");
        BigInteger one = new BigInteger("1");
        s = new BigInteger("1"); t = x; u = y;
        while (!u.equals(zero)) {
            if (!u.and(one).equals(zero))
                s = (s.multiply(t)).mod(z);
            u = u.shiftRight(1);
            t = (t.multiply(t)).mod(z);
        }
        return s;
    }

    private MSet ext_gcd(BigInteger a, BigInteger b, MSet set)
    {
        BigInteger zero = new BigInteger("0");
        if(b.equals(zero)){
            set.d = a;
            set.x = new BigInteger("1");
            set.y = new BigInteger("0");
        } else{
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

    private BigInteger mod_inverse(BigInteger a, BigInteger m)
    {
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
}
