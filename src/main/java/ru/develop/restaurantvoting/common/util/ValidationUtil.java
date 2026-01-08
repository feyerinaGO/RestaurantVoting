package ru.develop.restaurantvoting.common.util;

import lombok.experimental.UtilityClass;
import ru.develop.restaurantvoting.common.HasId;
import ru.develop.restaurantvoting.common.error.IllegalRequestDataException;

@UtilityClass
public class ValidationUtil {

    public static void checkIsNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must has id=" + id);
        }
    }
}