package com.personal.customer;

import com.personal.clients.fraud.FraudCheckResponse;
import com.personal.clients.fraud.FraudClient;
import com.personal.clients.notification.NotificationClient;
import com.personal.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service("FeignImpl")
public class CustomerServiceFeign implements CustomerServiceInterface {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
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

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());
        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("Fraudster");
        }
        //TODO SEND NOTIFICATION
        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome", customer.getFisrtName())
                )
        );
    }
}
