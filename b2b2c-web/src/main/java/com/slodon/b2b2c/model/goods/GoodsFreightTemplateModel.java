package com.slodon.b2b2c.model.goods;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.constant.FreightConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.goods.*;
import com.slodon.b2b2c.dao.read.seller.StoreReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsFreightExtendWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsFreightTemplateWriteMapper;
import com.slodon.b2b2c.goods.dto.CalculateExpressDTO;
import com.slodon.b2b2c.goods.dto.GoodsFreightTemplateDTO;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import com.slodon.b2b2c.goods.example.GoodsFreightExtendExample;
import com.slodon.b2b2c.goods.example.GoodsFreightTemplateExample;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
public class GoodsFreightTemplateModel {

    @Resource
    private GoodsFreightTemplateReadMapper goodsFreightTemplateReadMapper;
    @Resource
    private GoodsFreightTemplateWriteMapper goodsFreightTemplateWriteMapper;
    @Resource
    private GoodsFreightExtendReadMapper goodsFreightExtendReadMapper;
    @Resource
    private GoodsFreightExtendWriteMapper goodsFreightExtendWriteMapper;
    @Resource
    private GoodsExtendReadMapper goodsExtendReadMapper;
    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private ProductReadMapper productReadMapper;
    @Resource
    private StoreReadMapper storeReadMapper;

    /**
     * 新增运费模板
     *
     * @param goodsFreightTemplate
     * @return
     */
    public Integer saveGoodsFreightTemplate(GoodsFreightTemplate goodsFreightTemplate) {
        int count = goodsFreightTemplateWriteMapper.insert(goodsFreightTemplate);
        if (count == 0) {
            throw new MallException("添加运费模板失败，请重试");
        }
        return count;
    }

    /**
     * 新增运费模板
     *
     * @param goodsFreightTemplateDTO
     * @param vendor
     * @return
     */
    @Transactional
    public Integer saveFreightTemplate(GoodsFreightTemplateDTO goodsFreightTemplateDTO, Vendor vendor) {
        //同一模板中同一个地区不可以重复添加
        JSONArray jsonArray = JSONObject.parseArray(goodsFreightTemplateDTO.getFreightExtendList());
        List<String> list = new LinkedList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String cityCodes = jsonObject.getString("cityCode");
            String cityNames = jsonObject.getString("cityName");
            List<String> cityCodeList = Arrays.asList(cityCodes.split(","));
            if (CollectionUtils.isEmpty(list)) {
                list.addAll(cityCodeList);
            } else {
                for (int j = 0; j < cityCodeList.size(); j++) {
                    List<String> cityNameList = Arrays.asList(cityNames.split(","));
                    AssertUtil.isTrue(list.contains(cityCodeList.get(j)), cityNameList.get(j) + "运费模板不可重复");
                }
                list.addAll(cityCodeList);
            }
        }
        //判断模板名称是否重复
        GoodsFreightTemplateExample example = new GoodsFreightTemplateExample();
        example.setStoreId(vendor.getStoreId());
        example.setTemplateName(goodsFreightTemplateDTO.getTemplateName());

        //添加goods_freight_template表
        GoodsFreightTemplate goodsFreightTemplate = new GoodsFreightTemplate();
        BeanUtils.copyProperties(goodsFreightTemplateDTO, goodsFreightTemplate);
        goodsFreightTemplate.setUpdateTime(new Date());
        goodsFreightTemplate.setStoreId(vendor.getStoreId());
        int count = 0;
        if (StringUtils.isEmpty(goodsFreightTemplateDTO.getFreightTemplateId())) {
            int nameCount = goodsFreightTemplateReadMapper.countByExample(example);
            AssertUtil.isTrue(nameCount > 0, "模板名称已存在，请重新填写");
            count = goodsFreightTemplateWriteMapper.insert(goodsFreightTemplate);
            if (count == 0) {
                throw new MallException("添加运费模板失败，请重试");
            }

            //添加goods_freight_extend表
            String freightExtendList = goodsFreightTemplateDTO.getFreightExtendList();
            List<GoodsFreightExtend> goodsFreightExtends = JSONObject.parseArray(freightExtendList, GoodsFreightExtend.class);
            for (GoodsFreightExtend goodsFreightExtend : goodsFreightExtends) {
                goodsFreightExtend.setFreightTemplateId(goodsFreightTemplate.getFreightTemplateId());
                count = goodsFreightExtendWriteMapper.insert(goodsFreightExtend);
                if (count == 0) {
                    throw new MallException("运费模板详情信息保存失败，请重试！");
                }
            }
        } else {
            example.setFreightTemplateIdNotEquals(goodsFreightTemplateDTO.getFreightTemplateId());
            int nameCount = goodsFreightTemplateReadMapper.countByExample(example);
            AssertUtil.isTrue(nameCount > 0, "模板名称已存在，请重新填写");

            String freightExtendList = goodsFreightTemplateDTO.getFreightExtendList();
            List<GoodsFreightExtend> goodsFreightExtendList = JSONObject.parseArray(freightExtendList, GoodsFreightExtend.class);
            for (GoodsFreightExtend goodsFreightExtend : goodsFreightExtendList) {
                if (goodsFreightExtend.getCityCode().equals("CN")) {
                    continue;
                }
                goodsFreightExtend.setFreightTemplateId(goodsFreightTemplate.getFreightTemplateId());
                count = goodsFreightExtendWriteMapper.insert(goodsFreightExtend);
                if (count == 0) {
                    throw new MallException("运费模板详情信息保存失败，请重试！");
                }
            }
        }
        return count;
    }

    /**
     * 根据freightTemplateId删除运费模板
     *
     * @param freightTemplateId freightTemplateId
     * @return
     */
    @Transactional
    public Integer deleteGoodsFreightTemplate(Integer freightTemplateId) {
        GoodsFreightTemplate goodsFreightTemplate = goodsFreightTemplateReadMapper.getByPrimaryKey(freightTemplateId);
        AssertUtil.notNull(goodsFreightTemplate, "获取运费模板对象失败");

        //查询是否有商品正在使用该模板，如果使用不能删除
        GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
        goodsExtendExample.setFreightId(freightTemplateId);
        List<GoodsExtend> goodsExtends = goodsExtendReadMapper.listByExample(goodsExtendExample);
        int goodsNum = goodsExtendReadMapper.countByExample(goodsExtendExample);
        if (goodsNum > 0) {
            throw new MallException("已有商品使用该运费模板，不能进行删除");
        }
        //再删除模板详情
        GoodsFreightExtendExample goodsFreightExtendExample = new GoodsFreightExtendExample();
        goodsFreightExtendExample.setFreightTemplateId(freightTemplateId);
        int count = goodsFreightExtendWriteMapper.deleteByExample(goodsFreightExtendExample);
        if (count == 0) {
            throw new MallException("删除运费模板详情信息失败，请重试！");
        }
        count = goodsFreightTemplateWriteMapper.deleteByPrimaryKey(freightTemplateId);
        if (count == 0) {
            log.error("根据freightTemplateId：" + freightTemplateId + "删除运费模板失败");
            throw new MallException("删除运费模板失败,请重试");
        }
        return count;
    }

    /**
     * 根据freightTemplateId更新运费模板
     *
     * @param goodsFreightTemplate
     * @return
     */
    public Integer updateGoodsFreightTemplate(GoodsFreightTemplate goodsFreightTemplate) {
        if (StringUtils.isEmpty(goodsFreightTemplate.getFreightTemplateId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsFreightTemplateWriteMapper.updateByPrimaryKeySelective(goodsFreightTemplate);
        if (count == 0) {
            log.error("根据freightTemplateId：" + goodsFreightTemplate.getFreightTemplateId() + "更新运费模板失败");
            throw new MallException("更新运费模板失败,请重试");
        }
        return count;
    }

    /**
     * 根据freightTemplateId更新运费模板
     *
     * @param goodsFreightTemplateDTO
     * @return
     */
    @Transactional
    public Integer updateFreightTemplate(GoodsFreightTemplateDTO goodsFreightTemplateDTO) {
        //同一模板中同一个地区不可以重复添加
        JSONArray jsonArray = JSONObject.parseArray(goodsFreightTemplateDTO.getFreightExtendList());
        List<String> list = new LinkedList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String cityCodes = jsonObject.getString("cityCode");
            String cityNames = jsonObject.getString("cityName");
            List<String> cityCodeList = Arrays.asList(cityCodes.split(","));
            if (CollectionUtils.isEmpty(list)) {
                list.addAll(cityCodeList);
            } else {
                for (int j = 0; j < cityCodeList.size(); j++) {
                    List<String> cityNameList = Arrays.asList(cityNames.split(","));
                    AssertUtil.isTrue(list.contains(cityCodeList.get(j)), cityNameList.get(j) + "运费模板不可重复");
                }
                list.addAll(cityCodeList);
            }
        }

        GoodsFreightTemplate freightTemplate = goodsFreightTemplateReadMapper.getByPrimaryKey(goodsFreightTemplateDTO.getFreightTemplateId());
        AssertUtil.notNull(freightTemplate, "获取运费模板对象失败");
        GoodsFreightTemplate goodsFreightTemplate = new GoodsFreightTemplate();
        BeanUtils.copyProperties(goodsFreightTemplateDTO, goodsFreightTemplate);
        goodsFreightTemplate.setUpdateTime(new Date());
        int count = goodsFreightTemplateWriteMapper.updateByPrimaryKeySelective(goodsFreightTemplate);
        if (count == 0) {
            log.error("根据freightTemplateId：" + goodsFreightTemplate.getFreightTemplateId() + "更新运费模板失败");
            throw new MallException("更新运费模板失败,请重试");
        }

        if (!StringUtils.isEmpty(goodsFreightTemplateDTO.getFreightExtendList())) {
            //先删除再插入
            GoodsFreightExtendExample goodsFreightExtendExample = new GoodsFreightExtendExample();
            goodsFreightExtendExample.setFreightTemplateId(goodsFreightTemplate.getFreightTemplateId());
            goodsFreightExtendWriteMapper.deleteByExample(goodsFreightExtendExample);

            //插入
            List<GoodsFreightExtend> goodsFreightExtends = JSONObject.parseArray(goodsFreightTemplateDTO.getFreightExtendList(), GoodsFreightExtend.class);
            for (GoodsFreightExtend goodsFreightExtend : goodsFreightExtends) {
                goodsFreightExtend.setFreightTemplateId(freightTemplate.getFreightTemplateId());
                count = goodsFreightExtendWriteMapper.insert(goodsFreightExtend);
                if (count == 0) {
                    throw new MallException("更新运费模板详情信息失败，请重试！");
                }
            }
        }
        return count;
    }

    /**
     * 根据freightTemplateId获取运费模板详情
     *
     * @param freightTemplateId freightTemplateId
     * @return
     */
    public GoodsFreightTemplate getGoodsFreightTemplateByFreightTemplateId(Integer freightTemplateId) {
        GoodsFreightTemplate freightTemplate = goodsFreightTemplateReadMapper.getByPrimaryKey(freightTemplateId);
        AssertUtil.notNull(freightTemplate, "获取运费模板信息为空，请重试！");

        GoodsFreightExtendExample goodsFreightExtendExample = new GoodsFreightExtendExample();
        goodsFreightExtendExample.setFreightTemplateId(freightTemplateId);
        goodsFreightExtendExample.setOrderBy("freight_extend_id");
        List<GoodsFreightExtend> goodsFreightExtends = goodsFreightExtendReadMapper.listByExample(goodsFreightExtendExample);
        freightTemplate.setFreightExtendList(goodsFreightExtends);
        return freightTemplate;
    }

    /**
     * 根据条件获取运费模板列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsFreightTemplate> getGoodsFreightTemplateList(GoodsFreightTemplateExample example, PagerInfo pager) {
        List<GoodsFreightTemplate> goodsFreightTemplateList;
        if (pager != null) {
            pager.setRowsCount(goodsFreightTemplateReadMapper.countByExample(example));
            goodsFreightTemplateList = goodsFreightTemplateReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsFreightTemplateList = goodsFreightTemplateReadMapper.listByExample(example);
        }
        return goodsFreightTemplateList;
    }

    /**
     * 根据商品模板类型获取模板
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsFreightTemplate> getGoodsFreightTemplatesByType(GoodsFreightTemplateExample example, PagerInfo pager) {
        List<GoodsFreightTemplate> goodsFreightTemplateList;
        if (pager != null) {
            pager.setRowsCount(goodsFreightTemplateReadMapper.countByExample(example));
            goodsFreightTemplateList = goodsFreightTemplateReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
            if (!CollectionUtil.isEmpty(goodsFreightTemplateList)) {
                for (GoodsFreightTemplate goodsFreightTemplate : goodsFreightTemplateList) {
                    GoodsFreightExtendExample goodsFreightExtendExample = new GoodsFreightExtendExample();
                    goodsFreightExtendExample.setFreightTemplateId(goodsFreightTemplate.getFreightTemplateId());
                    goodsFreightExtendExample.setOrderBy("freight_extend_id");
                    List<GoodsFreightExtend> goodsFreightExtends = goodsFreightExtendReadMapper.listByExample(goodsFreightExtendExample);
                    goodsFreightTemplate.setFreightExtendList(goodsFreightExtends);
                }
            }
        } else {
            goodsFreightTemplateList = goodsFreightTemplateReadMapper.listByExample(example);
            if (!CollectionUtil.isEmpty(goodsFreightTemplateList)) {
                for (GoodsFreightTemplate goodsFreightTemplate : goodsFreightTemplateList) {
                    GoodsFreightExtendExample goodsFreightExtendExample = new GoodsFreightExtendExample();
                    goodsFreightExtendExample.setFreightTemplateId(goodsFreightTemplate.getFreightTemplateId());
                    goodsFreightExtendExample.setOrderBy("freight_extend_id");
                    List<GoodsFreightExtend> goodsFreightExtends = goodsFreightExtendReadMapper.listByExample(goodsFreightExtendExample);
                    goodsFreightTemplate.setFreightExtendList(goodsFreightExtends);
                }
            }
        }
        return goodsFreightTemplateList;
    }

    /**
     * 复制运费模板
     *
     * @param freightTemplateId
     * @return
     */
    @Transactional
    public Integer copyGoodsFreightTemplate(Integer freightTemplateId) {
        //根据模板id获取模板对象
        GoodsFreightTemplate goodsFreightTemplate = goodsFreightTemplateReadMapper.getByPrimaryKey(freightTemplateId);
        AssertUtil.notNull(goodsFreightTemplate, "获取运费模板信息为空，请重试。");

        //复制运费模板对象
        GoodsFreightTemplate goodsFreightTemplateCopy = new GoodsFreightTemplate();
        BeanUtils.copyProperties(goodsFreightTemplate, goodsFreightTemplateCopy);
        goodsFreightTemplateCopy.setTemplateName("copy-" + goodsFreightTemplate.getTemplateName());
        goodsFreightTemplateCopy.setUpdateTime(new Date());

        Integer count = goodsFreightTemplateWriteMapper.insert(goodsFreightTemplateCopy);
        if (count == 0) {
            throw new MallException("复制运费模板信息失败，请重试！");
        }

        //获取模板扩展表对象
        GoodsFreightExtendExample goodsFreightExtendExample = new GoodsFreightExtendExample();
        goodsFreightExtendExample.setFreightTemplateId(freightTemplateId);
        List<GoodsFreightExtend> goodsFreightExtends = goodsFreightExtendReadMapper.listByExample(goodsFreightExtendExample);
        if (CollectionUtils.isEmpty(goodsFreightExtends)) {
            throw new MallException("获取运费模板扩展表信息为空，请重试。");
        }

        //复制运费模板扩展对象
        for (GoodsFreightExtend goodsFreightExtend : goodsFreightExtends) {
            GoodsFreightExtend goodsFreightExtendCopy = new GoodsFreightExtend();
            BeanUtils.copyProperties(goodsFreightExtend, goodsFreightExtendCopy);
            goodsFreightExtendCopy.setFreightTemplateId(goodsFreightTemplateCopy.getFreightTemplateId());
            count = goodsFreightExtendWriteMapper.insert(goodsFreightExtendCopy);
            if (count == 0) {
                throw new MallException("复制运费模板扩展表信息失败，请重试！");
            }
        }
        return count;
    }

    /**
     * 计算运费
     *
     * @param dto
     * @return
     */
    public BigDecimal calculateExpressFee(CalculateExpressDTO dto) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 运费模板，保存本次计算需要用到的运费模板：key=模板ID，value=模板对象
        Map<String, GoodsFreightTemplate> tranMap = new HashMap<>();
        // 运费模板对应的数量（重量、件数）：key=模板ID，value=使用该模板计算运费的数量
        Map<String, BigDecimal> numMap = new HashMap<>();

        // 统计本次计算中的模板和数量
        for (CalculateExpressDTO.ProductInfo productInfo : dto.getProductList()) {
            //查询商品的运费模板
            GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
            goodsExtendExample.setGoodsId(productInfo.getGoodsId());
            List<GoodsExtend> goodsExtendList = goodsExtendReadMapper.listByExample(goodsExtendExample);
            AssertUtil.notEmpty(goodsExtendList, "获取商品扩展信息为空");
            GoodsExtend goodsExtend = goodsExtendList.get(0);
            Goods goods = goodsReadMapper.getByPrimaryKey(productInfo.getGoodsId());

            Store store = storeReadMapper.getByPrimaryKey(goods.getStoreId());
            AssertUtil.notNull(store, "获取店铺信息失败");
            //购买商品总金额
            BigDecimal sumAmount = productInfo.getProductPrice().multiply(new BigDecimal(productInfo.getNumber()));
            //大于0则表示购买金额达到该额度时免运费
            if (store.getFreeFreightLimit() > 0) {
                if (sumAmount.compareTo(new BigDecimal(store.getFreeFreightLimit())) >= 0) {
                    continue;
                }
            }
            //先判断运费模板，再判断统一运费
            if (StringUtil.isNullOrZero(goodsExtend.getFreightId())) {
                AssertUtil.isTrue(goodsExtend.getFreightFee() == null, "商品[" + goods.getGoodsName() + "]未设置运费统一");
                totalPrice = totalPrice.add(goodsExtend.getFreightFee());
                continue;
            }
            String tranId = goodsExtend.getFreightId().toString();

            GoodsFreightTemplate template = null;
            if (tranMap.get(tranId) == null) {
                // 如果tranMap中没有，则从数据库取
                template = goodsFreightTemplateReadMapper.getByPrimaryKey(goodsExtend.getFreightId());
                tranMap.put(tranId, template);
                // 初始化模板对应的数量
                numMap.put(tranId, BigDecimal.ZERO);
            } else {
                // 如果tranMap中已有，则直接从map中取
                template = tranMap.get(tranId);
            }
            if (template == null) {
                log.error("商品[" + goods.getGoodsName() + "]没有设置运费模板");
                continue;
            }

            //件数/重量
            BigDecimal num = BigDecimal.ZERO;
            if (template.getChargeType() == null) {
                log.error("商品[" + goods.getGoodsName() + "]模板类型有误");
                continue;
            }
            if (template.getChargeType().equals(FreightConst.CHARGE_TYPE_2)) {
                Product product = productReadMapper.getByPrimaryKey(productInfo.getProductId());
                //重量模板，取sku重量
                if (product.getWeight() == null) {
                    log.error("sku信息错误：[" + product.getProductId() + "]没有设置重量，跳过运费计算");
                    continue;
                }
                num = (product.getWeight()).multiply(new BigDecimal(productInfo.getNumber()));
            } else if (template.getChargeType().equals(FreightConst.CHARGE_TYPE_3)) {
                Product product = productReadMapper.getByPrimaryKey(productInfo.getProductId());
                //体积模板，取sku体积
                BigDecimal length = product.getLength();
                BigDecimal width = product.getWidth();
                BigDecimal height = product.getHeight();
                if (length == null || width == null || height == null) {
                    log.error("sku信息错误：[" + product.getProductId() + "]没有设置长宽高，跳过运费计算");
                    continue;
                }
                //长（cm）×宽(cm)×高(cm)
                num = length.multiply(width).multiply(height).multiply(new BigDecimal(productInfo.getNumber()));
            } else {
                //件数模板，即为sku数量
                num = new BigDecimal(productInfo.getNumber());
            }

            // 记录数量和
            numMap.put(tranId, numMap.get(tranId).add(num));
        }

        // 计算每个模板的运费
        Iterator<String> tranItr = tranMap.keySet().iterator();
        while (tranItr.hasNext()) {
            String tranId = tranItr.next();
            GoodsFreightTemplate template = tranMap.get(tranId);
            if (template == null || template.getIsFree().equals(FreightConst.IS_FREE_YES)) {
                continue;
            }
            BigDecimal numBig = numMap.get(tranId);
            if (numBig == null || numBig.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            // 数量向上取整
            int num = (numBig.setScale(0, BigDecimal.ROUND_UP)).intValue();

            BigDecimal currentPrice = BigDecimal.ZERO;

            // 运费计算标志，如果计算出来了就赋值true
            boolean feeFlg = false;
            GoodsFreightExtendExample freightExtendExample = new GoodsFreightExtendExample();
            freightExtendExample.setFreightTemplateId(template.getFreightTemplateId());
            List<GoodsFreightExtend> extendList = goodsFreightExtendReadMapper.listByExample(freightExtendExample);
            if (!CollectionUtils.isEmpty(extendList)) {
                currentPrice = this.calculationPrice(extendList, num, dto.getCityCode());
                if (currentPrice.compareTo(BigDecimal.ZERO) > 0) {
                    feeFlg = true;
                }
            }
            if (!feeFlg) {
                // 如果程序运行到此处，说明没有设置对应的区域价格; 然后按照全国统一价格进行计算运费
                GoodsFreightExtendExample f = new GoodsFreightExtendExample();
                f.setCityCode("CN");
                f.setFreightTemplateId(template.getFreightTemplateId());
                extendList = goodsFreightExtendReadMapper.listByExample(f);
                GoodsFreightExtend freightExtend = extendList.get(0);
                currentPrice = this.getFee(freightExtend, num);
            }
            //该商品的运费
            totalPrice = totalPrice.add(currentPrice);
        }
        return totalPrice;
    }

    /**
     * 计算运费
     *
     * @param extendList
     * @param num
     * @param cityCode
     * @return
     */
    private BigDecimal calculationPrice(List<GoodsFreightExtend> extendList, int num, String cityCode) {
        BigDecimal price = BigDecimal.ZERO;
        // 运费计算
        for (GoodsFreightExtend extend : extendList) {
            String[] arr = extend.getCityCode().split(",");
            for (String city : arr) {
                if (city.equals("CN")) {
                    // 全国，记录下全国的运费，如果没有区域匹配则调用全国的运费计算方法
                    continue;
                } else if (city.equals(cityCode)) {
                    // 如果传入的城市ID与当前的相等，表示订单地址符合当前运费信息计算方法
                    price = this.getFee(extend, num);
                    break;
                }
            }
        }
        return price;
    }

    /**
     * 根据运费信息计算运费
     *
     * @param freightExtend
     * @param num
     * @return
     */
    private BigDecimal getFee(GoodsFreightExtend freightExtend, int num) {
        BigDecimal price = BigDecimal.ZERO;

        Integer weight = freightExtend.getBaseNumber();
        BigDecimal fee = freightExtend.getBasePrice();
        Integer addWeight = freightExtend.getAddNumber();
        BigDecimal addFee = freightExtend.getAddPrice();

        //多出的件数/重量
        int surplus = Math.max((num - weight), 0);
        //首件的钱
        price = price.add(fee);
        // 只有当增加数量大于0才计算，否则会出现死循环
        if (addWeight > 0 && surplus > 0 && addFee.compareTo(BigDecimal.ZERO) > 0) {
            //计算多出的钱
            for (int i = 0; i < surplus; i += addWeight) {
                price = price.add(addFee);
            }
        }
        return price.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}