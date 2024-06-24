package com.qzx.convert.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.jodconverter.DocumentConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.office.OfficeException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * 文件转换工具类
 */
public class OfficeConvertUtil {


    private static final DocumentConverter documentConverter;

    static {
        documentConverter = SpringBootBeanUtil.getBean("documentConverter",DocumentConverter.class);
    }
    /**
     * 文件转换为pdf
     * @param convertName 转换后文件路径（包含名称，文件名称以pdf或html结尾）
     * @param targetFileName 原始文件保存至本地
     * @param sourceStream 原始文件流
     * @throws OfficeException
     */
    public static void covertFile(String convertName,String targetFileName, InputStream sourceStream ) throws OfficeException {
        // 原始文件保存至本地
        File file = FileUtil.file(targetFileName);
        File targetFile = FileUtil.file(convertName);
        if (!judgeFileIsExist(file,sourceStream) && !judgeFileIsExist(targetFile)){ //如果原始文件不存在则进行转换
            IoUtil.copy(sourceStream,IoUtil.toBuffered(FileUtil.getOutputStream(file)));
            documentConverter.convert(file).to(targetFile).execute();
        }
    }

    /**
     * 文件转换为pdf
     * @param sourceStream 原始文件流
     * @param outputStream 目标流
     * @param extension 扩展名
     * @throws OfficeException
     */
    public static void covertFile(OutputStream outputStream, InputStream sourceStream,String extension) throws OfficeException {
        // 原始文件保存至本地
        documentConverter
                .convert(sourceStream)
                .as(DefaultDocumentFormatRegistry.getFormatByExtension(extension))
                .to(outputStream)
                .as(DefaultDocumentFormatRegistry.PDF)
                .execute();
    }

    /**
     * 文件转换为pdf
     * @param convertName 转换后文件路径（包含名称，文件名称以pdf结尾）
     * @param sourceStream 原始文件
     * @throws OfficeException
     */
    public static void covertFile(String convertName, File sourceStream ) throws OfficeException {
        // 原始文件保存至本地
        documentConverter.convert(sourceStream).to(FileUtil.file(convertName)).execute();
    }

    /**
     * 删除文件
     */
    public static void deleteFile(File file){
        if (file.exists()){
            file.delete();
        }
    }

    /**
     * 关闭资源
     */
    public static void closeIo(Closeable closeable){
        if (closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * pdf在线预览设置
     */
    public static void pdfOnlineResponseConfig(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "filename=" + URLEncoder.encode(fileName, "UTF-8"));
    }

    /**
     * 文件名转换为pdf
     */
    public static String convertFileName(String fileName){
        return fileName.substring(0,fileName.lastIndexOf("."))+(isHtml(fileName)?".html":".pdf");
    }

    public static String getSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public static Boolean isHtml(String fileName){
        String suffix = getSuffix(fileName);
        return suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx") || suffix.equalsIgnoreCase("csv");
    }

    /**
     * 判断文件是否已存在
     */
    private static boolean judgeFileIsExist(InputStream targetInputStream,InputStream sourceInputStream){
        return Objects.equals(DigestUtil.md5Hex(sourceInputStream),DigestUtil.md5Hex(targetInputStream));
    }
    /**
     * 判断文件是否已存在
     */
    private static boolean judgeFileIsExist(File file){
        if (!file.exists()){
            return false;
        }
        return true;
    }
    /**
     * 判断文件是否已存在
     */
    private static boolean judgeFileIsExist(File file,InputStream inputStream){
        if (!file.exists()){
            return false;
        }
        return Objects.equals(DigestUtil.md5Hex(inputStream),DigestUtil.md5Hex(FileUtil.getInputStream(file)));
    }
    /**
     * 文件提前转换为可在线预览的内容
     */
    public static void addTask(){

    }

}
