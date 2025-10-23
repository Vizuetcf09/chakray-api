package com.chakray.chakray_api.data;

import com.chakray.chakray_api.model.User;
import com.chakray.chakray_api.model.Address;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserData {

    public static final List<User> USERS = Arrays.asList(
        User.builder()
                .id(UUID.randomUUID())
                .email("user0@another.com")
                .name("another0")
                .phone("+1 55 555 555 55")
                .password("7c4a8d09ca3762af61e59520943dc26494f8941b")
                .tax_id("AARR990101XXX")
                .created_at(LocalDateTime.of(2023, 1, 1, 0, 0))
                .addresses(Arrays.asList(
                        Address.builder()
                                .id(1L)
                                .name("workaddress")
                                .street("Industry No. 0")
                                .countryCode("MX")
                                .build(),
                                
                        Address.builder()
                                .id(2L)
                                .name("homeaddress")
                                .street("Gardens No. 0")
                                .countryCode("MX")
                                .build()))
        .build(),

        User.builder()
                .id(UUID.randomUUID())
                        .email("user1@mail.com")
                        .name("user1")
                        .phone("+1 55 666 666 66")
                        .password("7c4a8d09ca3762af61e59520943dc26494f8941c")
                        .tax_id("BBCC900202YYY")
                        .created_at(LocalDateTime.of(2024, 2, 1, 0, 0))
                .addresses(Arrays.asList(
                        Address.builder()
                                .id(3L)
                                .name("workaddress")
                                .street("Industry No. 1")
                                .countryCode("US")
                                .build(),
                        Address.builder()
                                .id(4L)
                                .name("homeaddress")
                                .street("Gardens No. 1")
                                .countryCode("US")
                                .build()))
        .build(),

        User.builder()
                .id(UUID.randomUUID())
                    .email("user2@mail.com")
                    .name("user3")
                    .phone("+1 55 777 777 77")
                    .password("7c4a8d09ca3762af61e59520943dc26494f8941d")
                    .tax_id("CCDD910303ZZZ")
                    .created_at(LocalDateTime.of(2025, 3, 1, 0, 0))
                .addresses(Arrays.asList(
                        Address.builder()
                                .id(5L)
                                .name("workaddress")
                                .street("Industry No. 2")
                                .countryCode("JP")
                                .build(),
                        Address.builder()
                                .id(6L)
                                .name("homeaddress")
                                .street("Gardens No. 2")
                                .countryCode("JP")
                                .build()))
        .build());
}
