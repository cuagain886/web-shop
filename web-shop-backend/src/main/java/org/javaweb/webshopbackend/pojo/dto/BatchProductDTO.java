package org.javaweb.webshopbackend.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量操作商品请求DTO
 *
 * @author WebShop Team
 * @date 2025-11-04
 */
@Data
@Schema(description = "批量操作商品请求")
public class BatchProductDTO {

    @Schema(description = "商品ID列表", example = "[1, 2, 3, 4]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "商品ID列表不能为空")
    private List<Long> productIds;
}

