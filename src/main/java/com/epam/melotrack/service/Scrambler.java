package com.epam.melotrack.service;

import org.apache.commons.codec.digest.DigestUtils;

public class Scrambler {

    private final static String SALT = "abscondam";
    private final static int HASHING_COUNT = 3;

    public static String encrypt(String string) {
        String stringHex = null;
        if (string != null) {
            stringHex = string;
            int hashingCount = HASHING_COUNT;
            while (hashingCount > 0) {
                stringHex = DigestUtils.md5Hex(stringHex + SALT);
                hashingCount--;
            }
        }
        return stringHex;
    }

}
