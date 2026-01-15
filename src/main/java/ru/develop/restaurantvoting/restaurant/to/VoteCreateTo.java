package ru.develop.restaurantvoting.restaurant.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteCreateTo {
    @NotNull(message = "Restaurant ID is required")
    Integer restaurantId;
}