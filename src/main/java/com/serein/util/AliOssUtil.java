package com.serein.util;

import static com.serein.util.FileUtil.createLocalFileName;
import static com.serein.util.FileUtil.createOSSFileName;

import cn.hutool.core.io.FileUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.model.vo.userVO.LoginUserVO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/27
 * @Time: 17:52
 * @Description: oss工具类
 */

@Slf4j
public class AliOssUtil {

  static String BUCKET_NAME="";

  static String END_POINT="";

  static String ACCESS_KEY_ID="";

  static String ACCESS_KEY_SECRET="";


  //上传到oss
  public static String uploadImageOSS(MultipartFile img) {
    OSS ossClient= new OSSClientBuilder().build(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    if (!ossClient.doesBucketExist(BUCKET_NAME)) {
      ossClient.createBucket(BUCKET_NAME);
      CreateBucketRequest createBucketRequest = new CreateBucketRequest(BUCKET_NAME);
      createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
      ossClient.createBucket(createBucketRequest);
    }
    //filePath是存到oss的文件名，fileUrl是访问的路径
    String filePath = createOSSFileName(img.getOriginalFilename());
    PutObjectRequest putObjectRequest = null;
    try {
      putObjectRequest = new PutObjectRequest(BUCKET_NAME, filePath, img.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    PutObjectResult result = ossClient.putObject(putObjectRequest);
    return  "https://" + BUCKET_NAME + "." + END_POINT + "/" + filePath;
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
