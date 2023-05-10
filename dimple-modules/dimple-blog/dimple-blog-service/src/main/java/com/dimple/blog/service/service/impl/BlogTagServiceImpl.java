package com.dimple.blog.service.service.impl;

import com.dimple.blog.api.bo.BlogTagBO;
import com.dimple.blog.service.entity.BlogTag;
import com.dimple.blog.service.mapper.BlogTagMapper;
import com.dimple.blog.service.service.BlogTagService;
import com.dimple.common.core.utils.bean.BeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service业务层处理
 *
 * @author Dimple
 * @date 2023-02-13
 */
@Service
public class BlogTagServiceImpl implements BlogTagService {
    @Autowired
    private BlogTagMapper blogTagMapper;

    @Override
    public BlogTagBO selectBlogTagById(Long id) {
        return BeanMapper.convert(blogTagMapper.selectBlogTagById(id), BlogTagBO.class);
    }

    @Override
    public List<BlogTagBO> selectBlogTagByIds(List<Long> ids) {
        List<BlogTag> blogTags = blogTagMapper.selectBlogTagListByIds(ids);
        return BeanMapper.convertList(blogTags, BlogTagBO.class);
    }

    @Override
    public List<BlogTagBO> selectBlogTagByTitles(List<String> titles) {
        List<BlogTag> blogTags = blogTagMapper.selectBlogTagByTitles(titles);
        return BeanMapper.convertList(blogTags, BlogTagBO.class);
    }

    @Override
    public List<BlogTagBO> selectBlogTagList(BlogTagBO blogTagBO) {
        BlogTag blogTag = BeanMapper.convert(blogTagBO, BlogTag.class);
        List<BlogTag> blogTags = blogTagMapper.selectBlogTagList(blogTag);
        return BeanMapper.convertList(blogTags, BlogTagBO.class);
    }

    @Override
    public Long insertBlogTag(BlogTagBO blogTagBO) {
        BlogTag blogTag = BeanMapper.convert(blogTagBO, BlogTag.class);
        blogTagMapper.insertBlogTag(blogTag);
        return blogTag.getId();
    }

    @Override
    public int updateBlogTag(BlogTagBO blogTagBO) {
        BlogTag blogTag = BeanMapper.convert(blogTagBO, BlogTag.class);
        return blogTagMapper.updateBlogTag(blogTag);
    }

    @Override
    public int deleteBlogTagByIds(Long[] ids) {
        return blogTagMapper.deleteBlogTagByIds(ids);
    }

}
