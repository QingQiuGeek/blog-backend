package com.serein.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: QingQiu
 * @Date: 2025/3/2
 * @Description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
  private Header header;
  private Parameter parameter;
  private Payload payload;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Header {
    private String appId;
    private String uid;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Parameter {
    private Chat chat;

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Chat {
    private String domain;
    private double temperature;
    private int maxTokens;

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Payload {
    private Message message;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Message {
    private List<Text> text;
  }

  @Data
  public static class Text {
    String role;
    String content;
  }
}
