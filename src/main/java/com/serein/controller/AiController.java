package com.serein.controller;

import com.serein.service.AiService;
import com.serein.util.BR;
import com.serein.util.R;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * @Author:懒大王Smile
 * @Date: 2025/5/8
 * @Time: 15:14
 * @Description: ai聊天Controller
 */

@RestController
@RequestMapping("/ai")
public class AiController {

     @Resource
     private AiService aiService;

    /**
     * 发送问题
     * @param msg
     * @return
     */
    @GetMapping(value = "/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSse(String msg) {
      return aiService.chatSse(msg);
    }

    /**
     * 发送问题
     * @param msg
     * @return
     */
    @GetMapping(value = "/chat/flux",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWebFlux(String msg) {
      return aiService.chatWebFlux(msg);
    }

    @GetMapping(value = "/chat/flux/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatFluxSse(String msg) {
      return aiService.chatWebFlux(msg)
          .map(data -> ServerSentEvent.builder(data).build());
    }

}
