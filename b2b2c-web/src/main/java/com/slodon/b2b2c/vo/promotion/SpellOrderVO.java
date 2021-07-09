package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.SpellTeam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装拼团活动订单VO对象
 * @Author wuxy
 * @date 2020.11.05 19:12
 */
@Data
public class SpellOrderVO implements Serializable {

    private static final long serialVersionUID = 2485601200256673412L;
    @ApiModelProperty("拼团活动id")
    private Integer spellId;

    @ApiModelProperty("拼团活动名称")
    private String spellName;

    @ApiModelProperty("拼团活动团队id")
    private Integer spellTeamId;

    @ApiModelProperty("要求成团人数")
    private Integer requiredNum;

    @ApiModelProperty("团状态(1-拼团中；2-成功；3-失败）")
    private Integer state;

    @ApiModelProperty("团状态值(1-拼团中；2-成功；3-失败）")
    private String stateValue;

    @ApiModelProperty("开团时间")
    private Date createTime;

    @ApiModelProperty("截团时间")
    private Date endTime;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("成团类型：1-自助成团；2-虚拟成团（店铺操作完成）")
    private Integer finishType;

    @ApiModelProperty("成团类型：1-自助成团；2-虚拟成团（店铺操作完成）")
    private String finishTypeValue;

    @ApiModelProperty("会员订单信息")
    private List<SpellMemberOrderVO> memberList;

    public SpellOrderVO(SpellTeam spellTeam) {
        spellId = spellTeam.getSpellId();
        spellName = spellTeam.getSpellName();
        spellTeamId = spellTeam.getSpellTeamId();
        requiredNum = spellTeam.getRequiredNum();
        state = spellTeam.getState();
        stateValue = dealStateValue(spellTeam.getState());
        createTime = spellTeam.getCreateTime();
        endTime = spellTeam.getEndTime();
        goodsId = spellTeam.getGoodsId();
        goodsName = spellTeam.getGoodsName();
        goodsImage = FileUrlUtil.getFileUrl(spellTeam.getGoodsImage(), ImageSizeEnum.SMALL);
        finishType = spellTeam.getFinishType();
        finishTypeValue = dealFinishTypeValue(spellTeam.getFinishType(), spellTeam.getState());
    }

    public static String dealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        switch (state) {
            case SpellConst.SPELL_GROUP_STATE_1:
                value = "拼团中";
                break;
            case SpellConst.SPELL_GROUP_STATE_2:
                value = "拼团成功";
                break;
            case SpellConst.SPELL_GROUP_STATE_3:
                value = "拼团失败";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static String dealFinishTypeValue(Integer finishType, Integer state) {
        String value = null;
        if (state == SpellConst.SPELL_GROUP_STATE_2) {
            if (StringUtils.isEmpty(finishType)) return Language.translate("未知");
            switch (finishType) {
                case SpellConst.FINISH_TYPE_1:
                    value = "自助成团";
                    break;
                case SpellConst.FINISH_TYPE_2:
                    value = "模拟成团";
                    break;
            }
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
