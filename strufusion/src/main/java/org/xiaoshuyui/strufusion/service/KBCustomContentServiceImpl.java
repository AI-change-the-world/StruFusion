package org.xiaoshuyui.strufusion.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
}
