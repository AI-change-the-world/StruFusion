package org.xiaoshuyui.strufusion;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("org.xiaoshuyui.strufusion.mapper")
public class StrufusionApplication {

  public static void main(String[] args) {
    SpringApplication.run(StrufusionApplication.class, args);
  }
}
