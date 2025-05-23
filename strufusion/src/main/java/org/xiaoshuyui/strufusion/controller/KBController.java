package org.xiaoshuyui.strufusion.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiaoshuyui.strufusion.common.Result;
import org.xiaoshuyui.strufusion.entity.KB;
import org.xiaoshuyui.strufusion.entity.requests.NewKBRequest;
import org.xiaoshuyui.strufusion.service.KBService;

@Slf4j
@RestController
@RequestMapping("/kb")
public class KBController {
  private final KBService kbService;

  public KBController(KBService kbService) {
    this.kbService = kbService;
  }

  @PostMapping("/create")
  public Result postMethodName(@RequestBody NewKBRequest request) {
    long id = kbService.createKB(request);
    if (id == -1) {
      return Result.error("创建知识库失败！");
    }

    return Result.OK();
  }

  @GetMapping("/list")
  public Result list() {
    List<KB> kbs = kbService.list();
    return Result.OK_data(kbs);
  }
}
