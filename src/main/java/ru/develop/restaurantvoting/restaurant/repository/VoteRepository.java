package ru.develop.restaurantvoting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.common.BaseRepository;
import ru.develop.restaurantvoting.common.error.NotFoundException;
import ru.develop.restaurantvoting.model.Vote;

import java.time.LocalDate;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.voteDate = :date")
    Optional<Vote> getByUserAndDate(int userId, LocalDate date);

    @Query("SELECT v FROM Vote v JOIN FETCH v.restaurant WHERE v.user.id = :userId ORDER BY v.voteDate DESC")
    java.util.List<Vote> getAllByUser(int userId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.restaurant.id = :restaurantId AND v.voteDate = :date")
    int countByRestaurantAndDate(int restaurantId, LocalDate date);

    default Vote getExistedByUserAndDate(int userId, LocalDate date) {
        return getByUserAndDate(userId, date)
                .orElseThrow(() -> new NotFoundException("Vote for date=" + date + " not found"));
    }
}