package org.xiaoshuyui.strufusion.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.xiaoshuyui.strufusion.entity.KBCustomContent;
import org.xiaoshuyui.strufusion.mapper.KBCustomContentMapper;

@Service
public class KBCustomContentServiceImpl extends ServiceImpl<KBCustomContentMapper, KBCustomContent>
    implements KBCustomContentService {

  final KBCustomContentMapper kbCustomContentMapper;

  public KBCustomContentServiceImpl(KBCustomContentMapper kbCustomContentMapper) {
    this.kbCustomContentMapper = kbCustomContentMapper;
  }

  public List<KBCustomContent> getContentsByFileId(Long kbFileId) {
    QueryWrapper<KBCustomContent> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("kb_file_id", kbFileId);
    queryWrapper.eq("is_deleted", 0);
    return kbCustomContentMapper.selectList(queryWrapper);
  }
}
