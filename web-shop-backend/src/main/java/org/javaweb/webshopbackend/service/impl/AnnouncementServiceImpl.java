package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaweb.webshopbackend.mapper.AnnouncementMapper;
import org.javaweb.webshopbackend.pojo.entity.Announcement;
import org.javaweb.webshopbackend.pojo.common.PageResult;
import org.javaweb.webshopbackend.service.AnnouncementService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告Service实现类
 * 
 * @author WebShop Team
 * @date 2025-11-12
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public List<Announcement> getPublishedAnnouncements() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
               .orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getCreatedTime);
        return this.list(wrapper);
    }

    @Override
    public PageResult<Announcement> pageAnnouncements(Integer pageNum, Integer pageSize, Integer status) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        
        wrapper.orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getCreatedTime);
        
        Page<Announcement> result = this.page(page, wrapper);
        
        return new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
    }

    @Override
    public boolean createAnnouncement(Announcement announcement) {
        return this.save(announcement);
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        return this.updateById(announcement);
    }

    @Override
    public boolean deleteAnnouncement(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean publishAnnouncement(Long id) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(1);
        return this.updateById(announcement);
    }
}
