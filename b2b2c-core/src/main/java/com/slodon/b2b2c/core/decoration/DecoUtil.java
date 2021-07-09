package com.slodon.b2b2c.core.decoration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.encoder.EncoderUtil;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.util.Md5;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 装修页生成工具类
 */
public final class DecoUtil {

    public static final String PC_MAIN_NAVIGATION = "pc_main_navigation";
    public static final String PC_MAIN_BANNER = "pc_main_banner";
    public static final String PC_SINGLE_ADV = "pc_single_adv";
    public static final String PC_GOODS_FLOOR_01 = "pc_goods_floor_01";
    public static final String GOODS_LIKELY = "goods_likely";
    public static final String MIX_GOODS_CAT_01 = "mix_goods_cat_01";
    public static final String PC_FOUR_ADV = "pc_four_adv";
    public static final String PC_THREE_ADV = "pc_three_adv";
    public static final String PC_FIVE_ADV = "pc_five_adv";
    public static final String PC_THREE_COLUMN = "pc_three_column";
    public static final String PC_THREE_COLUMNS = "pc_three_columns";
    public static final String PC_ADV_11 = "pc_adv_11";
    public static final String PC_ADV_12 = "pc_adv_12";
    public static final String PC_ADV_13 = "pc_adv_13";
    public static final String PC_ADV_19 = "pc_adv_19";
    public static final String PC_DECO_TPL_LOCATION = "/WEB-INF/views/default/decoration/";
    public static final String PC_DECO_TPL_SUFFIX = ".ftl";

    public static final String URL_TYPE_KEYWORD = "keyword";                //关键字
    public static final String URL_TYPE_GOODS = "goods";                    //商品
    public static final String URL_TYPE_CATEGORY = "category";              //分类
    public static final String URL_TYPE_URL = "url";                        //链接
    public static final String URL_TYPE_TOPIC = "topic";                    //专题
    public static final String URL_TYPE_STORE_LIST = "store_list";          //店铺街
    public static final String URL_TYPE_BRAND_HOME = "brand_home";          //品牌列表
    public static final String URL_TYPE_POINTS_CENTER = "points_center";    //积分中心
    public static final String URL_TYPE_VOUCHER_CENTER = "voucher_center";  //领券中心
    public static final String URL_TYPE_FLASH_HOME = "flash_home";          //秒杀首页
    public static final String URL_TYPE_TUAN_HOME = "tuan_home";            //团购首页
    public static final String URL_TYPE_SPECIAL_HOME = "special_home";      //专题首页
    public static final String URL_TYPE_RECOMMEND_HOME = "recommend_home";  //推荐首页
    public static final String URL_TYPE_JTT_HOME = "jtt_home";              //阶梯团首页

//    public final static String createTFL(String uniqueKey, Path filePath, String dataCode) {
//        StringBuffer tflStr = new StringBuffer();
//        try (BufferedReader buf = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
//            //替换数据
//            String line;
//            while ((line = buf.readLine()) != null) {
//                tflStr.append(line.replaceAll(dataCode, dataCode + uniqueKey));
//            }
//            return tflStr.toString();
//        } catch (IOException e) {
//            throw new RuntimeException(e.getMessage(), e);
//        }
//    }

//    public final static DecoImg getDecoImg(JSONObject jsonObject, Long storeId) {
//        DecoImg decoImg = new DecoImg();
//        decoImg.setImgUrl(jsonObject.getString("imgUrl"));
//        decoImg.setTitle(jsonObject.getString("title"));
//        decoImg.setMainTitle(jsonObject.getString("main_title"));
//        decoImg.setSubTitle(jsonObject.getString("sub_title"));
//        if (null != jsonObject.getString("link_type") && null != jsonObject.getString("link_value")) {
//            JSONObject info = jsonObject.getJSONObject("info");
//            if (null != info) {
//                decoImg.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), info, jsonObject.getString("link_value"), storeId));
//            } else {
//                decoImg.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), null, jsonObject.getString("link_value"), storeId));
//            }
//        }
//        return decoImg;
//    }

//    public static List<DecoImg> getDecoImgList(JSONArray jsonArray, Long storeId) {
//        ArrayList<DecoImg> decoImgs = new ArrayList<>();
//        for (int i = 0; i < jsonArray.size(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            DecoImg decoImg = new DecoImg();
//            decoImg.setImgUrl(jsonObject.getString("imgUrl"));
//            decoImg.setTitle(jsonObject.getString("title"));
//            decoImg.setMainTitle(jsonObject.getString("main_title"));
//            decoImg.setSubTitle(jsonObject.getString("sub_title"));
//            if (null != jsonObject.getString("link_type") && null != jsonObject.getString("link_value")) {
//                JSONObject info = jsonObject.getJSONObject("info");
//                if (null != info) {
//                    decoImg.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), info, jsonObject.getString("link_value"), storeId));
//                } else {
//                    decoImg.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), null, jsonObject.getString("link_value"), storeId));
//                }
//            }
//            decoImgs.add(decoImg);
//        }
//        return decoImgs;
//    }

//    public static String createLinkUrl(String linkUrlType, JSONObject info, String linkUrlValue, Long storeId) {
//        //生成linkUrl
//        String linkUrl;
//        switch (linkUrlType) {
//            case URL_TYPE_CATEGORY:
//                if (storeId != 0) {
//                    linkUrl = DomainUrlUtil.SLD_PC_URL + "/store_search-" + linkUrlValue + "-" + EncoderUtil.getStoreIdDecoder(storeId) + "html";
//                    break;
//                }
//                switch (Optional.ofNullable(info).map(o -> o.getIntValue("grade")).orElse(0)) {
//                    case 1:
//                        linkUrl = DomainUrlUtil.SLD_PC_URL + "/cate1-" + info.getString("id") + ".html";
//                        break;
//                    case 2:
//                        linkUrl = DomainUrlUtil.SLD_PC_URL + "/list-" + info.getString("id") + ".html";
//                        break;
//                    case 3:
//                        linkUrl = DomainUrlUtil.SLD_PC_URL + "/cate-" + info.getString("id") + "-0-0-0-0-0-0-0-0.html";
//                        break;
//                    default:
//                        linkUrl = "";
//                }
//                break;
//            case URL_TYPE_GOODS:
//                if (info != null) {
//                    String commonId = info.getString("commonId");
//                    linkUrl = DomainUrlUtil.SLD_PC_URL + "/goods-" + commonId + ".html";
//                } else {
//                    linkUrl = "";
//                }
//                break;
//            case URL_TYPE_URL:
//                linkUrl = linkUrlValue;
//                break;
//            case URL_TYPE_KEYWORD:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/search2.html?keyword=" + linkUrlValue;
//                break;
//            case URL_TYPE_TOPIC:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/topic-" + Optional.ofNullable(info).map(o -> o.getIntValue("id")).orElse(0) + ".html";
//                break;
//            case URL_TYPE_STORE_LIST:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/seller/storeList.html";
//                break;
//            case URL_TYPE_BRAND_HOME:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/brand.html";
//                break;
//            case URL_TYPE_POINTS_CENTER:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/jifen.html";
//                break;
//            case URL_TYPE_VOUCHER_CENTER:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/voucher.html";
//                break;
//            case URL_TYPE_FLASH_HOME:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/flashSale.html";
//                break;
//            case URL_TYPE_TUAN_HOME:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/tuan.html";
//                break;
//            case URL_TYPE_SPECIAL_HOME:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/temai.html";
//                break;
//            case URL_TYPE_RECOMMEND_HOME:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/recommend.html";
//                break;
//            case URL_TYPE_JTT_HOME:
//                linkUrl = DomainUrlUtil.SLD_PC_URL + "/jtt-sale.html";
//                break;
//            default:
//                linkUrl = "";
//        }
//        return linkUrl;
//    }

//    public static List<DecoBanner> getBanners(JSONArray jsonArray, Long storeId) {
//        ArrayList<DecoBanner> decoBanners = new ArrayList<>();
//        Date now = new Date();
//        for (int i = 0; i < jsonArray.size(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            //筛选符合条件的轮播图
//            if (jsonObject.getInteger("status") == 1
//                    && (now.compareTo(jsonObject.getDate("startTime")) >= 0)
//                    && (now.compareTo(jsonObject.getDate("endTime"))) <= 0) {
//                DecoBanner decoBanner = new DecoBanner();
//                decoBanner.setImgUrl(jsonObject.getString("imgUrl"));
//                decoBanner.setTitle(jsonObject.getString("title"));
//                decoBanner.setSort(jsonObject.getIntValue("sort"));
//                if (null != jsonObject.getString("link_type") && null != jsonObject.getString("link_value")) {
//                    JSONObject info = jsonObject.getJSONObject("info");
//                    if (null != info) {
//                        decoBanner.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), info, jsonObject.getString("link_value"), storeId));
//                    } else {
//                        decoBanner.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), null, jsonObject.getString("link_value"), storeId));
//                    }
//                }
//                decoBanners.add(decoBanner);
//            }
//        }
//        Collections.sort(decoBanners, (o1, o2) -> o1.getSort() >= o2.getSort() ? 1 : -1);
//        return decoBanners;
//    }

//    public static List<DecoNavBar> getNavBars(JSONArray jsonArray, Long storeId) {
//        ArrayList<DecoNavBar> decoNavBars = new ArrayList<>();
//        for (int i = 0; i < jsonArray.size(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            DecoNavBar decoBar = new DecoNavBar();
//            decoBar.setImgUrl(jsonObject.getString("imgUrl"));
//            decoBar.setTitle(jsonObject.getString("title"));
//            decoBar.setContent(jsonObject.getString("content"));
//            decoBar.setSort(jsonObject.getIntValue("sort"));
//            if (null != jsonObject.getString("link_type") && null != jsonObject.getString("link_value")) {
//                JSONObject info = jsonObject.getJSONObject("info");
//                if (null != info) {
//                    decoBar.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), info, jsonObject.getString("link_value"), storeId));
//                } else {
//                    decoBar.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), null, jsonObject.getString("link_value"), storeId));
//                }
//            }
//            decoNavBars.add(decoBar);
//        }
//        Collections.sort(decoNavBars, (o1, o2) -> o1.getSort() >= o2.getSort() ? 1 : -1);
//        return decoNavBars;
//    }

//    public static DecoTitle getDecoTitle(JSONObject jsonObject, Integer type, Long storeId) {
//        DecoTitle decoTitle = new DecoTitle();
//        switch (type) {
//            case 1:
//                decoTitle.setTitle(jsonObject.getJSONObject("title").getString("initialValue"));
//                decoTitle.setSubTitle(jsonObject.getJSONObject("sub_title").getString("initialValue"));
//                if (null != jsonObject.getString("link_type") && null != jsonObject.getString("link_value")) {
//                    JSONObject info = jsonObject.getJSONObject("info");
//                    if (null != info) {
//                        decoTitle.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), info, jsonObject.getString("link_value"), storeId));
//                    } else {
//                        decoTitle.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), null, jsonObject.getString("link_value"), storeId));
//                    }
//                }
//                break;
//            case 2:
//                decoTitle.setTitle(jsonObject.getString("title_name"));
//                decoTitle.setTitleColor(jsonObject.getString("title_color"));
//                decoTitle.setTitleBgColor(jsonObject.getString("title_bg_color"));
//                break;
//        }
//        return decoTitle;
//    }

//    public static DecoFirstAdv getDecoFirstAdv(JSONObject jsonObject) {
//        if (null == jsonObject) return null;
//        DecoFirstAdv decoFirstAdv = new DecoFirstAdv();
//        decoFirstAdv.setImgUrl(jsonObject.getString("imgUrl"));
//        decoFirstAdv.setShowSwitch(jsonObject.getBoolean("show_switch"));
//        decoFirstAdv.setShowRadioSele(jsonObject.getString("show_radio_sele"));
//        if (null != jsonObject.getString("link_type") && null != jsonObject.getString("link_value")) {
//            JSONObject info = jsonObject.getJSONObject("info");
//            if (null != info) {
//                decoFirstAdv.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), info, jsonObject.getString("link_value"), 0L));
//            } else {
//                decoFirstAdv.setLinkUrl(createLinkUrl(jsonObject.getString("link_type"), null, jsonObject.getString("link_value"), 0L));
//            }
//        }
//
//        return decoFirstAdv;
//    }

    public static String encodePreview(String key) {
        String value = String.valueOf(System.currentTimeMillis());
        return value.substring(value.length() - 6) + Md5.getMd5String((value.substring(value.length() - 6) + key)).substring(0, 10);
    }

    public static Boolean validatePreview(String origin, String key) {
        return Md5.getMd5String(origin.substring(0, 6) + key).substring(0, 10).equalsIgnoreCase(origin.substring(6));
    }

}
