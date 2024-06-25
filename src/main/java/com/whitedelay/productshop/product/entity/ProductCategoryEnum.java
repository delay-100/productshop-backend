package com.whitedelay.productshop.product.entity;

import lombok.Getter;

@Getter
public enum ProductCategoryEnum {
    CLOTHING(Category.CLOTHING),
    ELECTRONICS(Category.ELECTRONICS),
    FOOD(Category.FOOD),
    BOOKS(Category.BOOKS);

    private final String category;

    ProductCategoryEnum(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public static class Category {
        public static final String CLOTHING = "의류";
        public static final String ELECTRONICS = "가전제품";
        public static final String FOOD = "식품";
        public static final String BOOKS = "도서";
    }
}
