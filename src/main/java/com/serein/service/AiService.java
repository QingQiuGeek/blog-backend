package com.serein.service;

import jakarta.servlet.annotation.WebFilter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/6/3
 * @Description:
 */
public interface AiService {

  SseEmitter chatSse(String msg);

  Flux<String> chatWebFlux(String msg);

}
