package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberProductLookLogReadMapper;
import com.slodon.b2b2c.dao.write.member.MemberProductLookLogWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.example.MemberFollowProductExample;
import com.slodon.b2b2c.member.example.MemberProductLookLogExample;
import com.slodon.b2b2c.member.pojo.MemberFollowProduct;
import com.slodon.b2b2c.member.pojo.MemberProductLookLog;
import com.slodon.b2b2c.model.goods.GoodsExtendModel;
import com.slodon.b2b2c.vo.member.ProductLookLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MemberProductLookLogModel {

    @Resource
    private MemberProductLookLogReadMapper memberProductLookLogReadMapper;
    @Resource
    private GoodsReadMapper goodsReadMapper;

    @Resource
    private MemberProductLookLogWriteMapper memberProductLookLogWriteMapper;
    @Resource
    private MemberFollowProductModel memberFollowProductModel;
    @Resource
    private GoodsExtendModel goodsExtendModel;


    /**
     * 新增商品浏览记录表
     *
     * @param memberProductLookLog
     * @return
     */
    public Integer saveMemberProductLookLog(MemberProductLookLog memberProductLookLog) {
        int count = memberProductLookLogWriteMapper.insert(memberProductLookLog);
        if (count == 0) {
            throw new MallException("添加商品浏览记录表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除商品浏览记录表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteMemberProductLookLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberProductLookLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除商品浏览记录表失败");
            throw new MallException("删除商品浏览记录表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新商品浏览记录表
     *
     * @param memberProductLookLog
     * @return
     */
    public Integer updateMemberProductLookLog(MemberProductLookLog memberProductLookLog) {
        if (StringUtils.isEmpty(memberProductLookLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberProductLookLogWriteMapper.updateByPrimaryKeySelective(memberProductLookLog);
        if (count == 0) {
            log.error("根据logId：" + memberProductLookLog.getLogId() + "更新商品浏览记录表失败");
            throw new MallException("更新商品浏览记录表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取商品浏览记录表详情
     *
     * @param logId logId
     * @return
     */
    public MemberProductLookLog getMemberProductLookLogByLogId(Integer logId) {
        return memberProductLookLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取商品浏览记录表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberProductLookLog> getMemberProductLookLogList(MemberProductLookLogExample example, PagerInfo pager) {
        List<MemberProductLookLog> memberProductLookLogList;
        if (pager != null) {
            pager.setRowsCount(memberProductLookLogReadMapper.countByExample(example));
            memberProductLookLogList = memberProductLookLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberProductLookLogList = memberProductLookLogReadMapper.listByExample(example);
        }
        return memberProductLookLogList;
    }

    /**
     * 根据条件获取商品浏览记录数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getMemberProductLookLogCount(MemberProductLookLogExample example) {
        return memberProductLookLogReadMapper.countByExample(example);
    }

    /**
     * 根据logIds删除商品浏览记录表
     *
     * @param logIds
     * @param memberId
     * @return
     */
    public Integer deleteMemberProductLookLog(String logIds, Integer memberId) {
        MemberProductLookLogExample example = new MemberProductLookLogExample();
        example.setMemberId(memberId);
        example.setLogIdIn(logIds);
        int count = memberProductLookLogWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据logIds：" + logIds + "删除商品浏览记录表失败");
            throw new MallException("删除商品浏览记录表失败,请重试");
        }
        return count;
    }

    /**
     * 清空商品浏览记录表
     *
     * @return
     */
    public Integer emptyMemberProductLookLog(Integer memberId) {
        MemberProductLookLogExample example = new MemberProductLookLogExample();
        example.setMemberId(memberId);
        int count = memberProductLookLogWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("清空商品浏览记录表失败");
            throw new MallException("清空商品浏览记录表失败,请重试");
        }
        return count;
    }


    /**
     * 根据条件获取商品浏览记录表列表
     *
     * @param memberId
     * @param pager    分页信息
     * @return
     */
    public List<ProductLookLogVO> getList(Integer memberId, PagerInfo pager) throws Exception {
        //根据条件查询集合
        MemberProductLookLogExample example = new MemberProductLookLogExample();
        example.setMemberId(memberId);
        example.setGroupBy("DATE_FORMAT(create_time,'%Y-%m-%d')");
        example.setOrderBy("DATE_FORMAT(create_time,'%Y-%m-%d')");
        //按时间分组
        pager.setRowsCount(memberProductLookLogReadMapper.listFieldsPageByExampleAndDay(example, pager.getStart(), 0).size());
        List<String> list = memberProductLookLogReadMapper.listFieldsPageByExampleAndDay(example, pager.getStart(), pager.getPageSize());

        //响应
        List<ProductLookLogVO> vos = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if (!CollectionUtils.isEmpty(list)) {
            for (String day : list) {
                //循环一次为某一天我的足迹
                ProductLookLogVO vo = new ProductLookLogVO();

                //按照时间重新查询那一天我的足迹
                MemberProductLookLogExample timeExample = new MemberProductLookLogExample();
                timeExample.setMemberId(memberId);
                timeExample.setCreateTimeAfter(sdf.parse(day + " 00:00:00"));
                timeExample.setCreateTimeBefore(sdf.parse(day + " 23:59:59"));
                timeExample.setOrderBy("create_time DESC");
                List<MemberProductLookLog> productLookLogList = memberProductLookLogReadMapper.listByExample(timeExample);

                List<ProductLookLogVO.ProductLookLogInfo> infos = new ArrayList<>();
                productLookLogList.forEach(productLookLog -> {
                    ProductLookLogVO.ProductLookLogInfo productLookLogInfo = new ProductLookLogVO.ProductLookLogInfo(productLookLog);
                    productLookLogInfo.setIsFollowProduct(false);
                    MemberFollowProductExample followProductExample = new MemberFollowProductExample();
                    followProductExample.setMemberId(memberId);
                    followProductExample.setGoodsId(productLookLog.getGoodsId());
                    List<MemberFollowProduct> followProductList = memberFollowProductModel.getMemberFollowProductList(followProductExample, null);
                    if (!CollectionUtils.isEmpty(followProductList)) {
                        productLookLogInfo.setIsFollowProduct(true);
                    }
                    infos.add(productLookLogInfo);
                });
                vo.setProductLookLogInfoList(infos);

                //vos集合添加数据
                Date date = new Date();
                String today = simpleDateFormat.format(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -1);
                String yesterday = simpleDateFormat.format(calendar.getTime());

                if (day.equals(today)) {
                    vo.setTime("今天");
                } else if (day.equals(yesterday)) {
                    vo.setTime("昨天");
                } else {
                    vo.setTime(day);
                }
                vos.add(vo);
            }
        }
        return vos;
    }

    /**
     * 新增商品浏览记录表
     *
     * @param product  sku
     * @param memberId 会员id
     * @return
     */
    @Transactional
    public Integer saveMemberProductLookLog(Product product, Integer memberId) {
        //按照goodsId、memberId查询记录数，表中最多只能查出一条数据，如果没有数据，就插入；有数据就更新时间
        MemberProductLookLogExample example = new MemberProductLookLogExample();
        example.setMemberId(memberId);
        example.setGoodsId(product.getGoodsId());
        Goods goods = goodsReadMapper.getByPrimaryKey(product.getGoodsId());
        GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
        goodsExtendExample.setGoodsId(goods.getGoodsId());
        GoodsExtend goodsExtend = goodsExtendModel.getGoodsExtendList(goodsExtendExample, null).get(0);
        GoodsExtend goodsExtendUpdate = new GoodsExtend();
        goodsExtendUpdate.setId(goodsExtend.getId());
        goodsExtendUpdate.setExtendId(goodsExtend.getExtendId());
        goodsExtendUpdate.setClickNumber(goodsExtend.getClickNumber() + 1);
        goodsExtendModel.updateGoodsExtend(goodsExtendUpdate);
        int countMemberAndProduct = memberProductLookLogReadMapper.countByExample(example);
        if (countMemberAndProduct > 0) {
            //有数据，更新时间
            MemberProductLookLog updateOne = new MemberProductLookLog();
            updateOne.setGoodsName(product.getGoodsName());
            updateOne.setGoodsBrief(goods.getGoodsBrief());
            updateOne.setGoodsImage(product.getMainImage());
            updateOne.setProductPrice(StringUtil.isNullOrZero(product.getActivityPrice()) ? product.getProductPrice() : product.getActivityPrice());
            updateOne.setCreateTime(new Date());
            //条件必须有,才能更新
            memberProductLookLogWriteMapper.updateByExampleSelective(updateOne, example);
        } else {
            //添加我的足迹
            MemberProductLookLog insertOne = new MemberProductLookLog();
            insertOne.setMemberId(memberId);
            insertOne.setGoodsId(product.getGoodsId());
            insertOne.setGoodsName(product.getGoodsName());
            insertOne.setGoodsBrief(goods.getGoodsBrief());
            insertOne.setGoodsImage(product.getMainImage());
            insertOne.setProductId(product.getProductId());
            insertOne.setProductPrice(StringUtil.isNullOrZero(product.getActivityPrice()) ? product.getProductPrice() : product.getActivityPrice());
            insertOne.setStoreId(product.getStoreId());
            insertOne.setCreateTime(new Date());
            memberProductLookLogWriteMapper.insert(insertOne);
        }
        return 0;
    }
}