package ru.develop.restaurantvoting.util;

import lombok.experimental.UtilityClass;
import ru.develop.restaurantvoting.model.Restaurant;
import ru.develop.restaurantvoting.to.RestaurantTo;

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
}