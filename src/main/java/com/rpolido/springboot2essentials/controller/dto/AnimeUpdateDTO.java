package com.rpolido.springboot2essentials.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnimeUpdateDTO {

    private long id;
    private String name;

}
