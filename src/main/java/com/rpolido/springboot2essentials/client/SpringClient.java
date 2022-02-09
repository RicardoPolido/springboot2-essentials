package com.rpolido.springboot2essentials.client;

import com.rpolido.springboot2essentials.domain.Anime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class SpringClient {

    public static void main(String[] args) {

        final var entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 1);
        log.info(entity.toString());

        final var object = new RestTemplate().getForObject("http://localhost:8080/animes/2", Anime.class);
        log.info(object.toString());

    }
}