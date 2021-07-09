package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoreCertificateExample implements Serializable {
    private static final long serialVersionUID = 5232952302532147827L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer certificateIdNotEquals;

    /**
     * 用于批量操作
     */
    private String certificateIdIn;

    /**
     * 资质id
     */
    private Integer certificateId;

    /**
     * 商户用户id
     */
    private Long vendorId;

    /**
     * 商户用户名
     */
    private String vendorName;

    /**
     * 商户用户名,用于模糊查询
     */
    private String vendorNameLike;

    /**
     * 入驻类型：0-个人入驻，1-企业入驻
     */
    private Integer enterType;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 公司名称,用于模糊查询
     */
    private String companyNameLike;

    /**
     * 企业注册省编码
     */
    private String companyProvinceCode;

    /**
     * 企业注册市编码
     */
    private String companyCityCode;

    /**
     * 企业注册区编码
     */
    private String companyAreaCode;

    /**
     * 企业注册省市区组合
     */
    private String areaInfo;

    /**
     * 公司详细地址
     */
    private String companyAddress;

    /**
     * 营业执照扫描件
     */
    private String businessLicenseImage;

    /**
     * 身份证正面扫描件
     */
    private String personCardUp;

    /**
     * 身份证背面扫描件
     */
    private String personCardDown;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人姓名,用于模糊查询
     */
    private String contactNameLike;

    /**
     * 补充认证一
     */
    private String moreQualification1;

    /**
     * 补充认证二
     */
    private String moreQualification2;

    /**
     * 补充认证三
     */
    private String moreQualification3;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照certificateId倒序排列
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