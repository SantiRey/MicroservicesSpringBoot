package com.personal.customer;

public record CustomerRegistrationRequest(String firstName,
                                          String lastName,
                                          String email
                              ) {
}
