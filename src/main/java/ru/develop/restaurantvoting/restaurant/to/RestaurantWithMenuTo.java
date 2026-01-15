package ru.develop.restaurantvoting.restaurant.to;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RestaurantWithMenuTo extends RestaurantTo {
    private List<MenuItemTo> menuItems;

    public RestaurantWithMenuTo(Integer id, String name, List<MenuItemTo> menuItems) {
        super(id, name);
        this.menuItems = menuItems;
    }
}