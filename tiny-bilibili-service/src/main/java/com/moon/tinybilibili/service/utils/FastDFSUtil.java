package com.moon.tinybilibili.service.utils;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.moon.tinybilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Chanmoey
 * @date 2022年07月13日
 */
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件!");
        }

        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }

    /**
     * 文件上传接口
     * @param file 文件
     * @return 存储的相对路径
     * @throws IOException IOE
     */
    public String uploadCommonFile(MultipartFile file) throws IOException {
        Set<MetaData> metaData = new HashSet<>();
        String fileType = this.getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(
                file.getInputStream(),
                file.getSize(), fileType, metaData);
        return storePath.getPath();
    }

    /**
     * 文件删除接口
     * @param filePath 文件路径
     */
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }
}
