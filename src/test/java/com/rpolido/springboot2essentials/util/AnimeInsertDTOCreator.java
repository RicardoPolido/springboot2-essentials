package com.rpolido.springboot2essentials.util;

import com.rpolido.springboot2essentials.controller.dto.AnimeInsertDTO;

import static com.rpolido.springboot2essentials.util.AnimeCreator.createAnimeToBeSaved;

public class AnimeInsertDTOCreator {

    public static AnimeInsertDTO createAnimeInsertDTO() {
        return AnimeInsertDTO.builder()
                .name(createAnimeToBeSaved().getName())
                .build();
    }

}
