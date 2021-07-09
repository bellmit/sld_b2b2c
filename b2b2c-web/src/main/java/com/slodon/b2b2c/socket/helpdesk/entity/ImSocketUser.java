package com.slodon.b2b2c.socket.helpdesk.entity;

import lombok.Data;

/**
 * socket客户端用户信息
 */
@Data
public class ImSocketUser {

    private String         storeId;                        //店铺id
    private String         userId;                         //memberId或vendorId
    private String         role;                           //角色类型，1==会员，2==vendor客服

    public ImSocketUser(String storeId, String userId, String role) {
        this.storeId = storeId;
        this.userId = userId;
        this.role = role;
    }
}
