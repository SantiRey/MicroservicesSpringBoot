package com.personal.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("RestTemplateImpl")
@AllArgsConstructor
public class CustomerService implements CustomerServiceInterface{

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;


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
        //TODO SEND NOTIFICATION
    }
}
