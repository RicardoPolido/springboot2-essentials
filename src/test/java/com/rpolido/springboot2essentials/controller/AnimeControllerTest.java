package com.rpolido.springboot2essentials.controller;

import com.rpolido.springboot2essentials.controller.dto.AnimeInsertDTO;
import com.rpolido.springboot2essentials.controller.dto.AnimeUpdateDTO;
import com.rpolido.springboot2essentials.service.AnimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static com.rpolido.springboot2essentials.util.AnimeCreator.createValidAnime;
import static com.rpolido.springboot2essentials.util.AnimeInsertDTOCreator.createAnimeInsertDTO;
import static com.rpolido.springboot2essentials.util.AnimeUpdateDTOCreator.createAnimeUpdateDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController controller;

    @Mock
    private AnimeService service;

    @BeforeEach
    void setup() {

        final var animePage = new PageImpl<>(List.of(createValidAnime()));
        BDDMockito.when(service.listAll(any())).
                thenReturn(animePage);

        BDDMockito.when(service.listAllNonPaged()).
                thenReturn(List.of(createValidAnime()));

        BDDMockito.when(service.findByIdOrThrowBadRequestException(anyLong()))
            .thenReturn(createValidAnime());

        BDDMockito.when(service.findByName(anyString()))
                .thenReturn(List.of(createValidAnime()));

        BDDMockito.when(service.save(any(AnimeInsertDTO.class)))
                .thenReturn(createValidAnime());

        BDDMockito.doNothing().when(service).replace(any(AnimeUpdateDTO.class));

        BDDMockito.doNothing().when(service).delete(anyLong());

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {

        final var expectedName = createValidAnime().getName();
        final var animePage = controller.list(null).getBody();

        assertThat(animePage).isNotNull();
        assertThat(animePage.toList()).isNotEmpty()
            .hasSize(1);

        assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("List All returns list of anime when successful")
    void list_ReturnsListOfAnimes_WhenSuccessful() {

        final var expectedName = createValidAnime().getName();
        final var animes = controller.listAll().getBody();

        assertThat(animes).isNotNull();
        assertThat(animes).isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnAnime_WhenSuccessful() {

        final var expectedId = createValidAnime().getId();
        final var anime = controller.findById(1L).getBody();

        assertThat(anime).isNotNull();

        assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns list of anime when successful")
    void findByName_ReturnListOfAnime_WhenSuccessful() {

        final var expectedName = createValidAnime().getName();
        final var animes = controller.findByName("name").getBody();

        assertThat(animes).isNotNull();
        assertThat(animes).isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByName_ReturnEmtpyListOfAnime_WhenAnimeIsNotFound() {

        BDDMockito.when(service.findByName(anyString()))
                .thenReturn(Collections.emptyList());

        final var animes = controller.findByName("name").getBody();

        assertThat(animes).isNotNull()
            .isEmpty();

    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnAnime_WhenSuccessful() {

        final var anime = controller.save(createAnimeInsertDTO()).getBody();

        assertThat(anime).isNotNull().isEqualTo(createValidAnime());

    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {

        assertThatCode(() -> controller.replace(createAnimeUpdateDTO()))
                .doesNotThrowAnyException();

        final var entity = controller.replace(createAnimeUpdateDTO());

        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);

    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {

        assertThatCode(() -> controller.delete(1))
                .doesNotThrowAnyException();

        final var entity = controller.delete(1);

        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(NO_CONTENT);

    }
}