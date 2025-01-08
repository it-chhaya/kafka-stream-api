package co.istad.kafka_stream_api.event.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderReport {

    private String orderId;
    private String orderDate;
    private String status;
    private Customer customer;
    private List<OrderDetail> orderDetails;
    private Payment payment;
    private Shipping shipping;

}
