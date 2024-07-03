package com.whitedelay.productshop.order.dto;

import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderProductRequestDto {
//    private int quantity;
//    private Long productOptionId; // nullable, 옵션이 없을 수도 있음
//
//    private int productPrice;
//    private int optionPrice; // nullable, 없을수도 있음

    private Order order;
    private Product product;

    private int orderProductQuantity;
    private int orderProductPrice;

    private Long orderProductOptionId;
    private int orderProductOptionPrice;

}
