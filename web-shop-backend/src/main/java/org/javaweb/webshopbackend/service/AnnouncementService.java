package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.Announcement;
import org.javaweb.webshopbackend.pojo.common.PageResult;

import java.util.List;

/**
 * 公告Service接口
 * 
 * @author WebShop Team
 * @date 2025-11-12
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 获取所有已发布的公告（按置顶和时间排序）
     * 
     * @return 公告列表
     */
    List<Announcement> getPublishedAnnouncements();

    /**
     * 分页查询公告
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 状态
     * @return 分页结果
     */
    PageResult<Announcement> pageAnnouncements(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 创建公告
     * 
     * @param announcement 公告对象
     * @return 是否成功
     */
    boolean createAnnouncement(Announcement announcement);

    /**
     * 更新公告
     * 
     * @param announcement 公告对象
     * @return 是否成功
     */
    boolean updateAnnouncement(Announcement announcement);

    /**
     * 删除公告
     * 
     * @param id 公告ID
     * @return 是否成功
     */
    boolean deleteAnnouncement(Long id);

    /**
     * 发布公告
     * 
     * @param id 公告ID
     * @return 是否成功
     */
    boolean publishAnnouncement(Long id);
}
