package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.blog.dao.dos.Archives;
import com.mszlu.blog.dao.mapper.ArticleBodyMapper;
import com.mszlu.blog.dao.mapper.ArticleMapper;
import com.mszlu.blog.dao.mapper.ArticleTagMapper;
import com.mszlu.blog.dao.pojo.Article;
import com.mszlu.blog.dao.pojo.ArticleBody;
import com.mszlu.blog.dao.pojo.ArticleTag;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.*;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.ArticleBodyVo;
import com.mszlu.blog.vo.ArticleVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author xiaokaixin
 * @Date 2021/8/14 17:46
 * @Version 1.0
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleTagMapper articleTagMapper;



    // @Override
    // public Result listArticle(PageParams pageParams) {
    //     /**
    //      *  1.分页查询article数据库表
    //      */
    //     Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
    //     LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
    //     if(pageParams.getCategoryId()!=null){
    //         // and category_id =
    //         queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
    //     }
    //
    //     List<Long> articleIdList = new ArrayList<>();
    //     if(pageParams.getTagId()!=null){
    //         // 加入标签，条件查询
    //         //article表中，并没有tag字段，一遍文章，有多个标签
    //         //article_tag  article_id 1:n
    //         LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
    //         articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
    //         List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
    //         for(ArticleTag articleTag:articleTags){
    //             articleIdList.add(articleTag.getArticleId());
    //         }
    //         if (articleIdList.size() > 0){
    //             // and id in（）
    //             queryWrapper.in(Article::getId,articleIdList);
    //         }
    //
    //     }
    //     //是否置顶进行排序
    //     queryWrapper.orderByDesc(Article::getWeight);
    //     //order by create_date desc
    //     queryWrapper.orderByDesc(Article::getCreateDate);
    //     Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
    //     List<Article> records = articlePage.getRecords();
    //     //能直接返回吗
    //     List<ArticleVo> articleVoList = copyList(records,true,true);
    //     return Result.success(articleVoList);
    // }

    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = this.articleMapper.
                listArticle(
                        page,
                        pageParams.getCategoryId(),
                        pageParams.getTagId(),
                        pageParams.getYear(),
                        pageParams.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true));
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id,title from article order by view_counts desc limits 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id,title from article order by create_Date desc limits 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    /**
     * 文章归档
     * @return
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    @Autowired
    private ThreadService threadService;

    @Override
    public Result findArticleById(Long id) {
        /**
         * 根据id查询，文章信息
         * 根据bodyId和categoryId 去做关联查询
         */
        Article article = articleMapper.selectById(id);
        ArticleVo articleVo = copy(article,true,true,true,true);
        //查看完文章了，新增阅读次数
        //查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加写锁阻塞去他的读操作，性能就会比较低
        //更新了 增加了此次接口的 耗时 如果一旦更新出了问题，不能影响 查看文章的操作
        //线程池 可以把更新操作 扔到线程中去执行，和主线程不相关了
        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    @Override
    public Result publish(ArticleParam articleParam) {

        //这个接口 要加入到登陆拦截器中
        SysUser sysUser = UserThreadLocal.get();
        /**
         * 1.发布文章 目的 构建Article对象
         * 2.作者id 当前的登陆用户
         * 3.标签 要将标签加入  到关联列表当中
         * 4.body 内存存储 article bodyId
         */
        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setWeight(Article.Article_Common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        //插入之后会生成一个文章id
        this.articleMapper.insert(article);
        //tag
        List<TagVo> tags = articleParam.getTags();
        if(tags!=null){
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong( tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }

        }

        //body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);

        Map<String,String> map = new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }

    /**
     * 依次拷贝
     * @param records
     * @param isTag
     * @param isAuthor
     * @return
     */
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for(Article record: records){
            articleVoList.add(copy(record,isTag ,isAuthor,false,false));
        }
        return  articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for(Article record: records){
            articleVoList.add(copy(record,isTag ,isAuthor,isBody,isCategory));
        }
        return  articleVoList;
    }
    @Autowired
    private CategoryService categoryService;

    /**
     * 将类拷贝到另一个类，同时判断是否添加数据
     * 工具类：BeanUtils.copyProperties
     * @param article
     * @param isTag
     * @param isAuthor
     * @return
     */
    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory){
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(String.valueOf(article.getId().toString()));
        BeanUtils.copyProperties(article,articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有的接口，都需要标签,作者信息
        if(isTag){
            Long articleId = article.getId();

            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if(isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if(isCategory){
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;

    }
}
