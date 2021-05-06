package com.lwh147.rtms.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description: MD5加密工具类
 * @author: lwh
 * @create: 2021/5/5 13:12
 * @version: v1.0
 **/
public class MD5Util {

    private MD5Util() {
    }

    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 16是表示转换为16进制数
        return new BigInteger(1, digest).toString(16);
    }

}
