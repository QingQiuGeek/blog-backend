package com.serein.util;

import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/17
 * @Time: 12:53
 * @Description:
 */

public class IPUtil {

  private static final String UNKNOWN = "unknown";
  private static final String HEADER_FORWARDED = "x-forwarded-for";
  private static final String HEADER_PROXY = "Proxy-Client-IP";
  private static final String HEADER_WL_PROXY = "WL-Proxy-Client-IP";
  private static final String HEADER_HTTP = "HTTP_CLIENT_IP";
  private static final String HEADER_HTTP_FORWARDED = "HTTP_X_FORWARDED_FOR";
  private static final String LOCAL_IP = "127.0.0.1";
  private static final String LOCAL_HOST = "localhost";

  /**
   * 获取 IP 地址
   *
   * @param request
   * @return
   */
  public static String getIpAddr(HttpServletRequest request) {
    String ip = request.getHeader(HEADER_FORWARDED);

    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader(HEADER_PROXY);
    }

    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader(HEADER_WL_PROXY);
    }

    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader(HEADER_HTTP);
    }

    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader(HEADER_HTTP_FORWARDED);
    }

    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }

    // 本机访问
    if (LOCAL_IP.equalsIgnoreCase(ip) || LOCAL_HOST.equalsIgnoreCase(ip)
        || "0:0:0:0:0:0:0:1".equalsIgnoreCase(ip)) {
      // 根据网卡取本机配置的 IP
      try {
        InetAddress localHost = InetAddress.getLocalHost();
        ip = localHost.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }

    // 对于通过多个代理的情况，第一个 IP 为客户端真实 IP,多个 IP 按照','分割
    if (ip != null && ip.length() > 15) {
      if (ip.indexOf(",") > 15) {
        ip = ip.substring(0, ip.indexOf(","));
      }
    }
    return ip;
  }


  private static Searcher searcher;

  /**
   * 判断是否为合法 IP
   *
   * @return
   */
  public static boolean checkIp(String ipAddress) {
    String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
    Pattern pattern = Pattern.compile(ip);
    Matcher matcher = pattern.matcher(ipAddress);
    return matcher.matches();
  }

  /**
   * 在服务启动时，将 ip2region 加载到内存中
   */
  @PostConstruct
  private static void initIp2Region() {
    try {
      InputStream inputStream = new ClassPathResource("/ipdb/ip2region.xdb").getInputStream();
      byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
      searcher = Searcher.newWithBuffer(bytes);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * 获取 ip 所属地址
   *
   * @param ip
   * @return
   */
  public static String getIpRegion(String ip) {

    boolean isIp = checkIp(ip);

    if (isIp) {

      initIp2Region();

      try {
        // searchIpInfo 的数据格式： 国家|区域|省份|城市|ISP
        String searchIpInfo = searcher.search(ip);

        String[] splitIpInfo = searchIpInfo.split("\\|");

        if (splitIpInfo.length > 0) {
          if ("中国".equals(splitIpInfo[0])) {
            // 国内属地返回省份
            return splitIpInfo[2];
          } else if ("0".equals(splitIpInfo[0])) {
            if ("内网IP".equals(splitIpInfo[4])) {
              // 内网 IP
              return splitIpInfo[4];
            } else {
              return "";
            }
          } else {
            // 国外属地返回国家
            return splitIpInfo[0];
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return "";
    } else {
      throw new IllegalArgumentException("非法的IP地址");
    }

  }


  /**
   * 对于请求次数多的ip，进行限流
   */
  public static void isHotIp() {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      throw new BusinessException(ErrorCode.UNEXPECT_ERROR, ErrorInfo.SYS_ERROR);
    }
    HttpServletRequest request = requestAttributes.getRequest();
    String hotIpKey = Common.HOT_IP_KEY + IPUtil.getIpAddr(request);

//    boolean hotIP = JdHotKeyStore.isHotKey(hotIpKey);
//    if (hotIP) {
//      //对于请求次数多的ip，进行限流
//      throw new BusinessException(ErrorCode.SYSTEM_BUSY, ErrorInfo.SYSTEM_BUSY);
//    }
  }
}

