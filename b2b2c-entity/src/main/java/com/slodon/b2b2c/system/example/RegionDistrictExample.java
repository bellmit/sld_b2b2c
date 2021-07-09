package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class RegionDistrictExample implements Serializable {
    private static final long serialVersionUID = 7473188716013622763L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer idNotEquals;

    /**
     * 用于批量操作
     */
    private String idIn;

    /**
     * 自增物理主键
     */
    private Integer id;

    /**
     * 国际编码
     */
    private String nativeCode;

    /**
     * 区县名称
     */
    private String districtName;

    /**
     * 区县名称,用于模糊查询
     */
    private String districtNameLike;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 城市名称,用于模糊查询
     */
    private String cityNameLike;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 国家编码
     */
    private String countryCode;

    /**
     * 国家名称
     */
    private String countryName;

    /**
     * 国家名称,用于模糊查询
     */
    private String countryNameLike;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 省份名称,用于模糊查询
     */
    private String provinceNameLike;

    /**
     * 备注省份编码
     */
    private String remarkCode;

    /**
     * 地区级别 1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县 4-街道
     */
    private Integer regionLevel;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照id倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;
}