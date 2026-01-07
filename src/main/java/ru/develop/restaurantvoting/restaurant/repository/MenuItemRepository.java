package ru.develop.restaurantvoting.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.common.BaseRepository;
import ru.develop.restaurantvoting.common.error.NotFoundException;
import ru.develop.restaurantvoting.model.MenuItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MenuItemRepository extends BaseRepository<MenuItem> {

    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId ORDER BY m.menuDate DESC")
    List<MenuItem> getAllByRestaurant(int restaurantId);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.menuDate = :date ORDER BY m.id")
    List<MenuItem> getByRestaurantAndDate(int restaurantId, LocalDate date);

    @Query("SELECT m FROM MenuItem m WHERE m.id = :id AND m.restaurant.id = :restaurantId")
    Optional<MenuItem> get(int restaurantId, int id);

    @Transactional
    @Modifying
    @Query("DELETE FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.menuDate = :date")
    void deleteByRestaurantAndDate(int restaurantId, LocalDate date);

    default MenuItem getBelonged(int restaurantId, int id) {
        return get(restaurantId, id).orElseThrow(() ->
                new NotFoundException("Menu item id=" + id + " doesn't belong to Restaurant id=" + restaurantId));
    }
}