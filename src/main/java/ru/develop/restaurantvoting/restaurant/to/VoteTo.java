package ru.develop.restaurantvoting.to;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.develop.restaurantvoting.common.to.BaseTo;

import java.time.LocalDate;

@Value
@EqualsAndHashCode(callSuper = true)
public class VoteTo extends BaseTo {
    Integer restaurantId;
    String restaurantName;
    LocalDate voteDate;

    public VoteTo(Integer id, Integer restaurantId, String restaurantName, LocalDate voteDate) {
        super(id);
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.voteDate = voteDate;
    }
}