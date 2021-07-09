package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.StoreTplReadMapper;
import com.slodon.b2b2c.dao.write.msg.StoreTplWriteMapper;
import com.slodon.b2b2c.msg.dto.StoreTplDTO;
import com.slodon.b2b2c.msg.example.StoreTplExample;
import com.slodon.b2b2c.msg.pojo.StoreTpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class StoreTplModel {

    @Resource
    private StoreTplReadMapper storeTplReadMapper;
    @Resource
    private StoreTplWriteMapper storeTplWriteMapper;

    /**
     * 新增商家消息模板--内置表
     *
     * @param storeTpl
     * @return
     */
    public Integer saveStoreTpl(StoreTpl storeTpl) {
        int count = storeTplWriteMapper.insert(storeTpl);
        if (count == 0) {
            throw new MallException("添加商家消息模板--内置表失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplCode删除商家消息模板--内置表
     *
     * @param tplCode tplCode
     * @return
     */
    public Integer deleteStoreTpl(String tplCode) {
        if (StringUtils.isEmpty(tplCode)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeTplWriteMapper.deleteByPrimaryKey(tplCode);
        if (count == 0) {
            log.error("根据tplCode：" + tplCode + "删除商家消息模板--内置表失败");
            throw new MallException("删除商家消息模板--内置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode更新商家消息模板--内置表
     *
     * @param storeTpl
     * @return
     */
    public Integer updateStoreTpl(StoreTpl storeTpl) {
        if (StringUtils.isEmpty(storeTpl.getTplCode())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeTplWriteMapper.updateByPrimaryKeySelective(storeTpl);
        if (count == 0) {
            log.error("根据tplCode：" + storeTpl.getTplCode() + "更新商家消息模板--内置表失败");
            throw new MallException("更新商家消息模板--内置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode更新商家消息模板--内置表
     *
     * @param storeTplDTO
     * @return
     */
    @SneakyThrows
    public Integer updateStoreTpl(StoreTplDTO storeTplDTO) {
        if (StringUtils.isEmpty(storeTplDTO.getTplCode())) {
            throw new MallException("请选择要修改的数据");
        }
        StoreTpl storeTpl = new StoreTpl();
        PropertyUtils.copyProperties(storeTpl, storeTplDTO);
        int count = storeTplWriteMapper.updateByPrimaryKeySelective(storeTpl);
        if (count == 0) {
            log.error("根据tplCode：" + storeTpl.getTplCode() + "更新商家消息模板--内置表失败");
            throw new MallException("更新商家消息模板--内置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode获取商家消息模板--内置表详情
     *
     * @param tplCode tplCode
     * @return
     */
    public StoreTpl getStoreTplByTplCode(String tplCode) {
        return storeTplReadMapper.getByPrimaryKey(tplCode);
    }

    /**
     * 根据条件获取商家消息模板--内置表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreTpl> getStoreTplList(StoreTplExample example, PagerInfo pager) {
        List<StoreTpl> storeTplList;
        if (pager != null) {
            pager.setRowsCount(storeTplReadMapper.countByExample(example));
            storeTplList = storeTplReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeTplList = storeTplReadMapper.listByExample(example);
        }
        return storeTplList;
    }
}