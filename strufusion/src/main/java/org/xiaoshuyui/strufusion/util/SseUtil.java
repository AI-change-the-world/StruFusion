package org.xiaoshuyui.strufusion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
public class SseUtil {
  static final ObjectMapper mapper = new ObjectMapper();
  ;

  public static void sseSend(SseEmitter emitter, Object o) {
    try {
      if (o instanceof String) {
        emitter.send(o);
      } else {
        emitter.send(mapper.writeValueAsString(o));
      }

    } catch (Exception e) {
      log.error(e.getMessage());
      emitter.completeWithError(e);
    }
  }
}
