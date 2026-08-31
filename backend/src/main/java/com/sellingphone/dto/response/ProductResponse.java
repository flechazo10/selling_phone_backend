package com.sellingphone.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Integer id;
    private String name;
    private String image; // Ảnh đại diện duy nhất
    private String brandName;
    private String categoryName;
    private BigDecimal minPrice;
}