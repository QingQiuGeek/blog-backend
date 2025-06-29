package com.serein.strategy;

import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/6/4
 * @Description: chat策略类，暂分为未登录用户、登录+用户，未来可增加vip等
 */
public interface ChatStrategy {
  Flux<String> processMessage(String msg);
}
