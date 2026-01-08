package ru.develop.restaurantvoting.user;

import ru.develop.restaurantvoting.MatcherFactory;
import ru.develop.restaurantvoting.common.util.JsonUtil;
import ru.develop.restaurantvoting.user.model.Role;
import ru.develop.restaurantvoting.user.model.User;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.*;
import static ru.develop.restaurantvoting.restaurant.VoteTestData.*;

public class UserTestData {
    public static final MatcherFactory.Matcher<User> USER_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class, "registered", "votes", "password");
    public static MatcherFactory.Matcher<User> USER_WITH_VOTES_MATCHER =
            MatcherFactory.usingAssertions(User.class,
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("registered", "votes.user", "votes.restaurant.menuItems", "votes.restaurant.votes", "password").isEqualTo(e),
                    (a, e) -> {
                        throw new UnsupportedOperationException();
                    });

    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 2;
    public static final int NOT_FOUND = 100;
    public static final String USER_MAIL = "user@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";

    public static final User user = new User(USER_ID, "User", USER_MAIL, "password", Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", ADMIN_MAIL, "admin", Role.ADMIN, Role.USER);

    static {
        user.setVotes(List.of(userVote1));
        admin.setVotes(List.of(adminVote1));
        restaurant1.setVotes(List.of(userVote1));
        restaurant2.setVotes(List.of(adminVote1));
    }

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", false, new Date(), Collections.singleton(Role.USER));
    }

    public static User getUpdated() {
        return new User(USER_ID, "UpdatedName", USER_MAIL, "newPass", false, new Date(), List.of(Role.ADMIN));
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }
}