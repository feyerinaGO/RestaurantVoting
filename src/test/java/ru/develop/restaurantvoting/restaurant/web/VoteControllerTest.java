package ru.develop.restaurantvoting.restaurant.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.develop.restaurantvoting.AbstractControllerTest;
import ru.develop.restaurantvoting.TestTimeProviderConfig;
import ru.develop.restaurantvoting.restaurant.model.Vote;
import ru.develop.restaurantvoting.restaurant.repository.VoteRepository;
import ru.develop.restaurantvoting.restaurant.service.VoteService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.*;
import static ru.develop.restaurantvoting.restaurant.VoteTestData.*;
import static ru.develop.restaurantvoting.user.UserTestData.*;
import static ru.develop.restaurantvoting.restaurant.web.VoteController.REST_URL;

@Transactional
class VoteControllerTest extends AbstractControllerTest {

    private static final String REST_URL_TODAY = REST_URL  + "/today";

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private TestTimeProviderConfig.TestTimeProvider testTimeProvider;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                    [
                        {
                            "id": 1,
                            "restaurantId": 1,
                            "restaurantName": "Burger King",
                            "voteDate": "2026-01-15"
                        }
                    ]
                    """, false));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllAsAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                    [
                        {
                            "id": 2,
                            "restaurantId": 2,
                            "restaurantName": "McDonalds",
                            "voteDate": "2026-01-15"
                        }
                    ]
                    """, false));
    }

    @Test
    void getAllUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_TODAY))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                    {
                        "id": 1,
                        "restaurantId": 1,
                        "restaurantName": "Burger King",
                        "voteDate": "2026-01-15"
                    }
                    """, false));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getTodayAsAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_TODAY))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                    {
                        "id": 2,
                        "restaurantId": 2,
                        "restaurantName": "McDonalds",
                        "voteDate": "2026-01-15"
                    }
                    """, false));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getTodayNotFound() throws Exception {
        voteRepository.deleteById(VOTE1_ID);

        perform(MockMvcRequestBuilders.get(REST_URL_TODAY))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getTodayUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_TODAY))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createVote() throws Exception {
        voteRepository.delete(userVote1);

        String newVoteJson = """
        {
            "restaurantId": 3
        }
        """;

        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newVoteJson))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String response = action.andReturn().getResponse().getContentAsString();
        assertThat(response).contains("\"restaurantId\":3");
        assertThat(response).contains("\"restaurantName\":\"KFC\"");

        List<Vote> userVotes = voteRepository.getAllByUser(USER_ID);
        assertThat(userVotes).hasSize(1);
        assertThat(userVotes.get(0).getRestaurant().getId()).isEqualTo(RESTAURANT3_ID);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createVoteAsAdmin() throws Exception {
        voteRepository.deleteById(VOTE2_ID);

        String newVoteJson = """
            {
                "restaurantId": 3
            }
            """;

        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newVoteJson))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                    {
                        "restaurantId": 3
                    }
                    """, false));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createVoteDuplicateForToday() throws Exception {
        String newVoteJson = """
            {
                "restaurantId": 2
            }
            """;

        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newVoteJson))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("User has already voted today")));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createVoteForNonExistentRestaurant() throws Exception {
        voteRepository.deleteById(VOTE1_ID);

        String newVoteJson = """
            {
                "restaurantId": 999
            }
            """;

        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newVoteJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createVoteWithInvalidData() throws Exception {
        voteRepository.deleteById(VOTE1_ID);

        String invalidJson = "{}";

        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void createVoteUnauthorized() throws Exception {
        String newVoteJson = """
            {
                "restaurantId": 1
            }
            """;

        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newVoteJson))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateVoteBeforeDeadline() throws Exception {
        testTimeProvider.setTimeBeforeDeadline();

        String updatedVoteJson = """
            {
                "restaurantId": 3
            }
            """;

        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedVoteJson))
                .andExpect(status().isNoContent())
                .andDo(print());

        Vote updated = voteRepository.getExistedByUserAndDate(USER_ID, LocalDate.now());
        assertThat(updated.getRestaurant().getId()).isEqualTo(RESTAURANT3_ID);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateVoteBeforeDeadlineAsAdmin() throws Exception {
        testTimeProvider.setTimeBeforeDeadline();

        String updatedVoteJson = """
            {
                "restaurantId": 1
            }
            """;

        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedVoteJson))
                .andExpect(status().isNoContent())
                .andDo(print());

        Vote updated = voteRepository.getExistedByUserAndDate(ADMIN_ID, LocalDate.now());
        assertThat(updated.getRestaurant().getId()).isEqualTo(RESTAURANT1_ID);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateVoteAfterDeadline() throws Exception {
        testTimeProvider.setTimeAfterDeadline();

        String updatedVoteJson = """
            {
                "restaurantId": 3
            }
            """;

        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedVoteJson))
                .andDo(print())
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string(containsString("Cannot change vote after")));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateVoteNoVoteForToday() throws Exception {
        voteRepository.deleteById(VOTE1_ID);

        testTimeProvider.setTimeBeforeDeadline();

        String updatedVoteJson = """
            {
                "restaurantId": 3
            }
            """;

        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedVoteJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateVoteForNonExistentRestaurant() throws Exception {
        testTimeProvider.setTimeBeforeDeadline();

        String updatedVoteJson = """
            {
                "restaurantId": 999
            }
            """;

        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedVoteJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateVoteUnauthorized() throws Exception {
        String updatedVoteJson = """
            {
                "restaurantId": 3
            }
            """;

        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedVoteJson))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createVoteThenUpdateBeforeDeadline() throws Exception {
        voteRepository.deleteById(VOTE1_ID);

        String newVoteJson = """
            {
                "restaurantId": 2
            }
            """;

        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newVoteJson))
                .andExpect(status().isCreated());

        testTimeProvider.setTimeBeforeDeadline();

        String updatedVoteJson = """
            {
                "restaurantId": 3
            }
            """;

        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedVoteJson))
                .andExpect(status().isNoContent());

        Vote finalVote = voteRepository.getExistedByUserAndDate(USER_ID, LocalDate.now());
        assertThat(finalVote.getRestaurant().getId()).isEqualTo(RESTAURANT3_ID);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testVotingHistoryOrdering() throws Exception {
        Vote yesterdayVote = new Vote(null, LocalDate.now().minusDays(1));
        yesterdayVote.setRestaurant(restaurant2);
        yesterdayVote.setUser(user);
        voteRepository.save(yesterdayVote);

        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    assertThat(json).contains("\"id\":1");
                    assertThat(json).contains("\"id\":3");
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testCreateVoteTwiceDifferentDays() throws Exception {
        voteRepository.deleteById(VOTE1_ID);

        String firstVoteJson = """
            {
                "restaurantId": 1
            }
            """;
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstVoteJson))
                .andExpect(status().isCreated());

        String secondVoteJson = """
            {
                "restaurantId": 2
            }
            """;
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(secondVoteJson))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testUpdateVoteTwiceBeforeDeadline() throws Exception {
        testTimeProvider.setTimeOneHourBeforeDeadline();

        String firstUpdate = """
            {
                "restaurantId": 2
            }
            """;
        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstUpdate))
                .andExpect(status().isNoContent());

        testTimeProvider.setTimeThirtyMinutesBeforeDeadline();
        String secondUpdate = """
            {
                "restaurantId": 3
            }
            """;
        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(secondUpdate))
                .andExpect(status().isNoContent());

        Vote finalVote = voteRepository.getExistedByUserAndDate(USER_ID, LocalDate.now());
        assertThat(finalVote.getRestaurant().getId()).isEqualTo(RESTAURANT3_ID);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testCreateAndUpdateFlow() throws Exception {
        voteRepository.deleteById(VOTE1_ID);

        testTimeProvider.setTimeOneHourBeforeDeadline();
        String createVoteJson = """
            {
                "restaurantId": 1
            }
            """;
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createVoteJson))
                .andExpect(status().isCreated());

        testTimeProvider.setTimeThirtyMinutesBeforeDeadline();
        String updateVoteJson = """
            {
                "restaurantId": 2
            }
            """;
        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateVoteJson))
                .andExpect(status().isNoContent());

        testTimeProvider.setTimeAfterDeadline();
        String lateUpdateJson = """
            {
                "restaurantId": 3
            }
            """;
        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(lateUpdateJson))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());

        Vote finalVote = voteRepository.getExistedByUserAndDate(USER_ID, LocalDate.now());
        assertThat(finalVote.getRestaurant().getId()).isEqualTo(RESTAURANT2_ID);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testEmptyVotingHistoryForNewUser() throws Exception {
        voteRepository.deleteById(VOTE1_ID);

        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testVoteResponseContainsAllRequiredFields() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_TODAY))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    assertThat(json).contains("\"id\"");
                    assertThat(json).contains("\"restaurantId\"");
                    assertThat(json).contains("\"restaurantName\"");
                    assertThat(json).contains("\"voteDate\"");
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testUpdateVoteExactlyAtDeadline() throws Exception {
        testTimeProvider.setTimeAtDeadline();

        String updateVoteJson = """
            {
                "restaurantId": 3
            }
            """;

        perform(MockMvcRequestBuilders.put(REST_URL_TODAY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateVoteJson))
                .andDo(print())
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string(containsString("Cannot change vote after")));
    }
}