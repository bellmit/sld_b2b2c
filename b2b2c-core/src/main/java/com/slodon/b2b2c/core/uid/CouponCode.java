package com.slodon.b2b2c.core.uid;

import java.util.Date;

/**
 * 随机生成优惠券编码
 */
public class CouponCode {

    public static final long MIN_VALUE = 0x8000000000000000L;

    public static final long MAX_VALUE = 0x7fffffffffffffffL;

    final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'P', 'N'};

    /**
     * @param i
     * @param shift
     * @return
     */
    private static String toUnsignedString(long i, int shift) {
        char[] buf = new char[64];
        int charPos = 64;
        int radix = 1 << shift;
        long mask = radix - 1;
        do {
            buf[--charPos] = digits[(int) (i & mask)];
            i >>>= shift;
        } while (i != 0);
        return new String(buf, charPos, (64 - charPos));
    }


    /**
     * j为2的次方，如转成16进制就是4，32进制就是5...
     *
     * @param i
     * @param j
     * @return
     */
    public static String getRand(long i, int j) {
        return toUnsignedString(i, j);
    }


    /**
     * 随机码＋时间戳＋随机码的生成
     *
     * @return
     */
    public static Long getRand() {
        String str1, str2, str3;
        str1 = getRandStr(2);
        str3 = getRandStr(3);
        str2 = (new Date()).getTime() + "";
        return Long.parseLong(str1 + str2 + str3);
    }

    // 主键生成
    public static String getKey() {
        return getRand(getRand(), 6);
    }


    /**
     * 生成指定长度的随机串
     *
     * @param length
     * @return
     */
    public static String getRandStr(Integer length) {
        String str = "";
        while (str.length() != length) {
            str = (Math.random() + "").substring(2, 2 + length);
        }
        return str;
    }
}
