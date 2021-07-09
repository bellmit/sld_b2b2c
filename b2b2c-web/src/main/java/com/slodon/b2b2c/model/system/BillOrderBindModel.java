package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.BillOrderBindReadMapper;
import com.slodon.b2b2c.dao.write.system.BillOrderBindWriteMapper;
import com.slodon.b2b2c.system.example.BillOrderBindExample;
import com.slodon.b2b2c.system.pojo.BillOrderBind;
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
public class BillOrderBindModel {
    @Resource
    private BillOrderBindReadMapper billOrderBindReadMapper;

    @Resource
    private BillOrderBindWriteMapper billOrderBindWriteMapper;

    /**
     * 新增结算单与订单绑定表
     *
     * @param billOrderBind
     * @return
     */
    public Integer saveBillOrderBind(BillOrderBind billOrderBind) {
        int count = billOrderBindWriteMapper.insert(billOrderBind);
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
    public Integer deleteBillOrderBind(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = billOrderBindWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除结算单与订单绑定表失败");
            throw new MallException("删除结算单与订单绑定表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId更新结算单与订单绑定表
     *
     * @param billOrderBind
     * @return
     */
    public Integer updateBillOrderBind(BillOrderBind billOrderBind) {
        if (StringUtils.isEmpty(billOrderBind.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = billOrderBindWriteMapper.updateByPrimaryKeySelective(billOrderBind);
        if (count == 0) {
            log.error("根据bindId：" + billOrderBind.getBindId() + "更新结算单与订单绑定表失败");
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
    public BillOrderBind getBillOrderBindByBindId(Integer bindId) {
        return billOrderBindReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取结算单与订单绑定表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<BillOrderBind> getBillOrderBindList(BillOrderBindExample example, PagerInfo pager) {
        List<BillOrderBind> billOrderBindList;
        if (pager != null) {
            pager.setRowsCount(billOrderBindReadMapper.countByExample(example));
            billOrderBindList = billOrderBindReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            billOrderBindList = billOrderBindReadMapper.listByExample(example);
        }
        return billOrderBindList;
    }
}