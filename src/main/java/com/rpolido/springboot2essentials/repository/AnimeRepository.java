package com.rpolido.springboot2essentials.repository;

import com.rpolido.springboot2essentials.domain.Anime;

import java.util.List;

public interface AnimeRepository {

    List<Anime> listAll();

}
