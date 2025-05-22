package org.xiaoshuyui.strufusion.controller;

import io.swagger.v3.oas.annotations.Parameter;

import java.util.UUID;
import java.util.concurrent.Executors;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xiaoshuyui.strufusion.common.Result;
import org.xiaoshuyui.strufusion.common.SseResponse;
import org.xiaoshuyui.strufusion.service.KBFileService;
import org.xiaoshuyui.strufusion.util.DocReader;
import org.xiaoshuyui.strufusion.util.SseUtil;

@RestController
@Slf4j
@RequestMapping("/file")
public class KBFileController {
  final KBFileService kbFileService;

  public KBFileController(KBFileService kbFileService) {
    this.kbFileService = kbFileService;
  }

  @Deprecated(since = "use `uploadFiles`, this is for test")
  @PostMapping(value = "/{kbId}/upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result uploadFile(
      @Parameter(description = "文件", required = true) @RequestPart("file") MultipartFile file,
      @PathVariable("kbId") Long kbId) {
    try {
      kbFileService.extract(
          kbId, DocReader.extractText(file.getInputStream()), file.getOriginalFilename());
      return Result.OK_data("done");
    } catch (Exception e) {
      e.printStackTrace();
      return Result.error("上传失败 " + e.getMessage());
    }
  }

  @Deprecated(since = "this is for test")
  @GetMapping("/intent")
  public Result intentRec(@RequestParam("kbId") Long kbId, @RequestParam("q") String b) {
    return Result.OK_data(kbFileService.intentRecognition(b, kbId));
  }

  @PostMapping(value = "/{kbId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public SseEmitter uploadFiles(
      @Parameter(description = "文件列表", required = true) @RequestPart("files") MultipartFile[] files,
      @PathVariable("kbId") Long kbId) {
    SseEmitter emitter = new SseEmitter(36000000L);
    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              for (var file : files) {
                try {
                  kbFileService.extract(
                      kbId,
                      DocReader.extractText(file.getInputStream()),
                      file.getOriginalFilename());
                  SseUtil.sseSend(emitter, file.getOriginalFilename() + "上传成功");
                } catch (Exception e) {
                  SseUtil.sseSend(emitter, file.getOriginalFilename() + "上传失败 " + e.getMessage());
                  e.printStackTrace();
                  continue;
                }
              }
              emitter.complete();
            });

    return emitter;
  }

  @Data
  public static class KBRequest {
    Long kbId;
    String message;
  }

  @PostMapping("/streamChat")
  public SseEmitter streamChat(
      @RequestBody KBRequest request) {
    SseEmitter emitter = new SseEmitter(36000000L);
    SseResponse<String> response = new SseResponse<>();
    String uuid = UUID.randomUUID().toString();
    response.setUuid(uuid);
    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              try {
                kbFileService.streamChat(request.getMessage(), request.getKbId(), emitter, response);
              } catch (Exception e) {
                e.printStackTrace();
              } finally {

                response.setDone(true);
                SseUtil.sseSend(emitter, response);
                emitter.complete();
              }
            });

    return emitter;
  }
}
