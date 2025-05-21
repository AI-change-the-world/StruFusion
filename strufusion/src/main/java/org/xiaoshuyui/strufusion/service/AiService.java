package org.xiaoshuyui.strufusion.service;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Service
@Slf4j
public class AiService {

  private final OpenAiChatModel chatModel;
  private final OpenAiStreamingChatModel streamingChatModel;

  public AiService(
      @Qualifier("defaultChatModel") OpenAiChatModel chatModel,
      @Qualifier("defaultStreamingChatModel") OpenAiStreamingChatModel streamingChatModel) {
    this.chatModel = chatModel;
    this.streamingChatModel = streamingChatModel;
  }

  public String chat(String message) {
    return chatModel.chat(message);
  }

  public Flux<String> streamChat(String message) {
    return Flux.create(
        emitter -> {
          streamingChatModel.chat(
              message,
              new StreamingChatResponseHandler() {

                @Override
                public void onPartialResponse(String partialResponse) {
                  emitter.next(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                  emitter.complete();
                }

                @Override
                public void onError(Throwable error) {
                  log.error("stream chat error :" + error);
                  emitter.error(error);
                }
              });
        },
        FluxSink.OverflowStrategy.BUFFER);
  }
}
