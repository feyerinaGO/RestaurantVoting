package ru.develop.restaurantvoting.restaurant.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.restaurant.repository.RestaurantRepository;
import ru.develop.restaurantvoting.restaurant.repository.VoteRepository;
import ru.develop.restaurantvoting.user.repository.UserRepository;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public Vote save(int userId, int restaurantId, Vote vote) {
        vote.setUser(userRepository.getExisted(userId));
        vote.setRestaurant(restaurantRepository.getExisted(restaurantId));
        return voteRepository.save(vote);
    }
}