package ru.develop.restaurantvoting.util;

import lombok.experimental.UtilityClass;
import ru.develop.restaurantvoting.model.MenuItem;
import ru.develop.restaurantvoting.to.MenuItemTo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class MenuItemsUtil {

    public static MenuItemTo createTo(MenuItem menuItem) {
        return new MenuItemTo(menuItem.getId(), menuItem.getMenuDate(),
                menuItem.getDescription(), menuItem.getPrice());
    }

    public static List<MenuItemTo> getTos(Collection<MenuItem> menuItems) {
        return menuItems.stream()
                .map(MenuItemsUtil::createTo)
                .collect(Collectors.toList());
    }
}