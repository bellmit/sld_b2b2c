package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.SignRecordReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SignRecordWriteMapper;
import com.slodon.b2b2c.promotion.example.SignRecordExample;
import com.slodon.b2b2c.promotion.pojo.SignRecord;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class SignRecordModel {
    @Resource
    private SignRecordReadMapper signRecordReadMapper;

    @Resource
    private SignRecordWriteMapper signRecordWriteMapper;

    /**
     * 新增签到临时记录表，没有记录增加，有记录更新表
     *
     * @param signRecord
     * @return
     */
    public Integer saveSignRecord(SignRecord signRecord) {
        int count = signRecordWriteMapper.insert(signRecord);
        if (count == 0) {
            throw new MallException("添加签到临时记录表，没有记录增加，有记录更新表失败，请重试");
        }
        return count;
    }

    /**
     * 根据recordId删除签到临时记录表，没有记录增加，有记录更新表
     *
     * @param recordId recordId
     * @return
     */
    public Integer deleteSignRecord(Integer recordId) {
        if (StringUtils.isEmpty(recordId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = signRecordWriteMapper.deleteByPrimaryKey(recordId);
        if (count == 0) {
            log.error("根据recordId：" + recordId + "删除签到临时记录表，没有记录增加，有记录更新表失败");
            throw new MallException("删除签到临时记录表，没有记录增加，有记录更新表失败,请重试");
        }
        return count;
    }

    /**
     * 根据recordId更新签到临时记录表，没有记录增加，有记录更新表
     *
     * @param signRecord
     * @return
     */
    public Integer updateSignRecord(SignRecord signRecord) {
        if (StringUtils.isEmpty(signRecord.getRecordId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = signRecordWriteMapper.updateByPrimaryKeySelective(signRecord);
        if (count == 0) {
            log.error("根据recordId：" + signRecord.getRecordId() + "更新签到临时记录表，没有记录增加，有记录更新表失败");
            throw new MallException("更新签到临时记录表，没有记录增加，有记录更新表失败,请重试");
        }
        return count;
    }

    /**
     * 根据recordId获取签到临时记录表，没有记录增加，有记录更新表详情
     *
     * @param recordId recordId
     * @return
     */
    public SignRecord getSignRecordByRecordId(Integer recordId) {
        return signRecordReadMapper.getByPrimaryKey(recordId);
    }

    /**
     * 根据条件获取签到临时记录表，没有记录增加，有记录更新表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SignRecord> getSignRecordList(SignRecordExample example, PagerInfo pager) {
        List<SignRecord> signRecordList;
        if (pager != null) {
            pager.setRowsCount(signRecordReadMapper.countByExample(example));
            signRecordList = signRecordReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            signRecordList = signRecordReadMapper.listByExample(example);
        }
        return signRecordList;
    }
}