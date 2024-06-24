package com.qzx.convert.config;

import com.qzx.convert.utils.LocalOfficeUtil;
import com.qzx.convert.utils.SpringBootBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.DocumentConverter;
import org.jodconverter.LocalConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.jodconverter.office.OfficeUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Arrays;


@Configuration
@Slf4j
public class LocalOfficeConfig {

    private LocalOfficeManager manager = null;

    @Bean
    @Order(-9999)
    public SpringBootBeanUtil getSpringBootBean(){
        return new SpringBootBeanUtil();
    }

    /**
     * 获取并启动office插件
     *  得预先安装libreoffice
     */
    @Bean
    @ConditionalOnProperty(prefix = "office",name = "enabled",havingValue = "true")
    public LocalOfficeManager localOfficeManager(){
        /**
         * 获取插件文件
         */
        final File libreOffice = LocalOfficeUtil.getLibreOffice();
        if (libreOffice==null){
            throw new RuntimeException("找不到office组件，请确认'office.home'配置是否有误");
        }
        try {
            boolean killOffice = LocalOfficeUtil.killProcess();
            if (killOffice) {
                log.warn("检测到有正在运行的office进程，已自动结束该进程");
            }
            final OfficeProperties officeProperties = SpringBootBeanUtil.getBean("officeProperties", OfficeProperties.class);
            final int[] ports = Arrays.stream(officeProperties.getPorts().split(",")).mapToInt(Integer::valueOf).toArray();
            manager = LocalOfficeManager.builder()
                    .officeHome(libreOffice)
                    .portNumbers(ports)
                    .maxTasksPerProcess(officeProperties.getMaxTasksPerProcess())
                    .build();
            manager.start();
        } catch (OfficeException e) {
            e.printStackTrace();
        }
        return manager;
    }

    @Bean
    @ConditionalOnBean({OfficeManager.class})
    public DocumentConverter documentConverter(OfficeManager officeManager){
        return LocalConverter.make(officeManager);
    }

    /**
     * 关闭服务时关闭office服务
     */
    @PreDestroy
    public void destroyOfficeManager() {
        if (manager!=null && manager.isRunning()) {
            log.info("结束office进程");
            OfficeUtils.stopQuietly(manager);
        }
    }
}
