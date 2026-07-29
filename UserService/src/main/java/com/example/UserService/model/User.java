package com.example.UserService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private String keycloakId;

    @Column(unique = true)
    private String email;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Roles roles = Roles.USER;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
