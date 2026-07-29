package com.example.UserService.Dto;

import lombok.Data;
@Data
public class RegisterResponse {

    private String keycloakId;
    private String email;
    private String firstName;
    private String lastName;

}
