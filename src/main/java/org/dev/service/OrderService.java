package org.dev.service;

import org.dev.annotations.Component;

@Component
public class OrderService {

    private final PaymentService paymentService;
    private final CouponService couponService;



    public OrderService(StripePaymentService paymentService) {
        this.paymentService = paymentService;
        this.couponService = rate -> System.out.println("Applying coupon at " + rate + " percent");
    }

    public void placeOrder(double total) {

      couponService.applyCoupon(5);
      paymentService.processPayment(total);
    }
}
