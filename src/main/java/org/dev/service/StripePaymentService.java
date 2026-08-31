package org.dev.service;

import org.dev.annotations.Component;

@Component
public class StripePaymentService implements PaymentService {

    @Override
    public void processPayment(double amount) {
        System.out.println("The amount is " + amount);
    }
}
