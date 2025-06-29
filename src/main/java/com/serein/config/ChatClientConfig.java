package com.serein.config;

import com.serein.advisor.MyLoggerAdvisor;
import com.serein.advisor.ReReadingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: QingQiu
 * @Date: 2025/6/3
 * @Description: ChatClient配置类
 */
@Configuration
public class ChatClientConfig {

  private String SYSTEM_PROMPT = "你是一个全领域通识专家，擅长为用户解答各种问题";

  @Bean
  public ChatClient initChatClient(ChatModel dashscopeChatModel) {
    return ChatClient.builder(dashscopeChatModel)
        .defaultSystem(SYSTEM_PROMPT)
        .defaultAdvisors(
            new MessageChatMemoryAdvisor(new InMemoryChatMemory())
            // 自定义日志 Advisor，可按需开启
            ,new MyLoggerAdvisor()
            // 自定义推理增强 Advisor，可按需开启
            ,new ReReadingAdvisor()
        )
        .build();
  }
}
