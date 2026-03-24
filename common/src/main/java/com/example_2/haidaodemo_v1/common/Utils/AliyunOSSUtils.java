package com.example_2.haidaodemo_v1.common.Utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class AliyunOSSUtils {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;
    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    /**
     * 上传文件
     * <hr/>
     * 🧩 逻辑：远程链接aliyun，返回存存储后的url
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param  file
     * @return url
     * @author Sisfuzues
     * @date 2026/3/16 11:03
     */
    public String upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String suffixName = null;
        if (originalFilename != null) {
            suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + suffixName;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName,fileName,file.getInputStream());

        String url = "https://" + bucketName + "." + endpoint + "/" + fileName;

        ossClient.shutdown();
        return url;
    }
}
