package org.xiaoshuyui.stufusion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "ai.openai")
public class OpenAiProperties {
    private ChatOptions chat;
    private String apiKey;
    private String baseUrl;

    @Data
    public static class ChatOptions {
        private String model;
    }
}
