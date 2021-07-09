package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.read.member.MemberFollowProductReadMapper;
import com.slodon.b2b2c.dao.write.member.MemberFollowProductWriteMapper;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.goods.ProductReadMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.member.example.MemberFollowProductExample;
import com.slodon.b2b2c.member.pojo.MemberFollowProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MemberFollowProductModel {

    @Resource
    private MemberFollowProductReadMapper memberFollowProductReadMapper;
    @Resource
    private ProductReadMapper productReadMapper;
    @Resource
    private GoodsReadMapper goodsReadMapper;

    @Resource
    private MemberFollowProductWriteMapper memberFollowProductWriteMapper;

    /**
     * 新增会员收藏商品表
     *
     * @param memberFollowProduct
     * @return
     */
    public Integer saveMemberFollowProduct(MemberFollowProduct memberFollowProduct) {
        int count = memberFollowProductWriteMapper.insert(memberFollowProduct);
        if (count == 0) {
            throw new MallException("添加会员收藏商品表失败，请重试");
        }
        return count;
    }

    /**
     * 收藏/取消收藏商品
     *
     * @param productIds
     * @param isCollect
     * @param memberId
     * @return
     */
    @Transactional
    public Boolean editMemberFollowProduct(String productIds, Boolean isCollect, Integer memberId) {
        //基于货品id查询商品表数据
        ProductExample example = new ProductExample();
        example.setProductIdIn(productIds);
        List<Product> list = productReadMapper.listByExample(example);
        if (isCollect) {
            //true 收藏
            for (Product product : list) {
                //查重
                MemberFollowProductExample followProductExample = new MemberFollowProductExample();
                followProductExample.setGoodsId(product.getGoodsId());      //spu商品表 goodsId
                followProductExample.setMemberId(memberId);
                List<MemberFollowProduct> memberFollowProductList = memberFollowProductReadMapper.listByExample(followProductExample);
                Goods goods = goodsReadMapper.getByPrimaryKey(product.getGoodsId());
                if (CollectionUtils.isEmpty(memberFollowProductList)) {
                    //goodsId 未收藏,直接新增
                    MemberFollowProduct memberFollowProduct = new MemberFollowProduct();

                    memberFollowProduct.setMemberId(memberId);
                    memberFollowProduct.setProductId(product.getProductId());
                    memberFollowProduct.setProductPrice(product.getProductPrice());
                    memberFollowProduct.setProductImage(product.getMainImage());
                    memberFollowProduct.setGoodsId(product.getGoodsId());
                    memberFollowProduct.setGoodsName(product.getGoodsName());
                    memberFollowProduct.setGoodsBrief(goods.getGoodsBrief());
                    memberFollowProduct.setStoreId(product.getStoreId());
                    memberFollowProduct.setStoreName(product.getStoreName());
                    memberFollowProduct.setCreateTime(new Date());
                    memberFollowProduct.setGoodsCategoryId(product.getCategoryId3());
                    memberFollowProduct.setStoreCategoryId(1);                  //待确认

                    int count = memberFollowProductWriteMapper.insert(memberFollowProduct);
                    AssertUtil.notNullOrZero(count, "添加会员收藏商品表失败，请重试");

                } else {
                    //goodsId 收藏,再次查询 sku表 货品表 productId
                    for (MemberFollowProduct followProduct : memberFollowProductList) {
                        if (product.getProductId() != followProduct.getProductId()) {
                            //productId 不一样,更新会员收藏商品表,否则跳过
                            followProduct.setProductId(product.getProductId());
                            followProduct.setProductPrice(product.getProductPrice());
                            followProduct.setProductImage(product.getMainImage());
                            followProduct.setCreateTime(new Date());

                            int count = memberFollowProductWriteMapper.updateByPrimaryKeySelective(followProduct);
                            AssertUtil.notNullOrZero(count, "更新会员收藏商品表失败，请重试");
                        }
                    }
                }
            }
        } else {
            //取消收藏,直接删除
            MemberFollowProductExample followProductExample = new MemberFollowProductExample();
            followProductExample.setProductIdIn(productIds);
            followProductExample.setMemberId(memberId);
            int count = memberFollowProductWriteMapper.deleteByExample(followProductExample);
        }

        return true;
    }

    /**
     * 根据followId删除会员收藏商品表
     *
     * @param followId followId
     * @return
     */
    public Integer deleteMemberFollowProduct(Integer followId) {
        if (StringUtils.isEmpty(followId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberFollowProductWriteMapper.deleteByPrimaryKey(followId);
        if (count == 0) {
            log.error("根据followId：" + followId + "删除会员收藏商品表失败");
            throw new MallException("删除会员收藏商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据followId更新会员收藏商品表
     *
     * @param memberFollowProduct
     * @return
     */
    public Integer updateMemberFollowProduct(MemberFollowProduct memberFollowProduct) {
        if (StringUtils.isEmpty(memberFollowProduct.getFollowId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberFollowProductWriteMapper.updateByPrimaryKeySelective(memberFollowProduct);
        if (count == 0) {
            log.error("根据followId：" + memberFollowProduct.getFollowId() + "更新会员收藏商品表失败");
            throw new MallException("更新会员收藏商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据followId获取会员收藏商品表详情
     *
     * @param followId followId
     * @return
     */
    public MemberFollowProduct getMemberFollowProductByFollowId(Integer followId) {
        return memberFollowProductReadMapper.getByPrimaryKey(followId);
    }

    /**
     * 根据条件获取会员收藏商品表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberFollowProduct> getMemberFollowProductList(MemberFollowProductExample example, PagerInfo pager) {
        List<MemberFollowProduct> memberFollowProductList;
        if (pager != null) {
            pager.setRowsCount(memberFollowProductReadMapper.countByExample(example));
            memberFollowProductList = memberFollowProductReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberFollowProductList = memberFollowProductReadMapper.listByExample(example);
        }
        return memberFollowProductList;
    }

    /**
     * 根据条件获取会员收藏商品数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getMemberFollowProductCount(MemberFollowProductExample example) {
        return memberFollowProductReadMapper.countByExample(example);
    }
}