package com.lyl.demo;

import java.math.BigInteger;

public class HexUtils {
    private static final char[] HEXES = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };
    private static final char[] BIG_HEXES = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
    };

    public static byte[] string2Hex(String str) {
        str = str.trim();
        if (str.length() < 2) {
            return null;
        }
        if (str.length() % 2 != 0) {
            return null;
        }
        byte[] result = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); ) {
            result[i / 2] = new BigInteger(str.substring(i, i + 2), 16).byteValue();
            i += 2;
        }
        return result;
    }

    /**
     * byte数组 转换成 16进制小写字符串
     */
    public static String bytes2Hex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        StringBuilder hex = new StringBuilder();

        for (byte b : bytes) {
            hex.append(HEXES[(b >> 4) & 0x0F]);
            hex.append(HEXES[b & 0x0F]);
        }

        return hex.toString();
    }

    /**
     * byte数组 转换成 16进制大写字符串
     */
    public static String bytes2HexB(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        StringBuilder hex = new StringBuilder();

        for (byte b : bytes) {
            hex.append(BIG_HEXES[(b >> 4) & 0x0F]);
            hex.append(BIG_HEXES[b & 0x0F]);
        }
        return hex.toString();
    }
}
