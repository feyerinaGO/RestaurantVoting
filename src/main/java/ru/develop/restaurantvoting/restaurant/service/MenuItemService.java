package ru.develop.restaurantvoting.restaurant.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.common.error.DataConflictException;
import ru.develop.restaurantvoting.restaurant.model.MenuItem;
import ru.develop.restaurantvoting.restaurant.model.Restaurant;
import ru.develop.restaurantvoting.restaurant.repository.MenuItemRepository;
import ru.develop.restaurantvoting.restaurant.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public List<MenuItem> getRestaurantMenu(int restaurantId) {
        log.info("Get all menu items for restaurant {}", restaurantId);
        restaurantRepository.getExisted(restaurantId);
        return menuItemRepository.getAllByRestaurant(restaurantId);
    }

    public List<MenuItem> getMenuByDate(int restaurantId, LocalDate date) {
        log.info("Get menu items for restaurant {} on date {}", restaurantId, date);
        restaurantRepository.getExisted(restaurantId);
        return menuItemRepository.getByRestaurantAndDate(restaurantId, date);
    }

    public MenuItem getMenuItem(int restaurantId, int menuItemId) {
        log.info("Get menu item {} for restaurant {}", menuItemId, restaurantId);
        return menuItemRepository.getBelonged(restaurantId, menuItemId);
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public MenuItem createMenuItem(int restaurantId, MenuItem menuItem) {
        log.info("Create menu item for restaurant {}", restaurantId);
        Restaurant restaurant = restaurantRepository.getExisted(restaurantId);
        menuItem.setRestaurant(restaurant);

        menuItemRepository.findByRestaurantAndDateAndNameIgnoreCase(
                restaurantId,
                menuItem.getMenuDate(),
                menuItem.getName()
        ).ifPresent(existing -> {
            throw new DataConflictException(
                    "Menu item with name '" + menuItem.getName() +
                            "' already exists for this restaurant on " + menuItem.getMenuDate()
            );
        });

        return menuItemRepository.save(menuItem);
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void updateMenuItem(int restaurantId, int menuItemId, MenuItem menuItem) {
        log.info("Update menu item {} for restaurant {}", menuItemId, restaurantId);
        MenuItem existingItem = menuItemRepository.getBelonged(restaurantId, menuItemId);
        Restaurant restaurant = restaurantRepository.getExisted(restaurantId);

        if (!existingItem.getName().equalsIgnoreCase(menuItem.getName()) ||
                !existingItem.getMenuDate().equals(menuItem.getMenuDate())) {

            menuItemRepository.findByRestaurantAndDateAndNameIgnoreCase(
                    restaurantId,
                    menuItem.getMenuDate(),
                    menuItem.getName()
            ).ifPresent(duplicate -> {
                if (duplicate.id() != menuItemId) {
                    throw new DataConflictException(
                            "Menu item with name '" + menuItem.getName() +
                                    "' already exists for this restaurant on " + menuItem.getMenuDate()
                    );
                }
            });
        }

        menuItem.setId(menuItemId);
        menuItem.setRestaurant(restaurant);
        menuItemRepository.save(menuItem);
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void deleteMenuItem(int restaurantId, int menuItemId) {
        log.info("Delete menu item {} for restaurant {}", menuItemId, restaurantId);
        menuItemRepository.deleteExisted(menuItemId);
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void deleteMenuByDate(int restaurantId, LocalDate date) {
        log.info("Delete all menu items for restaurant {} on date {}", restaurantId, date);
        menuItemRepository.deleteByRestaurantAndDate(restaurantId, date);
    }
}