package com.doctor.diary.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 */
@Component
public class OssHelper {

    private static final String ENDPOINT = "oss-cn-beijing.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "********";
    private static final String ACCESS_KEY_SECRET = "********";
    private static final String BUCKET_NAME = "****";
    
    /**
     * 获取OSS桶名称
     * 
     * @return 桶名称
     */
    public String getBucketName() {
        return BUCKET_NAME;
    }
    
    /**
     * 上传文件到OSS
     * 
     * @param file 文件
     * @return 文件访问URL
     * @throws IOException 如果读取文件失败
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        
        try {
            // 获取文件输入流
            InputStream inputStream = file.getInputStream();
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = "diary/" + UUID.randomUUID().toString().replaceAll("-", "") + suffix;
            
            // 上传文件
            ossClient.putObject(BUCKET_NAME, fileName, inputStream);
            
            // 生成文件访问URL
            return "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + fileName;
            
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    /**
     * 从OSS删除文件
     * 
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        
        try {
            // 从URL中提取文件名
            String fileName = fileUrl.substring(fileUrl.indexOf(BUCKET_NAME) + BUCKET_NAME.length() + 1);
            
            // 删除文件
            ossClient.deleteObject(BUCKET_NAME, fileName);
            
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
} 