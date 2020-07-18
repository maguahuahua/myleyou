package com.leyou.web;

import com.leyou.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chenxm
 * @date 2020/7/4 - 17:11
 */

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;


    /**
     * 返回的是图片路径，如：http://image.leyou.com/group1/M00/00/00/wKgrIl8DATqAI8xoAA5Et7wM_YQ615_60x60.png
     *  http://image.leyou.com/ 会转发到Nginx的fastDFS模块处理，加上后边的图片所存在的storage路径，前端再请求，显示图片
     */

    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(uploadService.uploadImage(file));
    }
}
