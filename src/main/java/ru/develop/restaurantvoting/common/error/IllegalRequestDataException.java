package ru.develop.restaurantvoting.common.error;

import static ru.develop.restaurantvoting.common.error.ErrorType.BAD_REQUEST;

public class IllegalRequestDataException extends AppException {
    public IllegalRequestDataException(String msg) {
        super(msg, BAD_REQUEST);
    }
}