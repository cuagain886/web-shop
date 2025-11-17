package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.BrowsingHistoryMapper;
import org.javaweb.webshopbackend.pojo.entity.BrowsingHistory;
import org.javaweb.webshopbackend.service.BrowsingHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 浏览历史 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory> implements BrowsingHistoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordBrowsing(Long userId, Long productId) {
        log.info("记录浏览历史：userId={}, productId={}", userId, productId);

        // 检查是否已存在相同记录
        LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowsingHistory::getUserId, userId)
               .eq(BrowsingHistory::getProductId, productId);
        
        BrowsingHistory existingHistory = this.getOne(wrapper);
        
        if (existingHistory != null) {
            // 更新浏览时间（通过 MyBatis-Plus 自动填充）
            existingHistory.setId(existingHistory.getId()); // 触发更新
            this.updateById(existingHistory);
            log.info("更新浏览时间");
        } else {
            // 新增浏览记录
            BrowsingHistory history = new BrowsingHistory();
            history.setUserId(userId);
            history.setProductId(productId);
            this.save(history);
            log.info("新增浏览记录");
        }
    }

    @Override
    public List<BrowsingHistory> getUserHistory(Long userId, Integer limit) {
        log.info("获取用户浏览历史：userId={}, limit={}", userId, limit);

        return baseMapper.selectByUserIdWithProduct(userId, limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHistory(Long userId, Long historyId) {
        log.info("删除浏览记录：userId={}, historyId={}", userId, historyId);

        // 验证权限
        BrowsingHistory history = this.getById(historyId);
        if (history == null) {
            throw new IllegalArgumentException("浏览记录不存在");
        }
        if (!history.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权删除此记录");
        }

        this.removeById(historyId);
        log.info("浏览记录删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearHistory(Long userId) {
        log.info("清空用户浏览历史：userId={}", userId);

        LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowsingHistory::getUserId, userId);
        
        this.remove(wrapper);
        log.info("浏览历史清空成功");
    }

    @Override
    public List<Long> getHotProducts(Integer limit) {
        log.info("获取热门商品：limit={}", limit);

        return baseMapper.selectHotProducts(limit);
    }
}

