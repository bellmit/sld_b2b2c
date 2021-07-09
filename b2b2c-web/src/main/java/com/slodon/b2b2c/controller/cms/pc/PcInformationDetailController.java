package com.slodon.b2b2c.controller.cms.pc;

import com.slodon.b2b2c.cms.example.InformationCategoryExample;
import com.slodon.b2b2c.cms.example.InformationExample;
import com.slodon.b2b2c.cms.pojo.Information;
import com.slodon.b2b2c.cms.pojo.InformationCategory;
import com.slodon.b2b2c.core.constant.InformationCateConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.model.cms.InformationCategoryModel;
import com.slodon.b2b2c.model.cms.InformationModel;
import com.slodon.b2b2c.vo.cms.InforRankVO;
import com.slodon.b2b2c.vo.cms.PcInformationCateVO;
import com.slodon.b2b2c.vo.cms.PcInformationVO;
import com.slodon.b2b2c.vo.cms.RecommInforVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author lxk
 */
@Api(tags = "pc-资讯详情页")
@RestController
@Slf4j
@RequestMapping("v3/cms/front/info")
public class PcInformationDetailController {

    @Resource
    private InformationCategoryModel informationCategoryModel;
    @Resource
    private InformationModel informationModel;

    @ApiOperation("获取资讯详情页内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "informationId", value = "资讯id", required = true)
    })
    @GetMapping("detail")
    public JsonResult getDetail(HttpServletRequest request,
                                @RequestParam(value = "informationId") Integer informationId) {
        //构造返回数据
        HashMap<String, Object> dataMap = new LinkedHashMap<>();
        //资讯分类
        InformationCategoryExample categoryExample = new InformationCategoryExample();
        categoryExample.setIsShow(InformationCateConst.IS_SHOW_YES);
        categoryExample.setOrderBy("sort asc,create_time desc");
        List<InformationCategory> informationCategoryList = informationCategoryModel.getInformationCategoryList(categoryExample, null);

        ArrayList<PcInformationCateVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationCategoryList)) {
            for (InformationCategory category : informationCategoryList) {
                vos.add(new PcInformationCateVO(category));
            }
        }
        dataMap.put("cateList", vos);

        //查询资讯详情内容
        //查询资讯
        Information informationDb = informationModel.getInformationByInformationId(informationId);
        AssertUtil.notNull(informationDb, "资讯不存在");
        //增加阅读量
        Information updateInformation = new Information();
        updateInformation.setInformationId(informationId);
        updateInformation.setPageView(informationDb.getPageView() + 1);
        informationModel.updateInformation(updateInformation);

        //图片加域名
        if (!StringUtils.isEmpty(informationDb.getCoverImage())) {
            informationDb.setCoverImage(FileUrlUtil.getFileUrl(informationDb.getCoverImage(), null));
        }
        //富文本替换图片域名
        if (!StringUtils.isEmpty(informationDb.getContent())) {
            informationDb.setContent(informationDb.getContent().replaceAll("[$][{][(]domainUrlUtil.SLD_IMAGE_RESOURCES[)]!}", DomainUrlUtil.SLD_IMAGE_RESOURCES));
        }
        PcInformationVO inforVO = new PcInformationVO(informationDb);
        dataMap.put("information", inforVO);

        //获取推荐资讯列表
        InformationExample example = new InformationExample();
        example.setIsRecommend(InformationCateConst.IS_RECOMMEND_YES);//推荐
        example.setIsShow(InformationCateConst.IS_SHOW_YES);//显示
        //默认按照id倒序排序,查询前4条数据
        PagerInfo pagerInfo = new PagerInfo(4, 1);
        List<Information> informationRecommend = informationModel.getInformationList(example, pagerInfo);

        ArrayList<RecommInforVO> recommInforVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationRecommend)) {
            for (Information recommInfor : informationRecommend) {
                if (!StringUtils.isEmpty(recommInfor.getCoverImage())) {
                    recommInfor.setCoverImage(FileUrlUtil.getFileUrl(recommInfor.getCoverImage(), null));
                }
                recommInforVOS.add(new RecommInforVO(recommInfor));
            }
        }
        dataMap.put("recommendList", recommInforVOS);

        //获取资讯排行榜列表
        InformationExample rankExample = new InformationExample();
        rankExample.setIsShow(InformationCateConst.IS_SHOW_YES);
        rankExample.setOrderBy("page_view desc,create_time desc");
        //默认按照 read_volume 倒序排序,查询前10条数据
        PagerInfo rankPageInfo = new PagerInfo(10, 1);
        List<Information> informationRanks = informationModel.getInformationList(rankExample, rankPageInfo);

        ArrayList<InforRankVO> inforRankVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationRanks)) {
            for (Information informationRank : informationRanks) {
                inforRankVOS.add(new InforRankVO(informationRank));
            }
        }
        dataMap.put("rankList", inforRankVOS);
        return SldResponse.success(dataMap);
    }
}
