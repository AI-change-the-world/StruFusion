package org.xiaoshuyui.strufusion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("kb_custom_content")
public class KBCustomContent {
  @TableId(value = "kb_custom_content_id", type = IdType.AUTO)
  Long id;

  @TableField("kb_id")
  Long kbId;

  @TableField("kb_file_id")
  Long kbFileId;

  @TableField("kb_custom_content_name")
  String name;

  @TableField("kb_custom_content_alias")
  String alias;

  @TableField("kb_custom_content_content")
  String content;

  @TableField(value = "updated_at")
  LocalDateTime updatedAt;

  @JsonIgnore
  @TableField(value = "is_deleted")
  Integer isDeleted;

  @TableField(value = "created_at")
  LocalDateTime createdAt;

  // str, list
  @TableField(value = "content_type")
  String contentType;

  // reference
  @TableField(value = "reference")
  String reference;
}
