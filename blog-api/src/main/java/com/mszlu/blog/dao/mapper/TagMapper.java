package com.mszlu.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mszlu.blog.dao.pojo.Tag;

import java.util.List;

/**
 * @Author xiaokaixin
 * @Date 2021/8/14 17:34
 * @Version 1.0
 */
public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据文章id查旋文章标签列表
     * @param articleId
     * @return
     */
    List<Tag> findTagsByArticleId(Long articleId);

    List<Long> findHotsTagIds(int limit);

    List<Tag> findTagsByTagIds(List<Long> tagIds);

}
