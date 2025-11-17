package org.javaweb.webshopbackend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus 配置类
 * 
 * @author WebShop Team
 * @date 2025-11-02
 */
@Configuration
@EnableTransactionManagement
@MapperScan("org.javaweb.webshopbackend.mapper")
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器配置
     * 包含分页插件、乐观锁插件、防止全表更新删除插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 1. 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置请求的页面大于最大页后操作，true调回到首页，false继续请求
        paginationInnerInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认500条，-1不受限制
        paginationInnerInterceptor.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        
        // 2. 乐观锁插件
        // 当要更新一条记录的时候，希望这条记录没有被别人更新
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        // 3. 防止全表更新与删除插件
        // 阻止恶意的全表更新删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        
        return interceptor;
    }
}


