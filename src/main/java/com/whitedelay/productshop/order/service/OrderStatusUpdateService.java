package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderProduct;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import com.whitedelay.productshop.order.repository.OrderProductRepository;
import com.whitedelay.productshop.order.repository.OrderRepository;
import com.whitedelay.productshop.product.entity.Product;
import com.whitedelay.productshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatusUpdateService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    @Scheduled(cron = "0 * * * * ?") // 매 1분마다 스케줄 실행 (테스트 용도) -> 다되면 plusMinutes를 plusDays로 바꾸기
    @Transactional
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Order order : orders) {
            // 배송 상태 업데이트
            if (order.getOrderDate().plusMinutes(1).isBefore(now) && order.getOrderStatus() == OrderStatusEnum.PAYMENT_COMPLETED) {
                order.setOrderStatus(OrderStatusEnum.SHIPPING);
            } else if (order.getOrderDate().plusMinutes(2).isBefore(now) && order.getOrderStatus() == OrderStatusEnum.SHIPPING) {
                order.setOrderStatus(OrderStatusEnum.DELIVERY_COMPLETED);
            }

            // 반품 처리
            // 반품한 상품은 반품 신청 후 D+1에 재고에 반영 됨. 재고에 반영된후 상태는 반품완료로 변경됨
            if (order.getOrderStatus() == OrderStatusEnum.RETURN_REQUESTED && order.getOrderDate().plusMinutes(3).isBefore(now)) {
                List<OrderProduct> orderProducts = orderProductRepository.findByOrderOrderId(order.getOrderId());
                for (OrderProduct orderProduct : orderProducts) {
                    Product product = orderProduct.getProduct();
                    product.setProductStock(product.getProductStock() + orderProduct.getOrderProductQuantity());
                    productRepository.save(product);
                }
                order.setOrderStatus(OrderStatusEnum.RETURN_COMPLETED);
            }
        }

        orderRepository.saveAll(orders);
    }
}
