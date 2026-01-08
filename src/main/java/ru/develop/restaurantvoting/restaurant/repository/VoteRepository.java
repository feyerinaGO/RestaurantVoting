package ru.develop.restaurantvoting.restaurant.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.common.BaseRepository;
import ru.develop.restaurantvoting.common.error.DataConflictException;
import ru.develop.restaurantvoting.common.error.NotFoundException;
import ru.develop.restaurantvoting.restaurant.model.Vote;

import java.time.LocalDate;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.voteDate = :date")
    Optional<Vote> getByUserAndDate(int userId, LocalDate date);

    @Query("SELECT v FROM Vote v JOIN FETCH v.restaurant WHERE v.user.id = :userId ORDER BY v.voteDate DESC")
    java.util.List<Vote> getAllByUser(int userId);

    @Query("SELECT v FROM Vote v WHERE v.id = :id and v.user.id = :userId")
    Optional<Vote> get(int userId, int id);

    default Vote getBelonged(int userId, int id) {
        return get(userId, id).orElseThrow(() ->
                new DataConflictException("Vote id=" + id + " doesn't belong to User id=" + userId));
    }

    default Vote getExistedByUserAndDate(int userId, LocalDate date) {
        return getByUserAndDate(userId, date)
                .orElseThrow(() -> new NotFoundException("Vote for date=" + date + " not found"));
    }
}