package org.xiaoshuyui.strufusion.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xiaoshuyui.strufusion.common.SseResponse;
import org.xiaoshuyui.strufusion.entity.DataWithThink;
import org.xiaoshuyui.strufusion.entity.KB;
import org.xiaoshuyui.strufusion.entity.KBCustomContent;
import org.xiaoshuyui.strufusion.entity.KBFile;
import org.xiaoshuyui.strufusion.mapper.KBFileMapper;
import org.xiaoshuyui.strufusion.mapper.KBMapper;
import org.xiaoshuyui.strufusion.util.SseUtil;

@Service
public class KBFileService {
  private final KBFileMapper kbFileMapper;
  private final AiService aiService;
  private final KBMapper kbMapper;
  private final KBCustomContentServiceImpl kbCustomContentService;

  public KBFileService(
      KBFileMapper kbFileMapper,
      AiService aiService,
      KBMapper kbMapper,
      KBCustomContentServiceImpl kbCustomContentService) {
    this.kbFileMapper = kbFileMapper;
    this.aiService = aiService;
    this.kbMapper = kbMapper;
    this.kbCustomContentService = kbCustomContentService;
  }

  @Data
  static class RelatedKB {
    String name;
    Long id;
  }

  static String contentExtract = """
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

  // static String intentRec = """
  // 你是一个智能信息系统助手，当前正在处理一个名为「{{kb_name}}」的数据库，数据库的主要功能是：【{{kb_des}}】。

  // 你会收到一条用户的自然语言提问，以及该数据库支持的字段列表。字段以 JSON 数组形式给出，每个字段包含：
  // - 中文名（name）
  // - 英文别名（alias）
  // - 字段类型（type），可选值为 string 或 number

  // 你的任务是：
  // 1. 判断用户问题中涉及到的字段（可以是多个）
  // 2. 尝试从问题中提取出与这些字段匹配的具体值：
  // - 对于字符串类型（type = "string"）的字段，直接提取具体值（如“张三”、“Java”），若无明确值则设为 null
  // - 对于数值类型（type = "number"）的字段，提取数值和比较关系（如“高于3年经验”→ gt 3），若无具体值则设为 null

  // ---

  // 字段列表如下（JSON）：
  // {{fields_json}}

  // ---

  // 用户输入的问题是：
  // “{{user_input}}”

  // ---

  // 请你根据问题分析后，输出相关字段，每行一个，格式如下：

  // 字段中文名；alias；类型；比较关系（string 类型为 eq 或 null）；值（可以为 null）

  // 例如：
  // 候选人姓名；name；string；eq；张三
  // 技能专长；skills；string；eq；Java
  // 工作经验；experience；number；gt；3
  // 期望薪资；expected_salary；number；null；null

  // 如果没有任何匹配字段，请仅输出一行：
  // 无

  // ---

  // **注意事项**：
  // - 如果问题中包含“查询目标”（如某人的姓名、某个编号），请务必返回该字段，类型为 string，比较关系为 eq
  // - 如果字段是 number 类型，且用户提问中包含了范围/比较（如“大于”、“小于”、“至少”、“最多”等），请正确提取为 gt、lt、ge、le、eq
  // 等常见逻辑关系；如果无比较词则设为 null
  // - 每行只输出一条结构化记录，不要换行、不嵌套、不解释
  // """;

  static String intentRec = """
      你是一个智能信息系统助手，当前正在处理一个名为「{{kb_name}}」的数据库，数据库的主要功能是：【{{kb_des}}】。

      你会收到一条用户的自然语言提问，以及该数据库支持的字段列表。字段以 JSON 数组形式给出，每个字段包含：
      - 中文名（name）
      - 英文别名（alias）
      - 字段类型（type），可选值为 string 或 number

      你的任务是：
      1. 判断用户问题中涉及到的字段（可以是多个）
      2. 尝试从问题中提取出与这些字段匹配的具体值：
         - 对于字符串类型（type = "string"）的字段，直接提取具体值（如“张三”、“Java”），若无明确值则设为 null
         - 对于数值类型（type = "number"）的字段，提取数值和比较关系（如“高于3年经验”→ gt 3），若无具体值则设为 null

      ---

      字段列表如下（JSON）：
      {{fields_json}}

      ---

      用户输入的问题是：
      “{{user_input}}”

      ---

      请你根据问题分析后，输出相关字段，每行一个，格式如下：

      字段中文名；alias；类型；比较关系（string 类型为 eq 或 null）；值（可以为 null）

      例如：
      候选人姓名；name；string；eq；张三
      技能专长；skills；string；eq；Java
      工作经验；experience；number；gt；3
      期望薪资；expected_salary；number；null；null

      如果没有任何匹配字段，请仅输出一行：
      无

      ---

      **注意事项**：
      - 如果问题中包含“查询目标”（如某人的姓名、某个编号），请务必返回该字段，类型为 string，比较关系为 eq
      - 如果字段是 number 类型，且用户提问中包含了范围/比较（如“大于”、“小于”、“至少”、“最多”等），请正确提取为 gt、lt、ge、le、eq 等常见逻辑关系；如果无比较词则设为 null
      - 每行只输出一条结构化记录，不要换行、不嵌套、不解释
                                    """;

  static String chatPrompt = """
      你是一个智能信息系统的助手，当前正在处理一个{{kb_name}}数据库,数据库主要功能是【{{kb_des}}】。

      以下是用户的问题：
      【{{question}}】

      系统从数据库中查找到的相关内容如下（字段：内容）：
      {{content}}

      请根据这些信息，用简洁准确的语言回答用户的问题。如果信息不足，请直接说明。
            """;

  static String relatedKbPrompt = """
      你是一个智能问答系统助手，当前系统中包含多个知识库，每个知识库都有编号、名称和简要描述。

      用户将提出一个问题，你需要根据问题内容判断与之最相关的**一个**知识库（最多一个）。

      知识库列表如下（Markdown 表格格式）：

      {{kb_markdown}}

      ---

      请你判断用户的问题最主要涉及哪个知识库，并返回该知识库的编号和名称。

      输出格式如下（只返回一行）：
      编号; 知识库名称

      例如：
      1; 人才简历库

      如果没有任何匹配的知识库，请返回：
      无

      ---

      用户输入的问题是：
      “{{user_input}}”
            """;

  public String intentRecognition(String userInput, Long kbId) {
    QueryWrapper<KB> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("kb_id", kbId);
    queryWrapper.eq("is_deleted", 0);
    var kb = kbMapper.selectOne(queryWrapper);
    if (kb == null) {
      return "无";
    }
    return aiService.chat(
        intentRec
            .replace("{{fields_json}}", kb.getPoints())
            .replace("{{user_input}}", userInput)
            .replace("{{kb_name}}", kb.getName())
            .replace("{{kb_des}}", kb.getDescription()));
  }

  public String intentRecognition(String userInput, KB kb) {
    return aiService.chat(
        intentRec
            .replace("{{fields_json}}", kb.getPoints())
            .replace("{{user_input}}", userInput)
            .replace("{{kb_name}}", kb.getName())
            .replace("{{kb_des}}", kb.getDescription()));
  }

  public void streamChat(String message, SseEmitter emitter, SseResponse<DataWithThink> response) {

    DataWithThink dataWithThink = new DataWithThink();
    QueryWrapper<KB> kbQueryWrapper = new QueryWrapper<>();
    kbQueryWrapper.eq("is_deleted", 0);
    // 知识库路由
    List<KB> kbs = kbMapper.selectList(kbQueryWrapper);
    if (kbs == null || kbs.isEmpty()) {
      response.setMessage("无知识库匹配。");
      dataWithThink.setData("无知识库匹配。");
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }

    String releatedKbString = kbToMarkdown(kbs);
    dataWithThink.setThink("1. 匹配最恰当的知识库...\n");
    response.setData(dataWithThink);
    SseUtil.sseSend(emitter, response);
    String processedReleatedString = aiService.chat(
        relatedKbPrompt
            .replace("{{kb_markdown}}", releatedKbString)
            .replace("{{user_input}}", message));

    List<RelatedKB> relatedKBs = parseRelatedKB(processedReleatedString);
    if (relatedKBs == null || relatedKBs.isEmpty()) {
      response.setMessage("无最恰当的知识库，请重新再问...");
      dataWithThink.setData("无最恰当的知识库，请重新再问...");
      response.setData(dataWithThink);
      SseUtil.sseSend(emitter, response);
      return;
    }

    dataWithThink.setThink("最匹配的知识库为： " + processedReleatedString + "\n");
    response.setData(dataWithThink);
    SseUtil.sseSend(emitter, response);

    RelatedKB relatedKB = relatedKBs.get(0);

    response.setMessage("正在进行意图识别...");
    dataWithThink.setThink("2. 正在进行意图识别...\n");
    response.setData(dataWithThink);
    SseUtil.sseSend(emitter, response);
    QueryWrapper<KB> qw = new QueryWrapper<>();
    qw.eq("kb_id", relatedKB.getId());
    qw.eq("is_deleted", 0);
    var kb = kbMapper.selectOne(qw);
    if (kb == null) {
      response.setMessage("未找到知识库。");
      dataWithThink.setData("未找到知识库。");
      response.setData(dataWithThink);
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }

    String intent = intentRecognition(message, kb);
    dataWithThink.setThink("意图识别结果为： " + intent + "\n");
    response.setData(dataWithThink);
    SseUtil.sseSend(emitter, response);
    if (intent.equals("无")) {
      response.setMessage("无效的意图，流程结束。");
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }
    dataWithThink.setThink("3. 进行内容提取...\n");
    response.setMessage("进行内容提取...");
    SseUtil.sseSend(emitter, response);
    List<Map<String, Object>> fieldMap = parseFieldsFromModelOutput(intent);
    dataWithThink.setThink("查询字段包括:" + fieldMap + "\n");
    SseUtil.sseSend(emitter, response);
    response.setData(dataWithThink);
    if (fieldMap.isEmpty()) {
      response.setMessage("无匹配字段，流程结束。");
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }

    /// TODO 是否增加一个权重字段，用于增强检索性能
    // boolean hasThemeFilter = false;

    // for (Map<String, Object> field : fieldMap) {
    // if ("contract_type".equals(field.get("alias")) && field.get("content") !=
    // null) {
    // hasThemeFilter = true;
    // break;
    // }
    // }

    QueryWrapper<KBCustomContent> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("kb_id", kb.getId());

    // 外层包一层 .and()，用于逻辑分组（可选）
    queryWrapper.and(
        wrapper -> {
          for (Map<String, Object> field : fieldMap) {
            wrapper.or(
                orWrapper -> {
                  orWrapper
                      .eq("kb_custom_content_name", field.get("name"))
                      .eq("kb_custom_content_alias", field.get("alias"));

                  Object content = field.get("content");
                  String type = (String) field.get("type");
                  String op = (String) field.get("op");

                  if (content != null && "number".equalsIgnoreCase(type) && op != null) {
                    String contentStr = content.toString();
                    switch (op) {
                      case "eq":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) = {0}", contentStr);
                        break;
                      case "gt":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) > {0}", contentStr);
                        break;
                      case "lt":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) < {0}", contentStr);
                        break;
                      case "ge":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) >= {0}", contentStr);
                        break;
                      case "le":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) <= {0}", contentStr);
                        break;
                    }
                  }

                  // string 类型：不加 content 限制，扩大召回
                  if (content != null && "string".equalsIgnoreCase(type)) {
                    orWrapper.like("kb_custom_content_content", content);
                  }
                });
          }
        });
    var kbCustomContentList = kbCustomContentService.list(queryWrapper);
    if (kbCustomContentList.isEmpty()) {
      response.setMessage("无匹配内容，流程结束。");
      dataWithThink.setData("无匹配内容，流程结束。");
      dataWithThink.setThink("");
      response.setData(dataWithThink);
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }

    Set<Long> ids = new HashSet<>();
    for (var kbCustomContent : kbCustomContentList) {
      ids.add(kbCustomContent.getKbFileId());
    }

    List<Long> rangeIds = ids.stream().map(Long::valueOf).collect(Collectors.toList());

    QueryWrapper<KBCustomContent> contentQueryWrapper = new QueryWrapper<>();
    contentQueryWrapper.in("kb_file_id", rangeIds);
    List<KBCustomContent> allContents = kbCustomContentService.list(contentQueryWrapper);

    String content = toMarkdownTable(allContents);

    dataWithThink.setThink("匹配的结果为:\n" + content + "\n");
    response.setData(dataWithThink);

    String prompt = chatPrompt
        .replace("{{kb_name}}", kb.getName())
        .replace("{{kb_des}}", kb.getDescription())
        .replace("{{question}}", message)
        .replace("{{content}}", content);
    response.setMessage("内容提取完成，正在生成回答...");
    SseUtil.sseSend(emitter, response);
    aiService
        .streamChat(prompt)
        .doOnNext(
            data -> {
              dataWithThink.setData(data);
              dataWithThink.setThink("");
              response.setData(dataWithThink);
              SseUtil.sseSend(emitter, response);
            })
        .blockLast();
  }

  @Deprecated
  public void streamChat(
      String message, Long kbId, SseEmitter emitter, SseResponse<DataWithThink> response) {
    DataWithThink dataWithThink = new DataWithThink();

    response.setMessage("正在进行意图识别...");
    dataWithThink.setThink("正在进行意图识别...\n");
    response.setData(dataWithThink);
    SseUtil.sseSend(emitter, response);
    QueryWrapper<KB> qw = new QueryWrapper<>();
    qw.eq("kb_id", kbId);
    qw.eq("is_deleted", 0);
    var kb = kbMapper.selectOne(qw);
    if (kb == null) {
      response.setMessage("未找到知识库。");
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }

    String intent = intentRecognition(message, kb);
    dataWithThink.setThink(intent + "\n");
    response.setData(dataWithThink);
    if (intent.equals("无")) {
      response.setMessage("无效的意图，流程结束。");
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }
    response.setMessage("意图识别完成，结果为" + intent + ", 正在进行内容提取...");
    SseUtil.sseSend(emitter, response);
    List<Map<String, Object>> fieldMap = parseFieldsFromModelOutput(intent);
    dataWithThink.setThink("查询字段包括:\n" + fieldMap + "\n");
    response.setData(dataWithThink);
    if (fieldMap.isEmpty()) {
      response.setMessage("无匹配字段，流程结束。");
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }

    QueryWrapper<KBCustomContent> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("kb_id", kbId);
    // for (var field : fieldMap) {
    // queryWrapper.eq("kb_custom_content_name", field.get("name"));
    // queryWrapper.eq("kb_custom_content_alias", field.get("alias"));
    // if (!field.get("content").equals(null)) {
    // queryWrapper.like("kb_custom_content_content", field.get("content"));
    // }
    // }
    // 外层包一层 .and()，用于逻辑分组（可选）
    queryWrapper.and(
        wrapper -> {
          for (Map<String, Object> field : fieldMap) {
            wrapper.or(
                orWrapper -> {
                  orWrapper
                      .eq("kb_custom_content_name", field.get("name"))
                      .eq("kb_custom_content_alias", field.get("alias"));

                  Object content = field.get("content");
                  String type = (String) field.get("type");
                  String op = (String) field.get("op");

                  if (content != null && "number".equalsIgnoreCase(type) && op != null) {
                    String contentStr = content.toString();
                    switch (op) {
                      case "eq":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) = {0}", contentStr);
                        break;
                      case "gt":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) > {0}", contentStr);
                        break;
                      case "lt":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) < {0}", contentStr);
                        break;
                      case "ge":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) >= {0}", contentStr);
                        break;
                      case "le":
                        orWrapper.apply(
                            "CAST(kb_custom_content_content AS DECIMAL) <= {0}", contentStr);
                        break;
                    }
                  }
                  // string 类型：不加 content 限制，扩大召回
                  if (content != null && "string".equalsIgnoreCase(type)) {
                    orWrapper.like("kb_custom_content_content", content);
                  }
                });
          }
        });
    var kbCustomContentList = kbCustomContentService.list(queryWrapper);
    if (kbCustomContentList.isEmpty()) {
      response.setMessage("无匹配内容，流程结束。");
      response.setDone(true);
      SseUtil.sseSend(emitter, response);
      return;
    }

    Set<Long> ids = new HashSet<>();
    for (var kbCustomContent : kbCustomContentList) {
      ids.add(kbCustomContent.getKbFileId());
    }

    List<Long> rangeIds = ids.stream().map(Long::valueOf).collect(Collectors.toList());

    QueryWrapper<KBCustomContent> contentQueryWrapper = new QueryWrapper<>();
    contentQueryWrapper.in("kb_file_id", rangeIds);
    List<KBCustomContent> allContents = kbCustomContentService.list(contentQueryWrapper);

    String content = toMarkdownTable(allContents);

    dataWithThink.setThink("匹配的结果为:\n" + content + "\n");
    response.setData(dataWithThink);

    String prompt = chatPrompt
        .replace("{{kb_name}}", kb.getName())
        .replace("{{kb_des}}", kb.getDescription())
        .replace("{{question}}", message)
        .replace("{{content}}", content);
    response.setMessage("内容提取完成，正在生成回答...");
    SseUtil.sseSend(emitter, response);
    aiService
        .streamChat(prompt)
        .doOnNext(
            data -> {
              dataWithThink.setData(data);
              dataWithThink.setThink("");
              response.setData(dataWithThink);
              SseUtil.sseSend(emitter, response);
            })
        .blockLast();
  }

  private String toMarkdownTable(List<KBCustomContent> dataList) {
    // Step 1: 聚合所有列名（kb_custom_content_name）
    Set<String> columnSet = new LinkedHashSet<>();
    for (KBCustomContent item : dataList) {
      columnSet.add(item.getName());
    }
    List<String> columns = new ArrayList<>(columnSet);

    // Step 2: 按 id 聚合成 Map<id, Map<name, content>>
    Map<Long, Map<String, String>> rowMap = new LinkedHashMap<>();
    for (KBCustomContent item : dataList) {
      rowMap
          .computeIfAbsent(item.getKbFileId(), k -> new HashMap<>())
          .put(item.getName(), item.getContent());
    }

    // Step 3: 构建 Markdown 表格
    StringBuilder sb = new StringBuilder();

    // Header
    sb.append("|");
    for (String col : columns) {
      sb.append(" ").append(col).append(" |");
    }
    sb.append("\n|");
    for (int i = 0; i < columns.size(); i++) {
      sb.append("------|");
    }

    // Rows
    for (Map<String, String> row : rowMap.values()) {
      sb.append("\n|");
      for (String col : columns) {
        String cell = row.getOrDefault(col, "");
        sb.append(" ").append(cell).append(" |");
      }
    }

    return sb.toString();
  }

  private List<Map<String, Object>> parseFieldsFromModelOutput(String modelOutput) {
    List<Map<String, Object>> result = new ArrayList<>();
    String[] lines = modelOutput.split("\\r?\\n");

    for (String line : lines) {
      // 跳过空行或“无”
      if (line.trim().isEmpty() || line.trim().equalsIgnoreCase("无")) {
        continue;
      }

      String[] parts = line.split("；");
      if (parts.length >= 5) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("name", parts[0].trim());
        fieldMap.put("alias", parts[1].trim());
        fieldMap.put("type", parts[2].trim().toLowerCase());

        String op = parts[3].trim().toLowerCase();
        fieldMap.put("op", op.equals("null") ? null : op);

        String content = parts[4].trim();
        fieldMap.put("content", content.equalsIgnoreCase("null") ? null : content);

        result.add(fieldMap);
      }
    }

    return result;
  }

  public String extract(String text, String fieldsJson) {
    String prompt = contentExtract.replace("{{fields_json}}", fieldsJson).replace("{{text}}", text);
    return aiService.chat(prompt);
  }

  @Transactional
  public void extract(Long kbId, String fileContent, String filename) throws Exception {
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
    KBFile kbFile = new KBFile();
    kbFile.setContent(fileContent);
    kbFile.setKbId(kbId);
    kbFile.setName(filename);

    kbFileMapper.insert(kbFile);

    String result = extract(fileContent, kb.getPoints());

    List<KBCustomContent> customContentList = parseToCustomContent(result, kbFile.getId(), kbId);

    kbCustomContentService.saveBatch(customContentList);
  }

  static Pattern pattern = Pattern.compile("^(.+?)；(.+?)；(.+)$", Pattern.MULTILINE);

  private List<KBCustomContent> parseToCustomContent(String inputText, long kbFileId, long kbId) {
    List<KBCustomContent> resultList = new ArrayList<>();

    Matcher matcher = pattern.matcher(inputText);

    while (matcher.find()) {
      String name = matcher.group(1).trim();
      String alias = matcher.group(2).trim();
      String content = matcher.group(3).trim();

      KBCustomContent contentObj = new KBCustomContent();
      contentObj.setName(name);
      contentObj.setAlias(alias);
      contentObj.setContent(content);
      contentObj.setKbId(kbId);
      contentObj.setKbFileId(kbFileId);

      resultList.add(contentObj);
    }

    return resultList;
  }

  public List<KBFile> list(Long kbId) {
    QueryWrapper<KBFile> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("kb_id", kbId);
    queryWrapper.eq("is_deleted", 0);

    return kbFileMapper.selectList(queryWrapper);
  }

  private String kbToMarkdown(List<KB> kbs) {
    StringBuilder sb = new StringBuilder();
    sb.append("|编号|名称｜描述|\n");
    sb.append("|:---|:---|:---|\n");
    for (KB kb : kbs) {
      sb.append("|" + kb.getId() + "|" + kb.getName() + "|" + kb.getDescription() + "|");
    }
    return sb.toString();
  }

  private List<RelatedKB> parseRelatedKB(String modelOutput) {
    List<RelatedKB> result = new ArrayList<>();
    if (modelOutput == null || modelOutput.trim().equalsIgnoreCase("无")) {
      return result; // 返回空列表
    }

    String[] lines = modelOutput.split("\\r?\\n");
    for (String line : lines) {
      String[] parts = line.split(";", 2);
      if (parts.length == 2) {
        try {
          Long id = Long.parseLong(parts[0].trim());
          String name = parts[1].trim();
          RelatedKB kb = new RelatedKB();
          kb.setId(id);
          kb.setName(name);
          result.add(kb);
        } catch (NumberFormatException e) {
          // 忽略非法行
          System.err.println("解析失败的行: " + line);
        }
      }
    }

    return result;
  }
}
