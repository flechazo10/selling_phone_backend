package com.sellingphone.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VersionResponse {
    private Integer versionId;
    private String  colour;
    private String  storage;
    private String  material;
    private BigDecimal price;
    private Integer stock;
    private String  imageUrl;
}