package ru.develop.restaurantvoting.restaurant.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.common.error.DataConflictException;
import ru.develop.restaurantvoting.restaurant.model.Restaurant;
import ru.develop.restaurantvoting.restaurant.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    @Cacheable("restaurants")
    public List<Restaurant> getRestaurantsWithTodayMenu() {
        log.info("Get all restaurants with today's menu");
        return restaurantRepository.getAllWithMenuByDate(LocalDate.now());
    }

    @Cacheable("restaurants")
    public List<Restaurant> getRestaurantsWithMenuByDate(LocalDate date) {
        log.info("Get all restaurants with menu for date {}", date);
        return restaurantRepository.getAllWithMenuByDate(date);
    }

    public Optional<Restaurant> getRestaurantWithTodayMenu(int id) {
        log.info("Get restaurant {} with today's menu", id);
        return restaurantRepository.getWithMenuByDate(id, LocalDate.now());
    }

    public Optional<Restaurant> getRestaurantWithMenu(int id) {
        log.info("Get restaurant {} with all menu", id);
        return restaurantRepository.getWithMenu(id);
    }

    public Optional<Restaurant> getRestaurantOptional(int id) {
        log.info("Get restaurant {} (optional)", id);
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> getAllRestaurants() {
        log.info("Get all restaurants");
        return restaurantRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public Restaurant createRestaurant(Restaurant restaurant) {
        log.info("Create restaurant {}", restaurant.getName());

        restaurantRepository.findByNameIgnoreCase(restaurant.getName())
                .ifPresent(existing -> {
                    throw new DataConflictException("Restaurant with name '" + restaurant.getName() + "' already exists");
                });

        return restaurantRepository.save(restaurant);
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public Restaurant updateRestaurant(int id, Restaurant restaurant) {
        log.info("Update restaurant {}", id);
        Restaurant existing = restaurantRepository.getExisted(id);

        if (!existing.getName().equalsIgnoreCase(restaurant.getName())) {
            restaurantRepository.findByNameIgnoreCase(restaurant.getName())
                    .ifPresent(duplicate -> {
                        if (duplicate.id() != id) {
                            throw new DataConflictException("Restaurant with name '" + restaurant.getName() + "' already exists");
                        }
                    });
        }

        existing.setName(restaurant.getName());
        existing.setAddress(restaurant.getAddress());
        return restaurantRepository.save(existing);
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void deleteRestaurant(int id) {
        log.info("Delete restaurant {}", id);
        restaurantRepository.deleteExisted(id);
    }
}