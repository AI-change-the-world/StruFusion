package org.xiaoshuyui.strufusion.entity.requests;

import java.util.List;
import lombok.Data;

@Data
public class NewKBRequest {
  String name;
  String description;
  List<Point> points;

  @Data
  public static class Point {
    String name;
    String alias;
  }
}
