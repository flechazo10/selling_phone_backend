package com.sellingphone.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ProductDetailResponse {
    private Integer id;
    private String name;
    private String image;
    private String brandName;
    private String description;
    private List<String> imageUrls;
    private SpecificationResponse specs;
    private List<VersionResponse> versions;
}
