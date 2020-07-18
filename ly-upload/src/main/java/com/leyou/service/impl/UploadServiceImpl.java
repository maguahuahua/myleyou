package com.leyou.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.config.UploadProperties;
import com.leyou.service.UploadService;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author chenxm
 * @date 2020/7/4 - 17:17
 */

@Service
@Slf4j

@EnableConfigurationProperties(UploadProperties.class)     //根据读取配置文件的类，取得属性
public class UploadServiceImpl implements UploadService {

    //已有配置类
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Autowired
    private UploadProperties prop;

//    private static final List<String> ALLOW_TYPE = Arrays.asList("image/jepg", "image/jpg");

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型
            String contentType = file.getContentType();
            if (!prop.getAllowTypes().contains(contentType)) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //准备目标路径
//            File desc = new File("/c/xxx", file.getOriginalFilename());
//            file.transferTo(desc);

            //上传到FastDFS
            //得到后缀名
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            //上传并生成缩略图
            StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), extension, null);
            //缩略图路径
            String thumbImagePath = thumbImageConfig.getThumbImagePath(storePath.getFullPath());

            //返回路径,  storePath.getFullPath()非缩略图方式
            return prop.getBaseUrl() + thumbImagePath;

        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }

    }
}