package com.mszlu.blog.service;

import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParam;

/**
 * @Author xiaokaixin
 * @Date 2021/8/18 15:41
 * @Version 1.0
 */
public interface CommentsService {

    /**
     * 根据文章id查询所有文章评论列表
     * @param id
     * @return
     */
    Result commentsByArticleId(Long id);

    /**
     * 评论
     * @param commentParam
     * @return
     */
    Result comment(CommentParam commentParam);
}
