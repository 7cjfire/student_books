package com.onlinecollege.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onlinecollege.common.Result;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.course.entity.EduComment;
import com.onlinecollege.course.mapper.EduCommentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 课程评论接口
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "课程评论")
public class CommentController {

    private final EduCommentMapper commentMapper;

    @PostMapping
    @Operation(summary = "发表评论")
    public Result<EduComment> add(@RequestBody EduComment comment,
                                  @RequestHeader(value = "X-User-Id", required = false) String userId,
                                  @RequestHeader(value = "X-User-Name", required = false) String userName) {
        if (!StringUtils.hasText(comment.getContent())) {
            throw BusinessException.badRequest("评论内容不能为空");
        }
        if (comment.getCourseId() == null) {
            throw BusinessException.badRequest("courseId 不能为空");
        }
        if (StringUtils.hasText(userId)) {
            try {
                comment.setUserId(Long.parseLong(userId));
            } catch (NumberFormatException ignored) {}
        }
        if (StringUtils.hasText(userName) && !StringUtils.hasText(comment.getNickname())) {
            comment.setNickname(userName);
        }
        comment.setId(null);
        commentMapper.insert(comment);
        return Result.success(comment);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "按课程分页查评论（最新在前）")
    public Result<Page<EduComment>> list(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<EduComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EduComment> w = new LambdaQueryWrapper<EduComment>()
                .eq(EduComment::getCourseId, courseId)
                .orderByDesc(EduComment::getCreateTime);
        return Result.success(commentMapper.selectPage(page, w));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(commentMapper.deleteById(id) > 0);
    }
}
