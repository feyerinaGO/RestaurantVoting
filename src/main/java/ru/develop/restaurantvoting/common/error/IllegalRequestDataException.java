package ru.develop.restaurantvoting.error;

import static ru.develop.restaurantvoting.error.ErrorType.BAD_REQUEST;

public class IllegalRequestDataException extends AppException {
    public IllegalRequestDataException(String msg) {
        super(msg, BAD_REQUEST);
    }
}