package com.slodon.b2b2c.model.promotion;


import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.SeckillStageReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SeckillStageWriteMapper;
import com.slodon.b2b2c.promotion.example.SeckillStageExample;
import com.slodon.b2b2c.promotion.pojo.Seckill;
import com.slodon.b2b2c.promotion.pojo.SeckillStage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 秒杀场次model
 */
@Component
@Slf4j
public class SeckillStageModel {
    @Resource
    private SeckillStageReadMapper seckillStageReadMapper;

    @Resource
    private SeckillStageWriteMapper seckillStageWriteMapper;

    /**
     * 新增秒杀场次
     *
     * @param seckillStage
     * @return
     */
    public Integer saveSeckillStage(SeckillStage seckillStage) {
        int count = seckillStageWriteMapper.insert(seckillStage);
        if (count == 0) {
            throw new MallException("添加秒杀场次失败，请重试");
        }
        return count;
    }

    /**
     * 保存多个秒杀场次
     *
     * @param seckill
     * @param stages
     * @return
     */
    public void saveSeckillStage(Seckill seckill, String stages) throws Exception {
        SeckillStage seckillStage = new SeckillStage();
        seckillStage.setSeckillId(seckill.getSeckillId());
        seckillStage.setSeckillName(seckill.getSeckillName());
        seckillStage.setCreateTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startTimeDate = sdf.parse(sdf.format(seckill.getStartTime()));
        Date endTimeDate = sdf.parse(sdf.format(seckill.getEndTime()));
        List<String> list = new ArrayList<>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startTimeDate);
        while (endTimeDate.after(startCal.getTime())) {
            list.add(sdf.format(startCal.getTime()));
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        list.add(sdf.format(endTimeDate));

        //遍历获取活动期间每一天的日期
        for (int i = 0; i < list.size(); i++) {
            SimpleDateFormat sdfSec = new SimpleDateFormat("yyyy年MM月dd日");
            String dateNew = sdfSec.format(sdf.parse(list.get(i)));
            SimpleDateFormat sdfThd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<String> stageList = Arrays.asList(stages.split(","));
            Collections.sort(stageList);
            for (Integer j = 0; j < stageList.size(); j++) {
                seckillStage.setStageName(dateNew + stageList.get(j) + "场次");
                //场次开始时间为活动开始日期加场次时间
                seckillStage.setStartTime(sdfThd.parse(list.get(i) + " " + stageList.get(j) + ":00"));
                if (j.equals(stageList.size() - 1)) {
                    //场次结束时间为当天23：59：59
                    seckillStage.setEndTime(sdfThd.parse(list.get(i) + " 23:59:59"));
                } else {
                    //场次结束时间为下场次开始前1秒
                    Date endtime = sdfThd.parse(list.get(i) + " " + stageList.get(j + 1) + ":00");
                    Calendar c = Calendar.getInstance();
                    c.setTime(endtime);
                    c.add(Calendar.SECOND, -1);
                    endtime = c.getTime();
                    seckillStage.setEndTime(endtime);
                }
                int count = seckillStageWriteMapper.insert(seckillStage);
                if (count == 0) {
                    throw new MallException("添加秒杀场次失败，请重试");
                }
            }
        }
    }


    /**
     * 获取两个时间中的每一天
     * @param startDate 开始时间 yyyy-MM-dd
     * @param endDate 结束时间 yyyy-MM-dd
     * @return
     */
    private List<String> getDays(Date startDate, Date endDate){
        //定义一个接受时间的集合
        List<Date> lDate = new ArrayList<>();
        lDate.add(startDate);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(startDate);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(endDate);
        // 测试此日期是否在指定日期之后
        while (endDate.after(calBegin.getTime()))  {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        List<String> datas = new LinkedList<>();
        for (Date date : lDate) {
            datas.add(new SimpleDateFormat("yyyy-MM-dd").format(date));
        }
        return datas;
    }

    /**
     * 根据stageId删除秒杀场次
     *
     * @param stageId stageId
     * @return
     */
    public Integer deleteSeckillStage(Integer stageId) {
        if (StringUtils.isEmpty(stageId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = seckillStageWriteMapper.deleteByPrimaryKey(stageId);
        if (count == 0) {
            log.error("根据stageId：" + stageId + "删除秒杀场次失败");
            throw new MallException("删除秒杀场次失败,请重试");
        }
        return count;
    }

    public Integer deleteSeckillStageBySeckillId(Integer seckillId) {
        SeckillStageExample example = new SeckillStageExample();
        example.setSeckillId(seckillId);
        int count = seckillStageWriteMapper.deleteByExample(example);
        return count;
    }


    /**
     * 根据stageId更新秒杀场次
     *
     * @param seckillStage
     * @return
     */
    public Integer updateSeckillStage(SeckillStage seckillStage) {
        if (StringUtils.isEmpty(seckillStage.getStageId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = seckillStageWriteMapper.updateByPrimaryKeySelective(seckillStage);
        if (count == 0) {
            log.error("根据stageId：" + seckillStage.getStageId() + "更新秒杀场次失败");
            throw new MallException("更新秒杀场次失败,请重试");
        }
        return count;
    }

    /**
     * 根据stageId获取秒杀场次详情
     *
     * @param stageId stageId
     * @return
     */
    public SeckillStage getSeckillStageByStageId(Integer stageId) {
        return seckillStageReadMapper.getByPrimaryKey(stageId);
    }

    /**
     * 根据条件获取秒杀场次列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillStage> getSeckillStageList(SeckillStageExample example, PagerInfo pager) {
        List<SeckillStage> seckillStageList;
        if (pager != null) {
            pager.setRowsCount(seckillStageReadMapper.countByExample(example));
            seckillStageList = seckillStageReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            seckillStageList = seckillStageReadMapper.listByExample(example);
        }
        return seckillStageList;
    }

    /**
     * 根据条件获取秒杀场次列表
     *
     * @param fields  查询字段，逗号分隔
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillStage> getSeckillStageFieldList(String fields, SeckillStageExample example, PagerInfo pager) {
        List<SeckillStage> seckillStageList;
        if (pager != null) {
            pager.setRowsCount(seckillStageReadMapper.countByExample(example));
            seckillStageList = seckillStageReadMapper.listPageByFieldsExample(fields, example, pager.getStart(), pager.getPageSize());
        } else {
            seckillStageList = seckillStageReadMapper.listByFieldsExample(fields, example);
        }
        return seckillStageList;
    }


}