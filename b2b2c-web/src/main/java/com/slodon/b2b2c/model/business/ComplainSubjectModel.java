package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.example.ComplainSubjectExample;
import com.slodon.b2b2c.business.pojo.ComplainSubject;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.business.ComplainSubjectReadMapper;
import com.slodon.b2b2c.dao.write.business.ComplainSubjectWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class ComplainSubjectModel {

    @Resource
    private ComplainSubjectReadMapper complainSubjectReadMapper;
    @Resource
    private ComplainSubjectWriteMapper complainSubjectWriteMapper;

    /**
     * 新增投诉主题表
     *
     * @param complainSubject
     * @return
     */
    public Integer saveComplainSubject(ComplainSubject complainSubject) {
        int count = complainSubjectWriteMapper.insert(complainSubject);
        if (count == 0) {
            throw new MallException("添加投诉主题表失败，请重试");
        }
        return count;
    }

    /**
     * 根据complainSubjectId删除投诉主题表
     *
     * @param complainSubjectId complainSubjectId
     * @return
     */
    public Integer deleteComplainSubject(Integer complainSubjectId) {
        if (StringUtils.isEmpty(complainSubjectId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = complainSubjectWriteMapper.deleteByPrimaryKey(complainSubjectId);
        if (count == 0) {
            log.error("根据complainSubjectId：" + complainSubjectId + "删除投诉主题表失败");
            throw new MallException("删除投诉主题表失败,请重试");
        }
        return count;
    }

    /**
     * 根据complainSubjectId更新投诉主题表
     *
     * @param complainSubject
     * @return
     */
    public Integer updateComplainSubject(ComplainSubject complainSubject) {
        if (StringUtils.isEmpty(complainSubject.getComplainSubjectId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = complainSubjectWriteMapper.updateByPrimaryKeySelective(complainSubject);
        if (count == 0) {
            log.error("根据complainSubjectId：" + complainSubject.getComplainSubjectId() + "更新投诉主题表失败");
            throw new MallException("更新投诉主题表失败,请重试");
        }
        return count;
    }

    /**
     * 根据complainSubjectId获取投诉主题表详情
     *
     * @param complainSubjectId complainSubjectId
     * @return
     */
    public ComplainSubject getComplainSubjectByComplainSubjectId(Integer complainSubjectId) {
        return complainSubjectReadMapper.getByPrimaryKey(complainSubjectId);
    }

    /**
     * 根据条件获取投诉主题表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<ComplainSubject> getComplainSubjectList(ComplainSubjectExample example, PagerInfo pager) {
        List<ComplainSubject> complainSubjectList;
        if (pager != null) {
            pager.setRowsCount(complainSubjectReadMapper.countByExample(example));
            complainSubjectList = complainSubjectReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            complainSubjectList = complainSubjectReadMapper.listByExample(example);
        }
        return complainSubjectList;
    }
}