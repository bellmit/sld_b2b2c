package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.integral.IntegralBillOrderBindReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralBillOrderBindWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralBillOrderBindExample;
import com.slodon.b2b2c.integral.pojo.IntegralBillOrderBind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 结算单与订单绑定表model
 */
@Component
@Slf4j
public class IntegralBillOrderBindModel {
    @Resource
    private IntegralBillOrderBindReadMapper integralBillOrderBindReadMapper;

    @Resource
    private IntegralBillOrderBindWriteMapper integralBillOrderBindWriteMapper;

    /**
     * 新增结算单与订单绑定表
     *
     * @param integralBillOrderBind
     * @return
     */
    public Integer saveIntegralBillOrderBind(IntegralBillOrderBind integralBillOrderBind) {
        int count = integralBillOrderBindWriteMapper.insert(integralBillOrderBind);
        if (count == 0) {
            throw new MallException("添加结算单与订单绑定表失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除结算单与订单绑定表
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteIntegralBillOrderBind(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralBillOrderBindWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除结算单与订单绑定表失败");
            throw new MallException("删除结算单与订单绑定表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId更新结算单与订单绑定表
     *
     * @param integralBillOrderBind
     * @return
     */
    public Integer updateIntegralBillOrderBind(IntegralBillOrderBind integralBillOrderBind) {
        if (StringUtils.isEmpty(integralBillOrderBind.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralBillOrderBindWriteMapper.updateByPrimaryKeySelective(integralBillOrderBind);
        if (count == 0) {
            log.error("根据bindId：" + integralBillOrderBind.getBindId() + "更新结算单与订单绑定表失败");
            throw new MallException("更新结算单与订单绑定表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取结算单与订单绑定表详情
     *
     * @param bindId bindId
     * @return
     */
    public IntegralBillOrderBind getIntegralBillOrderBindByBindId(Integer bindId) {
        return integralBillOrderBindReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取结算单与订单绑定表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralBillOrderBind> getIntegralBillOrderBindList(IntegralBillOrderBindExample example, PagerInfo pager) {
        List<IntegralBillOrderBind> integralBillOrderBindList;
        if (pager != null) {
            pager.setRowsCount(integralBillOrderBindReadMapper.countByExample(example));
            integralBillOrderBindList = integralBillOrderBindReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralBillOrderBindList = integralBillOrderBindReadMapper.listByExample(example);
        }
        return integralBillOrderBindList;
    }
}