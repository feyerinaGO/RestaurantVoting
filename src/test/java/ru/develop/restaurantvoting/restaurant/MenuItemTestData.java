package ru.develop.restaurantvoting.restaurant;

import ru.develop.restaurantvoting.MatcherFactory;
import ru.develop.restaurantvoting.restaurant.model.MenuItem;
import ru.develop.restaurantvoting.restaurant.to.MenuItemTo;

import java.time.LocalDate;
import java.util.List;

public class MenuItemTestData {
    public static final MatcherFactory.Matcher<MenuItem> MENU_ITEM_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(MenuItem.class, "restaurant");
    public static final MatcherFactory.Matcher<MenuItemTo> MENU_ITEM_TO_MATCHER =
            MatcherFactory.usingEqualsComparator(MenuItemTo.class);

    public static final int MENU_ITEM1_ID = 1;
    public static final int MENU_ITEM4_ID = 4;
    public static final LocalDate TODAY = LocalDate.now();
    public static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    public static final MenuItem menuItem1 = new MenuItem(MENU_ITEM1_ID, "Whopper", TODAY, "Big Burger", 500);
    public static final MenuItem menuItem2 = new MenuItem(MENU_ITEM1_ID + 1, "Cheeseburger", TODAY, "Cheese Burger", 300);
    public static final MenuItem menuItem3 = new MenuItem(MENU_ITEM1_ID + 2, "Fries", TODAY, "French Fries", 200);
    public static final MenuItem menuItem4 = new MenuItem(MENU_ITEM4_ID, "Big Mac", TODAY, "Big Mac Burger", 550);
    public static final MenuItem menuItem5 = new MenuItem(MENU_ITEM4_ID + 1, "McChicken", TODAY, "Chicken Burger", 450);
    public static final MenuItem menuItem6 = new MenuItem(MENU_ITEM4_ID + 2, "Nuggets", TODAY, "Chicken Nuggets", 350);
    public static final MenuItem menuItem7 = new MenuItem(MENU_ITEM4_ID + 3, "Original Recipe", TODAY, "Fried Chicken", 600);
    public static final MenuItem menuItem8 = new MenuItem(MENU_ITEM4_ID + 4, "Zinger", TODAY, "Spicy Chicken", 650);
    public static final MenuItem menuItem9 = new MenuItem(MENU_ITEM4_ID + 5, "Twister", TODAY, "Chicken Wrap", 500);

    public static final List<MenuItem> restaurant1MenuToday = List.of(menuItem1, menuItem2, menuItem3);
    public static final List<MenuItem> restaurant2MenuToday = List.of(menuItem4, menuItem5, menuItem6);
    public static final List<MenuItem> restaurant3MenuToday = List.of(menuItem7, menuItem8, menuItem9);

    public static final MenuItem yesterdayMenuItem1 = new MenuItem(10, "Whopper", YESTERDAY, "Big Burger", 500);
    public static final MenuItem yesterdayMenuItem2 = new MenuItem(11, "Cheeseburger", YESTERDAY, "Cheese Burger", 300);
    public static final MenuItem yesterdayMenuItem3 = new MenuItem(12, "Big Mac", YESTERDAY, "Big Mac Burger", 550);

    public static MenuItem getNew() {
        return new MenuItem(null, "New Item", TODAY, "New Description", 400);
    }

    public static MenuItem getUpdated() {
        return new MenuItem(MENU_ITEM1_ID, "Updated Whopper", TODAY, "Updated Big Burger", 550);
    }

    public static MenuItemTo createTo(MenuItem menuItem) {
        return new MenuItemTo(menuItem.getId(), menuItem.getMenuDate(), menuItem.getDescription(), menuItem.getPrice());
    }
}