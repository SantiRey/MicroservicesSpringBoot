package com.personal.customer;

import com.personal.amqp.RabbitMQMessageProducer;
import com.personal.clients.fraud.FraudCheckResponse;
import com.personal.clients.fraud.FraudClient;
import com.personal.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service("FeignImpl")
@Slf4j
public class CustomerServiceFeign implements CustomerServiceInterface {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    @Override

    public void registerCustomer(CustomerRegistrationRequest request) {
        log.info("hello from ServiceFeign");
        Customer customer = Customer.builder()
                .fisrtName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());
        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("Fraudster");
        }
        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to my microservies",
                        customer.getFisrtName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

    }
}
