package com.example.UserService.service;

import com.example.UserService.Dto.RegisterRequest;
import com.example.UserService.Dto.RegisterResponse;
import com.example.UserService.UserRepository;
import com.example.UserService.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;

    public RegisterResponse registerUser(@Valid RegisterRequest requestdUser){

        if(repository.existsByEmail(requestdUser.getEmail())){
            User exitstingUser = repository.findByEmail(requestdUser.getEmail());

            return mapToResponse(exitstingUser);
        }

        User newUser = new User();
        newUser.setEmail(requestdUser.getEmail());
        newUser.setFirstName(requestdUser.getFirstName());
        newUser.setLastName(requestdUser.getLastName());
        newUser.setKeycloakId(requestdUser.getKeycloakId());

        User savedUser = repository.save(newUser);

        return mapToResponse(savedUser);
    }

    private RegisterResponse mapToResponse(User savedUser) {
        RegisterResponse userResponse = new RegisterResponse();
        userResponse.setKeycloakId(savedUser.getKeycloakId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());

        return userResponse;
    }



    public Boolean verifyUser(String id){
        return repository.existsByKeycloakId(id);
    }


}
