package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.SmsCodeReadMapper;
import com.slodon.b2b2c.dao.write.msg.SmsCodeWriteMapper;
import com.slodon.b2b2c.msg.example.SmsCodeExample;
import com.slodon.b2b2c.msg.pojo.SmsCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SmsCodeModel {

    @Resource
    private SmsCodeReadMapper smsCodeReadMapper;
    @Resource
    private SmsCodeWriteMapper smsCodeWriteMapper;

    /**
     * 新增手机短信验证码记录
     *
     * @param smsCode
     * @return
     */
    public Integer saveSmsCode(SmsCode smsCode) {
        int count = smsCodeWriteMapper.insert(smsCode);
        if (count == 0) {
            throw new MallException("添加手机短信验证码记录失败，请重试");
        }
        return count;
    }

    /**
     * 根据codeId删除手机短信验证码记录
     *
     * @param codeId codeId
     * @return
     */
    public Integer deleteSmsCode(Integer codeId) {
        if (StringUtils.isEmpty(codeId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = smsCodeWriteMapper.deleteByPrimaryKey(codeId);
        if (count == 0) {
            log.error("根据codeId：" + codeId + "删除手机短信验证码记录失败");
            throw new MallException("删除手机短信验证码记录失败,请重试");
        }
        return count;
    }

    /**
     * 根据codeId更新手机短信验证码记录
     *
     * @param smsCode
     * @return
     */
    public Integer updateSmsCode(SmsCode smsCode) {
        if (StringUtils.isEmpty(smsCode.getCodeId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = smsCodeWriteMapper.updateByPrimaryKeySelective(smsCode);
        if (count == 0) {
            log.error("根据codeId：" + smsCode.getCodeId() + "更新手机短信验证码记录失败");
            throw new MallException("更新手机短信验证码记录失败,请重试");
        }
        return count;
    }

    /**
     * 根据codeId获取手机短信验证码记录详情
     *
     * @param codeId codeId
     * @return
     */
    public SmsCode getSmsCodeByCodeId(Integer codeId) {
        return smsCodeReadMapper.getByPrimaryKey(codeId);
    }

    /**
     * 根据条件获取手机短信验证码记录列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SmsCode> getSmsCodeList(SmsCodeExample example, PagerInfo pager) {
        List<SmsCode> smsCodeList;
        if (pager != null) {
            pager.setRowsCount(smsCodeReadMapper.countByExample(example));
            smsCodeList = smsCodeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            smsCodeList = smsCodeReadMapper.listByExample(example);
        }
        return smsCodeList;
    }

    /**
     * 获取符合条件的数量
     *
     * @param example 查询条件
     * @return
     */
    public Integer getSmsCodeCount(SmsCodeExample example) {
        return smsCodeReadMapper.countByExample(example);
    }
}