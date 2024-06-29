package com.whitedelay.productshop.order.service;

import com.whitedelay.productshop.order.entity.Order;
import com.whitedelay.productshop.order.entity.OrderStatusEnum;
import com.whitedelay.productshop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatusUpdateService {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 자정(00시)마다 스케줄 실행
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Order order : orders) {
            if (order.getOrderDate().plusMinutes(1).isBefore(now) && order.getOrderStatus() == OrderStatusEnum.PAYMENT_COMPLETED) {
                order.setOrderStatus(OrderStatusEnum.SHIPPING);
            } else if (order.getOrderDate().plusMinutes(2).isBefore(now) && order.getOrderStatus() == OrderStatusEnum.SHIPPING) {
                order.setOrderStatus(OrderStatusEnum.DELIVERY_COMPLETED);
            }
        }

        orderRepository.saveAll(orders);
    }
}
