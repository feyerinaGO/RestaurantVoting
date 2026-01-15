package ru.develop.restaurantvoting.restaurant.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.develop.restaurantvoting.app.AuthUser;
import ru.develop.restaurantvoting.restaurant.service.VoteService;
import ru.develop.restaurantvoting.restaurant.to.VoteCreateTo;
import ru.develop.restaurantvoting.restaurant.to.VoteTo;
import ru.develop.restaurantvoting.restaurant.util.VotesUtil;

import java.util.List;

@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class VoteController {
    static final String REST_URL = "/api/profile/votes";

    private final VoteService voteService;

    @GetMapping
    public List<VoteTo> getAll(@AuthenticationPrincipal AuthUser authUser) {
        log.info("getAll for user {}", authUser.id());
        return VotesUtil.getTos(voteService.getUserVotes(authUser.id()));
    }

    @GetMapping("/today")
    public ResponseEntity<VoteTo> getToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("getToday for user {}", authUser.id());
        try {
            return ResponseEntity.ok(VotesUtil.createTo(voteService.getTodayVote(authUser.id())));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public VoteTo createVote(@AuthenticationPrincipal AuthUser authUser,
                             @Valid @RequestBody VoteCreateTo voteTo) {
        log.info("create vote for restaurant {} by user {}", voteTo.getRestaurantId(), authUser.id());
        return VotesUtil.createTo(voteService.createVote(authUser.id(), voteTo.getRestaurantId()));
    }

    @PutMapping(value = "/today", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVote(@AuthenticationPrincipal AuthUser authUser,
                           @Valid @RequestBody VoteCreateTo voteTo) {
        log.info("update vote for restaurant {} by user {}", voteTo.getRestaurantId(), authUser.id());
        voteService.updateVote(authUser.id(), voteTo.getRestaurantId());
    }
}