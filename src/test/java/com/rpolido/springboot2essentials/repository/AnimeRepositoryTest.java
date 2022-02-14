package com.rpolido.springboot2essentials.repository;

import com.rpolido.springboot2essentials.domain.Anime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    private Anime createAnime() {
        return Anime.builder()
                .name("Hajime no Ipo")
                .build();
    }

    @Test
    @DisplayName("Save persist anime when Successful")
    public void save_PersistAnime_WhenSuccessful() {
        final var animeToBeSaved = createAnime();
        final var animeSaved = animeRepository.save(animeToBeSaved);

        assertThat(animeSaved).isNotNull();
        assertThat(animeSaved.getId()).isNotNull();
        assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
    }

    @Test
    @DisplayName("Save update anime when Successful")
    public void save_UpdateAnime_WhenSuccessful() {
        final var animeToBeSaved = createAnime();
        final var animeSaved = animeRepository.save(animeToBeSaved);

        animeSaved.setName("Overlord");

        final var animeUpdated = animeRepository.save(animeSaved);

        assertThat(animeUpdated).isNotNull();
        assertThat(animeUpdated.getId()).isNotNull();
        assertThat(animeUpdated.getName()).isEqualTo(animeSaved.getName());
    }

    @Test
    @DisplayName("Delete removes anime when Successful")
    public void delete_RemoveAnime_WhenSuccessful() {
        final var animeToBeSaved = createAnime();
        final var animeSaved = animeRepository.save(animeToBeSaved);

        animeSaved.setName("Overlord");

        animeRepository.delete(animeSaved);

        final var animeDeletado = animeRepository.findById(animeSaved.getId());

        assertThat(animeDeletado).isEmpty();
    }

    @Test
    @DisplayName("Find by Name returns list of anime when Successful")
    public void findByName_ReturnsListOfAnime_WhenSuccessful() {
        final var animeToBeSaved = createAnime();
        final var animeSaved = animeRepository.save(animeToBeSaved);

        final var name = animeSaved.getName();

        final var animes = animeRepository.findByName(name);

        assertThat(animes).isNotEmpty();
        assertThat(animes).contains(animeSaved);
    }

    @Test
    @DisplayName("Find by Name returns empty list when no anime is found")
    public void findByName_ReturnsEmptyList_WhenAnimeIsNotFound() {

        final var animes = animeRepository.findByName("empty");

        assertThat(animes).isNotNull();
        assertThat(animes).isEmpty();
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    public void save_ThrowConstraintViolationException_WhenNameIsEmpty() {
        final var anime = new Anime();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> animeRepository.save(anime))
                .withMessageContaining("The anime name cannot be empty");

    }
}