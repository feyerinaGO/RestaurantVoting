package ru.develop.restaurantvoting.restaurant.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.restaurant.model.MenuItem;
import ru.develop.restaurantvoting.restaurant.repository.MenuItemRepository;
import ru.develop.restaurantvoting.restaurant.repository.RestaurantRepository;

@Service
@AllArgsConstructor
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public MenuItem save(int restaurantId, MenuItem menuItem) {
        menuItem.setRestaurant(restaurantRepository.getExisted(restaurantId));
        return menuItemRepository.save(menuItem);
    }
}