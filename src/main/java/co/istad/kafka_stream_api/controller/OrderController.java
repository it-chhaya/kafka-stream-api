package co.istad.kafka_stream_api.controller;

import co.istad.kafka_stream_api.event.OrderEvent;
import co.istad.kafka_stream_api.event.OrderReport;
import co.istad.kafka_stream_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderEvent> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/reports")
    public OrderReport getOrderReports() {
        return orderService.getOrderReport();
    }

}
