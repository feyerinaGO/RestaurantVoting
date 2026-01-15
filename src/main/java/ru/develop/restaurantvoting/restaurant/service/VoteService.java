package ru.develop.restaurantvoting.restaurant.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.common.error.DataConflictException;
import ru.develop.restaurantvoting.common.error.IllegalRequestDataException;
import ru.develop.restaurantvoting.common.error.NotFoundException;
import ru.develop.restaurantvoting.common.util.TimeProvider;
import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.restaurant.repository.RestaurantRepository;
import ru.develop.restaurantvoting.restaurant.repository.VoteRepository;
import ru.develop.restaurantvoting.user.model.User;
import ru.develop.restaurantvoting.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class VoteService {
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final TimeProvider timeProvider;

    public Vote getTodayVote(int userId) {
        log.info("Get today's vote for user {}", userId);
        return voteRepository.getByUserAndDate(userId, timeProvider.getCurrentDate())
                .orElseThrow(() -> new NotFoundException("Vote for today not found"));
    }

    public List<Vote> getUserVotes(int userId) {
        log.info("Get all votes for user {}", userId);
        return voteRepository.getAllByUser(userId);
    }

    @Transactional
    public Vote createVote(int userId, int restaurantId) {
        log.info("Create vote for restaurant {} by user {}", restaurantId, userId);

        LocalDate today = timeProvider.getCurrentDate();

        if (voteRepository.getByUserAndDate(userId, today).isPresent()) {
            throw new DataConflictException("User has already voted today");
        }

        Vote vote = new Vote(null, today);
        vote.setUser(userRepository.getExisted(userId));
        vote.setRestaurant(restaurantRepository.getExisted(restaurantId));
        return voteRepository.save(vote);
    }

    @Transactional
    public void updateVote(int userId, int restaurantId) {
        log.info("Update vote for restaurant {} by user {}", restaurantId, userId);

        LocalDate today = timeProvider.getCurrentDate();
        Vote vote = voteRepository.getByUserAndDate(userId, today)
                .orElseThrow(() -> new NotFoundException("No vote found for today"));

        if (!timeProvider.canChangeVote(today)) {
            throw new IllegalRequestDataException("Cannot change vote after " + TimeProvider.VOTE_DEADLINE);
        }

        vote.setRestaurant(restaurantRepository.getExisted(restaurantId));
        voteRepository.save(vote);
    }
}