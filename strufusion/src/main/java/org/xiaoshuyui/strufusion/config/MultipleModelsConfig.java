package org.xiaoshuyui.strufusion.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MultipleModelsConfig {

  @Resource private OpenAiProperties openAiProperties;

  @Bean("defaultChatModel")
  public OpenAiChatModel openAiChatModel() {
    String baseUrl = openAiProperties.getBaseUrl();
    String apiKey = openAiProperties.getApiKey();
    String model = openAiProperties.getModel();

    log.info("初始化 OpenAiChatModel：baseUrl={}, model={}", baseUrl, model);

    return OpenAiChatModel.builder().apiKey(apiKey).baseUrl(baseUrl).modelName(model).build();
  }

  @Bean("defaultStreamingChatModel")
  public OpenAiStreamingChatModel streamingChatModel() {
    String baseUrl = openAiProperties.getBaseUrl();
    String apiKey = openAiProperties.getApiKey();
    String model = openAiProperties.getModel();

    log.info("初始化 StreamingChatModel：baseUrl={}, model={}", baseUrl, model);

    return OpenAiStreamingChatModel.builder()
        .apiKey(apiKey)
        .baseUrl(baseUrl)
        .modelName(model)
        .build();
  }
}
