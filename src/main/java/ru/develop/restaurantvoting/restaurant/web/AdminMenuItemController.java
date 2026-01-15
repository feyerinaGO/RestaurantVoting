package ru.develop.restaurantvoting.restaurant.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.develop.restaurantvoting.restaurant.model.MenuItem;
import ru.develop.restaurantvoting.restaurant.service.MenuItemService;
import ru.develop.restaurantvoting.restaurant.to.MenuItemTo;
import ru.develop.restaurantvoting.restaurant.util.MenuItemsUtil;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.develop.restaurantvoting.common.util.ValidationUtil.assureIdConsistent;
import static ru.develop.restaurantvoting.common.util.ValidationUtil.checkIsNew;

@RestController
@RequestMapping(value = AdminMenuItemController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class AdminMenuItemController {
    static final String REST_URL = "/api/admin/restaurants/{restaurantId}/menu-items";

    private final MenuItemService menuItemService;

    @GetMapping
    public List<MenuItemTo> getAll(@PathVariable int restaurantId) {
        log.info("getAll for restaurant {}", restaurantId);
        return MenuItemsUtil.getTos(menuItemService.getRestaurantMenu(restaurantId));
    }

    @GetMapping("/{id}")
    public MenuItemTo get(@PathVariable int restaurantId, @PathVariable int id) {
        log.info("get {} for restaurant {}", id, restaurantId);
        return MenuItemsUtil.createTo(menuItemService.getMenuItem(restaurantId, id));
    }

    @GetMapping("/by-date")
    public List<MenuItemTo> getByDate(@PathVariable int restaurantId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("getByDate {} for restaurant {}", date, restaurantId);
        return MenuItemsUtil.getTos(menuItemService.getMenuByDate(restaurantId, date));
    }

    @PostMapping
    public ResponseEntity<MenuItem> create(@PathVariable int restaurantId, @Valid @RequestBody MenuItem menuItem) {
        log.info("create {} for restaurant {}", menuItem, restaurantId);
        checkIsNew(menuItem);
        MenuItem created = menuItemService.createMenuItem(restaurantId, menuItem);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int restaurantId, @Valid @RequestBody MenuItem menuItem, @PathVariable int id) {
        log.info("update {} with id={} for restaurant {}", menuItem, id, restaurantId);
        assureIdConsistent(menuItem, id);
        menuItemService.updateMenuItem(restaurantId, id, menuItem);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int restaurantId, @PathVariable int id) {
        log.info("delete {} for restaurant {}", id, restaurantId);
        menuItemService.deleteMenuItem(restaurantId, id);
    }

    @DeleteMapping("/by-date")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByDate(@PathVariable int restaurantId,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("deleteByDate {} for restaurant {}", date, restaurantId);
        menuItemService.deleteMenuByDate(restaurantId, date);
    }
}