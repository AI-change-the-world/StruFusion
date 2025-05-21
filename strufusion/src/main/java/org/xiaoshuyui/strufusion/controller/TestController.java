package org.xiaoshuyui.strufusion.controller;

import jakarta.annotation.Resource;
import java.util.concurrent.Executors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xiaoshuyui.strufusion.service.AiService;
import org.xiaoshuyui.strufusion.util.SseUtil;

@RestController
@RequestMapping("/test")
public class TestController {

  @Resource private AiService aiService;

  @GetMapping("/chat")
  public String chat(String message) {
    return aiService.chat(message);
  }

  @GetMapping("/streamChat")
  public SseEmitter streamChat(String message) {
    SseEmitter emitter = new SseEmitter();
    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              aiService
                  .streamChat(message)
                  .doOnNext(
                      data -> {
                        SseUtil.sseSend(emitter, data);
                      })
                  .doOnComplete(emitter::complete)
                  .doOnError(emitter::completeWithError)
                  .subscribe();
            });

    return emitter;
  }
}
