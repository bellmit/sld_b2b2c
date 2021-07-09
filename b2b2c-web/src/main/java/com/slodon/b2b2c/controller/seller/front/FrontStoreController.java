package com.slodon.b2b2c.controller.seller.front;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.StoreCateConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.member.example.MemberFollowStoreExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberFollowStore;
import com.slodon.b2b2c.model.goods.GoodsExtendModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.member.MemberFollowStoreModel;
import com.slodon.b2b2c.model.seller.StoreCertificateModel;
import com.slodon.b2b2c.model.seller.StoreInnerLabelModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.example.StoreExample;
import com.slodon.b2b2c.seller.example.StoreInnerLabelExample;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import com.slodon.b2b2c.seller.pojo.StoreInnerLabel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.seller.FrontStoreListVO;
import com.slodon.b2b2c.vo.seller.FrontStoreVO;
import com.slodon.b2b2c.vo.seller.StoreCategoryTreeVO;
import com.slodon.b2b2c.vo.seller.StoreGoodsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "front-移动端店铺接口")
@RestController
@Slf4j
@RequestMapping("v3/seller/front/store")
public class FrontStoreController {

    @Resource
    private StoreModel storeModel;
    @Resource
    private VendorModel vendorModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;
    @Resource
    private StoreInnerLabelModel storeInnerLabelModel;
    @Resource
    private MemberFollowStoreModel memberFollowStoreModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsExtendModel goodsExtendModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @ApiOperation("获取店铺列表")
    @OperationLogger(option = "获取店铺列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键词", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序条件：1-销量排序，2-人气排序，默认1", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<FrontStoreListVO>> getList(HttpServletRequest request,
                                                        @RequestParam(value = "keyword", required = false) String keyword,
                                                        @RequestParam(value = "sort", required = false, defaultValue = "1") Integer sort) {

        Member member = UserUtil.getUser(request, Member.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreExample storeExample = new StoreExample();
        storeExample.setStoreNameLike(keyword);
        storeExample.setGoodsNumberNotNull("notNull");
        storeExample.setState(StoreConst.STORE_STATE_OPEN);
        if (sort == 2) {
            storeExample.setOrderBy("store_look_volume DESC");
        } else {
            storeExample.setOrderBy("store_sales_volume DESC");
        }
        storeExample.setPager(pager);
        List<Store> storeList = storeModel.getStoreList(storeExample, pager);

        GoodsExample goodsExample1 = new GoodsExample();
        goodsExample1.setGoodsNameLike(keyword);
        goodsExample1.setState(GoodsConst.GOODS_STATE_UPPER);
        List<Goods> goodsLists = goodsModel.getGoodsList(goodsExample1, null);
        if (!CollectionUtils.isEmpty(goodsLists)) {
            for (Goods goods : goodsLists) {
                Store storeDb = storeModel.getStoreByStoreId(goods.getStoreId());
                if (!storeList.contains(storeDb)) {
                    storeList.add(storeDb);
                }
            }
        }
        List<FrontStoreListVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeList)) {
            for (Store store : storeList) {
                //默认店铺logo
                if (StringUtil.isEmpty(store.getStoreLogo())) {
                    store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
                }
                //默认店铺背景图片(取移动端横幅)
                if (StringUtil.isEmpty(store.getStoreBannerMobile())) {
                    store.setStoreBannerMobile(stringRedisTemplate.opsForValue().get("default_image_store_backdrop"));
                }
                FrontStoreListVO vo = new FrontStoreListVO(store);
                //会员是否收藏
                if (!StringUtil.isNullOrZero(member.getMemberId())) {
                    MemberFollowStoreExample example = new MemberFollowStoreExample();
                    example.setMemberId(member.getMemberId());
                    example.setStoreId(store.getStoreId());
                    List<MemberFollowStore> memberFollowStoreList = memberFollowStoreModel.getMemberFollowStoreList(example, null);
                    if (!CollectionUtils.isEmpty(memberFollowStoreList)) {
                        vo.setIsFollow("true");
                    } else {
                        vo.setIsFollow("false");
                    }
                } else {
                    vo.setIsFollow("false");
                }
                //查询店铺内的热销商品
                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setStoreId(store.getStoreId());
                goodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
                goodsExample.setOrderBy("actual_sales + virtual_sales DESC");
                List<Goods> goodsList = goodsModel.getGoodsList(goodsExample, null);
                List<StoreGoodsVO> goodsVOS = new ArrayList<>();
                if (!CollectionUtils.isEmpty(goodsList)) {
                    for (Goods goods : goodsList) {
                        GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
                        goodsExtendExample.setGoodsId(goods.getGoodsId());
                        GoodsExtend goodsExtend = goodsExtendModel.getGoodsExtendList(goodsExtendExample, null).get(0);
                        StoreGoodsVO storeGoodsVO = new StoreGoodsVO(goods, goodsExtend);
                        goodsVOS.add(storeGoodsVO);
                    }
                }
                //店铺热销商品个数
                vo.setHotGoodsNumber(goodsList.size());
                //店铺热销商品列表
                vo.setGoodsListVOList(goodsVOS);
                //查询店铺内本月上新商品
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //获取30天之前的时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, -30);
                String dayDate = simpleDateFormat.format(calendar.getTime());
                //该店铺本月上新的商品
                GoodsExample newGoodsExample = new GoodsExample();
                newGoodsExample.setStoreId(store.getStoreId());
                newGoodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
                newGoodsExample.setOnlineTimeLikeAfter(dayDate);
                newGoodsExample.setOrderBy("online_time DESC");
                List<Goods> newGoodsList = goodsModel.getGoodsList(newGoodsExample, null);
                List<StoreGoodsVO> newGoodsVOS = new ArrayList<>();
                if (!CollectionUtils.isEmpty(newGoodsList)) {
                    for (Goods goods : newGoodsList) {
                        GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
                        goodsExtendExample.setGoodsId(goods.getGoodsId());
                        List<GoodsExtend> goodsExtendList = goodsExtendModel.getGoodsExtendList(goodsExtendExample, null);
                        StoreGoodsVO newGoodsvo = new StoreGoodsVO(goods, goodsExtendList.get(0));
                        newGoodsVOS.add(newGoodsvo);
                    }
                }
                //本月上新商品个数
                vo.setNewGoodsNumber(newGoodsList.size());
                //本月上新商品列表
                vo.setNewGoodsListVOS(newGoodsVOS);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, storeExample.getPager()));
    }

    @ApiOperation("获取店铺详情接口")
    @OperationLogger(option = "获取店铺详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<FrontStoreVO> detail(HttpServletRequest request, @RequestParam("storeId") Long storeId) throws IOException, WriterException {
        Member member = UserUtil.getUser(request, Member.class);

        Store store = storeModel.getStoreByStoreId(storeId);
        //默认店铺logo
        if (StringUtil.isEmpty(store.getStoreLogo())) {
            store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
        }
        //默认店铺背景图片(取移动端横幅)
        if (StringUtil.isEmpty(store.getStoreBannerMobile())) {
            store.setStoreBannerMobile(stringRedisTemplate.opsForValue().get("default_image_store_backdrop"));
        }
        //默认pc端店铺横幅
        if (StringUtils.isEmpty(store.getStoreBannerPc())) {
            store.setStoreBannerPc(stringRedisTemplate.opsForValue().get("default_image_store_banner_pc"));
        }
        FrontStoreVO vo = new FrontStoreVO(store);
        //公司名称
        VendorExample vendorExample = new VendorExample();
        vendorExample.setStoreId(storeId);
        vendorExample.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
        Vendor vendor = vendorModel.getVendorList(vendorExample, null).get(0);
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(vendor.getVendorId());
        StoreCertificate storeCertificate = storeCertificateModel.getStoreCertificateList(storeCertificateExample, null).get(0);
        if (!StringUtil.isEmpty(storeCertificate.getCompanyName())) {
            vo.setCompanyName(storeCertificate.getCompanyName());
        }

        //店铺二维码
        String codeUrl = DomainUrlUtil.SLD_H5_URL + "/#/pages/store/shopHomePage?vid=" + storeId;
        String qrCode = storeQRCode(codeUrl);
        vo.setStoreQRCode(qrCode);
        vo.setShareLink(DomainUrlUtil.SLD_H5_URL + "/#/pages/store/shopHomePage?vid=" + storeId);

        //会员是否收藏
        if (!StringUtil.isNullOrZero(member.getMemberId())) {
            MemberFollowStoreExample example = new MemberFollowStoreExample();
            example.setMemberId(member.getMemberId());
            example.setStoreId(store.getStoreId());
            List<MemberFollowStore> memberFollowStoreList = memberFollowStoreModel.getMemberFollowStoreList(example, null);
            if (!CollectionUtils.isEmpty(memberFollowStoreList)) {
                vo.setIsFollow("true");
            } else {
                vo.setIsFollow("false");
            }
        } else {
            vo.setIsFollow("false");
        }
        return SldResponse.success(vo);
    }

    @ApiOperation("获取店铺内部商品分类接口")
    @OperationLogger(option = "获取店铺内部商品分类接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true)
    })
    @GetMapping("storeCategory")
    public JsonResult storeCategory(HttpServletRequest request, @RequestParam("storeId") Long storeId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<StoreCategoryTreeVO> tree = new ArrayList<>();
        StoreInnerLabelExample storeInnerLabelExample = new StoreInnerLabelExample();
        storeInnerLabelExample.setParentInnerLabelId(0);
        storeInnerLabelExample.setStoreId(storeId);
        storeInnerLabelExample.setOrderBy("inner_label_sort ASC");
        storeInnerLabelExample.setIsShow(StoreCateConst.STORE_LABEL_IS_SHOW);
        List<StoreInnerLabel> storeInnerLabelList = storeInnerLabelModel.getStoreInnerLabelList(storeInnerLabelExample, pager);
        generateTree(tree, storeInnerLabelList, 2);

        return SldResponse.success(tree);
    }

    /**
     * 递归生成树
     *
     * @param treeList
     * @param data
     * @return
     */
    private List<StoreCategoryTreeVO> generateTree(List<StoreCategoryTreeVO> treeList, List<StoreInnerLabel> data, Integer grade) {
        if (grade > 0) {
            for (StoreInnerLabel storeInnerLabel : data) {
                StoreCategoryTreeVO tree = new StoreCategoryTreeVO();
                tree.setInnerLabelId(storeInnerLabel.getInnerLabelId());
                tree.setInnerLabelName(storeInnerLabel.getInnerLabelName());
                tree.setInnerLabelSort(storeInnerLabel.getInnerLabelSort());
                tree.setIsShow(storeInnerLabel.getIsShow());
                tree.setParentInnerLabelId(storeInnerLabel.getParentInnerLabelId());
                tree.setCreateTime(storeInnerLabel.getCreateTime());
                tree.setUpdateTime(storeInnerLabel.getUpdateTime());

                StoreInnerLabelExample storeInnerLabelExample = new StoreInnerLabelExample();
                storeInnerLabelExample.setParentInnerLabelId(storeInnerLabel.getInnerLabelId());
                storeInnerLabelExample.setIsShow(StoreCateConst.STORE_LABEL_IS_SHOW);
                tree.setChildren(generateTree(new ArrayList<>(),
                        storeInnerLabelModel.getStoreInnerLabelList(storeInnerLabelExample, null), grade - 1));
                treeList.add(tree);
            }
        }
        return treeList;
    }

    /**
     * 生成店铺二维码
     *
     * @param codeUrl
     * @return
     * @throws IOException
     * @throws WriterException
     */
    public String storeQRCode(String codeUrl) throws IOException, WriterException {

        //将图片转化成base64进行输出
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Map<EncodeHintType, String> hints = new HashMap<>();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE, 300,
                300, hints);
        // 生成二维码
        String codeImg = "";
        MatrixToImageWriter.writeToStream(bitMatrix, "jpg", baos);
        codeImg = new String(Base64.encodeBase64(baos.toByteArray()));
        return codeImg;
    }

}
