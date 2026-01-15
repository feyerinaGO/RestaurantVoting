package ru.develop.restaurantvoting.restaurant.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.develop.restaurantvoting.restaurant.model.Restaurant;
import ru.develop.restaurantvoting.restaurant.service.RestaurantService;
import ru.develop.restaurantvoting.restaurant.to.RestaurantCreateTo;

import java.net.URI;

@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class AdminRestaurantController {
    static final String REST_URL = "/api/admin/restaurants";

    private final RestaurantService restaurantService;

    @GetMapping("/{id}/with-menu")
    public ResponseEntity<Restaurant> getWithMenu(@PathVariable int id) {
        log.info("getWithMenu {}", id);
        return restaurantService.getRestaurantWithMenu(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@PathVariable int id) {
        log.info("get {}", id);
        return restaurantService.getRestaurantOptional(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> create(@Valid @RequestBody RestaurantCreateTo restaurantTo) {
        log.info("create {}", restaurantTo);
        Restaurant restaurant = new Restaurant(null, restaurantTo.getName(), restaurantTo.getAddress());
        Restaurant created = restaurantService.createRestaurant(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody RestaurantCreateTo restaurantTo, @PathVariable int id) {
        log.info("update {} with id={}", restaurantTo, id);
        Restaurant restaurant = new Restaurant(id, restaurantTo.getName(), restaurantTo.getAddress());
        restaurantService.updateRestaurant(id, restaurant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        restaurantService.deleteRestaurant(id);
    }
}