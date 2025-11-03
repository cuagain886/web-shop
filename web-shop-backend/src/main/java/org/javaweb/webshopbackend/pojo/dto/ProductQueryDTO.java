package org.javaweb.webshopbackend.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品查询DTO
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@Schema(description = "商品查询条件")
public class ProductQueryDTO {

    @Schema(description = "商品名称（模糊查询）", example = "手机")
    private String name;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "最低价格", example = "100.00")
    private BigDecimal minPrice;

    @Schema(description = "最高价格", example = "1000.00")
    private BigDecimal maxPrice;

    @Schema(description = "商品状态（0-下架，1-上架）", example = "1")
    private Integer status;

    @Schema(description = "是否热门", example = "true")
    private Boolean isHot;

    @Schema(description = "是否推荐", example = "true")
    private Boolean isRecommend;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}

