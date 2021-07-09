package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.constant.AdminConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.system.ReasonReadMapper;
import com.slodon.b2b2c.dao.write.system.ReasonWriteMapper;
import com.slodon.b2b2c.system.example.ReasonExample;
import com.slodon.b2b2c.system.pojo.Reason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class ReasonModel {

    @Resource
    private ReasonReadMapper reasonReadMapper;
    @Resource
    private ReasonWriteMapper reasonWriteMapper;

    /**
     * 新增原因表
     *
     * @param reason
     * @return
     */
    public Integer saveReason(Reason reason) {
        int count = reasonWriteMapper.insert(reason);
        if (count == 0) {
            throw new MallException("添加原因表失败，请重试");
        }
        return count;
    }

    /**
     * 根据reasonId删除原因表
     *
     * @param reasonId reasonId
     * @return
     */
    public Integer deleteReason(Integer reasonId) {
        if (StringUtils.isEmpty(reasonId)) {
            throw new MallException("请选择要删除的数据");
        }
        //查询原因
        Reason reasonDb = reasonReadMapper.getByPrimaryKey(reasonId);
        AssertUtil.notNull(reasonDb, "原因不存在");
        AssertUtil.isTrue(reasonDb.getIsInner() == AdminConst.IS_INNER_YES, "内置数据不可删除");

        int count = reasonWriteMapper.deleteByPrimaryKey(reasonId);
        if (count == 0) {
            log.error("根据reasonId：" + reasonId + "删除原因表失败");
            throw new MallException("删除原因表失败,请重试");
        }
        return count;
    }

    /**
     * 根据reasonId更新原因表
     *
     * @param reason
     * @return
     */
    public Integer updateReason(Reason reason) {
        if (StringUtils.isEmpty(reason.getReasonId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = reasonWriteMapper.updateByPrimaryKeySelective(reason);
        if (count == 0) {
            log.error("根据reasonId：" + reason.getReasonId() + "更新原因表失败");
            throw new MallException("更新原因表失败,请重试");
        }
        return count;
    }

    /**
     * 根据reasonId获取原因表详情
     *
     * @param reasonId reasonId
     * @return
     */
    public Reason getReasonByReasonId(Integer reasonId) {
        return reasonReadMapper.getByPrimaryKey(reasonId);
    }

    /**
     * 根据条件获取原因表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Reason> getReasonList(ReasonExample example, PagerInfo pager) {
        List<Reason> reasonList;
        if (pager != null) {
            pager.setRowsCount(reasonReadMapper.countByExample(example));
            reasonList = reasonReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            reasonList = reasonReadMapper.listByExample(example);
        }
        return reasonList;
    }

    /**
     * 根据条件获取原因数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getReasonCount(ReasonExample example) {
        return reasonReadMapper.countByExample(example);
    }
}