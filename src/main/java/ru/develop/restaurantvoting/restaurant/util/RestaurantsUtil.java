package ru.develop.restaurantvoting.restaurant.util;

import lombok.experimental.UtilityClass;
import ru.develop.restaurantvoting.restaurant.model.Restaurant;
import ru.develop.restaurantvoting.restaurant.to.MenuItemTo;
import ru.develop.restaurantvoting.restaurant.to.RestaurantTo;
import ru.develop.restaurantvoting.restaurant.to.RestaurantWithMenuTo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RestaurantsUtil {

    public static RestaurantTo createTo(Restaurant restaurant) {
        return new RestaurantTo(restaurant.getId(), restaurant.getName());
    }

    public static List<RestaurantTo> getTos(Collection<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantsUtil::createTo)
                .collect(Collectors.toList());
    }

    public static RestaurantWithMenuTo createWithMenuTo(Restaurant restaurant) {
        List<MenuItemTo> menuItems = restaurant.getMenuItems() != null ?
                restaurant.getMenuItems().stream()
                        .filter(menuItem -> menuItem.getMenuDate().equals(LocalDate.now()))
                        .map(menuItem -> new MenuItemTo(
                                menuItem.getId(),
                                menuItem.getMenuDate(),
                                menuItem.getName(),
                                menuItem.getDescription(),
                                menuItem.getPrice()))
                        .collect(Collectors.toList()) : List.of();
        return new RestaurantWithMenuTo(restaurant.getId(), restaurant.getName(), menuItems);
    }

    public static List<RestaurantWithMenuTo> getWithMenuTos(Collection<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantsUtil::createWithMenuTo)
                .collect(Collectors.toList());
    }
}