package org.xiaoshuyui.strufusion.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

public class DocReader {

  /**
   * 读取 文件正文内容
   *
   * @param inputStream 文件流
   * @return 正文文本
   * @throws IOException 读取失败时抛出
   */
  public static String extractText(InputStream inputStream) throws Exception {

    BodyContentHandler handler = new BodyContentHandler(-1); // no limit
    Metadata metadata = new Metadata();
    AutoDetectParser parser = new AutoDetectParser();
    ParseContext context = new ParseContext();

    parser.parse(inputStream, handler, metadata, context);

    return handler.toString();
  }
}
