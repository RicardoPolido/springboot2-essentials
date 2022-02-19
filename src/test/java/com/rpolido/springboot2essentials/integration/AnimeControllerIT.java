package com.rpolido.springboot2essentials.integration;

import com.rpolido.springboot2essentials.domain.Anime;
import com.rpolido.springboot2essentials.domain.DevDojoUser;
import com.rpolido.springboot2essentials.repository.AnimeRepository;
import com.rpolido.springboot2essentials.repository.DevDojoUserRepository;
import com.rpolido.springboot2essentials.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static com.rpolido.springboot2essentials.util.AnimeCreator.createAnimeToBeSaved;
import static com.rpolido.springboot2essentials.util.AnimeCreator.createValidAnime;
import static com.rpolido.springboot2essentials.util.AnimeInsertDTOCreator.createAnimeInsertDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private AnimeRepository repository;

    @Autowired
    private DevDojoUserRepository devDojoUserRepository;

    private static final DevDojoUser USER =  DevDojoUser.builder()
            .name("devdojo")
            .username("devdojo")
            .password("{bcrypt}$2a$10$NRjAwXNf73/SRdliAt9ebOZlmLo1ZbYwKKBQzM9GSwvV6raag5QkG")
            .authorities("ROLE_USER")
            .build();

    private static final DevDojoUser ADMIN =  DevDojoUser.builder()
            .name("ricardo")
            .username("ricardo")
            .password("{bcrypt}$2a$10$NRjAwXNf73/SRdliAt9ebOZlmLo1ZbYwKKBQzM9GSwvV6raag5QkG")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            final var restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("devdojo", "academy");

            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            final var restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("ricardo", "academy");

            return new TestRestTemplate(restTemplateBuilder);
        }

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {

        final var savedAnime = repository.save(createAnimeToBeSaved());

        devDojoUserRepository.save(USER);

        final var expectedName = createValidAnime().getName();

        final var animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {}).getBody();

        assertThat(animePage).isNotNull();

        assertThat(animePage.toList()).isNotEmpty()
                .hasSize(1);

        assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("List All returns list of anime when successful")
    void list_ReturnsListOfAnimes_WhenSuccessful() {

        devDojoUserRepository.save(USER);

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var expectedName = createValidAnime().getName();

        final var animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes).isNotNull();

        assertThat(animes).isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnAnime_WhenSuccessful() {

        devDojoUserRepository.save(USER);

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var expectedId = savedAnime.getId();
        final var anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

        assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns list of anime when successful")
    void findByName_ReturnListOfAnime_WhenSuccessful() {

        devDojoUserRepository.save(USER);

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var expectedName = savedAnime.getName();
        final var url = String.format("/animes/find?name=%s", expectedName);

        final var animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes).isNotNull();
        assertThat(animes).isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByName_ReturnEmtpyListOfAnime_WhenAnimeIsNotFound() {

        devDojoUserRepository.save(USER);

        final var animes = testRestTemplateRoleUser.exchange("/animes/find?name=dbz", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {}).getBody();

        assertThat(animes).isNotNull()
            .isEmpty();

    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnAnime_WhenSuccessful() {

        devDojoUserRepository.save(USER);

        final var animeInsertDTO = createAnimeInsertDTO();
        final var animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animeInsertDTO, Anime.class);

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(CREATED);
        assertThat(animeResponseEntity.getBody()).isNotNull();
        assertThat(animeResponseEntity.getBody().getId()).isNotNull();

    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {

        devDojoUserRepository.save(USER);

        final var savedAnime = repository.save(createAnimeToBeSaved());

        savedAnime.setName("new name");

        final var animeResponseEntity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT,
            new HttpEntity<>(savedAnime), Void.class);

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(NO_CONTENT);

    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {

        devDojoUserRepository.save(ADMIN);

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE,
               null, Void.class, savedAnime.getId());

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(NO_CONTENT);

    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_Returns403_WhenUserIsNotAdmin() {

        devDojoUserRepository.save(USER);

        final var savedAnime = repository.save(createAnimeToBeSaved());

        final var animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}", HttpMethod.DELETE,
                null, Void.class, savedAnime.getId());

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(FORBIDDEN);

    }
}