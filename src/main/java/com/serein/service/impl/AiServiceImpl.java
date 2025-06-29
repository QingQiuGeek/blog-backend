package com.serein.service.impl;

import com.serein.factory.LoginUserChatStrategyFactory;
import com.serein.service.AiService;
import com.serein.strategy.AnonymousUserChatStrategy;
import com.serein.strategy.ChatStrategy;
import com.serein.util.UserContext;
import jakarta.annotation.Resource;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/6/3
 * @Description:
 */

@Slf4j
@Service
public class AiServiceImpl implements AiService {

  @Resource
  BeanFactory beanFactory;

  @Resource
  LoginUserChatStrategyFactory loginUserChatStrategyFactory;

  @Override
  public Flux<String> chatWebFlux(String msg) {
    Long userId = UserContext.getUser();
    ChatStrategy chatStrategy;
    if(userId!=null){
      chatStrategy = loginUserChatStrategyFactory.create(userId);
    }else {
      chatStrategy = beanFactory.getBean(AnonymousUserChatStrategy.class);
    }
    return chatStrategy.processMessage(msg);
  }

  @Override
  public SseEmitter chatSse(String msg) {
    Long userId = UserContext.getUser();
    ChatStrategy chatStrategy;
    if(userId!=null){
      chatStrategy = loginUserChatStrategyFactory.create(userId);
    }else {
      chatStrategy = beanFactory.getBean(AnonymousUserChatStrategy.class);
    }
    // 创建一个超时时间较长的 SseEmitter 3 分钟超时
    SseEmitter sseEmitter = new SseEmitter(180000L);
    Flux<String> ccontentFlux = chatStrategy.processMessage(msg);
    ccontentFlux.subscribe(chunk -> {
          try {
            sseEmitter.send(chunk);
//            Thread.sleep(1000);
          } catch (Exception e) {
            sseEmitter.completeWithError(e);
          }
        }, sseEmitter::completeWithError, sseEmitter::complete);
    return sseEmitter;
  }
}
