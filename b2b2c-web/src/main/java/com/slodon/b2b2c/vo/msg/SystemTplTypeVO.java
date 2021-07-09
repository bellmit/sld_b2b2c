package com.slodon.b2b2c.vo.msg;

import com.slodon.b2b2c.msg.pojo.MemberTpl;
import com.slodon.b2b2c.msg.pojo.StoreTpl;
import com.slodon.b2b2c.msg.pojo.SystemTplType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装消息模板分类VO对象
 * @Author wuxy
 */
@Data
public class SystemTplTypeVO implements Serializable {

    private static final long serialVersionUID = -3467050838534658622L;
    @ApiModelProperty("消息模板分类编码")
    private String tplTypeCode;

    @ApiModelProperty("消息模板分类名称")
    private String tplName;

    @ApiModelProperty("会员模板列表")
    private List<MemberTplVO> memberTplList;

    @ApiModelProperty("商户模板列表")
    private List<StoreTplVO> storeTplList;

    public SystemTplTypeVO(SystemTplType systemTplType) {
        tplTypeCode = systemTplType.getTplTypeCode();
        tplName = systemTplType.getTplName();
        memberTplList = dealMemberTplList(systemTplType.getMemberTplList());
        storeTplList = dealStoreTplList(systemTplType.getStoreTplList());
    }

    public List<MemberTplVO> dealMemberTplList(List<MemberTpl> memberTplList) {
        List<MemberTplVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(memberTplList)) {
            memberTplList.forEach(memberTpl -> {
                vos.add(new MemberTplVO(memberTpl));
            });
        }
        return vos;
    }

    public List<StoreTplVO> dealStoreTplList(List<StoreTpl> storeTplList) {
        List<StoreTplVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeTplList)) {
            storeTplList.forEach(storeTpl -> {
                vos.add(new StoreTplVO(storeTpl));
            });
        }
        return vos;
    }
}
