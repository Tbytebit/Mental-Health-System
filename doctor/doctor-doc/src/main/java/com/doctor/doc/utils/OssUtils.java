package com.doctor.doc.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS文件上传工具类
 */
public class OssUtils {
    
    // OSS配置信息
    private static final String ENDPOINT = "oss-cn-beijing.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "********";
    private static final String ACCESS_KEY_SECRET = "********";
    private static final String BUCKET_NAME = "****";
    
    /**
     * 上传文件到OSS
     * 
     * @param file 文件对象
     * @param dir 目录
     * @return 文件访问路径
     */
    public static String uploadFile(MultipartFile file, String dir) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        
        try {
            // 获取上传文件流
            InputStream inputStream = file.getInputStream();
            
            // 生成文件路径
            String filePath = generateFilePath(file.getOriginalFilename(), dir);
            
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 设置文件类型
            metadata.setContentType(getContentType(file.getOriginalFilename()));
            
            // 上传文件
            ossClient.putObject(BUCKET_NAME, filePath, inputStream, metadata);
            
            // 获取文件访问路径
            return "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + filePath;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    /**
     * 生成文件路径
     * 
     * @param fileName 原始文件名
     * @param dir 目录
     * @return 文件路径
     */
    private static String generateFilePath(String fileName, String dir) {
        // 获取文件扩展名
        String extension = fileName.substring(fileName.lastIndexOf("."));
        
        // 生成日期路径
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        
        // 生成UUID文件名
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        
        // 拼接文件路径
        return dir + "/" + datePath + "/" + uuid + extension;
    }
    
    /**
     * 获取文件类型
     * 
     * @param fileName 文件名
     * @return 文件类型
     */
    private static String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        
        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".bmp":
                return "image/bmp";
            case ".webp":
                return "image/webp";
            case ".pdf":
                return "application/pdf";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".ppt":
                return "application/vnd.ms-powerpoint";
            case ".pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default:
                return "application/octet-stream";
        }
    }
} 