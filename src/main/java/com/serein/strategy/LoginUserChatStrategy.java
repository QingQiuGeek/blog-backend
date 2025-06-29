package com.serein.strategy;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

import com.serein.advisor.MyLoggerAdvisor;
import com.serein.advisor.ReReadingAdvisor;
import com.serein.util.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/6/4
 * @Description:
 */
//@Component
public class LoginUserChatStrategy implements ChatStrategy{

  private Long userId;

  //  @Resource
  private ChatClient chatClient;
//  public LoginUserChatStrategy(Long userId) {
//    this.userId = userId;
//  }

//  @Autowired
//  public LoginUserChatStrategy(ChatClient chatClient) {
//    this.chatClient = chatClient;
//  }
  public LoginUserChatStrategy(Long userId,ChatClient chatClient) {
    this.chatClient = chatClient;
    this.userId = userId;

  }

  /**
   * 登录用户消息持久化最大100条
   * @param msg
   * @return
   */
  @Override
  public Flux<String> processMessage(String msg) {
    return chatClient.prompt().user(msg)
        .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
        .stream()
        .content();
  }
}
