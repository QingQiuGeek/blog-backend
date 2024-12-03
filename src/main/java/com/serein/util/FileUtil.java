package com.serein.util;

import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.model.UserHolder;
import com.serein.model.vo.UserVO.LoginUserVO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/27
 * @Time: 20:51
 * @Description:
 */

@Slf4j
public class FileUtil {

  //可以上传的图片文件类型
  private static final List<String> VALID_IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png",
      ".gif");

  public static void checkImageType(String fileName) {
    log.info("fileName：" + fileName);
    if (fileName == null || fileName.isEmpty()) {
      throw new BusinessException(ErrorCode.FILE_TYPE_ERROR, ErrorInfo.FILE_TYPE_ERROR);
    }
    //获取文件后缀
    String fileType = getFileType(fileName);
    log.info("fileType：" + fileType);
    // 检查文件扩展名是否在有效列表中
    if (!VALID_IMAGE_EXTENSIONS.contains(fileType)) {
      throw new BusinessException(ErrorCode.FILE_TYPE_ERROR, ErrorInfo.FILE_TYPE_ERROR);
    }
  }

  public static boolean checkOrCreateDirectory(String path) {
    File directory = new File(path);
    // 检查路径是否存在
    if (directory.exists() && directory.isDirectory()) {
      return true;
      // 如果路径已存在，返回true
    } else {
      // 如果路径不存在，尝试创建多层文件夹
      boolean created = directory.mkdirs();
      if (created) {
        log.info("create file directory success：" + path);
        return created;
      }
      log.error("create file directory fail：" + path);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, ErrorInfo.SYS_ERROR);
      // 如果创建成功，返回true，失败返回false
    }
  }


  public static String createFileName(String fileName) {
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    //根据日期生成路径   2024/11/15/
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    String datePath = sdf.format(new Date());
    String userId = loginUserVO.getUserId().toString();
    String filePath = Common.IMG_UPLOAD_DIR + datePath + userId + "/";
    log.info("filePath：" + filePath);
    boolean b = checkOrCreateDirectory(filePath);
    if (!b) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, ErrorInfo.SYS_ERROR);
    }
    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
    String fileType = getFileType(fileName);
    String createFileName = filePath + uuid + fileType;
    log.info("createFileName：" + createFileName);
    return createFileName;
  }

  private static String getFileType(String fileName) {
    //后缀和文件后缀一致
    int index = fileName.lastIndexOf(".");
    // test.jpg -> .jpg
    return fileName.substring(index);
  }

  public static void checkFileSize(Long len) {
    log.info("fileSize：" + len);
    // 获取单位
    String unit = Common.IMG_SIZE_UNIT.toUpperCase();
    // 定义单位与换算系数的映射
    Map<String, Double> unitMap = Map.of("B", 1.0, "K", 1024.0, "M", 1048576.0);
    // 获取换算系数
    Double conversionFactor = unitMap.get(unit);
    if (conversionFactor == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.FILE_SIZE_ERROR);
    }
    // 计算文件大小
    double fileSize = len / conversionFactor;
    // 检查文件是否超过大小限制
    if (fileSize > Common.IMG_SIZE_LIMIT) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.FILE_SIZE_ERROR);
    }
  }

  public static String uploadImageLocal(MultipartFile image) {
    String originalFilename = image.getOriginalFilename();
    log.info("originalFilename：" + originalFilename);
    checkImageType(originalFilename);
    //已经在yaml文件配置了 max-file-size: 2MB，不需要checkSize
//    checkFileSize(image.getSize());
    try {
      String fileName = createFileName(originalFilename);
      // 保存/上传文件
      // fileName即是路径 + 文件名
      image.transferTo(new File(fileName));
      // 返回结果
      log.info("文件上传成功，{}", fileName);
      return fileName;
    } catch (IOException e) {
      throw new RuntimeException("文件上传失败", e);
    }
  }
}
