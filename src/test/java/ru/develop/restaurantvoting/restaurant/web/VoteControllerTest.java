package ru.develop.restaurantvoting.restaurant.web;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.develop.restaurantvoting.AbstractControllerTest;
import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.restaurant.repository.VoteRepository;
import ru.develop.restaurantvoting.restaurant.to.VoteTo;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.develop.restaurantvoting.restaurant.MenuItemTestData.TODAY;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.*;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.NOT_FOUND;
import static ru.develop.restaurantvoting.restaurant.VoteTestData.*;
import static ru.develop.restaurantvoting.restaurant.web.VoteController.REST_URL;
import static ru.develop.restaurantvoting.user.UserTestData.*;

class VoteControllerTest extends AbstractControllerTest {

    @Autowired
    private VoteRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<VoteTo> votes = objectMapper.readValue(json,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, VoteTo.class));
                    assertThat(votes).hasSize(1);
                    assertThat(votes.get(0).getRestaurantId()).isEqualTo(RESTAURANT1_ID);
                });
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllForAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<VoteTo> votes = objectMapper.readValue(json,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, VoteTo.class));
                    assertThat(votes).hasSize(1);
                    assertThat(votes.get(0).getRestaurantId()).isEqualTo(RESTAURANT2_ID);
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllWhenNoVotes() throws Exception {
        repository.delete(userVote1);
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    VoteTo vote = objectMapper.readValue(json, VoteTo.class);
                    assertThat(vote.getRestaurantId()).isEqualTo(RESTAURANT1_ID);
                    assertThat(vote.getVoteDate()).isEqualTo(TODAY);
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getTodayWhenNoVote() throws Exception {
        repository.delete(userVote1);
        assertThat(repository.getByUserAndDate(USER_ID, TODAY)).isEmpty();
        perform(MockMvcRequestBuilders.get(REST_URL + "/today"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getTodayForAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    VoteTo vote = objectMapper.readValue(json, VoteTo.class);
                    assertThat(vote.getRestaurantId()).isEqualTo(RESTAURANT2_ID);
                    assertThat(vote.getVoteDate()).isEqualTo(TODAY);
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void voteForRestaurant() throws Exception {
        repository.delete(userVote1);

        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT3_ID))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String json = action.andReturn().getResponse().getContentAsString();
        VoteTo created = objectMapper.readValue(json, VoteTo.class);
        assertThat(created.getRestaurantId()).isEqualTo(RESTAURANT3_ID);
        assertThat(created.getVoteDate()).isEqualTo(TODAY);
        Vote vote = repository.getByUserAndDate(USER_ID, TODAY).orElseThrow();
        assertThat(vote.getRestaurant().getId()).isEqualTo(RESTAURANT3_ID);
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
    void voteForNonExistentRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void changeVoteBeforeDeadline() throws Exception {
        Vote originalVote = repository.getByUserAndDate(USER_ID, TODAY).orElseThrow();
        assertThat(originalVote.getRestaurant().getId()).isEqualTo(RESTAURANT1_ID);
        if (LocalTime.now().isAfter(LocalTime.of(11, 0))) {
            System.out.println("Skipping changeVoteBeforeDeadline test because it's after 11:00");
            return;
        }
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT2_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String json = action.andReturn().getResponse().getContentAsString();
        VoteTo updated = objectMapper.readValue(json, VoteTo.class);
        assertThat(updated.getRestaurantId()).isEqualTo(RESTAURANT2_ID);
        assertThat(updated.getVoteDate()).isEqualTo(TODAY);
        Vote updatedVote = repository.getByUserAndDate(USER_ID, TODAY).orElseThrow();
        assertThat(updatedVote.getRestaurant().getId()).isEqualTo(RESTAURANT2_ID);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void voteTwiceSameRestaurant() throws Exception {
        if (LocalTime.now().isAfter(LocalTime.of(11, 0))) {
            System.out.println("Skipping voteTwiceSameRestaurant test because it's after 11:00");
            return;
        }
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String json = action.andReturn().getResponse().getContentAsString();
        VoteTo updated = objectMapper.readValue(json, VoteTo.class);
        assertThat(updated.getRestaurantId()).isEqualTo(RESTAURANT1_ID);
        Vote vote = repository.getByUserAndDate(USER_ID, TODAY).orElseThrow();
        assertThat(vote.getRestaurant().getId()).isEqualTo(RESTAURANT1_ID);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteTodayVote() throws Exception {
        if (LocalTime.now().isAfter(LocalTime.of(11, 0))) {
            System.out.println("Skipping deleteTodayVote test because it's after 11:00");
            return;
        }

        perform(MockMvcRequestBuilders.delete(REST_URL + "/today"))
                .andExpect(status().isNoContent());
        assertThat(repository.getByUserAndDate(USER_ID, TODAY)).isEmpty();
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteTodayVoteWhenNoVote() throws Exception {
        repository.delete(userVote1);
        assertThat(repository.getByUserAndDate(USER_ID, TODAY)).isEmpty();
        perform(MockMvcRequestBuilders.delete(REST_URL + "/today"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void adminCanVote() throws Exception {
        repository.delete(adminVote1);

        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT1_ID))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String json = action.andReturn().getResponse().getContentAsString();
        VoteTo created = objectMapper.readValue(json, VoteTo.class);
        assertThat(created.getRestaurantId()).isEqualTo(RESTAURANT1_ID);
        Vote vote = repository.getByUserAndDate(ADMIN_ID, TODAY).orElseThrow();
        assertThat(vote.getRestaurant().getId()).isEqualTo(RESTAURANT1_ID);
    }

    @Test
    void voteUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getVotingHistory() throws Exception {
        Vote yesterdayVote = new Vote(null, TODAY.minusDays(1));
        yesterdayVote.setRestaurant(restaurant3);
        yesterdayVote.setUser(user);
        repository.save(yesterdayVote);

        String responseContent = perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<VoteTo> votes = objectMapper.readValue(responseContent,
                objectMapper.getTypeFactory().constructCollectionType(List.class, VoteTo.class));

        assertThat(votes).hasSize(2);

        assertThat(votes.get(0).getVoteDate()).isEqualTo(TODAY);
        assertThat(votes.get(1).getVoteDate()).isEqualTo(TODAY.minusDays(1));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void voteForRestaurantWithoutMenuToday() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT1_ID))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void cannotVoteForDeletedRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void voteWithInvalidRestaurantId() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=abc"))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testBusinessLogic_OneVotePerDay() throws Exception {
        repository.delete(userVote1);
        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT1_ID))
                .andExpect(status().isCreated());
        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT2_ID))
                .andExpect(status().isOk());
        List<Vote> todayVotes = repository.getAllByUser(USER_ID).stream()
                .filter(v -> v.getVoteDate().equals(TODAY))
                .toList();
        assertThat(todayVotes).hasSize(1);
        assertThat(todayVotes.get(0).getRestaurant().getId()).isEqualTo(RESTAURANT2_ID);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testBusinessLogic_VoteAfterDeadlineSimulation() throws Exception {

        System.out.println("Current time: " + LocalTime.now() + ", Deadline: 11:00");
        if (LocalTime.now().isAfter(LocalTime.of(11, 0))) {
            perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + RESTAURANT2_ID))
                    .andDo(print())
                    .andExpect(status().isUnprocessableContent())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Cannot change vote after")));
        }
    }
}