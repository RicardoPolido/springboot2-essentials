package com.rpolido.springboot2essentials.controller;

import com.rpolido.springboot2essentials.controller.dto.AnimeInsertDTO;
import com.rpolido.springboot2essentials.controller.dto.AnimeUpdateDTO;
import com.rpolido.springboot2essentials.domain.Anime;
import com.rpolido.springboot2essentials.service.AnimeService;
import com.rpolido.springboot2essentials.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("animes")
@RequiredArgsConstructor
@Slf4j
public class AnimeController {

    private final DateUtil dateUtil;
    private final AnimeService animeService;

    @GetMapping
    public ResponseEntity<Page<Anime>> list(Pageable pageable) {
        log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAll(pageable));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable long id) {
        return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
    }

    @GetMapping(path = "/find")
    public ResponseEntity<List<Anime>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(animeService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimeInsertDTO anime) {
        return new ResponseEntity<>(animeService.save(anime), CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody AnimeUpdateDTO anime) {
        animeService.replace(anime);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        animeService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
