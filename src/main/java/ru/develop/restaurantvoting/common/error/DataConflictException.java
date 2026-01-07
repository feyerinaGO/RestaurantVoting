package ru.develop.restaurantvoting.error;

import static ru.develop.restaurantvoting.error.ErrorType.DATA_CONFLICT;

public class DataConflictException extends AppException {
    public DataConflictException(String msg) {
        super(msg, DATA_CONFLICT);
    }
}