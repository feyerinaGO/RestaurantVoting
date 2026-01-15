package ru.develop.restaurantvoting.restaurant.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.develop.restaurantvoting.AbstractControllerTest;
import ru.develop.restaurantvoting.common.util.JsonUtil;
import ru.develop.restaurantvoting.restaurant.model.Restaurant;
import ru.develop.restaurantvoting.restaurant.repository.RestaurantRepository;
import ru.develop.restaurantvoting.restaurant.to.RestaurantCreateTo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.*;
import static ru.develop.restaurantvoting.restaurant.web.AdminRestaurantController.REST_URL;
import static ru.develop.restaurantvoting.user.UserTestData.ADMIN_MAIL;
import static ru.develop.restaurantvoting.user.UserTestData.USER_MAIL;

class AdminRestaurantControllerTest extends AbstractControllerTest {

    private static final String REST_URL_SLASH = REST_URL + '/';

    @Autowired
    private RestaurantRepository repository;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Restaurant restaurant = JsonUtil.readValue(json, Restaurant.class);
                    assertThat(restaurant.getId()).isEqualTo(RESTAURANT1_ID);
                    assertThat(restaurant.getName()).isEqualTo("Burger King");
                    assertThat(restaurant.getAddress()).isEqualTo("123456 Moscow City");
                });
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getWithMenu() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Restaurant restaurant = JsonUtil.readValue(json, Restaurant.class);
                    assertThat(restaurant.getId()).isEqualTo(RESTAURANT1_ID);
                    assertThat(restaurant.getName()).isEqualTo("Burger King");
                    assertThat(restaurant.getMenuItems()).isNotNull();
                });
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getWithMenuNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + NOT_FOUND + "/with-menu"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getWithMenuForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void create() throws Exception {
        RestaurantCreateTo newRestaurantTo = new RestaurantCreateTo(null, "New Restaurant", "New Address 123");
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurantTo)))
                .andExpect(status().isCreated());

        Restaurant created = RESTAURANT_MATCHER.readFromJson(action);
        int newId = created.id();
        Restaurant newRestaurant = new Restaurant(newId, "New Restaurant", "New Address 123");
        RESTAURANT_MATCHER.assertMatch(created, newRestaurant);
        RESTAURANT_MATCHER.assertMatch(repository.getExisted(newId), newRestaurant);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createForbidden() throws Exception {
        RestaurantCreateTo newRestaurantTo = new RestaurantCreateTo(null, "New Restaurant", "New Address 123");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurantTo)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalid() throws Exception {
        RestaurantCreateTo invalid = new RestaurantCreateTo(null, "", "");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithDuplicateName() throws Exception {
        RestaurantCreateTo duplicate = new RestaurantCreateTo(null, "Burger King", "Different Address");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createHtmlUnsafe() throws Exception {
        RestaurantCreateTo unsafe = new RestaurantCreateTo(null, "<script>alert(123)</script>", "Address");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(unsafe)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        RestaurantCreateTo updatedTo = new RestaurantCreateTo(null, "Updated Name", "Updated Address");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        Restaurant updated = repository.getExisted(RESTAURANT1_ID);
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getAddress()).isEqualTo("Updated Address");
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateForbidden() throws Exception {
        RestaurantCreateTo updatedTo = new RestaurantCreateTo(null, "Updated Name", "Updated Address");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateNotFound() throws Exception {
        RestaurantCreateTo updatedTo = new RestaurantCreateTo(null, "Updated Name", "Updated Address");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalid() throws Exception {
        RestaurantCreateTo invalid = new RestaurantCreateTo(null, "", "");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithDuplicateName() throws Exception {
        RestaurantCreateTo duplicate = new RestaurantCreateTo(null, "McDonalds", "Different Address");

        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateHtmlUnsafe() throws Exception {
        RestaurantCreateTo unsafe = new RestaurantCreateTo(null, "<script>alert(123)</script>", "Address");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(unsafe)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + RESTAURANT1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());

        List<Restaurant> all = repository.findAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting(Restaurant::getName)
                .containsExactlyInAnyOrder("McDonalds", "KFC");
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + RESTAURANT1_ID))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void testCacheEvictionOnCreate() throws Exception {
        perform(MockMvcRequestBuilders.get("/api/restaurants/with-menu/today"))
                .andExpect(status().isOk());

        RestaurantCreateTo newRestaurantTo = new RestaurantCreateTo(null, "Cache Test Restaurant", "Cache Address");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurantTo)))
                .andExpect(status().isCreated());

        perform(MockMvcRequestBuilders.get("/api/restaurants/with-menu/today"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void testCacheEvictionOnUpdate() throws Exception {
        perform(MockMvcRequestBuilders.get("/api/restaurants/with-menu/today"))
                .andExpect(status().isOk());

        RestaurantCreateTo updatedTo = new RestaurantCreateTo(null, "Updated Cache Name", "Updated Address");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        perform(MockMvcRequestBuilders.get("/api/restaurants/with-menu/today"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void testCacheEvictionOnDelete() throws Exception {
        perform(MockMvcRequestBuilders.get("/api/restaurants/with-menu/today"))
                .andExpect(status().isOk());

        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + RESTAURANT1_ID))
                .andExpect(status().isNoContent());

        perform(MockMvcRequestBuilders.get("/api/restaurants/with-menu/today"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithValidData() throws Exception {
        RestaurantCreateTo newRestaurantTo = new RestaurantCreateTo(null, "Valid Restaurant", "Valid Address 123 Street");
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurantTo)))
                .andExpect(status().isCreated());

        String json = action.andReturn().getResponse().getContentAsString();
        Restaurant created = JsonUtil.readValue(json, Restaurant.class);
        assertThat(created.getName()).isEqualTo("Valid Restaurant");
        assertThat(created.getAddress()).isEqualTo("Valid Address 123 Street");

        Restaurant saved = repository.getExisted(created.getId());
        assertThat(saved.getName()).isEqualTo("Valid Restaurant");
        assertThat(saved.getAddress()).isEqualTo("Valid Address 123 Street");
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateOwnName() throws Exception {
        RestaurantCreateTo updatedTo = new RestaurantCreateTo(null, "Burger King Updated", "Same Address");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        Restaurant updated = repository.getExisted(RESTAURANT1_ID);
        assertThat(updated.getName()).isEqualTo("Burger King Updated");
        assertThat(updated.getAddress()).isEqualTo("Same Address");
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void testCreateWithMinimalData() throws Exception {
        RestaurantCreateTo newRestaurantTo = new RestaurantCreateTo(null, "Min", "Addr");
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurantTo)))
                .andExpect(status().isCreated());

        String json = action.andReturn().getResponse().getContentAsString();
        Restaurant created = JsonUtil.readValue(json, Restaurant.class);
        assertThat(created.getName()).isEqualTo("Min");
        assertThat(created.getAddress()).isEqualTo("Addr");
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void testCreateWithMaximumLengthData() throws Exception {
        String longName = "A".repeat(64);
        String longAddress = "B".repeat(128);
        RestaurantCreateTo newRestaurantTo = new RestaurantCreateTo(null, longName, longAddress);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurantTo)))
                .andExpect(status().isCreated());

        String json = action.andReturn().getResponse().getContentAsString();
        Restaurant created = JsonUtil.readValue(json, Restaurant.class);
        assertThat(created.getName()).isEqualTo(longName);
        assertThat(created.getAddress()).isEqualTo(longAddress);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithTooLongName() throws Exception {
        String tooLongName = "A".repeat(65);
        RestaurantCreateTo invalid = new RestaurantCreateTo(null, tooLongName, "Address");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithTooLongAddress() throws Exception {
        String tooLongAddress = "B".repeat(129);
        RestaurantCreateTo invalid = new RestaurantCreateTo(null, "Name", tooLongAddress);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getRestaurantThatWasDeleted() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + RESTAURANT1_ID))
                .andExpect(status().isNoContent());

        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void testRestaurantWithVotesAndMenuItems() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content -> {
                    String json = content.getResponse().getContentAsString();
                    Restaurant restaurant = JsonUtil.readValue(json, Restaurant.class);
                    assertThat(restaurant.getId()).isEqualTo(RESTAURANT1_ID);
                    assertThat(restaurant.getMenuItems()).isNotEmpty();
                });
    }
}