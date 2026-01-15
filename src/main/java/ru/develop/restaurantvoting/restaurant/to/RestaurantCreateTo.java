package ru.develop.restaurantvoting.restaurant.to;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.develop.restaurantvoting.common.validation.NoHtml;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RestaurantCreateTo extends RestaurantTo {
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String address;

    public RestaurantCreateTo(Integer id, String name, String address) {
        super(id, name);
        this.address = address;
    }
}