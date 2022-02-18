package com.rpolido.springboot2essentials.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimeInsertDTO {

    @NotEmpty(message = "The anime name cannot be empty")
    private String name;
}
