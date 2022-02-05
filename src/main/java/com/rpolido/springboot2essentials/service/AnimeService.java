package com.rpolido.springboot2essentials.service;

import com.rpolido.springboot2essentials.controller.dto.AnimeInsertDTO;
import com.rpolido.springboot2essentials.controller.dto.AnimeUpdateDTO;
import com.rpolido.springboot2essentials.domain.Anime;
import com.rpolido.springboot2essentials.mapper.AnimeMapper;
import com.rpolido.springboot2essentials.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository repository;

    public List<Anime> listAll() {
        return repository.findAll();
    }

    public List<Anime> findByName(String name) {
        return repository.findByName(name);
    }

    public Anime findByIdOrThrowBadRequestException(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Anime not Found"));
    }

    public Anime save(AnimeInsertDTO animeInsertDTO) {
        return repository.save(AnimeMapper.INSTANCE.toAnime(animeInsertDTO));
    }

    public void delete(long id) {
        repository.delete(findByIdOrThrowBadRequestException(id));
    }

    public void replace(AnimeUpdateDTO animeUpdateDTO) {

        final var savedAnime = findByIdOrThrowBadRequestException(animeUpdateDTO.getId());

        final var anime = AnimeMapper.INSTANCE.toAnime(animeUpdateDTO);
        anime.setId(savedAnime.getId());

        repository.save(anime);
    }
}
