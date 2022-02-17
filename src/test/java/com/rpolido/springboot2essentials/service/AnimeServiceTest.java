package com.rpolido.springboot2essentials.service;

import com.rpolido.springboot2essentials.domain.Anime;
import com.rpolido.springboot2essentials.exception.BadRequestException;
import com.rpolido.springboot2essentials.repository.AnimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.rpolido.springboot2essentials.util.AnimeCreator.createValidAnime;
import static com.rpolido.springboot2essentials.util.AnimeInsertDTOCreator.createAnimeInsertDTO;
import static com.rpolido.springboot2essentials.util.AnimeUpdateDTOCreator.createAnimeUpdateDTO;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService service;

    @Mock
    private AnimeRepository repository;

    @BeforeEach
    void setup() {

        final var animePage = new PageImpl<>(List.of(createValidAnime()));
        BDDMockito.when(repository.findAll(any(PageRequest.class))).
                thenReturn(animePage);

        BDDMockito.when(repository.findAll()).
                thenReturn(List.of(createValidAnime()));

        BDDMockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.of(createValidAnime()));

        BDDMockito.when(repository.findByName(anyString()))
                .thenReturn(List.of(createValidAnime()));

        BDDMockito.when(repository.save(any(Anime.class)))
                .thenReturn(createValidAnime());

        BDDMockito.doNothing().when(repository).delete(any(Anime.class));

    }

    @Test
    @DisplayName("ListAll returns list of anime inside page object when successful")
    void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {

        final var expectedName = createValidAnime().getName();
        final var animePage = service.listAll(PageRequest.of(1, 1));

        assertThat(animePage).isNotNull();
        assertThat(animePage.toList()).isNotEmpty()
                .hasSize(1);

        assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAllNonPaged returns list of anime when successful")
    void listAllNonPaged_ReturnsListOfAnimes_WhenSuccessful() {

        final var expectedName = createValidAnime().getName();
        final var animes = service.listAllNonPaged();

        assertThat(animes).isNotNull();
        assertThat(animes).isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns anime when successful")
    void findByIdOrThrowBadRequestException_ReturnAnime_WhenSuccessful() {

        final var expectedId = createValidAnime().getId();
        final var anime = service.findByIdOrThrowBadRequestException(1L);

        assertThat(anime).isNotNull();

        assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByIdOrBadRequestException throws BadRequestException when anime is not found")
    void findByIdOrThrowBadRequestException_BadRequestException_WhenAnimeIsNotFound() {

        BDDMockito.when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> service.findByIdOrThrowBadRequestException(1));

    }

    @Test
    @DisplayName("findByName returns list of anime when successful")
    void findByName_ReturnListOfAnime_WhenSuccessful() {

        final var expectedName = createValidAnime().getName();
        final var animes = service.findByName("name");

        assertThat(animes).isNotNull();
        assertThat(animes).isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByName_ReturnEmtpyListOfAnime_WhenAnimeIsNotFound() {

        BDDMockito.when(repository.findByName(anyString()))
                .thenReturn(Collections.emptyList());

        final var animes = service.findByName("name");

        assertThat(animes).isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnAnime_WhenSuccessful() {

        final var anime = service.save(createAnimeInsertDTO());

        assertThat(anime).isNotNull().isEqualTo(createValidAnime());

    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {

        assertThatCode(() -> service.replace(createAnimeUpdateDTO()))
                .doesNotThrowAnyException();

    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {

        assertThatCode(() -> service.delete(1))
                .doesNotThrowAnyException();

    }
}