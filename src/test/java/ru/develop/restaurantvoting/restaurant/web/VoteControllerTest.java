package ru.develop.restaurantvoting.restaurant.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.develop.restaurantvoting.AbstractControllerTest;
import ru.develop.restaurantvoting.common.util.JsonUtil;
import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.restaurant.repository.VoteRepository;
import ru.develop.restaurantvoting.restaurant.to.VoteTo;
import ru.develop.restaurantvoting.user.model.User;
import ru.develop.restaurantvoting.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.RESTAURANT1_ID;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.RESTAURANT2_ID;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.RESTAURANT3_ID;
import static ru.develop.restaurantvoting.restaurant.VoteTestData.*;
import static ru.develop.restaurantvoting.restaurant.web.VoteController.REST_URL;
import static ru.develop.restaurantvoting.user.UserTestData.*;

class VoteControllerTest extends AbstractControllerTest {

    @Autowired
    private VoteRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content -> {
                    String json = content.getResponse().getContentAsString();
                    List<VoteTo> voteTos = JsonUtil.readValues(json, VoteTo.class);
                    assertThat(voteTos).hasSize(1);
                    assertThat(voteTos.get(0).getRestaurantId()).isEqualTo(RESTAURANT1_ID);
                });
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllForAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content -> {
                    String json = content.getResponse().getContentAsString();
                    List<VoteTo> voteTos = JsonUtil.readValues(json, VoteTo.class);
                    assertThat(voteTos).hasSize(1);
                    assertThat(voteTos.get(0).getRestaurantId()).isEqualTo(RESTAURANT2_ID);
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content -> {
                    String json = content.getResponse().getContentAsString();
                    VoteTo voteTo = JsonUtil.readValue(json, VoteTo.class);
                    assertThat(voteTo.getRestaurantId()).isEqualTo(RESTAURANT1_ID);
                    assertThat(voteTo.getVoteDate()).isEqualTo(LocalDate.now());
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getTodayWhenNoVote() throws Exception {
        repository.delete(userVote1);

        perform(MockMvcRequestBuilders.get(REST_URL + "/today"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(content().string(""));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void vote() throws Exception {
        repository.delete(userVote1);

        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT3_ID)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String json = action.andReturn().getResponse().getContentAsString();
        VoteTo voteTo = JsonUtil.readValue(json, VoteTo.class);
        assertThat(voteTo.getRestaurantId()).isEqualTo(RESTAURANT3_ID);
        assertThat(voteTo.getVoteDate()).isEqualTo(LocalDate.now());

        List<Vote> userVotes = repository.getAllByUser(USER_ID);
        assertThat(userVotes).hasSize(1);
        assertThat(userVotes.get(0).getRestaurant().getId()).isEqualTo(RESTAURANT3_ID);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void voteChangeBeforeDeadline() throws Exception {
        Vote originalVote = repository.getByUserAndDate(USER_ID, LocalDate.now()).orElseThrow();

        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT3_ID)));

        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            action.andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

            String json = action.andReturn().getResponse().getContentAsString();
            VoteTo voteTo = JsonUtil.readValue(json, VoteTo.class);
            assertThat(voteTo.getRestaurantId()).isEqualTo(RESTAURANT3_ID);

            Vote updatedVote = repository.getByUserAndDate(USER_ID, LocalDate.now()).orElseThrow();
            assertThat(updatedVote.getRestaurant().getId()).isEqualTo(RESTAURANT3_ID);
            assertThat(updatedVote.getId()).isEqualTo(originalVote.getId());
        } else {
            action.andExpect(status().isUnprocessableContent())
                    .andExpect(content().string(containsString("Cannot change vote after")));

            Vote existingVote = repository.getByUserAndDate(USER_ID, LocalDate.now()).orElseThrow();
            assertThat(existingVote.getRestaurant().getId()).isEqualTo(RESTAURANT1_ID);
        }
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void voteForNonExistentRestaurant() throws Exception {
        repository.delete(userVote1);

        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", "999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void voteWithoutRestaurantId() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteToday() throws Exception {
        if (repository.getByUserAndDate(USER_ID, LocalDate.now()).isEmpty()) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurantId", String.valueOf(RESTAURANT1_ID)))
                    .andExpect(status().isCreated());
        }

        perform(MockMvcRequestBuilders.delete(REST_URL + "/today"))
                .andDo(print());

        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            perform(MockMvcRequestBuilders.delete(REST_URL + "/today"))
                    .andExpect(status().isNoContent());

            assertThat(repository.getByUserAndDate(USER_ID, LocalDate.now())).isEmpty();
        } else {
            perform(MockMvcRequestBuilders.delete(REST_URL + "/today"))
                    .andExpect(status().isUnprocessableContent());

            assertThat(repository.getByUserAndDate(USER_ID, LocalDate.now())).isPresent();
        }
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteTodayWhenNoVote() throws Exception {
        repository.getByUserAndDate(USER_ID, LocalDate.now())
                .ifPresent(repository::delete);

        perform(MockMvcRequestBuilders.delete(REST_URL + "/today"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void voteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testVoteSequence() throws Exception {
        repository.getByUserAndDate(USER_ID, LocalDate.now())
                .ifPresent(repository::delete);

        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID)))
                .andExpect(status().isCreated());

        Vote vote1 = repository.getByUserAndDate(USER_ID, LocalDate.now()).orElseThrow();
        assertThat(vote1.getRestaurant().getId()).isEqualTo(RESTAURANT1_ID);

        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurantId", String.valueOf(RESTAURANT2_ID)))
                    .andExpect(status().isOk());

            Vote vote2 = repository.getByUserAndDate(USER_ID, LocalDate.now()).orElseThrow();
            assertThat(vote2.getRestaurant().getId()).isEqualTo(RESTAURANT2_ID);
            assertThat(vote2.getId()).isEqualTo(vote1.getId());
        } else {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurantId", String.valueOf(RESTAURANT2_ID)))
                    .andExpect(status().isUnprocessableContent());

            Vote vote2 = repository.getByUserAndDate(USER_ID, LocalDate.now()).orElseThrow();
            assertThat(vote2.getRestaurant().getId()).isEqualTo(RESTAURANT1_ID);
        }
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void voteForDifferentUser() throws Exception {
        repository.getByUserAndDate(ADMIN_ID, LocalDate.now())
                .ifPresent(repository::delete);

        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT3_ID)))
                .andExpect(status().isCreated());

        Vote adminVote = repository.getByUserAndDate(ADMIN_ID, LocalDate.now()).orElseThrow();
        assertThat(adminVote.getRestaurant().getId()).isEqualTo(RESTAURANT3_ID);
        repository.getByUserAndDate(USER_ID, LocalDate.now())
                .ifPresent(userVote -> {
                    assertThat(userVote.getRestaurant().getId()).isEqualTo(RESTAURANT1_ID);
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getVotingHistory() throws Exception {
        User user = userRepository.getExisted(USER_ID);

        Vote yesterdayVote = new Vote(null, LocalDate.now().minusDays(1));
        yesterdayVote.setRestaurant(userVote1.getRestaurant());
        yesterdayVote.setUser(user);
        yesterdayVote = repository.save(yesterdayVote);

        try {
            perform(MockMvcRequestBuilders.get(REST_URL))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content -> {
                        String json = content.getResponse().getContentAsString();
                        List<VoteTo> voteTos = JsonUtil.readValues(json, VoteTo.class);
                        assertThat(voteTos.size()).isGreaterThanOrEqualTo(1);

                        boolean foundYesterdayVote = voteTos.stream()
                                .anyMatch(vote -> vote.getVoteDate().equals(LocalDate.now().minusDays(1)));
                        assertThat(foundYesterdayVote).isTrue();
                    });
        } finally {
            repository.delete(yesterdayVote);
        }
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testVoteAndDeleteScenario() throws Exception {
        repository.getByUserAndDate(USER_ID, LocalDate.now())
                .ifPresent(repository::delete);

        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT2_ID)))
                .andExpect(status().isCreated());

        assertThat(repository.getByUserAndDate(USER_ID, LocalDate.now())).isPresent();

        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            perform(MockMvcRequestBuilders.delete(REST_URL + "/today"))
                    .andExpect(status().isNoContent());

            assertThat(repository.getByUserAndDate(USER_ID, LocalDate.now())).isEmpty();
        } else {
            perform(MockMvcRequestBuilders.delete(REST_URL + "/today"))
                    .andExpect(status().isUnprocessableContent());

            assertThat(repository.getByUserAndDate(USER_ID, LocalDate.now())).isPresent();
        }
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void voteTwiceSameRestaurant() throws Exception {
        repository.getByUserAndDate(USER_ID, LocalDate.now())
                .ifPresent(repository::delete);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID)))
                .andExpect(status().isCreated());
        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurantId", String.valueOf(RESTAURANT1_ID)))
                    .andExpect(status().isOk());
        } else {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .param("restaurantId", String.valueOf(RESTAURANT1_ID)))
                    .andExpect(status().isUnprocessableContent());
        }
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testGetTodayReturnsCorrectFormat() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content -> {
                    String json = content.getResponse().getContentAsString();
                    assertThat(json).contains("\"id\"", "\"restaurantId\"", "\"restaurantName\"", "\"voteDate\"");
                });
    }
}