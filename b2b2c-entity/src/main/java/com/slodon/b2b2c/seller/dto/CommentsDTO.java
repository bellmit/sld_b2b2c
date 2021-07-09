package com.slodon.b2b2c.seller.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 商品评论DTO
 * @Author wuxy
 */
@Data
public class CommentsDTO implements Serializable {

    private static final long serialVersionUID = -8037451078093394280L;
    private Integer description;
    private Integer serviceAttitude;
    private Integer deliverSpeed;
    private Integer number;
}
