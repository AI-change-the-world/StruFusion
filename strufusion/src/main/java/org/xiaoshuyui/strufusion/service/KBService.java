package org.xiaoshuyui.strufusion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xiaoshuyui.strufusion.entity.KB;
import org.xiaoshuyui.strufusion.entity.requests.NewKBRequest;
import org.xiaoshuyui.strufusion.mapper.KBMapper;

@Service
@Slf4j
public class KBService {
  private final KBMapper kbMapper;

  public KBService(KBMapper kbMapper) {
    this.kbMapper = kbMapper;
  }

  static ObjectMapper objectMapper = new ObjectMapper();

  public long createKB(NewKBRequest request) {
    KB kb = new KB();
    kb.setName(request.getName());
    kb.setDescription(request.getDescription());
    try {
      kb.setPoints(objectMapper.writeValueAsString(request.getPoints()));
      kbMapper.insert(kb);
      return kb.getId();
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      return (long) -1;
    }
  }
}
