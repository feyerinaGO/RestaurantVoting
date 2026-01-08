package ru.develop.restaurantvoting.restaurant.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.develop.restaurantvoting.app.AuthUser;
import ru.develop.restaurantvoting.common.error.IllegalRequestDataException;
import ru.develop.restaurantvoting.common.util.TimeProvider;
import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.restaurant.repository.RestaurantRepository;
import ru.develop.restaurantvoting.restaurant.repository.VoteRepository;
import ru.develop.restaurantvoting.restaurant.service.VoteService;
import ru.develop.restaurantvoting.restaurant.to.VoteTo;
import ru.develop.restaurantvoting.restaurant.util.VotesUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class VoteController {
    static final String REST_URL = "/api/profile/votes";
    private static final LocalTime VOTE_DEADLINE = LocalTime.of(11, 0);

    private final VoteRepository repository;
    private final RestaurantRepository restaurantRepository;
    private final VoteService voteService;
    private final TimeProvider timeProvider;

    @GetMapping
    public List<VoteTo> getAll(@AuthenticationPrincipal AuthUser authUser) {
        log.info("getAll for user {}", authUser.id());
        return VotesUtil.getTos(repository.getAllByUser(authUser.id()));
    }

    @GetMapping("/today")
    public ResponseEntity<VoteTo> getToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("getToday for user {}", authUser.id());
        return ResponseEntity.of(repository.getByUserAndDate(authUser.id(), LocalDate.now())
                .map(VotesUtil::createTo));
    }

    @PostMapping
    public ResponseEntity<VoteTo> vote(@AuthenticationPrincipal AuthUser authUser,
                                       @RequestParam int restaurantId) {
        log.info("vote for restaurant {} by user {}", restaurantId, authUser.id());
        LocalDate today = LocalDate.now();
        Optional<Vote> existingVote = repository.getByUserAndDate(authUser.id(), today);

        if (existingVote.isPresent()) {
            if (!timeProvider.isBeforeDeadline()) {
                throw new IllegalRequestDataException("Cannot change vote after " + VOTE_DEADLINE);
            }
            Vote vote = existingVote.get();
            vote.setRestaurant(restaurantRepository.getExisted(restaurantId));
            repository.getBelonged(authUser.id(), vote.id());
            repository.save(vote);
            return ResponseEntity.ok(VotesUtil.createTo(vote));
        }

        Vote newVote = new Vote(null, today);
        Vote created = voteService.save(authUser.id(), restaurantId, newVote);
        return ResponseEntity.status(HttpStatus.CREATED).body(VotesUtil.createTo(created));
    }

    @DeleteMapping("/today")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("deleteToday for user {}", authUser.id());
        Vote vote = repository.getExistedByUserAndDate(authUser.id(), LocalDate.now());
        if (!timeProvider.isBeforeDeadline()) {
            throw new IllegalRequestDataException("Cannot delete vote after " + VOTE_DEADLINE);
        }
        repository.delete(vote);
    }
}