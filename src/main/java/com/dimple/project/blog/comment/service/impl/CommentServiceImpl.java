package com.dimple.project.blog.comment.service.impl;

import com.dimple.project.blog.comment.domain.Comment;
import com.dimple.project.blog.comment.mapper.CommentMapper;
import com.dimple.project.blog.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @className: CommentServiceImpl
 * @description:
 * @auther: Dimple
 * @date: 2019/10/26
 * @version: 1.0
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Override
    public List<Comment> selectCommentList(Comment comment) {
        return commentMapper.;
    }

    @Override
    public Comment selectCommentById(Long id) {
        return null;
    }

    @Override
    public int insertComment(Comment comment) {
        return 0;
    }

    @Override
    public int updateComment(Comment comment) {
        return 0;
    }

    @Override
    public int deleteCommentById(Long id) {
        return 0;
    }
}
