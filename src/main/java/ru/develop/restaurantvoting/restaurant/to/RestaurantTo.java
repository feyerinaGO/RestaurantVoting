package ru.develop.restaurantvoting.restaurant.to;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.develop.restaurantvoting.common.to.NamedTo;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RestaurantTo extends NamedTo {

    public RestaurantTo(Integer id, String name) {
        super(id, name);
    }
}