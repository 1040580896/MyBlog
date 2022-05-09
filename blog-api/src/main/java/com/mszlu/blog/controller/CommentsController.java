package com.mszlu.blog.controller;

import com.mszlu.blog.service.CommentsService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author xiaokaixin
 * @Date 2021/8/18 15:38
 * @Version 1.0
 */

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    //接口url：/comments/article/{id}
    @GetMapping("article/{id}")
    public Result comments(@PathVariable("id") Long id){
        return commentsService.commentsByArticleId(id);
    }

    /**
     * 评论功能
     * @param commentParam
     * @return
     */
    @PostMapping("create/change")
    public Result comment(@RequestBody CommentParam commentParam,@RequestHeader("Authorization") String token){
        System.out.println(token);
        return commentsService.comment(commentParam);
    }
}
