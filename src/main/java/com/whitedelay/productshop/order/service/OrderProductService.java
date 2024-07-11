package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.member.entity.Member;
import com.whitedelay.productshop.order.dto.OrderProductPayRequestDto;
import com.whitedelay.productshop.order.dto.OrderProductRequestDto;
import com.whitedelay.productshop.order.dto.OrderRequestDto;
import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderProduct;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import com.whitedelay.productshop.order.repository.OrderProductRepository;
import com.whitedelay.productshop.order.repository.OrderRepository;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.entity.ProductOption;
import com.whitedelay.productshop.product.repository.ProductOptionRepository;
import com.whitedelay.productshop.product.repository.ProductRepository;
import com.whitedelay.productshop.util.AES256Encoder;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderProductService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final AES256Encoder aes256Encoder;

    @Async
    @Transactional
    public void postOrderProductPay(Member member, OrderProductPayRequestDto orderProductPayRequestDto) {
        try {
            List<OrderProduct> orderProductList = new ArrayList<>();
            orderProductPayRequestDto.getOrderProductList().forEach(orderProduct -> {
                ProductOption productOption = productOptionRepository.findByIdForUpdate(orderProduct.getProductOptionId())
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품 옵션이 없습니다."));
                if (productOption.getProductOptionStock() < orderProduct.getQuantity()) {
                    throw new IllegalArgumentException("상품 옵션의 재고가 부족합니다.");
                }
                productOptionRepository.updateStock(orderProduct.getProductOptionId(), productOption.getProductOptionStock() - orderProduct.getQuantity());

                Product product = productRepository.findByProductId(orderProduct.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("찾는 상품이 없습니다."));

                orderProductList.add(OrderProduct.from(OrderProductRequestDto.from(product, orderProduct.getQuantity(), productOption)));
            });

            Order order = Order.from(OrderRequestDto.from(orderProductPayRequestDto, OrderStatusEnum.PAYMENT_COMPLETED, aes256Encoder, member));
            orderRepository.save(order);
            orderProductList.forEach(orderProduct -> orderProduct.setOrder(order));

            orderProductRepository.saveAll(orderProductList);
    } catch (Exception e) {
        System.out.println("e = " + e);
        throw e;
        }
    }
}
