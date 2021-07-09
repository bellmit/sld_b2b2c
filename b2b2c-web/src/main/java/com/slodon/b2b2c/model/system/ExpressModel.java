package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.system.ExpressReadMapper;
import com.slodon.b2b2c.dao.write.system.ExpressWriteMapper;
import com.slodon.b2b2c.system.example.ExpressExample;
import com.slodon.b2b2c.system.pojo.Express;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class ExpressModel {
    @Resource
    private ExpressReadMapper expressReadMapper;

    @Resource
    private ExpressWriteMapper expressWriteMapper;

    /**
     * 新增快递公司
     *
     * @param express
     * @return
     */
    public Integer saveExpress(Express express) {
        //物流公司名称查重
        ExpressExample expressExample = new ExpressExample();
        expressExample.setExpressName(express.getExpressName());
        List<Express> expresses = expressReadMapper.listByExample(expressExample);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(expresses), "物流公司名称重复,请重试");
        int count = expressWriteMapper.insert(express);
        if (count == 0) {
            throw new MallException("添加快递公司失败，请重试");
        }
        return count;
    }

    /**
     * 根据expressId删除快递公司
     *
     * @param expressId expressId
     * @return
     */
    public Integer deleteExpress(Integer expressId) {
        if (StringUtils.isEmpty(expressId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = expressWriteMapper.deleteByPrimaryKey(expressId);
        if (count == 0) {
            log.error("根据expressId：" + expressId + "删除快递公司失败");
            throw new MallException("删除快递公司失败,请重试");
        }
        return count;
    }

    /**
     * 根据expressId更新快递公司
     *
     * @param express
     * @return
     */
    public Integer updateExpress(Express express) {
        if (StringUtils.isEmpty(express.getExpressId())) {
            throw new MallException("请选择要修改的数据");
        }
        //物流公司名称查重
        if (!StringUtils.isEmpty(express.getExpressName())){
            ExpressExample expressExample = new ExpressExample();
            expressExample.setExpressName(express.getExpressName());
            expressExample.setExpressIdNotEquals(express.getExpressId());
            List<Express> expresses = expressReadMapper.listByExample(expressExample);
            AssertUtil.isTrue(!CollectionUtils.isEmpty(expresses), "物流公司名称重复,请重试");
        }
        int count = expressWriteMapper.updateByPrimaryKeySelective(express);
        if (count == 0) {
            log.error("根据expressId：" + express.getExpressId() + "更新快递公司失败");
            throw new MallException("更新快递公司失败,请重试");
        }
        return count;
    }

    /**
     * 根据expressId获取快递公司详情
     *
     * @param expressId expressId
     * @return
     */
    public Express getExpressByExpressId(Integer expressId) {
        return expressReadMapper.getByPrimaryKey(expressId);
    }

    /**
     * 根据条件获取快递公司列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Express> getExpressList(ExpressExample example, PagerInfo pager) {
        List<Express> expressList;
        if (pager != null) {
            pager.setRowsCount(expressReadMapper.countByExample(example));
            expressList = expressReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            expressList = expressReadMapper.listByExample(example);
        }
        return expressList;
    }
}