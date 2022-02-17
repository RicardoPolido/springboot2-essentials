package com.rpolido.springboot2essentials.util;

import com.rpolido.springboot2essentials.controller.dto.AnimeUpdateDTO;

import static com.rpolido.springboot2essentials.util.AnimeCreator.createValidUpdatedAnime;

public class AnimeUpdateDTOCreator {

    public static AnimeUpdateDTO createAnimeUpdateDTO() {
        return AnimeUpdateDTO.builder()
                .id(createValidUpdatedAnime().getId())
                .name(createValidUpdatedAnime().getName())
                .build();
    }

}
