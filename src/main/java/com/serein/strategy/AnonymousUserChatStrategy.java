package com.serein.strategy;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @Author: QingQiu
 * @Date: 2025/6/4
 * @Description:
 */

@Component
public class AnonymousUserChatStrategy implements ChatStrategy{

  @Resource
  ChatClient chatClient;

//  private final ChatClient chatClient1;

  public AnonymousUserChatStrategy(ChatClient chatClient, ChatModel chatModel) {
    this.chatClient = chatClient;
//    chatClient1 = ChatClient.builder(chatModel)
//        .defaultAdvisors(
//            new MessageChatMemoryAdvisor( new InMemoryChatMemory())
//        )
//        .build();
  }

  /**
   * 未登录用户消息不做持久化
   * @param msg
   * @return
   */
  @Override
  public Flux<String> processMessage(String msg) {
    return chatClient.prompt().user(msg).stream().content();
  }

}
