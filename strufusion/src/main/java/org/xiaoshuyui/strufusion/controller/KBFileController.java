package org.xiaoshuyui.strufusion.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoshuyui.strufusion.common.Result;
import org.xiaoshuyui.strufusion.service.KBFileService;
import org.xiaoshuyui.strufusion.util.DocReader;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@Slf4j
@RequestMapping("/file")
public class KBFileController {
    final KBFileService kbFileService;

    public KBFileController(KBFileService kbFileService) {
        this.kbFileService = kbFileService;
    }

    @PostMapping(value = "/{kbId}/upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadFile(
            @Parameter(description = "文件", required = true) @RequestPart("file") MultipartFile file,
            @PathVariable("kbId") Long kbId) {
        try {
            String res = kbFileService.extract(kbId, DocReader.extractText(file.getInputStream()));
            return Result.OK_data(res);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败 " + e.getMessage());
        }
    }
}
