package com.personal.customer;

import com.personal.clients.fraud.FraudCheckResponse;
import com.personal.clients.notification.NotificationClient;
import com.personal.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("RestTemplateImpl")
@AllArgsConstructor
public class CustomerService implements CustomerServiceInterface{

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private  final NotificationClient notificationClient;

    @Override
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .fisrtName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        //TODO Validation
        customerRepository.saveAndFlush(customer);
        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                "http://FRAUD/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()
        );
        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("Fraudster");
        }
        notificationClient.sendNotification(new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to my microservies", customer.getFisrtName())
            )
        );
        //TODO SEND NOTIFICATION
    }
}
