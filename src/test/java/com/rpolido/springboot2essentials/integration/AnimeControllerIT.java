package com.rpolido.springboot2essentials.integration;

import com.rpolido.springboot2essentials.domain.Anime;
import com.rpolido.springboot2essentials.repository.AnimeRepository;
import com.rpolido.springboot2essentials.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static com.rpolido.springboot2essentials.util.AnimeCreator.createAnimeToBeSaved;
import static com.rpolido.springboot2essentials.util.AnimeCreator.createValidAnime;
import static com.rpolido.springboot2essentials.util.AnimeInsertDTOCreator.createAnimeInsertDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AnimeRepository repository;

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var expectedName = createValidAnime().getName();

        final var animePage = testRestTemplate.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {}).getBody();

        assertThat(animePage).isNotNull();

        assertThat(animePage.toList()).isNotEmpty()
                .hasSize(1);

        assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("List All returns list of anime when successful")
    void list_ReturnsListOfAnimes_WhenSuccessful() {

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var expectedName = createValidAnime().getName();

        final var animes = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes).isNotNull();

        assertThat(animes).isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnAnime_WhenSuccessful() {

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var expectedId = savedAnime.getId();
        final var anime = testRestTemplate.getForObject("/animes/{id}", Anime.class, expectedId);

        assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns list of anime when successful")
    void findByName_ReturnListOfAnime_WhenSuccessful() {

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var expectedName = savedAnime.getName();
        final var url = String.format("/animes/find?name=%s", expectedName);

        final var animes = testRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes).isNotNull();
        assertThat(animes).isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByName_ReturnEmtpyListOfAnime_WhenAnimeIsNotFound() {

        final var animes = testRestTemplate.exchange("/animes/find?name=dbz", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes).isNotNull()
            .isEmpty();

    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnAnime_WhenSuccessful() {

        final var animeInsertDTO = createAnimeInsertDTO();
        final var animeResponseEntity = testRestTemplate.postForEntity("/animes", animeInsertDTO, Anime.class);

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(CREATED);
        assertThat(animeResponseEntity.getBody()).isNotNull();
        assertThat(animeResponseEntity.getBody().getId()).isNotNull();

    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {

        final var savedAnime = repository.save(createAnimeToBeSaved());

        savedAnime.setName("new name");

        final var animeResponseEntity = testRestTemplate.exchange("/animes", HttpMethod.PUT,
            new HttpEntity<>(savedAnime), Void.class);

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(NO_CONTENT);

    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var animeResponseEntity = testRestTemplate.exchange("/animes/{id}", HttpMethod.DELETE,
               null, Void.class, savedAnime.getId());

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(NO_CONTENT);

    }
}