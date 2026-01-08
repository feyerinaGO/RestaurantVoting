package ru.develop.restaurantvoting.restaurant.util;

import lombok.experimental.UtilityClass;
import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.restaurant.to.VoteTo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class VotesUtil {

    public static VoteTo createTo(Vote vote) {
        return new VoteTo(vote.getId(), vote.getRestaurant().getId(),
                vote.getRestaurant().getName(), vote.getVoteDate());
    }

    public static List<VoteTo> getTos(Collection<Vote> votes) {
        return votes.stream()
                .map(VotesUtil::createTo)
                .collect(Collectors.toList());
    }
}