// PersonDto.java
package com.sernova.dto;

import java.util.List;

import com.sernova.domain.Address;

public record PersonDto(Long id, String firstName, String lastName, Address address) {}

