package com.slodon.b2b2c.controller.cms.pc;


import com.slodon.b2b2c.cms.example.InformationCategoryExample;
import com.slodon.b2b2c.cms.example.InformationExample;
import com.slodon.b2b2c.cms.pojo.Information;
import com.slodon.b2b2c.cms.pojo.InformationCategory;
import com.slodon.b2b2c.cms.pojo.InformationIndex;
import com.slodon.b2b2c.core.constant.InformationCateConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.cms.InformationCategoryModel;
import com.slodon.b2b2c.model.cms.InformationIndexModel;
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
@Api(tags = "pc-资讯列表页")
@RestController
@Slf4j
@RequestMapping("v3/cms/front/information")
public class PcInformationController {

    @Resource
    private InformationCategoryModel informationCategoryModel;
    @Resource
    private InformationModel informationModel;
    @Resource
    private InformationIndexModel informationIndexModel;

    @ApiOperation("获取资讯列表页和首页内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cateId", value = "资讯分类id,资讯分类id为0时，获取资讯首页内容", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult list(HttpServletRequest request,
                           @RequestParam(value = "cateId", required = false, defaultValue = "0") Integer cateId) {

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

        //cateId为0时，获取的是资讯首页内容，加载轮播图和广告
        if (cateId == 0) {
            //查询数据
            InformationIndex informationIndex = informationIndexModel.getInformationIndexByIndexId(1);
            String data = informationIndex.getData();
            dataMap.put("indexData", data);
        }

        //查询资讯列表
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        InformationExample informationExample = new InformationExample();
        if (cateId != 0) {
            //查询该分类下的资讯
            informationExample.setCateId(cateId);
        }
        informationExample.setIsShow(InformationCateConst.IS_SHOW_YES);
        List<Information> informationList = informationModel.getInformationList(informationExample, pager);
        ArrayList<PcInformationVO> inforVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationCategoryList)) {
            for (Information information : informationList) {
                if (!StringUtils.isEmpty(information.getCoverImage())) {
                    information.setCoverImage(FileUrlUtil.getFileUrl(information.getCoverImage(), null));
                }
                inforVOS.add(new PcInformationVO(information));
            }
        }
        dataMap.put("informationList", inforVOS);

        //获取分页对象
        dataMap.put("pagination", pager.apiPage());

        //获取推荐资讯列表
        InformationExample example = new InformationExample();
        example.setIsRecommend(InformationCateConst.IS_RECOMMEND_YES);//推荐
        example.setIsShow(InformationCateConst.IS_SHOW_YES);//显示
        //默认按照id倒序排序
//        PagerInfo pagerInfo = new PagerInfo(4, 1);
        List<Information> informationRecommend = informationModel.getInformationList(example, null);
        ArrayList<RecommInforVO> recommInforVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationRecommend)) {
            for (Information information : informationRecommend) {
                if (!StringUtils.isEmpty(information.getCoverImage())) {
                    information.setCoverImage(FileUrlUtil.getFileUrl(information.getCoverImage(), null));
                }
                recommInforVOS.add(new RecommInforVO(information));
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

    @ApiOperation("获取资讯分类列表")
    @GetMapping("cateList")
    public JsonResult<List<PcInformationCateVO>> getCateList(HttpServletRequest request) {
        InformationCategoryExample categoryExample = new InformationCategoryExample();
        categoryExample.setIsShow(InformationCateConst.IS_SHOW_YES);
        categoryExample.setOrderBy("sort asc,create_time desc");
        List<InformationCategory> informationCategoryList = informationCategoryModel.getInformationCategoryList(categoryExample, null);

        List<PcInformationCateVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationCategoryList)) {
            for (InformationCategory category : informationCategoryList) {
                vos.add(new PcInformationCateVO(category));
            }
        }
        return SldResponse.success(vos);
    }

}
