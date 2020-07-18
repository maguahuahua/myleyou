package com.leyou.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author chenxm
 * @date 2020/7/4 - 17:14
 */
public interface UploadService {
    String uploadImage(MultipartFile file);
}
