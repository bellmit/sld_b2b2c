package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.ExcelExportUtil;
import com.slodon.b2b2c.core.util.ExcelUtil;
import com.slodon.b2b2c.core.util.FileDownloadUtils;
import com.slodon.b2b2c.dao.read.member.MemberBalanceLogReadMapper;
import com.slodon.b2b2c.dao.write.member.MemberBalanceLogWriteMapper;
import com.slodon.b2b2c.member.example.MemberBalanceLogExample;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import com.slodon.b2b2c.vo.member.MemberBalanceLogExportVO;
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
public class MemberBalanceLogModel {

    @Resource
    private MemberBalanceLogReadMapper memberBalanceLogReadMapper;
    @Resource
    private MemberBalanceLogWriteMapper memberBalanceLogWriteMapper;

    /**
     * 新增账户余额变化明细表
     *
     * @param memberBalanceLog
     * @return
     */
    public Integer saveMemberBalanceLog(MemberBalanceLog memberBalanceLog) {
        int count = memberBalanceLogWriteMapper.insert(memberBalanceLog);
        if (count == 0) {
            throw new MallException("添加账户余额变化明细表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除账户余额变化明细表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteMemberBalanceLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberBalanceLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除账户余额变化明细表失败");
            throw new MallException("删除账户余额变化明细表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新账户余额变化明细表
     *
     * @param memberBalanceLog
     * @return
     */
    public Integer updateMemberBalanceLog(MemberBalanceLog memberBalanceLog) {
        if (StringUtils.isEmpty(memberBalanceLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberBalanceLogWriteMapper.updateByPrimaryKeySelective(memberBalanceLog);
        if (count == 0) {
            log.error("根据logId：" + memberBalanceLog.getLogId() + "更新账户余额变化明细表失败");
            throw new MallException("更新账户余额变化明细表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取账户余额变化明细表详情
     *
     * @param logId logId
     * @return
     */
    public MemberBalanceLog getMemberBalanceLogByLogId(Integer logId) {
        return memberBalanceLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取账户余额变化明细表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberBalanceLog> getMemberBalanceLogList(MemberBalanceLogExample example, PagerInfo pager) {
        List<MemberBalanceLog> memberBalanceLogList;
        if (pager != null) {
            pager.setRowsCount(memberBalanceLogReadMapper.countByExample(example));
            memberBalanceLogList = memberBalanceLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberBalanceLogList = memberBalanceLogReadMapper.listByExample(example);
        }
        return memberBalanceLogList;
    }


    /**
     * 资金明细列表导出excel
     *
     * @param example  查询条件信息
     * @param request
     * @param response
     * @return
     */
    public Boolean balanceLogListExport(MemberBalanceLogExample example, HttpServletRequest request, HttpServletResponse response) {
        //1.查询资金明细列表
        List<MemberBalanceLog> memberBalanceLogList = memberBalanceLogReadMapper.listByExample(example);
        List<MemberBalanceLogExportVO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(memberBalanceLogList)) {
            memberBalanceLogList.forEach(memberBalanceLog -> {
                list.add(new MemberBalanceLogExportVO(memberBalanceLog));
            });
        }

        //2.导出excel

        //excel文件名
        ArrayList<File> fileNames = new ArrayList<>();
        String fileNameS = "";
        //导出目录
        String filePath = "/upload/";
        File files = new File(filePath);
        if (!files.exists() && !files.isDirectory()) {
            files.mkdirs();
        }
        //excel标题
        String titles = "会员名,变动金额（元）,冻结金额（元）,当前总金额（元）,当前冻结金额（元）,变更时间,操作管理员,描述";
        String[] titleArrray = titles.split(",");
        //excel文件限制2000条数据,超过则打包成zip
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
            if (count <= max) {
                //小于2000条,excel文件

                fileNameS = "会员资金明细-" + ExcelExportUtil.getSystemDate() + "-" + list.size() + ".xls";

                byte[] b = ExcelUtil.export("会员资金明细", titleArrray, list);
                File f = new File(filePath + fileNameS);
                FileUtils.writeByteArrayToFile(f, b, true);
                //下载
                FileDownloadUtils.setExcelHeadInfo(response, request, fileNameS);
                ServletOutputStream out = response.getOutputStream();
                //服务器上路径
                String excelPath = filePath + fileNameS;
                File file = new File(excelPath);
                FileInputStream in = new FileInputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);
                byte[] cache = new byte[4096];
                for (int offset = in.read(cache); offset != -1; offset = in.read(cache)) {
                    byteArrayOutputStream.write(cache, 0, offset);
                }

                byte[] bt = byteArrayOutputStream.toByteArray();
                out.write(bt);
                out.flush();
                out.close();
                in.close();
                if (file.exists()) {
                    file.delete();
                }
            } else {
                //大于2000条,打包成zip文件
                for (int i = 1; i <= num1; i++) {
                    fileNameS = "会员资金明细-" + ExcelExportUtil.getSystemDate() + "-" + i + ".xls";
                    List<MemberBalanceLogExportVO> balanceList = new ArrayList<>();
                    if (i == num1) {
                        for (int j = max * (i - 1); j < list.size(); j++) {
                            balanceList.add(list.get(j));
                        }
                    } else {
                        for (int j = max * (i - 1); j < 2000 * i; j++) {
                            balanceList.add(list.get(j));
                        }
                    }
                    byte[] b = ExcelUtil.export("会员资金明细", titleArrray, balanceList);
                    File f = new File(filePath + fileNameS);
                    FileUtils.writeByteArrayToFile(f, b, true);

                    fileNames.add(f);
                }

                //导出zip
                String zipName = "会员资金明细-" + ExcelExportUtil.getSystemDate() + ".zip";
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
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }
}