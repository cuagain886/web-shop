package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.OrdersMapper;
import org.javaweb.webshopbackend.pojo.dto.OrderCreateDTO;
import org.javaweb.webshopbackend.pojo.entity.*;
import org.javaweb.webshopbackend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSkuService productSkuService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders createOrder(Long userId, Long addressId, List<Long> cartItemIds, String note) {
        log.info("创建订单：userId={}, addressId={}, cartItemIds={}", userId, addressId, cartItemIds);

        // 1. 查询收货地址
        UserAddress address = userAddressService.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("收货地址不存在");
        }

        // 2. 查询购物车项
        List<ShoppingCart> cartItems = shoppingCartService.listByIds(cartItemIds);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("购物车为空");
        }

        // 验证购物车项是否属于当前用户
        for (ShoppingCart cartItem : cartItems) {
            if (!cartItem.getUserId().equals(userId)) {
                throw new IllegalArgumentException("购物车项不属于当前用户");
            }
        }

        // 3. 检查库存并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ShoppingCart cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new IllegalArgumentException("商品不存在或已下架");
            }
            if (!productService.checkStock(product.getId(), cartItem.getQuantity())) {
                throw new IllegalArgumentException("商品 " + product.getName() + " 库存不足");
            }
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 获取系统设置中的默认运费
        SystemSettings settings = systemSettingsService.getSettings();
        BigDecimal freight = settings != null && settings.getDefaultShipping() != null
            ? settings.getDefaultShipping()
            : BigDecimal.ZERO;

        // 4. 创建订单
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount.add(freight));
        order.setPayAmount(totalAmount.add(freight));
        order.setFreight(freight);
        order.setStatus(0);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() +
                                address.getDistrict() + address.getDetailAddress());
        order.setNote(note);
        this.save(order);

        // 5. 创建订单项并减库存
        for (ShoppingCart cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getCoverImage());
            orderItem.setSpecInfo(cartItem.getSpecInfo());
            orderItem.setSkuId(cartItem.getSkuId());  // 保存SKU ID
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            orderItem.setIsReviewed(0);
            orderItemService.save(orderItem);

            // 减少商品库存和SKU库存
            productService.updateProductStock(product.getId(), -cartItem.getQuantity());
            if (cartItem.getSkuId() != null) {
                productService.updateSkuStock(cartItem.getSkuId(), -cartItem.getQuantity());
            }
        }

        // 6. 清除购物车
        shoppingCartService.removeCarts(cartItemIds);

        log.info("订单创建成功：orderNo={}, totalAmount={}", order.getOrderNo(), totalAmount);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders createOrderFromItems(Long userId, Long addressId, List<OrderCreateDTO.OrderItemDTO> items, String note) {
        log.info("从商品列表创建订单：userId={}, addressId={}, items={}", userId, addressId, items.size());

        // 1. 查询收货地址
        UserAddress address = userAddressService.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("收货地址不存在");
        }

        // 2. 检查库存并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderCreateDTO.OrderItemDTO item : items) {
            Product product = productService.getById(item.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new IllegalArgumentException("商品不存在或已下架");
            }
            if (!productService.checkStock(product.getId(), item.getQuantity())) {
                throw new IllegalArgumentException("商品 " + product.getName() + " 库存不足");
            }
            
            // 获取SKU价格，如果没有SKU则使用商品价格
            BigDecimal itemPrice = product.getPrice();
            if (item.getSkuId() != null) {
                ProductSku sku = productSkuService.getById(item.getSkuId());
                if (sku != null && sku.getPrice() != null) {
                    itemPrice = sku.getPrice();
                    log.info("使用SKU价格：skuId={}, price={}", item.getSkuId(), itemPrice);
                }
            }
            
            BigDecimal itemTotal = itemPrice.multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 获取系统设置中的默认运费
        SystemSettings settings = systemSettingsService.getSettings();
        BigDecimal freight = settings != null && settings.getDefaultShipping() != null
            ? settings.getDefaultShipping()
            : BigDecimal.ZERO;

        // 3. 创建订单
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount.add(freight));
        order.setPayAmount(totalAmount.add(freight));
        order.setFreight(freight);
        order.setStatus(0);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() +
                                address.getDistrict() + address.getDetailAddress());
        order.setNote(note);
        this.save(order);

        // 4. 创建订单项并减库存
        for (OrderCreateDTO.OrderItemDTO item : items) {
            Product product = productService.getById(item.getProductId());

            // 获取SKU价格，如果没有SKU则使用商品价格
            BigDecimal itemPrice = product.getPrice();
            if (item.getSkuId() != null) {
                ProductSku sku = productSkuService.getById(item.getSkuId());
                if (sku != null && sku.getPrice() != null) {
                    itemPrice = sku.getPrice();
                }
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getCoverImage());
            orderItem.setSpecInfo("");
            orderItem.setSkuId(item.getSkuId());  // 保存SKU ID
            orderItem.setUnitPrice(itemPrice);  // 使用SKU价格或商品价格
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(itemPrice.multiply(new BigDecimal(item.getQuantity())));
            orderItem.setIsReviewed(0);
            orderItemService.save(orderItem);

            // 减少商品库存和SKU库存
            productService.updateProductStock(product.getId(), -item.getQuantity());
            if (item.getSkuId() != null) {
                productService.updateSkuStock(item.getSkuId(), -item.getQuantity());
            }
        }

        log.info("订单创建成功：orderNo={}, totalAmount={}", order.getOrderNo(), totalAmount);
        return order;
    }

    @Override
    public IPage<Orders> getOrderPage(Page<Orders> page, Long userId, Integer status) {
        log.info("分页查询用户订单：userId={}, status={}", userId, status);

        return baseMapper.selectPageWithItems(page, userId, status);
    }

    @Override
    public IPage<Orders> getAdminOrderPage(Page<Orders> page, Integer status, String keyword,
                                          LocalDateTime startTime, LocalDateTime endTime) {
        log.info("管理端分页查询订单：status={}, keyword={}", status, keyword);

        return baseMapper.selectAdminPage(page, status, keyword, startTime, endTime);
    }

    @Override
    public Orders getOrderDetail(String orderNo) {
        log.info("获取订单详情：orderNo={}", orderNo);

        Orders order = baseMapper.selectByOrderNoWithItems(orderNo);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        return order;
    }

    @Override
    public Orders getOrderDetailById(Long orderId) {
        log.info("获取订单详情：orderId={}", orderId);

        Orders order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 查询订单项
        List<OrderItem> items = orderItemService.getByOrderId(orderId);
        order.setItems(items);

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, Long userId, String reason) {
        log.info("取消订单：orderNo={}, userId={}, reason={}", orderNo, userId, reason);

        // 1. 查询订单
        Orders order = getOrderByNo(orderNo);
        
        // 2. 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }

        // 3. 验证订单状态（只有待付款和待发货可以取消）
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new IllegalArgumentException("当前订单状态不允许取消");
        }

        // 4. 恢复库存
        List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
        log.info("取消订单，恢复库存：订单项数量={}", orderItems.size());
        for (OrderItem item : orderItems) {
            log.info("恢复商品库存：productId={}, quantity={}, skuId={}",
                item.getProductId(), item.getQuantity(), item.getSkuId());
            productService.updateProductStock(item.getProductId(), item.getQuantity());
            if (item.getSkuId() != null) {
                log.info("恢复SKU库存：skuId={}, quantity={}", item.getSkuId(), item.getQuantity());
                productService.updateSkuStock(item.getSkuId(), item.getQuantity());
            } else {
                log.warn("订单项没有SKU ID，跳过SKU库存恢复：orderItemId={}", item.getId());
            }
        }

        // 5. 更新订单状态
        order.setStatus(4);  // 已取消
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        this.updateById(order);

        log.info("订单取消成功：orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminCancelOrder(String orderNo, String reason) {
        log.info("管理员取消订单：orderNo={}, reason={}", orderNo, reason);

        Orders order = getOrderByNo(orderNo);

        // 验证订单状态
        if (order.getStatus() >= 3) {
            throw new IllegalArgumentException("当前订单状态不允许取消");
        }

        // 恢复库存
        List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
        log.info("管理员取消订单，恢复库存：订单项数量={}", orderItems.size());
        for (OrderItem item : orderItems) {
            log.info("恢复商品库存：productId={}, quantity={}, skuId={}",
                item.getProductId(), item.getQuantity(), item.getSkuId());
            productService.updateProductStock(item.getProductId(), item.getQuantity());
            if (item.getSkuId() != null) {
                log.info("恢复SKU库存：skuId={}, quantity={}", item.getSkuId(), item.getQuantity());
                productService.updateSkuStock(item.getSkuId(), item.getQuantity());
            } else {
                log.warn("订单项没有SKU ID，跳过SKU库存恢复：orderItemId={}", item.getId());
            }
        }

        // 更新订单状态
        order.setStatus(4);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        this.updateById(order);

        log.info("订单取消成功：orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders payOrder(String orderNo, Long userId, Integer paymentMethod) {
        log.info("支付订单：orderNo={}, userId={}, paymentMethod={}", orderNo, userId, paymentMethod);

        // 1. 查询订单
        Orders order = getOrderByNo(orderNo);

        // 2. 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }

        // 3. 验证订单状态
        if (order.getStatus() != 0) {
            throw new IllegalArgumentException("订单状态不正确");
        }

        // 4. 更新订单状态
        order.setStatus(1);  // 待发货
        order.setPaymentMethod(paymentMethod);
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);

        // 5. 增加商品销量
        List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            productService.updateProductSales(item.getProductId(), item.getQuantity());
        }

        log.info("订单支付成功：orderNo={}", orderNo);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(String orderNo, String expressCompany, String trackingNo) {
        log.info("订单发货：orderNo={}, expressCompany={}, trackingNo={}", orderNo, expressCompany, trackingNo);

        // 1. 查询订单
        Orders order = getOrderByNo(orderNo);

        // 2. 验证订单状态
        if (order.getStatus() != 1) {
            throw new IllegalArgumentException("订单状态不正确");
        }

        // 3. 更新订单状态
        order.setStatus(2);  // 待收货
        order.setExpressCompany(expressCompany);
        order.setTrackingNo(trackingNo);
        order.setShipTime(LocalDateTime.now());
        this.updateById(order);

        // 4. 发送邮件通知
        User user = userService.getById(order.getUserId());
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailService.sendShipmentNotification(user.getEmail(), orderNo, expressCompany, trackingNo);
        }

        log.info("订单发货成功：orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(String orderNo, Long userId) {
        log.info("确认收货：orderNo={}, userId={}", orderNo, userId);

        // 1. 查询订单
        Orders order = getOrderByNo(orderNo);

        // 2. 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }

        // 3. 验证订单状态
        if (order.getStatus() != 2) {
            throw new IllegalArgumentException("订单状态不正确");
        }

        // 4. 更新订单状态
        order.setStatus(3);  // 已完成
        order.setReceiveTime(LocalDateTime.now());
        this.updateById(order);

        log.info("确认收货成功：orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(String orderNo, Long userId) {
        log.info("删除订单：orderNo={}, userId={}", orderNo, userId);

        Orders order = getOrderByNo(orderNo);

        // 验证权限
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }

        // 验证订单状态（只有已完成或已取消的订单可以删除）
        if (order.getStatus() != 3 && order.getStatus() != 4) {
            throw new IllegalArgumentException("当前订单状态不允许删除");
        }

        // 逻辑删除
        this.removeById(order.getId());

        log.info("订单删除成功：orderNo={}", orderNo);
    }

    @Override
    public Map<String, Long> countUserOrders(Long userId) {
        log.info("统计用户订单：userId={}", userId);

        Map<String, Long> result = new HashMap<>();
        result.put("all", baseMapper.countByUserIdAndStatus(userId, null));
        result.put("unpaid", baseMapper.countByUserIdAndStatus(userId, 0));
        result.put("unshipped", baseMapper.countByUserIdAndStatus(userId, 1));
        result.put("shipped", baseMapper.countByUserIdAndStatus(userId, 2));
        result.put("completed", baseMapper.countByUserIdAndStatus(userId, 3));

        return result;
    }

    @Override
    public Map<String, Object> getSalesStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("统计销售数据：startTime={}, endTime={}", startTime, endTime);

        Map<String, Object> result = new HashMap<>();
        
        // 统计销售额
        Double totalAmount = baseMapper.sumAmountByDateRange(startTime, endTime);
        result.put("totalAmount", totalAmount != null ? totalAmount : 0.0);

        // 统计订单数
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startTime != null, Orders::getCreatedTime, startTime)
               .le(endTime != null, Orders::getCreatedTime, endTime);
        long orderCount = this.count(wrapper);
        result.put("orderCount", orderCount);

        // 计算客单价
        if (orderCount > 0) {
            result.put("avgAmount", totalAmount / orderCount);
        } else {
            result.put("avgAmount", 0.0);
        }

        return result;
    }

    @Override
    public Map<Integer, Long> countByStatus() {
        log.info("统计各状态订单数量");

        Map<Integer, Long> result = new HashMap<>();
        List<Orders> statusList = baseMapper.countByStatus();
        
        // 处理统计结果
        for (Orders orders : statusList) {
            result.put(orders.getStatus(), (long) orders.getId());  // 这里需要根据实际返回值调整
        }

        return result;
    }

    /**
     * 根据订单号查询订单
     */
    private Orders getOrderByNo(String orderNo) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = this.getOne(wrapper);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        return order;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + String.format("%03d", (int)(Math.random() * 1000));
    }
}

