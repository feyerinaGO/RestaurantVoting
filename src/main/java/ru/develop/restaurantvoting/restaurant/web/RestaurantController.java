package ru.develop.restaurantvoting.restaurant.web;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.develop.restaurantvoting.restaurant.service.RestaurantService;
import ru.develop.restaurantvoting.restaurant.to.RestaurantTo;
import ru.develop.restaurantvoting.restaurant.to.RestaurantWithMenuTo;
import ru.develop.restaurantvoting.restaurant.util.RestaurantsUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class RestaurantController {
    static final String REST_URL = "/api/restaurants";

    private final RestaurantService restaurantService;

    @GetMapping
    public List<RestaurantTo> getAll() {
        log.info("getAll");
        return RestaurantsUtil.getTos(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTo> get(@PathVariable int id) {
        log.info("get {}", id);
        return restaurantService.getRestaurantOptional(id)
                .map(RestaurantsUtil::createTo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/with-menu/today")
    public List<RestaurantWithMenuTo> getAllWithMenuToday() {
        log.info("getAllWithMenuToday");
        return RestaurantsUtil.getWithMenuTos(restaurantService.getRestaurantsWithTodayMenu());
    }

    @GetMapping("/{id}/with-menu/today")
    public ResponseEntity<RestaurantWithMenuTo> getWithMenuToday(@PathVariable int id) {
        log.info("getWithMenuToday {}", id);
        return restaurantService.getRestaurantWithTodayMenu(id)
                .map(RestaurantsUtil::createWithMenuTo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/with-menu/by-date")
    public List<RestaurantWithMenuTo> getAllWithMenuByDate(
            @RequestParam @NotNull String date) {
        log.info("getAllWithMenuByDate {}", date);

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use ISO format (YYYY-MM-DD)");
        }

        return RestaurantsUtil.getWithMenuTos(restaurantService.getRestaurantsWithMenuByDate(parsedDate));
    }
}