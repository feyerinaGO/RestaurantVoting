package ru.develop.restaurantvoting.restaurant.to;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.develop.restaurantvoting.common.to.BaseTo;
import ru.develop.restaurantvoting.common.validation.NoHtml;

import java.time.LocalDate;

@Value
@EqualsAndHashCode(callSuper = true)
public class MenuItemTo extends BaseTo {
    @NotNull
    LocalDate menuDate;

    @NotBlank
    @Size(min = 2, max = 128)
    @NoHtml
    String description;

    @NotNull
    @Positive
    Integer price;

    public MenuItemTo(Integer id, LocalDate menuDate, String description, Integer price) {
        super(id);
        this.menuDate = menuDate;
        this.description = description;
        this.price = price;
    }
}