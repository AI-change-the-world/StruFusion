package org.xiaoshuyui.strufusion.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.openai")
public class OpenAiProperties {
  private String apiKey;
  private String baseUrl;
  private String model;

  @PostConstruct
  public void printProps() {
    System.out.println("=== OpenAiProperties 初始化 ===");
    System.out.println("baseUrl = " + baseUrl);
    System.out.println("model = " + model);
  }
}
