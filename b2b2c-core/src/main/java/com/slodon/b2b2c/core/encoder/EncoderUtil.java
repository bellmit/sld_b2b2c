package com.slodon.b2b2c.core.encoder;

/**
 * 商品GID编码, 解码工具
 */
public class EncoderUtil {

    /**
     * @param storeId 店铺ID
     * @return 商品gid
     */
    public static Long getStoreSidEncoder(Long storeId) {
        if (storeId <= 0) {
            return 0L;
        } else {
            return 100000 + storeId * 10 + (storeId * storeId + 2) % 9;
        }
    }

    /**
     * @param sid 店铺sid; sid为以1开头的6位数字,最后一位为校验位.
     * @return 商品storeId; 如果sid格式不对或校验不对,返回0
     */
    public static Long getStoreIdDecoder(Long sid) {
        if ((sid > 100000) && (sid < 999999)) {
            Long storeId = Long.valueOf(sid / 10 - 10000);
            Long checkCode = Long.valueOf(sid % 10);

            if (checkCode == (storeId * storeId + 2) % 9) {
                return storeId;
            } else {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    /**
     * @param helpListId 帮助列表ID, helpListId为以以15开头的8位数字组合,最后一位为校验位. 150002xx
     * @return 商品帮助列表ID; 如果格式不对或校验不对,返回0
     */
    public static Integer getHelpListIdDecoder(String helpListId) {
        if (helpListId.length() == 8) {
            //获取help list的数字ID
            Integer hID = Integer.valueOf(helpListId.substring(2, 6));
            Integer checkCode = Integer.valueOf(helpListId.substring(6));

            if (checkCode == (hID * hID + 5) % 99) {
                return hID;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * @param hID 帮助列表编码ID, 编码ID为以15开头的8位数字组合,最后一位为校验位. 150002xx
     * @return 商品帮助列表编码ID; 如果格式不对或校验不对,返回0
     */
    public static Integer getHelpListIdEncoder(Integer hID) {
        if (hID > 0) {
            return 15000000 + hID * 100 + (hID * hID + 5) % 99;
        } else {
            return 0;
        }
    }

    /**
     * @param helpId 帮助列表ID, helpListId为以以16开头的8位数字组合,最后一位为校验位. 160002xx
     * @return 商品帮助列表ID; 如果格式不对或校验不对,返回0
     */
    public static Integer getHelpIdDecoder(String helpId) {
        if (helpId.length() == 8) {
            //获取help list的数字ID
            Integer hID = Integer.valueOf(helpId.substring(2, 6));
            Integer checkCode = Integer.valueOf(helpId.substring(6));

            if (checkCode == (hID * hID * hID + 6) % 99) {
                return hID;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * @param hID 帮助列表编码ID, 编码ID为以16开头的8位数字组合,最后一位为校验位. 160002xx
     * @return 商品帮助列表编码ID; 如果格式不对或校验不对,返回0
     */
    public static Integer getHelpIdEncoder(Integer hID) {
        if (hID > 0) {
            return 16000000 + hID * 100 + (hID * hID * hID + 6) % 99;
        } else {
            return 0;
        }
    }

}
