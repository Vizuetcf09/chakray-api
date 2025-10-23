package com.chakray.chakray_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "app_user")
public class User {

    @Id
    @org.hibernate.annotations.UuidGenerator
    @GeneratedValue
    private UUID id;

    private String email;
    private String name;
    private String phone;
    private String password;
    private String tax_id;
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Address> addresses;
}
