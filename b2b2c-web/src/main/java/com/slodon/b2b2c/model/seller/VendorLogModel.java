package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.ExcelUtil;
import com.slodon.b2b2c.core.util.FileDownloadUtils;
import com.slodon.b2b2c.dao.read.seller.VendorLogReadMapper;
import com.slodon.b2b2c.dao.write.seller.VendorLogWriteMapper;
import com.slodon.b2b2c.seller.example.VendorLogExample;
import com.slodon.b2b2c.seller.pojo.VendorLog;
import com.slodon.b2b2c.vo.seller.VendorLogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class VendorLogModel {

    @Resource
    private VendorLogReadMapper vendorLogReadMapper;
    @Resource
    private VendorLogWriteMapper vendorLogWriteMapper;

    /**
     * 新增商家用户操作日志表
     *
     * @param vendorLog
     * @return
     */
    public Integer saveVendorLog(VendorLog vendorLog) {
        int count = vendorLogWriteMapper.insert(vendorLog);
        if (count == 0) {
            throw new MallException("添加商家用户操作日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除商家用户操作日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteVendorLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = vendorLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除商家用户操作日志表失败");
            throw new MallException("删除商家用户操作日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新商家用户操作日志表
     *
     * @param vendorLog
     * @return
     */
    public Integer updateVendorLog(VendorLog vendorLog) {
        if (StringUtils.isEmpty(vendorLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = vendorLogWriteMapper.updateByPrimaryKeySelective(vendorLog);
        if (count == 0) {
            log.error("根据logId：" + vendorLog.getLogId() + "更新商家用户操作日志表失败");
            throw new MallException("更新商家用户操作日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取商家用户操作日志表详情
     *
     * @param logId logId
     * @return
     */
    public VendorLog getVendorLogByLogId(Integer logId) {
        return vendorLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取商家用户操作日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<VendorLog> getVendorLogList(VendorLogExample example, PagerInfo pager) {
        List<VendorLog> vendorLogList;
        if (pager != null) {
            pager.setRowsCount(vendorLogReadMapper.countByExample(example));
            vendorLogList = vendorLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            vendorLogList = vendorLogReadMapper.listByExample(example);
        }
        return vendorLogList;
    }

    /**
     * 操作日志导出
     *
     * @param request
     * @param response
     * @param example
     */
    public boolean vendorLogExport(HttpServletRequest request, HttpServletResponse response, VendorLogExample example) {
        List<VendorLog> vendorLogList = vendorLogReadMapper.listByExample(example);
        List<VendorLogVO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(vendorLogList)) {
            vendorLogList.forEach(vendorLog -> {
                list.add(new VendorLogVO(vendorLog));
            });
        }
        // 用于存放生成的excel文件名称
        List<File> fileNames = new ArrayList<>();
        // 每次导出的excel的文件名
        String fileNameS = "";
        // 将excel导出的文件位置
        String filePath = "/upload/";
        File files = new File(filePath);
        // 如果文件夹不存在则创建
        if (!files.exists() && !files.isDirectory()) {
            files.mkdirs();
        }

        //excel标题
        String titles = "ID,操作人,操作行为,操作时间,IP";
        String[] titleArray = titles.split(",");

        // 创建Excel文件,每个excel限制2000条数据,超过则打包zip
        int count = list.size();
        int max = 2000;
        int num = count % max;
        int num1;
        if (num == 0) {
            num1 = count / max;
        } else {
            num1 = count / max + 1;
        }

        try {
            if (list.size() <= max) {
                // 导出的excel的文件名
                fileNameS = "操作日志-" + ExcelUtil.getSystemDate() + "-" + list.size() + ".xls";

                //下载单个excle
                byte b[] = ExcelUtil.export("操作日志", titleArray, list);

                File f = new File(filePath + fileNameS);
                FileUtils.writeByteArrayToFile(f, b, true);

                // 下载
                FileDownloadUtils.setExcelHeadInfo(response, request, fileNameS);

                ServletOutputStream out = response.getOutputStream();

                String path = "/upload/" + fileNameS; //Excel在服务器上的路径

                File file = new File(path);

                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(4096);

                byte[] cache = new byte[4096];
                for (int offset = fis.read(cache); offset != -1; offset = fis.read(cache)) {
                    byteOutputStream.write(cache, 0, offset);
                }

                byte[] bt = byteOutputStream.toByteArray();

                out.write(bt);
                out.flush();
                out.close();
                fis.close();
                if (file.exists()) {
                    file.delete();
                }

            } else {
                for (int i = 1; i <= num1; i++) {
                    fileNameS = "操作日志-" + ExcelUtil.getSystemDate() + "-" + i + ".xls";
                    List<VendorLogVO> exportDatas = new ArrayList<>();
                    if (i == num1) {
                        for (int j = max * (i - 1); j < list.size(); j++) {
                            exportDatas.add(list.get(j));
                        }
                    } else {
                        for (int j = max * (i - 1); j < 2000 * i; j++) {
                            exportDatas.add(list.get(j));
                        }
                    }

                    //下载单个excle
                    byte b[] = ExcelUtil.export("操作日志", titleArray, exportDatas);

                    File f = new File(filePath + fileNameS);
                    FileUtils.writeByteArrayToFile(f, b, true);

                    fileNames.add(new File(filePath + fileNameS));
                }

                String zipName = "操作日志-" + ExcelUtil.getSystemDate() + ".zip";
                // 导出压缩文件的全路径
                String zipFilePath = filePath + zipName;
                System.out.println(zipFilePath);
                // 导出zip
                File zip = new File(zipFilePath);

                // 下载
                FileDownloadUtils.setZipDownLoadHeadInfo(response, request, zipName);

                // 将excel文件生成压缩文件
                File srcFile[] = new File[fileNames.size()];
                for (int j = 0, n1 = fileNames.size(); j < n1; j++) {
                    srcFile[j] = new File(String.valueOf(fileNames.get(j)));
                }
                FileDownloadUtils.ZipFiles(srcFile, zip);

                OutputStream outputStream = response.getOutputStream();
                InputStream inputStream = new FileInputStream(zipFilePath);
                byte[] buffer = new byte[8192];
                int i = -1;
                while ((i = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, i);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                FileDownloadUtils.deleteDir(new File(filePath));
            }

        } catch (Exception e) {
            try {
                if (!response.isCommitted()) {
                    response.setContentType("text/html;charset=utf-8");
                    response.setHeader("Content-Disposition", "");
                    String html = FileDownloadUtils.getErrorHtml("下载失败");
                    response.getOutputStream().write(html.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return true;
    }
}