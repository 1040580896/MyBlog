package com.mszlu.blog.controller;

import com.mszlu.blog.common.aop.LogAnnotation;
import com.mszlu.blog.common.cache.Cache;
import com.mszlu.blog.service.ArticleService;
import com.mszlu.blog.vo.ArticleVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.*;

/**
 * @Author xiaokaixin
 * @Date 2021/8/14 17:35
 * @Version 1.0
 */
@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    /**
     * 首页  文字列表展示
     * @param pageParams
     * @return
     */
    @PostMapping
    //加上此注解，代表要对此接口记录日志
    @LogAnnotation(module="文章",operator="获取文章列表")
    public Result listArticle(@RequestBody PageParams pageParams){

        return articleService.listArticle(pageParams);
    }

    /**
     * 首页最热文章
     * @return
     */
    @PostMapping("hot")
    @Cache(expire = 5 * 60 * 1000,name = "hot_article")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    /**
     * 首页 最新文章
     * @return
     */
    @PostMapping("new")
    @Cache(expire = 5 * 60 * 1000,name = "new_article")
    public Result newArticles(){
        int limit = 5;
        return articleService.newArticles(limit);
    }
    /**
     * 首页 文章归档
     * @return
     */
    @Cache(expire = 5 * 60 * 1000,name = "list_article")
    @PostMapping("listArchives")
    public Result listArchives(){

        return articleService.listArchives();
    }

    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long id) {

        return articleService.findArticleById(id);
    }

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }
}
