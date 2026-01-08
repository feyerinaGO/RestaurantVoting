package ru.develop.restaurantvoting.restaurant.to;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.develop.restaurantvoting.common.to.NamedTo;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantTo extends NamedTo {

    public RestaurantTo(Integer id, String name) {
        super(id, name);
    }
}