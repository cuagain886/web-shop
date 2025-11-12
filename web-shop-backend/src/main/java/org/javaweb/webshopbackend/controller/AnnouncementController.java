package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.javaweb.webshopbackend.pojo.entity.Announcement;
import org.javaweb.webshopbackend.pojo.common.PageResult;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告Controller
 *
 * @author WebShop Team
 * @date 2025-11-12
 */
@Tag(name = "公告管理", description = "公告相关接口")
@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 获取所有已发布的公告（无需认证）
     */
    @Operation(summary = "获取已发布的公告列表")
    @GetMapping("/published")
    public Result<List<Announcement>> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementService.getPublishedAnnouncements();
        return Result.success(announcements);
    }

    /**
     * 分页查询公告
     */
    @Operation(summary = "分页查询公告")
    @GetMapping("/page")
    public Result<PageResult<Announcement>> pageAnnouncements(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        PageResult<Announcement> result = announcementService.pageAnnouncements(pageNum, pageSize, status);
        return Result.success(result);
    }

    /**
     * 获取公告详情
     */
    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        return Result.success(announcement);
    }

    /**
     * 创建公告
     */
    @Operation(summary = "创建公告")
    @PostMapping
    public Result<String> createAnnouncement(@RequestBody Announcement announcement) {
        boolean success = announcementService.createAnnouncement(announcement);
        if (success) {
            return Result.success("公告创建成功");
        }
        return Result.error("公告创建失败");
    }

    /**
     * 更新公告
     */
    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    public Result<String> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody Announcement announcement) {
        announcement.setId(id);
        boolean success = announcementService.updateAnnouncement(announcement);
        if (success) {
            return Result.success("公告更新成功");
        }
        return Result.error("公告更新失败");
    }

    /**
     * 删除公告
     */
    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.deleteAnnouncement(id);
        if (success) {
            return Result.success("公告删除成功");
        }
        return Result.error("公告删除失败");
    }

    /**
     * 发布公告
     */
    @Operation(summary = "发布公告")
    @PostMapping("/{id}/publish")
    public Result<String> publishAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.publishAnnouncement(id);
        if (success) {
            return Result.success("公告发布成功");
        }
        return Result.error("公告发布失败");
    }
}
