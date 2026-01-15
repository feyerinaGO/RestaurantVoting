package ru.develop.restaurantvoting.restaurant;

import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.user.UserTestData;

import java.time.LocalDate;

public class VoteTestData {
    public static final int VOTE1_ID = 1;
    public static final int VOTE2_ID = 2;
    public static final LocalDate TODAY = LocalDate.now();

    public static final Vote userVote1 = new Vote(VOTE1_ID, TODAY);
    public static final Vote adminVote1 = new Vote(VOTE2_ID, TODAY);

    static {
        userVote1.setRestaurant(RestaurantTestData.restaurant1);
        adminVote1.setRestaurant(RestaurantTestData.restaurant2);

        userVote1.setUser(UserTestData.user);
        adminVote1.setUser(UserTestData.admin);
    }
}