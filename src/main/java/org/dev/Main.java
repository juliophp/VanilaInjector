package org.dev;

import org.dev.core.ApplicationContext;
import org.dev.service.OrderService;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new ApplicationContext("org.dev.service");

        OrderService orderService = context.getBean(OrderService.class);
        orderService.placeOrder(500);

    }
}