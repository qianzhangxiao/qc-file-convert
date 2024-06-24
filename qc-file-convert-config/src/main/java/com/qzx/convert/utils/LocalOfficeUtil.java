package com.qzx.convert.utils;

import com.qzx.convert.config.OfficeProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class LocalOfficeUtil {

    public static final String OFFICE_HOME_KEY = "office.home";
    public static final String DEFAULT_OFFICE_HOME_VALUE = "default";

    private static final String EXECUTABLE_DEFAULT = "program/soffice.bin";
    private static final String EXECUTABLE_MAC = "program/soffice";
    private static final String EXECUTABLE_MAC_41 = "MacOS/soffice";
    private static final String EXECUTABLE_WINDOWS = "program/soffice.exe";

    private static final List<String> LINUX_LIBRE_OFFICE;
    private static final List<String> WINDOWS_LIBRE_OFFICE;
    private static final List<String> MAC_LIBRE_OFFICE;
    private static final List<String> MAC_LIBRE_OFFICE_41;

    private static OfficeProperties officeProperties;

    static {
        final String programFiles64 = System.getenv("ProgramFiles");
        final String programFiles32 = System.getenv("ProgramFiles(x86)");
        WINDOWS_LIBRE_OFFICE = Arrays.asList(
                programFiles32 + File.separator + "LibreOffice",
                programFiles64 + File.separator + "LibreOffice 7",
                programFiles32 + File.separator + "LibreOffice 7",
                programFiles64 + File.separator + "LibreOffice 6",
                programFiles32 + File.separator + "LibreOffice 6",
                programFiles64 + File.separator + "LibreOffice 5",
                programFiles32 + File.separator + "LibreOffice 5",
                programFiles64 + File.separator + "LibreOffice 4",
                programFiles32 + File.separator + "LibreOffice 4",
                programFiles32 + File.separator + "OpenOffice 4",
                programFiles64 + File.separator + "LibreOffice 3",
                programFiles32 + File.separator + "LibreOffice 3",
                programFiles32 + File.separator + "OpenOffice.org 3");
        LINUX_LIBRE_OFFICE = Arrays.asList(
                "/opt/libreoffice6.0",
                "/opt/libreoffice6.1",
                "/opt/libreoffice6.2",
                "/opt/libreoffice6.3",
                "/opt/libreoffice6.4",
                "/opt/libreoffice7.0",
                "/opt/libreoffice7.1",
                "/opt/libreoffice7.2",
                "/opt/libreoffice7.3",
                "/opt/libreoffice7.4",
                "/opt/libreoffice7.5",
                "/usr/lib64/libreoffice",
                "/usr/lib/libreoffice",
                "/usr/local/lib64/libreoffice",
                "/usr/local/lib/libreoffice",
                "/opt/libreoffice",
                "/usr/lib64/openoffice",
                "/usr/lib64/openoffice.org3",
                "/usr/lib64/openoffice.org",
                "/usr/lib/openoffice",
                "/usr/lib/openoffice.org3",
                "/usr/lib/openoffice.org",
                "/opt/openoffice4",
                "/opt/openoffice.org3");
        MAC_LIBRE_OFFICE = Arrays.asList(
                "/Applications/LibreOffice.app/Contents",
                "/Applications/OpenOffice.app/Contents",
                "/Applications/OpenOffice.org.app/Contents");
        MAC_LIBRE_OFFICE_41 = Arrays.asList(
                "/Applications/LibreOffice.app/Contents",
                "/Applications/OpenOffice.app/Contents",
                "/Applications/OpenOffice.org.app/Contents");
        officeProperties = SpringBootBeanUtil.getBean("officeProperties",OfficeProperties.class);
    }

    /**
     * 获取libreoffice
     */
    public static File getLibreOffice(){
        if (!ObjectUtils.isEmpty(officeProperties)&&!ObjectUtils.isEmpty(officeProperties.getOfficeHome())&&!DEFAULT_OFFICE_HOME_VALUE.equals(officeProperties.getOfficeHome())){
            return new File(officeProperties.getOfficeHome());
        }
        return OsUtil.IS_OS_WINDOWS?windowsLibre():OsUtil.IS_OS_MAC?macLibre():linuxLibre();
    }

    private static File windowsLibre(){
        return findOfficeHome(EXECUTABLE_WINDOWS,WINDOWS_LIBRE_OFFICE);
    }

    private static File linuxLibre(){
        return findOfficeHome(EXECUTABLE_DEFAULT,LINUX_LIBRE_OFFICE);
    }

    private static File macLibre(){
        File file = findOfficeHome(EXECUTABLE_MAC,MAC_LIBRE_OFFICE);
        if (file!=null){
            return file;
        }
        return findOfficeHome(EXECUTABLE_MAC_41,MAC_LIBRE_OFFICE_41);
    }

    private static File findOfficeHome(final String executablePath, final List<String> homePaths) {
        return homePaths.stream()
                .filter(homePath -> Files.isRegularFile(Paths.get(homePath, executablePath)))
                .findFirst()
                .map(File::new)
                .orElse(null);
    }

    public static boolean killProcess() {
        boolean flag = false;
        try {
            if (OsUtil.IS_OS_WINDOWS) {
                Process p = Runtime.getRuntime().exec("cmd /c tasklist ");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream os = p.getInputStream();
                byte[] b = new byte[256];
                while (os.read(b) > 0) {
                    baos.write(b);
                }
                String s = baos.toString();
                if (s.contains("soffice.bin")) {
                    Runtime.getRuntime().exec("taskkill /im " + "soffice.bin" + " /f");
                    flag = true;
                }
            } else if (OsUtil.IS_OS_MAC || OsUtil.IS_OS_MAC_OSX) {
                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ps -ef | grep " + "soffice.bin"});
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream os = p.getInputStream();
                byte[] b = new byte[256];
                while (os.read(b) > 0) {
                    baos.write(b);
                }
                String s = baos.toString();
                if (StringUtils.ordinalIndexOf(s, "soffice.bin", 3) > 0) {
                    String[] cmd = {"sh", "-c", "kill -15 `ps -ef|grep " + "soffice.bin" + "|awk 'NR==1{print $2}'`"};
                    Runtime.getRuntime().exec(cmd);
                    flag = true;
                }
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ps -ef | grep " + "soffice.bin" + " |grep -v grep | wc -l"});
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream os = p.getInputStream();
                byte[] b = new byte[256];
                while (os.read(b) > 0) {
                    baos.write(b);
                }
                String s = baos.toString();
                if (!s.startsWith("0")) {
                    String[] cmd = {"sh", "-c", "ps -ef | grep soffice.bin | grep -v grep | awk '{print \"kill -9 \"$2}' | sh"};
                    Runtime.getRuntime().exec(cmd);
                    flag = true;
                }
            }
        } catch (IOException e) {
            log.error("检测office进程异常", e);
        }
        return flag;
    }

}
