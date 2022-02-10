package com.rpolido.springboot2essentials.client;

import com.rpolido.springboot2essentials.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Log4j2
public class SpringClient {

    public static void main(String[] args) {

        final var entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 1);
        log.info(entity);

        final var object = new RestTemplate().getForObject("http://localhost:8080/animes/2", Anime.class);
        log.info(object);

        final var exchange = new RestTemplate().exchange("http://localhost:8080/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {});

        log.info(exchange.getBody());

//        final var kingdom = Anime.builder().name("kingdom").build();
//        final var kingdomSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdom, Anime.class);
//        log.info("Saved Anime {}", kingdomSaved);

        final var angelBeats = Anime.builder().name("Angel Beats").build();
        final var angelBeatsSaved = new RestTemplate().exchange("http://localhost:8080/animes", HttpMethod.POST, new HttpEntity<>(angelBeats, createJsonHeaders()), Anime.class);
        log.info("Saved Anime {}", angelBeatsSaved);

        final var animeToBeUpdate = angelBeatsSaved.getBody();
        animeToBeUpdate.setName("Angel Beats 2");
        final var animeAngelBeatsUpdated = new RestTemplate().exchange("http://localhost:8080/animes", HttpMethod.PUT, new HttpEntity<>(animeToBeUpdate, createJsonHeaders()), Void.class);
        log.info(animeAngelBeatsUpdated);

        final var animeAngelBeatsDelete = new RestTemplate().exchange("http://localhost:8080/animes/{id}", HttpMethod.DELETE, null, Void.class, animeToBeUpdate.getId());
        log.info(animeAngelBeatsDelete);

    }

    private static HttpHeaders createJsonHeaders() {
        final var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}