package com.dimple.blog.service.service;

import com.dimple.blog.api.bo.BlogArticleTagBO;

import java.util.List;


/**
 * Service接口
 *
 * @author Dimple
 * @date 2023-02-13
 */
public interface BlogArticleTagService {
    /**
     * 查询
     *
     * @param id 主键
     * @return
     */
    BlogArticleTagBO selectBlogArticleTagById(Long id);

    List<BlogArticleTagBO> selectBlogArticleTagByArticleId(Long article);

    /**
     * 查询列表
     *
     * @param blogArticleTag
     * @return 集合
     */
    List<BlogArticleTagBO> selectBlogArticleTagList(BlogArticleTagBO blogArticleTag);

    /**
     * 新增
     *
     * @param blogArticleTag
     * @return affected lines
     */
    int insertBlogArticleTag(BlogArticleTagBO blogArticleTag);

    /**
     * 修改
     *
     * @param blogArticleTag
     * @return affected lines
     */
    int updateBlogArticleTag(BlogArticleTagBO blogArticleTag);

    /**
     * 批量删除
     *
     * @param ids 需要删除的主键集合
     * @return affected lines
     */
    int deleteBlogArticleTagByIds(List<Long> ids);

    /**
     * 删除信息
     *
     * @param id 主键
     * @return affected lines
     */
    int deleteBlogArticleTagById(Long id);
}
