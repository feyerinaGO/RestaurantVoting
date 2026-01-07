package ru.develop.restaurantvoting.util;

import lombok.experimental.UtilityClass;
import ru.develop.restaurantvoting.model.Role;
import ru.develop.restaurantvoting.model.User;
import ru.develop.restaurantvoting.to.UserTo;

@UtilityClass
public class UsersUtil {

    public static User createNewFromTo(UserTo userTo) {
        return new User(null, userTo.getName(), userTo.getEmail().toLowerCase(), userTo.getPassword(), Role.USER);
    }

    public static User updateFromTo(User user, UserTo userTo) {
        user.setName(userTo.getName());
        user.setEmail(userTo.getEmail().toLowerCase());
        user.setPassword(userTo.getPassword());
        return user;
    }
}