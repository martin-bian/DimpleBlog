package com.dimple.project.blog.comment.controller;

import com.dimple.common.constant.UserConstants;
import com.dimple.common.utils.SecurityUtils;
import com.dimple.framework.aspectj.lang.annotation.Log;
import com.dimple.framework.aspectj.lang.enums.BusinessType;
import com.dimple.framework.web.controller.BaseController;
import com.dimple.framework.web.domain.AjaxResult;
import com.dimple.framework.web.page.TableDataInfo;
import com.dimple.project.blog.comment.domain.Comment;
import com.dimple.project.blog.comment.service.CommentService;
import com.dimple.project.system.domain.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @className: CommentController
 * @description:
 * @auther: Dimple
 * @date: 2019/10/26
 * @version: 1.0
 */
@RestController
@RequestMapping("/blog/comment")
public class CommentController extends BaseController {
    @Autowired
    private CommentService commentService;

    /**
     * 获取评论列表
     */
    @PreAuthorize("@permissionService.hasPermission('blog:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(Comment comment) {
        startPage();
        List<Comment> list = commentService.selectCommentList(comment);
        return getDataTable(list);
    }

    /**
     * 根据id获取详细信息
     */
    @PreAuthorize("@permissionService.hasPermission('blog:comment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return AjaxResult.success(commentService.selectCommentById(id));
    }

    /**
     * 新增评论
     */
    @PreAuthorize("@permissionService.hasPermission('blog:comment:add')")
    @Log(title = "评论管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Comment comment) {
        comment.setCreateBy(SecurityUtils.getUsername());
        return toAjax(commentService.insertComment(comment));
    }

    /**
     * 修改评论
     */
    @PreAuthorize("@permissionService.hasPermission('blog:comment:edit')")
    @Log(title = "评论管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Comment comment) {
        comment.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(commentService.updateComment(comment));
    }

    /**
     * 删除评论
     */
    @PreAuthorize("@permissionService.hasPermission('system:config:remove')")
    @Log(title = "评论管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(commentService.deleteCommentById(id));
    }
}
