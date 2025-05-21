package org.xiaoshuyui.strufusion.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.xiaoshuyui.strufusion.entity.KB;
import org.xiaoshuyui.strufusion.mapper.KBFileMapper;
import org.xiaoshuyui.strufusion.mapper.KBMapper;

@Service
public class KBFileService {
  private final KBFileMapper kbFileMapper;
  private final AiService aiService;
  private final KBMapper kbMapper;

  public KBFileService(KBFileMapper kbFileMapper, AiService aiService, KBMapper kbMapper) {
    this.kbFileMapper = kbFileMapper;
    this.aiService = aiService;
    this.kbMapper = kbMapper;
  }

  static String contentExtract =
      """
            你是信息抽取助手。

            下面给你一段字段定义，格式是 JSON 数组，数组里每个对象包含两个字段：
            "name" 是字段中文名，
            "alias" 是字段英文名。

            字段定义：
            {{fields_json}}

            请你根据字段定义，从下面这段文本中抽取对应字段的内容：

            文本：
            {{text}}

            请输出提取结果，每行格式为：
            字段中文名；字段英文名；对应内容

            如果文本中没有对应字段的内容，输出“无”。

            ---

            示例：
            字段定义：
            [
              {"name": "姓名", "alias": "name"},
              {"name": "年龄", "alias": "age"}
            ]

            文本：
            张三，年龄28岁。

            输出：
            姓名；name；张三
            年龄；age；28岁
                        """;

  public String extract(String text, String fieldsJson) {
    String prompt = contentExtract.replace("{{fields_json}}", fieldsJson).replace("{{text}}", text);
    return aiService.chat(prompt);
  }

  public String extract(Long kbId, String fileContent) throws Exception {
    QueryWrapper<KB> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("kb_id", kbId);
    queryWrapper.eq("is_deleted", 0);
    var kb = kbMapper.selectOne(queryWrapper);
    if (kb == null) {
      throw new Exception("知识库不存在");
    }
    if (kb.getPoints() == null) {
      throw new Exception("知识库没有定义字段");
    }

    return extract(fileContent, kb.getPoints());
  }
}
