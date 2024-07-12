package com.whitedelay.productshop.image.entity;

public enum ImageTableEnum {
    PRODUCT(ImageTable.PRODUCT),
    MEMBER(ImageTable.MEMBER);

    private final String imageTable;

    ImageTableEnum(String imageTable) { this.imageTable = imageTable; }

    public String getImageTable() { return imageTable; }

    public static class ImageTable {
        public static final String PRODUCT = "product";
        public static final String MEMBER = "member";
    }
}
