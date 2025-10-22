package com.chakray.chakray_api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Adress {

    private Integer id;
    private String name;
    private String street;
    private String country_code;
}
