package ru.develop.restaurantvoting.restaurant;

import ru.develop.restaurantvoting.MatcherFactory;
import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.restaurant.to.VoteTo;

import java.time.LocalDate;

public class VoteTestData {
    public static final MatcherFactory.Matcher<Vote> VOTE_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Vote.class, "user", "restaurant");
    public static final MatcherFactory.Matcher<VoteTo> VOTE_TO_MATCHER =
            MatcherFactory.usingEqualsComparator(VoteTo.class);

    public static final int VOTE1_ID = 1;
    public static final int VOTE2_ID = 2;
    public static final LocalDate TODAY = LocalDate.now();

    public static final Vote userVote1 = new Vote(VOTE1_ID, TODAY);
    public static final Vote adminVote1 = new Vote(VOTE2_ID, TODAY);

    static {
        userVote1.setRestaurant(RestaurantTestData.restaurant1);
        adminVote1.setRestaurant(RestaurantTestData.restaurant2);
    }

    public static VoteTo createTo(Vote vote) {
        return new VoteTo(vote.getId(), vote.getRestaurant().getId(),
                vote.getRestaurant().getName(), vote.getVoteDate());
    }
}