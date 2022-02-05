package com.rpolido.springboot2essentials.mapper;

import com.rpolido.springboot2essentials.controller.dto.AnimeInsertDTO;
import com.rpolido.springboot2essentials.controller.dto.AnimeUpdateDTO;
import com.rpolido.springboot2essentials.domain.Anime;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {

    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    public abstract Anime toAnime(AnimeInsertDTO animeInsertDTO);

    public abstract Anime toAnime(AnimeUpdateDTO animeUpdateDTO);

}
