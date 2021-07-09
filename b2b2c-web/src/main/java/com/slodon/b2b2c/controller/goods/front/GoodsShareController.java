package com.slodon.b2b2c.controller.goods.front;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.HttpClientUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

@Api(tags = "front-分享海报")
@RestController
@RequestMapping("v3/goods/front/goods/share")
public class GoodsShareController extends BaseController {

    private static final double rate = 1.3;//缩放比例
    //图片尺寸
    private static final int SHARE_W = (int) (540 / rate);                  //分享海报图片宽度
    private static final int SHARE_H = (int) (846 / rate);                  //分享海报图片高度
    private static final int GOODS_IMAGE_W = (int) (490 / rate);            //商品图片宽度
    private static final int GOODS_IMAGE_H = (int) (490 / rate);            //商品图片高度
    private static final int CODE_OUTSIDE_SIZE = (int) (148 / rate);        //二维码边框尺寸
    private static final int CODE_SIZE = (int) (148 / rate);                //二维码图片尺寸

    //字体大小，px
    private static final int GOODS_NAME_FONT_SIZE = (int) (26 / rate);      //商品名称
    private static final int GOODS_PRICE_FONT_SIZE = (int) (32 / rate);     //销售价
    private static final int MARKET_PRICE_FONT_SIZE = (int) (24 / rate);    //市场价
    private static final int GOODS_BREIF_FONT_SIZE = (int) (32 / rate);     //商品广告
    private static final int LOOK_DETAIL_FONT_SIZE = (int) (24 / rate);     //了解详情

    //间距
    private static final int WORD_SEPARATION = (int) (20 / rate);           //文字间距
    private static final int MARGINS = (int) (26 / rate);                   //上下边距，px
    private static final String FONT_NAME = "思源黑体 CN Medium";

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("分享海报")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "source", value = "来源 1、pc 2、app；3、公众号或微信内部浏览器；4、小程序", paramType = "query")
    })
    @GetMapping("sharePosters")
    public JsonResult sharePosters(HttpServletRequest request, HttpServletResponse response, Long productId,
                                   @RequestParam(value = "source", required = false, defaultValue = "1") Integer source) {

        Product product = productModel.getProductByProductId(productId);
        AssertUtil.notNull(product, "商品不存在");

        Goods goods = goodsModel.getGoodsByGoodsId(product.getGoodsId());

        // 生成二维码内容
        String text = DomainUrlUtil.SLD_H5_URL + "/#/pages/product/detail?productId=" + product.getProductId() + "&goodsId=" + product.getGoodsId();
        // 设置海报模板
        BufferedImage bufferedImage0 = null;
        // 读取二维码图片流
        BufferedImage bufferedImage1 = null;
        // 读取商品图片流
        BufferedImage bufferedImage2 = null;
        // 商品图片路径
        String imgPath = FileUrlUtil.getFileUrl(product.getMainImage(), null);


        InputStream inputStream = readFile(imgPath);

        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 内容所使用字符集编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            bufferedImage0 = new BufferedImage(SHARE_W, SHARE_H, BufferedImage.TYPE_INT_RGB);//底图
            bufferedImage1 = ImageIO.read(inputStream);//商品图片

            if (source == 4) {
                //小程序，生成太阳码
                bufferedImage2 = this.getXCXQR();
            } else {
                BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, CODE_SIZE, CODE_SIZE, hints);//二维码
                bufferedImage2 = MatrixToImageWriter.toBufferedImage(bitMatrix);
            }

            Graphics2D graphics2D = bufferedImage0.createGraphics();
            Rectangle rectangle = new Rectangle(SHARE_W, SHARE_H);

            graphics2D.setColor(Color.WHITE);
            graphics2D.fill(rectangle);

            int y = MARGINS + GOODS_NAME_FONT_SIZE;//y轴位置

            //商品名称
            String goodsName = StringUtils.isEmpty(goods.getGoodsBrief()) ? "推荐一个好物给你哦～" : goods.getGoodsBrief();
            graphics2D.setPaint(new Color(3355443));
            graphics2D.setFont(new Font(FONT_NAME, Font.PLAIN, GOODS_NAME_FONT_SIZE));
            drawString(graphics2D, goodsName, GOODS_NAME_FONT_SIZE * 1.4f, SHARE_W - 2 * MARGINS, 2, MARGINS, y, true, false);

            y += GOODS_NAME_FONT_SIZE * 1.4f + GOODS_PRICE_FONT_SIZE + WORD_SEPARATION;

            //价格
            String price = "￥" + product.getProductPrice();
            graphics2D.setFont(new Font(FONT_NAME, Font.PLAIN, GOODS_PRICE_FONT_SIZE));
            graphics2D.setPaint(new Color(16522268));
            drawString(graphics2D, price, GOODS_PRICE_FONT_SIZE, SHARE_W - 2 * MARGINS, 1,
                    MARGINS, y, true, false);

            int x = 2 * MARGINS + price.getBytes("gbk").length * GOODS_PRICE_FONT_SIZE / 2;//x轴位置

            if (!StringUtil.isNullOrZero(product.getMarketPrice())) {
                //市场价
                String marketPrice = "￥" + product.getMarketPrice();
                graphics2D.setFont(new Font(FONT_NAME, Font.PLAIN, MARKET_PRICE_FONT_SIZE));
                graphics2D.setPaint(new Color(10066329));
                drawString(graphics2D, marketPrice, MARKET_PRICE_FONT_SIZE, SHARE_W - 2 * MARGINS, 1,
                        x, y, true, false);

                //市场价中间横线
                graphics2D.drawLine(x, y - MARKET_PRICE_FONT_SIZE / 2,
                        x + marketPrice.getBytes("gbk").length * MARKET_PRICE_FONT_SIZE / 2, y - MARKET_PRICE_FONT_SIZE / 2); //中间横线
            }

            y += WORD_SEPARATION;

            //商品图片
            graphics2D.drawImage(bufferedImage1, (SHARE_W - GOODS_IMAGE_W) / 2, y, GOODS_IMAGE_W, GOODS_IMAGE_H, null);

            y += GOODS_IMAGE_H + MARGINS;

            //二维码外框
            graphics2D.setPaint(new Color(15921906));
            graphics2D.drawRect(MARGINS, y, CODE_OUTSIDE_SIZE, CODE_OUTSIDE_SIZE);

            //二维码
            graphics2D.drawImage(bufferedImage2, MARGINS + (CODE_OUTSIDE_SIZE - CODE_SIZE) / 2, y + (CODE_OUTSIDE_SIZE - CODE_SIZE) / 2, CODE_SIZE, CODE_SIZE, null);

            x = 2 * MARGINS + CODE_OUTSIDE_SIZE;//x轴位置
            y += 2 * MARGINS;

            //广告语
            String name2 = goods.getGoodsBrief();
            graphics2D.setFont(new Font(FONT_NAME, Font.PLAIN, GOODS_BREIF_FONT_SIZE));
            graphics2D.setPaint(new Color(3355443));
            drawString(graphics2D, name2, GOODS_BREIF_FONT_SIZE, SHARE_W - x - MARGINS, 1, x, y, true, false);

            y += GOODS_BREIF_FONT_SIZE + MARGINS;

            //了解详情
            String name = "长按/扫描二维码了解商品详情";
            graphics2D.setFont(new Font(FONT_NAME, Font.PLAIN, LOOK_DETAIL_FONT_SIZE));
            graphics2D.setPaint(new Color(10066329));
            drawString(graphics2D, name, LOOK_DETAIL_FONT_SIZE, SHARE_W - x - MARGINS, 1, x, y, true, false);

            graphics2D.dispose();

            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(bufferedImage0, "jpg", out);
            try {
                out.flush();
            } finally {
                out.close();
            }
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param g
     * @param text       文本
     * @param lineHeight 行高
     * @param maxWidth   行宽
     * @param maxLine    最大行数
     * @param left       左边距
     * @param top        上边距
     * @param trim       是否修剪文本（1、去除首尾空格，2、将多个换行符替换为一个）
     * @param lineIndent 是否首行缩进
     */
    private static void drawString(Graphics2D g, String text, float lineHeight, float maxWidth, int maxLine, float left,
                                   float top, boolean trim, boolean lineIndent) {
        if (text == null || text.length() == 0) return;
        if (trim) {
            text = text.replaceAll("\\n+", "\n").trim();
        }
        if (lineIndent) {
            text = "　　" + text.replaceAll("\\n", "\n　　");
        }
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);//设置抗锯齿
        drawString(g, text, lineHeight, maxWidth, maxLine, left, top);
    }

    /**
     * @param g
     * @param text       文本
     * @param lineHeight 行高
     * @param maxWidth   行宽
     * @param maxLine    最大行数
     * @param left       左边距
     * @param top        上边距
     */
    private static void drawString(Graphics2D g, String text, float lineHeight, float maxWidth, int maxLine, float left,
                                   float top) {
        if (text == null || text.length() == 0) return;

        FontMetrics fm = g.getFontMetrics();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            sb.append(c);
            int stringWidth = fm.stringWidth(sb.toString());
            if (c == '\n' || stringWidth > maxWidth) {
                if (c == '\n') {
                    i += 1;
                }
                if (maxLine > 1) {
                    g.drawString(text.substring(0, i), left, top);
                    drawString(g, text.substring(i), lineHeight, maxWidth, maxLine - 1, left, top + lineHeight);
                } else {
                    g.drawString(text.substring(0, i - 1) + "...", left, top);
                }
                return;
            }
        }
        g.drawString(text, left, top);
    }

    /**
     * 获取图片流
     *
     * @param fileUrl http://url:port/store/lesson/qrcode/2018/07/12909064255321.jpg
     * @return InputStream
     */
    public static InputStream readFile(String fileUrl) {
        if (StringUtil.isBlank(fileUrl)) {
            return null;
        }
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setConnectTimeout(6000);
            urlCon.setReadTimeout(6000);
            int code = urlCon.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new Exception("文件读取失败");
            }
            return urlCon.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取太阳码
     *
     * @return
     * @throws Exception
     */
    private BufferedImage getXCXQR() throws Exception {
        final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=";//获取access_token地址
        String appId = stringRedisTemplate.opsForValue().get("login_wx_mini_appid");
        String secret = stringRedisTemplate.opsForValue().get("login_wx_mini_appsecret");

        //获取小程序 access_token
        String access_token_resp = HttpClientUtil.httpGet(ACCESS_TOKEN_URL + appId + "&secret=" + secret);
        String access_token = JSON.parseObject(access_token_resp).getString("access_token");
        AssertUtil.notEmpty(access_token, "微信认证失败");

        URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token);//获取太阳码的地址
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");// 提交模式
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
        //构造json参数
        ModelMap modelMap = new ModelMap();
        modelMap.put("scene", "11111");
        modelMap.put("width", 60);
//        modelMap.put("page", "pages/index");//需要小程序发布成功才能加此代码
        ModelMap color = new ModelMap();
        color.put("r", 0);
        color.put("g", 0);
        color.put("b", 0);
        modelMap.put("line_color", color);
        printWriter.write(JSON.toJSONString(modelMap));
        printWriter.flush();

        return ImageIO.read(httpURLConnection.getInputStream());
    }

}
