package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.SpellTeam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装拼团活动查看团队VO对象
 * @Author wuxy
 * @date 2020.11.05 09:28
 */
@Data
public class SpellTeamVO implements Serializable {

    private static final long serialVersionUID = -5868887752569922974L;
    @ApiModelProperty("拼团活动团队id")
    private Integer spellTeamId;

    @ApiModelProperty("拼团活动id编号")
    private Integer spellId;

    @ApiModelProperty("活动商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("开团时间")
    private Date createTime;

    @ApiModelProperty("截团时间=开团时间+成团周期")
    private Date endTime;

    @ApiModelProperty("团长id")
    private Integer leaderMemberId;

    @ApiModelProperty("团长姓名")
    private String leaderMemberName;

    @ApiModelProperty("拼团人数")
    private Integer joinedNum;

    @ApiModelProperty("团状态(1-进行中；2-成功；3-失败）")
    private Integer state;

    @ApiModelProperty("团状态(1-进行中；2-成功；3-失败）")
    private String stateValue;

    @ApiModelProperty("要求团人数")
    private Integer requiredNum;

    @ApiModelProperty("差人数")
    private Integer missingNum;

    @ApiModelProperty("剩余时间")
    private Long distanceEndTime;

    @ApiModelProperty("拼团成员列表")
    private List<SpellTeamMemberVO> memberList;

    @ApiModelProperty("是否自己的团")
    private Boolean isSelf = false;

    @ApiModelProperty("是否模拟成团(0-关闭/1-开启）")
    private Integer isSimulateGroup;

    public SpellTeamVO(SpellTeam spellTeam) {
        spellTeamId = spellTeam.getSpellTeamId();
        spellId = spellTeam.getSpellId();
        goodsId = spellTeam.getGoodsId();
        createTime = spellTeam.getCreateTime();
        endTime = spellTeam.getEndTime();
        leaderMemberId = spellTeam.getLeaderMemberId();
        leaderMemberName = spellTeam.getLeaderMemberName();
        joinedNum = spellTeam.getJoinedNum();
        state = spellTeam.getState();
        stateValue = SpellOrderVO.dealStateValue(spellTeam.getState());
    }

}
