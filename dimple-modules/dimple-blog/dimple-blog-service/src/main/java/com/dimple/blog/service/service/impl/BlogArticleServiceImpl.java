package com.dimple.blog.service.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.dimple.blog.api.bo.BlogArticleBO;
import com.dimple.blog.api.bo.BlogArticleTagBO;
import com.dimple.blog.api.bo.BlogTagBO;
import com.dimple.blog.api.bo.KeyValue;
import com.dimple.blog.service.entity.BlogArticle;
import com.dimple.blog.service.mapper.BlogArticleMapper;
import com.dimple.blog.service.service.BlogArticleService;
import com.dimple.blog.service.service.BlogArticleTagService;
import com.dimple.blog.service.service.BlogTagService;
import com.dimple.common.core.utils.DateUtils;
import com.dimple.common.core.utils.bean.BeanMapper;
import com.dimple.common.core.web.vo.params.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Blog articleService业务层处理
 *
 * @author Dimple
 * @date 2023-02-13
 */
@Service
@Slf4j
public class BlogArticleServiceImpl implements BlogArticleService {
    @Autowired
    private BlogArticleMapper blogArticleMapper;
    @Autowired
    private BlogTagService blogTagService;
    @Autowired
    private BlogArticleTagService blogArticleTagService;

    @Override
    public BlogArticleBO selectBlogArticleById(Long id) {
        BlogArticleBO blogArticleBO = BeanMapper.convert(blogArticleMapper.selectBlogArticleDetailById(id), BlogArticleBO.class);
        fillArticleTags(blogArticleBO);
        return blogArticleBO;
    }

    @Override
    public List<BlogArticleBO> selectBlogArticleByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        List<BlogArticle> blogArticles = blogArticleMapper.selectBlogArticleByIds(ids);
        return BeanMapper.convertList(blogArticles, BlogArticleBO.class);
    }

    @Override
    public BlogArticleBO selectBlogArticleDetailById(Long id) {
        BlogArticleBO blogArticleBO = BeanMapper.convert(blogArticleMapper.selectBlogArticleDetailById(id), BlogArticleBO.class);
        fillArticleTags(blogArticleBO);
        return blogArticleBO;
    }

    private void fillArticleTags(BlogArticleBO blogArticleBO) {
        List<BlogArticleTagBO> blogArticleTagBOS = blogArticleTagService.selectBlogArticleTagByArticleId(blogArticleBO.getId());
        List<Long> tagIds = blogArticleTagBOS.stream().map(BlogArticleTagBO::getTagId).collect(Collectors.toList());
        List<String> blogTagBOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tagIds)) {
            blogTagBOS = blogTagService.selectBlogTagByIds(tagIds).stream().map(BlogTagBO::getTitle).collect(Collectors.toList());
        }
        blogArticleBO.setTags(blogTagBOS);
    }

    @Override
    public List<BlogArticleBO> selectBlogArticleList(BlogArticleBO blogArticleBO) {
        BlogArticle blogArticle = BeanMapper.convert(blogArticleBO, BlogArticle.class);
        List<BlogArticle> blogArticles = blogArticleMapper.selectBlogArticleList(blogArticle);
        return BeanMapper.convertList(blogArticles, BlogArticleBO.class);
    }

    @Override
    @Transactional
    public Long insertBlogArticle(BlogArticleBO blogArticleBO) {
        BlogArticle blogArticle = BeanMapper.convert(blogArticleBO, BlogArticle.class);
        blogArticle.setCreateTime(DateUtils.getNowDate());
        blogArticleMapper.insertBlogArticle(blogArticle);
        Long articleId = blogArticle.getId();
        saveBlogTags(blogArticleBO.getTags(), articleId);
        return articleId;
    }

    private void saveBlogTags(List<String> blogTags, Long articleId) {
        if (CollectionUtils.isEmpty(blogTags)) {
            log.warn("Current blog article no tags, just ignore save blog tags.");
            return;
        }
        // remove all mapping
        deleteArticleTagMapping(Arrays.asList(articleId));
        Map<String, Long> alreadyExistedTagsMap = blogTagService.selectBlogTagByTitles(blogTags).stream().collect(Collectors.toMap(e -> e.getTitle(), e -> e.getId()));
        // add new mapping
        for (String blogTag : blogTags) {
            Long tagId = alreadyExistedTagsMap.get(blogTag);
            if (Objects.isNull(tagId)) {
                tagId = blogTagService.insertBlogTag(new BlogTagBO(blogTag));
            }
            // save mapping
            BlogArticleTagBO blogArticleTagBO = new BlogArticleTagBO();
            blogArticleTagBO.setArticleId(articleId);
            blogArticleTagBO.setTagId(tagId);
            blogArticleTagService.insertBlogArticleTag(blogArticleTagBO);
        }
    }

    @Override
    public int updateBlogArticle(BlogArticleBO blogArticleBO) {
        BlogArticle blogArticle = BeanMapper.convert(blogArticleBO, BlogArticle.class);
        blogArticle.setUpdateTime(DateUtils.getNowDate());
        saveBlogTags(blogArticleBO.getTags(), blogArticleBO.getId());
        return blogArticleMapper.updateBlogArticle(blogArticle);
    }

    @Override
    public int deleteBlogArticleByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return AjaxResult.AFFECTED_ROW_FAIL;
        }
        deleteArticleTagMapping(ids);
        return blogArticleMapper.deleteBlogArticleByIds(ids);
    }

    private void deleteArticleTagMapping(List<Long> ids) {
        // delete tag mapping
        BlogArticleTagBO searchArticleTagMapping = new BlogArticleTagBO();
        List<Long> needRemoveRowIds = new ArrayList<>();
        for (Long articleId : ids) {
            searchArticleTagMapping.setArticleId(articleId);
            needRemoveRowIds.addAll(blogArticleTagService.selectBlogArticleTagList(searchArticleTagMapping).stream()
                    .map(BlogArticleTagBO::getId).collect(Collectors.toList()));
        }
        if (CollectionUtils.isEmpty(needRemoveRowIds)) {
            return;
        }
        blogArticleTagService.deleteBlogArticleTagByIds(needRemoveRowIds);
    }

    @Override
    public int deleteBlogArticleById(Long id) {
        return blogArticleMapper.deleteBlogArticleById(id);
    }

    @Override
    public int updateBlogArticleStatus(Long id, Integer articleStatus) {
        BlogArticle blogArticle = blogArticleMapper.selectBlogArticleById(id);
        if (Objects.isNull(blogArticle)) {
            return AjaxResult.AFFECTED_ROW_FAIL;
        }
        blogArticle.setArticleStatus(articleStatus);
        return blogArticleMapper.updateBlogArticle(blogArticle);
    }

    @Override
    public List<BlogArticleBO> selectBlogArticlePrevNext(Long id) {
        return BeanMapper.convertList(blogArticleMapper.selectBlogArticlePrevNext(id), BlogArticleBO.class);
    }

    @Override
    public List<BlogArticleBO> selectBlogArticleByTagId(Long tagId) {
        return BeanMapper.convertList(blogArticleMapper.selectBlogArticleByTagId(tagId), BlogArticleBO.class);
    }

    @Override
    public int likeArticle(Long articleId) {
        return blogArticleMapper.likeArticle(articleId);
    }

    @Override
    public List<KeyValue<Long, Long>> getPvByArticleId(Collection<Long> ids) {
        return blogArticleMapper.getPvByArticleId(ids);
    }

    @Override
    public List<KeyValue<Long, Long>> selectBlogArticleCountByCategoryIds(Set<Long> categoryIds) {
        return blogArticleMapper.selectBlogArticleCountByCategoryIds(categoryIds);
    }

}
