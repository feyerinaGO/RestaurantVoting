package ru.develop.restaurantvoting.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.develop.restaurantvoting.model.Restaurant;
import ru.develop.restaurantvoting.repository.RestaurantRepository;
import ru.develop.restaurantvoting.to.RestaurantTo;
import ru.develop.restaurantvoting.util.RestaurantsUtil;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.develop.restaurantvoting.common.util.ValidationUtil.assureIdConsistent;
import static ru.develop.restaurantvoting.common.util.ValidationUtil.checkIsNew;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class RestaurantController {
    static final String REST_URL = "/api/restaurants";

    private final RestaurantRepository repository;

    @GetMapping
    @Cacheable("restaurants")
    public List<RestaurantTo> getAllWithMenuToday() {
        log.info("getAllWithMenuToday");
        return RestaurantsUtil.getTos(repository.getAllWithMenuByDate(LocalDate.now(),
                Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTo> get(@PathVariable int id) {
        log.info("get {}", id);
        return ResponseEntity.of(repository.getWithMenuByDate(id, LocalDate.now())
                .map(RestaurantsUtil::createTo));
    }

    @GetMapping("/{id}/full")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Restaurant> getFull(@PathVariable int id) {
        log.info("getFull {}", id);
        return ResponseEntity.of(repository.getWithMenu(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    @CacheEvict(value = "restaurants", allEntries = true)
    public ResponseEntity<Restaurant> create(@RequestBody Restaurant restaurant) {
        log.info("create {}", restaurant);
        checkIsNew(restaurant);
        Restaurant created = repository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    @CacheEvict(value = "restaurants", allEntries = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {} with id={}", restaurant, id);
        assureIdConsistent(restaurant, id);
        repository.save(restaurant);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    @CacheEvict(value = "restaurants", allEntries = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        repository.deleteExisted(id);
    }

    @GetMapping("/by-date")
    public List<RestaurantTo> getAllByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("getAllByDate {}", date);
        return RestaurantsUtil.getTos(repository.getAllWithMenuByDate(date,
                Sort.by(Sort.Direction.ASC, "name")));
    }
}