package com.serein.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/27
 * @Time: 20:20
 * @Description: https://gitee.com/xiaoym/knife4j/issues/I4JT89
 */

@Component
public class WebMvcConfig {

  /**
   * 解决springboot升级到2.6.x之后，knife4j报错
   *
   * @param webEndpointsSupplier        the web endpoints supplier
   * @param servletEndpointsSupplier    the servlet endpoints supplier
   * @param controllerEndpointsSupplier the controller endpoints supplier
   * @param endpointMediaTypes          the endpoint media types
   * @param corsProperties              the cors properties
   * @param webEndpointProperties       the web endpoint properties
   * @param environment                 the environment
   * @return the web mvc endpoint handler mapping
   */
  @Bean
  public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
      WebEndpointsSupplier webEndpointsSupplier, ServletEndpointsSupplier servletEndpointsSupplier,
      ControllerEndpointsSupplier controllerEndpointsSupplier, EndpointMediaTypes endpointMediaTypes,
      CorsEndpointProperties corsProperties, WebEndpointProperties webEndpointProperties,
      Environment environment) {
    List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
    Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
    allEndpoints.addAll(webEndpoints);
    allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
    allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
    String basePath = webEndpointProperties.getBasePath();
    EndpointMapping endpointMapping = new EndpointMapping(basePath);
    boolean shouldRegisterLinksMapping = shouldRegisterLinksMapping(webEndpointProperties,
        environment, basePath);
    return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
        corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
        shouldRegisterLinksMapping, null);
  }

  /**
   * shouldRegisterLinksMapping
   * @param webEndpointProperties webEndpointProperties
   * @param environment environment
   * @param basePath /
   * @return boolean
   */
  private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties,
      Environment environment, String basePath) {
    return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
        || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
  }

}
