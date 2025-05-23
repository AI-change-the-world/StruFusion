package org.xiaoshuyui.strufusion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Data;

@TableName("kb")
@Data
public class KB {
  @TableId(value = "kb_id", type = IdType.AUTO)
  Long id;

  @TableField("kb_name")
  String name;

  @TableField("kb_description")
  String description;

  @TableField(value = "updated_at")
  LocalDateTime updatedAt;

  @JsonIgnore
  @TableField(value = "is_deleted")
  Integer isDeleted;

  @TableField(value = "created_at")
  LocalDateTime createdAt;

  /*
   * [
   * { "name" : "point1",
   * "alias" "point1_alias"
   * }
   * ]
   */
  @TableField(value = "kb_points")
  String points;

}
