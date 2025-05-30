package org.xiaoshuyui.strufusion.common;

import lombok.Data;

@Data
public class SseResponse<T> {
  T data;
  String message;
  boolean done;
  String uuid;
}
