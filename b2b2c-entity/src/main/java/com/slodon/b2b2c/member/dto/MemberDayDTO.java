package com.slodon.b2b2c.member.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员数量统计DTO
 */
@Data
public class MemberDayDTO implements Serializable {

    private static final long serialVersionUID = -4712728074851105682L;
    private String day;
    private Integer number;

}
