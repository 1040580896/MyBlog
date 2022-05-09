package com.mszlu.blog.service;

import com.mszlu.blog.vo.CategoryVo;
import com.mszlu.blog.vo.Result;

/**
 * @Author xiaokaixin
 * @Date 2021/8/18 07:49
 * @Version 1.0
 */
public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    /**
     * 查找所有类别
     * @return
     */
    Result findAll();

    Result findAllDetail();

    Result categoryDetailById(Long id);
}
