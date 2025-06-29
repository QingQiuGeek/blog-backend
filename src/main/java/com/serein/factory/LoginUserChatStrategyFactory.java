package com.serein.factory;

import com.serein.strategy.LoginUserChatStrategy;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: QingQiu
 * @Date: 2025/6/28
 * @Description:
 */
@Component
public class LoginUserChatStrategyFactory {
  @Autowired
  private ChatClient chatClient;

  public LoginUserChatStrategy create(Long userId) {
    return new LoginUserChatStrategy(userId, chatClient);
  }
}
