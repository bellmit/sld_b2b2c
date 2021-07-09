package com.slodon.b2b2c.controller.seller.front;

import com.slodon.b2b2c.core.constant.StoreCateConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreInnerLabelModel;
import com.slodon.b2b2c.seller.example.StoreInnerLabelExample;
import com.slodon.b2b2c.seller.pojo.StoreInnerLabel;
import com.slodon.b2b2c.vo.seller.StoreCategoryTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-店铺分类管理")
@Slf4j
@RestController
@RequestMapping("v3/seller/front/storeCategory")
public class FrontCategoryController extends BaseController {

    @Resource
    private StoreInnerLabelModel storeInnerLabelModel;

    @ApiOperation("获取店铺分类列表")
    @GetMapping("list")
    public JsonResult<List<StoreCategoryTreeVO>> getList(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        List<StoreCategoryTreeVO> tree = new ArrayList<>();
        StoreInnerLabelExample storeInnerLabelExample = new StoreInnerLabelExample();
        storeInnerLabelExample.setParentInnerLabelId(0);
        storeInnerLabelExample.setIsShow(StoreCateConst.STORE_LABEL_IS_SHOW);
        List<StoreInnerLabel> storeInnerLabelList = storeInnerLabelModel.getStoreInnerLabelList(storeInnerLabelExample, pager);
        if (!CollectionUtils.isEmpty(storeInnerLabelList)) {
            generateTree(tree, storeInnerLabelList, 2);
        }
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
                List<StoreCategoryTreeVO> vos = generateTree(new ArrayList<>(),
                        storeInnerLabelModel.getStoreInnerLabelList(storeInnerLabelExample, null), grade - 1);
                if (!CollectionUtils.isEmpty(vos)) {
                    tree.setChildren(vos);
                } else {
                    tree.setChildren(null);
                }
                treeList.add(tree);
            }
        }
        return treeList;
    }
}
