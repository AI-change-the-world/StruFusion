package org.xiaoshuyui.strufusion.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

import org.xiaoshuyui.strufusion.entity.KBCustomContent;

public interface KBCustomContentService extends IService<KBCustomContent> {
    List<KBCustomContent> getContentsByFileId(Long kbFileId);
}
