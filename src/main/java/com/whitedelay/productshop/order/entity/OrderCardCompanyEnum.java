package com.whitedelay.productshop.order.entity;

import com.whitedelay.productshop.product.entity.ProductCategoryEnum;

public enum OrderCardCompanyEnum {

    NH(OrderCardCompanyEnum.CardCompany.NH),
    SHINHAN(OrderCardCompanyEnum.CardCompany.SHINHAN),
    KAKAOBANK(OrderCardCompanyEnum.CardCompany.KAKAOBANK);

    private final String cardCompany;

    OrderCardCompanyEnum(String cardCompany) {
        this.cardCompany = cardCompany;
    }

    public String getCardCompany() {
        return cardCompany;
    }

    public static class CardCompany {
        public static final String NH = "농협";
        public static final String SHINHAN = "신한";
        public static final String KAKAOBANK = "카카오뱅크";
    }
}
