package com.example.UserService.Dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;
    private String firstName;
    private String lastName;
    private String keycloakId;
}
