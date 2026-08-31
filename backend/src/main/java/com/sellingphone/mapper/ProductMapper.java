package com.sellingphone.mapper;

import com.sellingphone.dto.response.ProductDetailResponse;
import com.sellingphone.dto.response.ProductResponse;
import com.sellingphone.dto.response.SpecificationResponse;
import com.sellingphone.dto.response.VersionResponse;
import com.sellingphone.entity.Product;
import com.sellingphone.entity.ProductImage;
import com.sellingphone.entity.Version;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getProductId());
        response.setName(product.getProductName());
        response.setImage(product.getImage());
        response.setBrandName(product.getBrand() != null ? product.getBrand().getBrandName() : null);
        response.setCategoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null);

        // Lấy giá thấp nhất từ các phiên bản
        BigDecimal minPrice = null;
        if (product.getVersions() != null && !product.getVersions().isEmpty()) {
            minPrice = product.getVersions().stream()
                    .map(Version::getPrice)
                    .filter(p -> p != null)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
        }
        response.setMinPrice(minPrice);

        return response;
    }

    public ProductDetailResponse toProductDetailResponse(Product product) {
        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(product.getProductId());
        response.setName(product.getProductName());
        response.setImage(product.getImage());
        response.setBrandName(product.getBrand() != null ? product.getBrand().getBrandName() : null);
        response.setDescription(product.getDescription());

        // Map imageUrls
        if (product.getImages() != null) {
            response.setImageUrls(product.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()));
        } else {
            response.setImageUrls(Collections.emptyList());
        }

        // Map specs
        if (product.getSpecification() != null) {
            SpecificationResponse specs = new SpecificationResponse();
            specs.setScreenSize(product.getSpecification().getScreenSize());
            specs.setScreenTech(product.getSpecification().getScreenTech());
            specs.setRearCamera(product.getSpecification().getRearCamera());
            specs.setFrontCamera(product.getSpecification().getFrontCamera());
            specs.setChipset(product.getSpecification().getChipset());
            specs.setRam(product.getSpecification().getRam());
            specs.setRom(product.getSpecification().getRom());
            specs.setBattery(product.getSpecification().getBattery());
            specs.setOs(product.getSpecification().getOs());
            specs.setScreenFeatures(product.getSpecification().getScreenFeatures());
            response.setSpecs(specs);
        }

        // Map versions and sort by price ascending
        List<VersionResponse> versionResponses = product.getVersions() != null
                ? product.getVersions().stream().map(this::toVersionResponse).collect(Collectors.toList())
                : Collections.emptyList();

        versionResponses.sort(Comparator.comparing(VersionResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())));
        response.setVersions(versionResponses);

        return response;
    }

    public VersionResponse toVersionResponse(Version version) {
        VersionResponse response = new VersionResponse();
        response.setVersionId(version.getVersionId());
        response.setColour(version.getColour());
        response.setStorage(version.getStorage());
        response.setMaterial(version.getMaterial());
        response.setPrice(version.getPrice());
        response.setStock(version.getStock());
        response.setImageUrl(version.getImageUrl());
        return response;
    }
}