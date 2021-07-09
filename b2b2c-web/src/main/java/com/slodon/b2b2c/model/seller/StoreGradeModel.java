package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.seller.StoreGradeReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreGradeWriteMapper;
import com.slodon.b2b2c.seller.dto.StoreGradeAddDTO;
import com.slodon.b2b2c.seller.dto.StoreGradeUpdateDTO;
import com.slodon.b2b2c.seller.example.StoreGradeExample;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class StoreGradeModel {

    @Resource
    private StoreGradeReadMapper storeGradeReadMapper;
    @Resource
    private StoreGradeWriteMapper storeGradeWriteMapper;

    /**
     * 新增店铺等级表
     *
     * @param storeGrade
     * @return
     */
    public Integer saveStoreGrade(StoreGrade storeGrade) {
        int count = storeGradeWriteMapper.insert(storeGrade);
        if (count == 0) {
            throw new MallException("添加店铺等级表失败，请重试");
        }
        return count;
    }

    /**
     * 新增店铺等级表
     *
     * @param storeGradeAddDTO
     * @return
     */
    public Integer saveStoreGrade(StoreGradeAddDTO storeGradeAddDTO) throws Exception {
        //判断等级名称是否重复
        StoreGradeExample example = new StoreGradeExample();
        example.setGradeName(storeGradeAddDTO.getGradeName());
        List<StoreGrade> storeGrades = storeGradeReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(storeGrades)) {
            throw new MallException("等级名称重复，请重新填写");
        }
        StoreGrade storeGradeInsert = new StoreGrade();
        PropertyUtils.copyProperties(storeGradeInsert, storeGradeAddDTO);
        int count = storeGradeWriteMapper.insert(storeGradeInsert);
        if (count == 0) {
            throw new MallException("添加店铺等级表失败，请重试");
        }
        return count;
    }

    /**
     * 根据gradeId删除店铺等级表
     *
     * @param gradeId gradeId
     * @return
     */
    public Integer deleteStoreGrade(Integer gradeId) {
        int count = storeGradeWriteMapper.deleteByPrimaryKey(gradeId);
        if (count == 0) {
            log.error("根据gradeId：" + gradeId + "删除店铺等级表失败");
            throw new MallException("删除店铺等级表失败,请重试");
        }
        return count;
    }

    /**
     * 根据gradeId更新店铺等级表
     *
     * @param storeGrade
     * @return
     */
    public Integer updateStoreGrade(StoreGrade storeGrade) {
        if (StringUtils.isEmpty(storeGrade.getGradeId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeGradeWriteMapper.updateByPrimaryKeySelective(storeGrade);
        if (count == 0) {
            log.error("根据gradeId：" + storeGrade.getGradeId() + "更新店铺等级表失败");
            throw new MallException("更新店铺等级表失败,请重试");
        }
        return count;
    }

    /**
     * 根据gradeId更新店铺等级表
     *
     * @param storeGradeUpdateDTO
     * @return
     */
    public Integer updateStoreGrade(StoreGradeUpdateDTO storeGradeUpdateDTO) throws Exception {
        //判断等级名称是否重复
        if (!StringUtil.isEmpty(storeGradeUpdateDTO.getGradeName())) {
            StoreGradeExample example = new StoreGradeExample();
            example.setGradeName(storeGradeUpdateDTO.getGradeName());
            example.setGradeIdNotEquals(storeGradeUpdateDTO.getGradeId());
            List<StoreGrade> storeGrades = storeGradeReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(storeGrades)) {
                throw new MallException("等级名称重复，请重新填写");
            }
        }
        StoreGrade storeGradeUpdate = new StoreGrade();
        PropertyUtils.copyProperties(storeGradeUpdate, storeGradeUpdateDTO);
        int count = storeGradeWriteMapper.updateByPrimaryKeySelective(storeGradeUpdate);
        if (count == 0) {
            log.error("根据gradeId：" + storeGradeUpdateDTO.getGradeId() + "更新店铺等级表失败");
            throw new MallException("更新店铺等级表失败,请重试");
        }
        return count;
    }

    /**
     * 根据gradeId获取店铺等级表详情
     *
     * @param gradeId gradeId
     * @return
     */
    public StoreGrade getStoreGradeByGradeId(Integer gradeId) {
        return storeGradeReadMapper.getByPrimaryKey(gradeId);
    }

    /**
     * 根据条件获取店铺等级表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreGrade> getStoreGradeList(StoreGradeExample example, PagerInfo pager) {
        List<StoreGrade> storeGradeList;
        if (pager != null) {
            pager.setRowsCount(storeGradeReadMapper.countByExample(example));
            storeGradeList = storeGradeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeGradeList = storeGradeReadMapper.listByExample(example);
        }
        return storeGradeList;
    }
}