package com.rpolido.springboot2essentials.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AnimeInsertDTO {

    @NotEmpty(message = "The anime name cannot be empty")
    private String name;
}
