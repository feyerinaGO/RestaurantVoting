package ru.develop.restaurantvoting.restaurant.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.common.BaseRepository;
import ru.develop.restaurantvoting.restaurant.model.Restaurant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Query("SELECT DISTINCT r FROM Restaurant r LEFT JOIN FETCH r.menuItems m WHERE m.menuDate = :date")
    List<Restaurant> getAllWithMenuByDate(LocalDate date, Sort sort);

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.menuItems WHERE r.id = :id")
    Optional<Restaurant> getWithMenu(int id);

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.menuItems m WHERE r.id = :id AND m.menuDate = :date")
    Optional<Restaurant> getWithMenuByDate(int id, LocalDate date);
}