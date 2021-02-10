package com.xuanhe.gmall.product.util;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class FastDfsUtil {
    @Autowired
    FastFileStorageClient storageClient;
    @Value("${project.dfsUrl}")
    String dfsUrl;

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extendName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extendName, null);
        return dfsUrl+storePath.getFullPath();
    }

    public void deleteFile(String fileUrl){
        if (StringUtils.isNullOrEmpty(fileUrl)) {
            throw new RuntimeException("要删除的文件路径为空");
        }
        storageClient.deleteFile(fileUrl);
    }

}
