package com.serein.util;

import static com.serein.util.FileUtil.createFileName;

import cn.hutool.core.io.FileUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.serein.constants.Common;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/27
 * @Time: 17:52
 * @Description:
 */

@Slf4j
//@Component
public class AliOssUtil {


  @Value("${oss.bucket}")
  static String BUCKET_NAME;

  @Value("${oss.endPoint}")
  static String END_POINT;

  @Value("${oss.accessKey}")
  static String ACCESS_KEY_ID;

  @Value("${oss.secretKey}")
  static String ACCESS_KEY_SECRET;


  // 创建OSS客户端实例
  private static OSS ossClient;

  static {
    // 初始化OSSClient
    ossClient = new OSSClientBuilder().build(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
  }

  //上传到oss
  public static String uploadImageOSS(MultipartFile img) {

    if (!ossClient.doesBucketExist(BUCKET_NAME)) {
      ossClient.createBucket(BUCKET_NAME);
      CreateBucketRequest createBucketRequest = new CreateBucketRequest(BUCKET_NAME);
      createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
      ossClient.createBucket(createBucketRequest);
    }
    //设置文件路径
    String filePath = createFileName(img.getOriginalFilename());
    String fileUrl = "https://" + BUCKET_NAME + "." + END_POINT + "/" + filePath;
    PutObjectRequest putObjectRequest = null;
    try {
      putObjectRequest = new PutObjectRequest(BUCKET_NAME, filePath, img.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    PutObjectResult result = ossClient.putObject(putObjectRequest);
    //上传文件
    return fileUrl;

  }


  public Boolean deleteImg(String filename) {
    File file = new File(Common.IMG_UPLOAD_DIR, filename);
    if (file.isDirectory()) {
      return false;
    }
    FileUtil.del(file);
    return true;
  }

}
