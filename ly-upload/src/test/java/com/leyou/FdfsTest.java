package com.leyou;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.google.inject.internal.cglib.transform.$ClassTransformer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author chenxm
 * @date 2020/7/6 - 17:00
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class FdfsTest {
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Test
    public void testUpload() throws FileNotFoundException {
        File file = new File("C:/Users/chenxm/Pictures/test.png");
        //上传并生成缩略图
        StorePath storePath = this.storageClient.uploadFile(new FileInputStream(file), file.length(), "png", null);
        //带分组的路径
        System.out.println(storePath.getFullPath());
        //不带分组的路径
        System.out.println(storePath.getPath());
    }


    @Test
    public void testUploadAndCreateThumb() throws FileNotFoundException {
        File file = new File("C:/Users/chenxm/Pictures/test.png");
        //上传并生成缩略图
        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(new FileInputStream(file), file.length(), "png", null);
        //带分组的路径
        System.out.println(storePath.getFullPath());
        //不带分组的路径
        System.out.println(storePath.getPath());

        //缩略图路径
        String thumbImagePath = thumbImageConfig.getThumbImagePath(storePath.getPath());
        System.out.println(thumbImagePath);
    }

}
