package com.whitedelay.productshop.product.entity;

public enum ProductStatusEnum {
    AVAILABLE(ProductStatus.AVAILABLE),
    OUT_OF_STOCK(ProductStatus.OUT_OF_STOCK),
    COMING_SOON(ProductStatus.COMING_SOON);

    private final String status;

    ProductStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static class ProductStatus {
        public static final String AVAILABLE = "판매중";
        public static final String OUT_OF_STOCK = "품절";
        public static final String COMING_SOON = "판매예정";
    }
}
