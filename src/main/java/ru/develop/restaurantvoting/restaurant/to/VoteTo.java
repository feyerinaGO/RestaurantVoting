package ru.develop.restaurantvoting.restaurant.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.develop.restaurantvoting.common.to.BaseTo;

import java.time.LocalDate;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteTo extends BaseTo {
    @NotNull
    Integer restaurantId;

    String restaurantName;
    LocalDate voteDate;

    public VoteTo(Integer id, @NotNull Integer restaurantId, String restaurantName, LocalDate voteDate) {
        super(id);
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.voteDate = voteDate;
    }
}