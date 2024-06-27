package com.whitedelay.productshop.order.entity;

public enum OrderStatusEnum {
    PAYING(OrderStatusEnum.Status.PAYING),
    PAYMENT_COMPLETED(OrderStatusEnum.Status.PAYMENT_COMPLETED),
    PAYMENT_FAILED(OrderStatusEnum.Status.PAYMENT_FAILED),
    PREPARING_SHIPMENT(OrderStatusEnum.Status.PREPARING_SHIPMENT),
    SHIPPING(OrderStatusEnum.Status.SHIPPING),
    DELIVERY_COMPLETED(OrderStatusEnum.Status.DELIVERY_COMPLETED),
    ORDER_CANCELLED(OrderStatusEnum.Status.ORDER_CANCELLED),
    RETURN_REQUESTED(OrderStatusEnum.Status.RETURN_REQUESTED),
    RETURN_COMPLETED(OrderStatusEnum.Status.RETURN_COMPLETED);

    private final String status;

    OrderStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static class Status {
        public static final String PAYING = "결제중";
        public static final String PAYMENT_COMPLETED = "결제완료";
        public static final String PAYMENT_FAILED = "결제실패";
        public static final String PREPARING_SHIPMENT = "배송준비중";
        public static final String SHIPPING = "배송중";
        public static final String DELIVERY_COMPLETED = "배송완료";
        public static final String ORDER_CANCELLED = "주문취소";
        public static final String RETURN_REQUESTED = "반품신청";
        public static final String RETURN_COMPLETED = "반품완료";
    }

}
