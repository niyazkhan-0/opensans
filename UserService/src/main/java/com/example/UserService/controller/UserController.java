package com.example.UserService.controller;

import com.example.UserService.Dto.RegisterRequest;
import com.example.UserService.Dto.RegisterResponse;
import com.example.UserService.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String id){
        return ResponseEntity.ok( userService.verifyUser(id));
    }

    @PostMapping
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest requestedUser){
           return ResponseEntity.ok(userService.registerUser(requestedUser));
    }

}
