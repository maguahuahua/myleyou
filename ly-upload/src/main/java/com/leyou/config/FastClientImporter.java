package com.leyou.config;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.web.servlet.view.document.AbstractPdfStamperView;

/**
 * @author chenxm
 * @date 2020/7/6 - 15:25
 *
 * 自动注入FdfsClient
 */

@Configuration
@Import(FdfsClientConfig.class)
//解决jmx重复注册bean的问题
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class FastClientImporter {
}
