package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.SpellTeamMember;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装拼团成员VO对象
 * @Author wuxy
 * @date 2020.11.05 09:35
 */
@Data
public class SpellTeamMemberVO implements Serializable {

    private static final long serialVersionUID = -861668070780134872L;
    @ApiModelProperty("拼团活动团队id")
    private Integer spellTeamId;

    @ApiModelProperty("参团会员id")
    private Integer memberId;

    @ApiModelProperty("参团会员名称")
    private String memberName;

    @ApiModelProperty("会员头像")
    private String memberAvatar;

    @ApiModelProperty("团长标识(0-非团长；1-团长）")
    private Integer isLeader;

    public SpellTeamMemberVO(SpellTeamMember member) {
        spellTeamId = member.getSpellTeamId();
        memberId = member.getMemberId();
        memberName = member.getMemberName();
        memberAvatar = dealMemberAvatar(member.getMemberAvatar().trim());
        isLeader = member.getIsLeader();
    }

    public static String dealMemberAvatar(String memberAvatar) {
        String value = null;
        if (StringUtils.isEmpty(memberAvatar)) return null;
        if (memberAvatar.startsWith("http")) {
            value = memberAvatar;
        } else {
            value = FileUrlUtil.getFileUrl(memberAvatar, null);
        }
        return value;
    }
}
