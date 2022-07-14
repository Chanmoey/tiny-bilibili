package com.moon.tinybilibili.service.utils;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.moon.tinybilibili.domain.exception.ConditionException;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Chanmoey
 * @date 2022年07月13日
 */
@Component
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;

    private static final String DEFAULT_GROUP = "group1";

    private static final String PATH_KEY = "path-key:";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";

    private static final String UPLOADED_No_KEY = "uploaded-no-key:";

    private static final int SLICE_SIZE = 1024 * 1024 * 2;

    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件!");
        }

        String fileName = file.getOriginalFilename();
        return getFileTypeByFileName(fileName);
    }

    public String getFileTypeByFileName(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new ConditionException("文件名不能为空!");
        }

        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }

    /**
     * 文件上传接口
     *
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
     * 支持断点续传的文件上传功能
     */
    public String uploadAppenderFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = getFileTypeByFileName(fileName);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(),
                file.getSize(), fileType
        );
        return storePath.getPath();
    }

    /**
     * 修改分片文件的内容
     */
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws IOException {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws IOException {
        if (file == null || sliceNo == null || totalSliceNo == null) {
            throw new ConditionException("参数异常!");
        }

        // 记录文件上传的路径
        String pathKey = PATH_KEY + fileMd5;
        // 记录已上传文件的大小
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        // 记录已上传文件的数量
        String uploadedNoKey = UPLOADED_No_KEY + fileMd5;

        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = 0L;
        if (!StringUtils.isNullOrEmpty(uploadedSizeStr)) {
            uploadedSize = Long.parseLong(uploadedSizeStr);
        }

        // 开始上传，第一个分片
        if (sliceNo == 1) {
            String path = this.uploadAppenderFile(file);
            if (StringUtils.isNullOrEmpty(path)) {
                throw new ConditionException("上传失败!");
            }

            // 操作redis
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        } else {
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtils.isNullOrEmpty(filePath)) {
                throw new ConditionException("上传失败!");
            }
            this.modifyAppenderFile(file, filePath, uploadedSize);
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        // 更新历史上传分片文件大小
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));

        // 如果所有分片全部上传完毕，清空redis中的记录
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        assert uploadedNoStr != null;
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultPath = "";
        if (uploadedNo.equals(totalSliceNo)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(uploadedNoKey, pathKey, uploadedSizeKey);
            redisTemplate.delete(keyList);
        }

        // 如果文件分片没有全部上传完成，用户是不需要关系文件存储路径的，只需要MD5就可以获取文件之前分片存储的路径了。
        return resultPath;
    }

    /**
     * 文件分片
     */
    public void convertFileToSlices(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String fileType = this.getFileTypeByFileName(fileName);
        File file = this.multipartFileToFile(multipartFile);
        long fileLength = file.length();

        int count = 1;
        for (int i = 0; i < fileLength; i += SLICE_SIZE) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");) {
                randomAccessFile.seek(i);
                byte[] bytes = new byte[SLICE_SIZE];
                int len = randomAccessFile.read(bytes);
                String path = "D:\\tmpfile\\" + count + "." + fileType;
                File slice = new File(path);
                try (FileOutputStream fos = new FileOutputStream(slice);) {
                    fos.write(bytes, 0, len);
                }
                count++;
            }
        }
        Files.delete(file.toPath());
    }

    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        assert originalFileName != null;
        String[] fileName = originalFileName.split("\\.");
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }

    /**
     * 文件删除接口
     *
     * @param filePath 文件路径
     */
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }

    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        long totalFileSize = fileInfo.getFileSize();
        String url = httpFdfsStorageAddr + path;
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        String rangeStr = request.getHeader("Range");
        String[] range;
        if (StringUtils.isNullOrEmpty(rangeStr)) {
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }
        range = rangeStr.split("bytes=|-");
        long begin = 0;
        if (range.length >= 2) {
            begin = Long.parseLong(range[1]);
        }
        long end = totalFileSize - 1;
        if (range.length >= 3) {
            end = Long.parseLong(range[2]);
        }
        long len = (end - begin) + 1;
        String contentRange = "bytes " + begin + "-" + end + "/" + totalFileSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        HttpUtil.get(url, headers, response);
    }
}
