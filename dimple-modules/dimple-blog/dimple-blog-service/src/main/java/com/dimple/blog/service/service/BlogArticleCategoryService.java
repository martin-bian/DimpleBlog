package com.dimple.blog.service.service;

import com.dimple.blog.api.bo.BlogArticleCategoryBO;

import java.util.List;


/**
 * Service接口
 *
 * @author Dimple
 * @date 2023-02-13
 */
public interface BlogArticleCategoryService {
    /**
     * 查询
     *
     * @param id 主键
     * @return 
     */
    BlogArticleCategoryBO selectBlogArticleCategoryById(Long id);

    /**
     * 查询列表
     *
     * @param blogArticleCategoryBO 
     * @return 集合
     */
    List<BlogArticleCategoryBO> selectBlogArticleCategoryList(BlogArticleCategoryBO blogArticleCategoryBO);

    /**
     * 新增
     *
     * @param blogArticleCategoryBO 
     * @return affected lines
     */
    int insertBlogArticleCategory(BlogArticleCategoryBO blogArticleCategoryBO);

    /**
     * 修改
     *
     * @param blogArticleCategoryBO 
     * @return affected lines
     */
    int updateBlogArticleCategory(BlogArticleCategoryBO blogArticleCategoryBO);

    /**
     * 批量删除
     *
     * @param ids 需要删除的主键集合
     * @return affected lines
     */
    int deleteBlogArticleCategoryByIds(Long[] ids);

    /**
     * 删除信息
     *
     * @param id 主键
     * @return affected lines
     */
    int deleteBlogArticleCategoryById(Long id);
}
