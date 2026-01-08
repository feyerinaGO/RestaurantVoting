package ru.develop.restaurantvoting.common.error;

import static ru.develop.restaurantvoting.common.error.ErrorType.DATA_CONFLICT;

public class DataConflictException extends AppException {
    public DataConflictException(String msg) {
        super(msg, DATA_CONFLICT);
    }
}