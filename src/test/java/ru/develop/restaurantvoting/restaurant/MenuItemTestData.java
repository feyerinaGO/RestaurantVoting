package ru.develop.restaurantvoting.restaurant;

import ru.develop.restaurantvoting.MatcherFactory;
import ru.develop.restaurantvoting.restaurant.model.MenuItem;

import java.time.LocalDate;

public class MenuItemTestData {
    public static final MatcherFactory.Matcher<MenuItem> MENU_ITEM_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(MenuItem.class, "restaurant");

    public static final int MENU_ITEM1_ID = 1;
    public static final int MENU_ITEM4_ID = 4;
    public static final LocalDate TODAY = LocalDate.now();
    public static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    public static MenuItem getNew() {
        return new MenuItem(null, "New Item", TODAY, "New Description", 400);
    }

    public static MenuItem getUpdated() {
        return new MenuItem(MENU_ITEM1_ID, "Updated Whopper", TODAY, "Updated Big Burger", 550);
    }
}