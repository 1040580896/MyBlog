package com.mszlu.blog.controller;

import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author xiaokaixin
 * @Date 2021/8/15 08:16
 * @Version 1.0
 */

@RestController
@RequestMapping("tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    /**
     * 最热标签
     * @return
     */
    @GetMapping("hot")
    public Result hot(){
        int limit = 6;
        //int i = 10/0;
        return tagService.hots(limit);
    }

    /**
     * 所有标签
     * @return
     */
    @GetMapping()
    public Result findAll(){
        return tagService.findAll();
    }

    /**
     * 所有详细标签
     * @return
     */
    @GetMapping("detail")
    public Result findAllDetail(){
        return tagService.findAllDetail();
    }

    @GetMapping("detail/{id}")
    public Result findDetailById(@PathVariable("id") Long id){
        return tagService.findDetailById(id);
    }


}
