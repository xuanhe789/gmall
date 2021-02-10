package com.xuanhe.gmall.product.controller;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.product.util.FastDfsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/admin/product")
@RestController
public class FileUploadController {
    @Autowired
    FastDfsUtil fastDfsUtil;

    @RequestMapping("/fileUpload")
    public Result uploadFile(MultipartFile file) throws IOException {
        if (file==null){
            throw new RuntimeException("上传的文件为空");
        }
        String path = fastDfsUtil.uploadFile(file);
        return Result.ok(path);
    }
}
