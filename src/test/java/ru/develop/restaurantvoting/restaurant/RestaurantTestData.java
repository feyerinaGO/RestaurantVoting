package ru.develop.restaurantvoting.restaurant;

import ru.develop.restaurantvoting.MatcherFactory;
import ru.develop.restaurantvoting.restaurant.model.Restaurant;
import ru.develop.restaurantvoting.restaurant.to.RestaurantTo;

import java.util.List;

public class RestaurantTestData {
    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "menuItems", "votes");
    public static final MatcherFactory.Matcher<RestaurantTo> RESTAURANT_TO_MATCHER =
            MatcherFactory.usingEqualsNoOrderComparator(RestaurantTo.class);

    public static final int RESTAURANT1_ID = 1;
    public static final int RESTAURANT2_ID = 2;
    public static final int RESTAURANT3_ID = 3;
    public static final int NOT_FOUND = 100;

    public static final Restaurant restaurant1 = new Restaurant(RESTAURANT1_ID, "Burger King", "123456 Moscow City");
    public static final Restaurant restaurant2 = new Restaurant(RESTAURANT2_ID, "McDonalds", "123456 Moscow, Red Square");
    public static final Restaurant restaurant3 = new Restaurant(RESTAURANT3_ID, "KFC", "123456 Moscow, Paveletskaya");

    public static final List<Restaurant> restaurants = List.of(restaurant1, restaurant2, restaurant3);

    public static Restaurant getNew() {
        return new Restaurant(null, "New Restaurant", "New Address");
    }

    public static Restaurant getUpdated() {
        return new Restaurant(RESTAURANT1_ID, "Updated Name", "Updated Address");
    }

    public static RestaurantTo createTo(Restaurant restaurant) {
        return new RestaurantTo(restaurant.getId(), restaurant.getName());
    }
}