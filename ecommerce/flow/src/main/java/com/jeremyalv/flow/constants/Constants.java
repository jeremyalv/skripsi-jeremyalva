package com.jeremyalv.flow.constants;

public final class Constants {
    public static final String USERS_TOPIC_NAME = "users";
    public static final String PRODUCTS_TOPIC_NAME = "products";
    public static final String ORDERS_TOPIC_NAME = "orders";

    public static final String PUBLISH_USERS_EVENT_NAME = "UserRegister";
    public static final String PUBLISH_ORDER_EVENT_NAME = "PlaceOrder";
    public static final String PUBLISH_PAY_ORDER_EVENT_NAME = "PayOrder";
    public static final String PUBLISH_CANCEL_ORDER_EVENT_NAME = "CancelOrder";
    public static final String PUBLISH_PRODUCT_EVENT_NAME = "CreateProduct";
    public static final String VIEW_PRODUCT_EVENT_NAME = "ViewProduct";

    public static final Integer ORDER_STATUS_PLACED = 0;
    public static final Integer ORDER_STATUS_CANCELLED = 1;
    public static final Integer ORDER_STATUS_FAILED = 2;
    public static final Integer ORDER_STATUS_PAID = 3;
    public static final Integer ORDER_STATUS_SHIPPED = 4;
}
