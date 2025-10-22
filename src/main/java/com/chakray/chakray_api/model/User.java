package com.chakray.chakray_api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String password;
    private String tax_id;
    private String created_at;
    private List<Adress> addresses;

}
