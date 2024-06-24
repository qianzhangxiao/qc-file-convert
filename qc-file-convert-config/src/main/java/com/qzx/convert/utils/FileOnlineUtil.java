package com.qzx.convert.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import org.jodconverter.office.OfficeException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 文件在线预览配置
 */
public class FileOnlineUtil {

    /**
     * 文件在线预览
     * @param targetPath 转换文件保存路径
     * @param targetFileName 转换后文件名称
     * @param sourceFileName 原始文件名称
     * @param sourceInputStream 原始文件流
     */
    public static void onlinePreview(HttpServletResponse response, String targetPath, String targetFileName, String sourceFileName, InputStream sourceInputStream) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;
        try {
            String convertName = targetPath+ File.separator+targetFileName;
            String sourceName = targetPath+File.separator+sourceFileName;
            bufferedInputStream = IoUtil.toBuffered(sourceInputStream);
            OfficeConvertUtil.covertFile(convertName,sourceName,bufferedInputStream );
            if (!OfficeConvertUtil.isHtml(sourceName)){
                OfficeConvertUtil.pdfOnlineResponseConfig(response,targetFileName);
            }
            outputStream = response.getOutputStream();
            IoUtil.copy(IoUtil.toBuffered(FileUtil.getInputStream(FileUtil.file(convertName))),outputStream);
        } catch (OfficeException | IOException e) {
            e.printStackTrace();
            throw new IOException("文件预览失败");
        } finally {
            OfficeConvertUtil.closeIo(bufferedInputStream);
            OfficeConvertUtil.closeIo(outputStream);
        }
    }
}
