package com.seyha.authentication.domain;

import com.seyha.authentication.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.UUID;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    private String firstname;
    private String lastname;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(nullable = false)
    private boolean enabled = true; // Required for isEnabled()
}
