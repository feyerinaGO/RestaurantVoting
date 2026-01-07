package ru.develop.restaurantvoting.error;

import static ru.develop.restaurantvoting.error.ErrorType.NOT_FOUND;

public class NotFoundException extends AppException {
    public NotFoundException(String msg) {
        super(msg, NOT_FOUND);
    }
}