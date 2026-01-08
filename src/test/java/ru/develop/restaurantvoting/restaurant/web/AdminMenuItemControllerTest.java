package ru.develop.restaurantvoting.restaurant.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.develop.restaurantvoting.AbstractControllerTest;
import ru.develop.restaurantvoting.common.util.JsonUtil;
import ru.develop.restaurantvoting.restaurant.model.MenuItem;
import ru.develop.restaurantvoting.restaurant.repository.MenuItemRepository;
import ru.develop.restaurantvoting.restaurant.util.MenuItemsUtil;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.develop.restaurantvoting.restaurant.MenuItemTestData.*;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.RESTAURANT1_ID;
import static ru.develop.restaurantvoting.restaurant.web.AdminMenuItemController.REST_URL;
import static ru.develop.restaurantvoting.user.UserTestData.ADMIN_MAIL;
import static ru.develop.restaurantvoting.user.UserTestData.USER_MAIL;

class AdminMenuItemControllerTest extends AbstractControllerTest {

    @Autowired
    private MenuItemRepository repository;

    private String getUrl(int restaurantId) {
        return REST_URL.replace("{restaurantId}", String.valueOf(restaurantId));
    }

    private String getUrlWithId(int restaurantId, int id) {
        return getUrl(restaurantId) + "/" + id;
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(RESTAURANT1_ID)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_ITEM_TO_MATCHER.contentJson(
                        createTo(menuItem1),
                        createTo(menuItem2),
                        createTo(menuItem3),
                        createTo(yesterdayMenuItem1),
                        createTo(yesterdayMenuItem2)
                ));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(RESTAURANT1_ID)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllForNonExistentRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(999)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrlWithId(RESTAURANT1_ID, MENU_ITEM1_ID)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_ITEM_TO_MATCHER.contentJson(createTo(menuItem1)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrlWithId(RESTAURANT1_ID, 999)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getMenuItemFromDifferentRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrlWithId(RESTAURANT1_ID, MENU_ITEM4_ID)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("doesn't belong to Restaurant")));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByDate() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(RESTAURANT1_ID) + "/by-date")
                .param("date", TODAY.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_ITEM_TO_MATCHER.contentJson(
                        createTo(menuItem1),
                        createTo(menuItem2),
                        createTo(menuItem3)
                ));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByDateForYesterday() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(RESTAURANT1_ID) + "/by-date")
                .param("date", YESTERDAY.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_ITEM_TO_MATCHER.contentJson(
                        createTo(yesterdayMenuItem1),
                        createTo(yesterdayMenuItem2)
                ));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByDateForNonExistentDate() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        perform(MockMvcRequestBuilders.get(getUrl(RESTAURANT1_ID) + "/by-date")
                .param("date", futureDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByDateWithoutParam() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(RESTAURANT1_ID) + "/by-date"))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void create() throws Exception {
        MenuItem newMenuItem = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(getUrl(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMenuItem)))
                .andExpect(status().isCreated());

        MenuItem created = MENU_ITEM_MATCHER.readFromJson(action);
        int newId = created.id();
        newMenuItem.setId(newId);
        MENU_ITEM_MATCHER.assertMatch(created, newMenuItem);
        MENU_ITEM_MATCHER.assertMatch(repository.getExisted(newId), newMenuItem);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createForbidden() throws Exception {
        MenuItem newMenuItem = getNew();
        perform(MockMvcRequestBuilders.post(getUrl(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMenuItem)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createForNonExistentRestaurant() throws Exception {
        MenuItem newMenuItem = getNew();
        perform(MockMvcRequestBuilders.post(getUrl(999))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMenuItem)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        MenuItem updated = getUpdated();
        perform(MockMvcRequestBuilders.put(getUrlWithId(RESTAURANT1_ID, MENU_ITEM1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        MENU_ITEM_MATCHER.assertMatch(repository.getBelonged(RESTAURANT1_ID, MENU_ITEM1_ID), updated);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateForbidden() throws Exception {
        MenuItem updated = getUpdated();
        perform(MockMvcRequestBuilders.put(getUrlWithId(RESTAURANT1_ID, MENU_ITEM1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateMenuItemFromDifferentRestaurant() throws Exception {
        MenuItem updated = new MenuItem(MENU_ITEM4_ID, "Updated Name", TODAY, "Updated Description", 600);
        perform(MockMvcRequestBuilders.put(getUrlWithId(RESTAURANT1_ID, MENU_ITEM4_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("doesn't belong to Restaurant")));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithIdMismatch() throws Exception {
        MenuItem updated = new MenuItem(999, "Updated Name", TODAY, "Updated Description", 600);
        perform(MockMvcRequestBuilders.put(getUrlWithId(RESTAURANT1_ID, MENU_ITEM1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(getUrlWithId(RESTAURANT1_ID, MENU_ITEM1_ID)))
                .andDo(print())
                .andExpect(status().isNoContent());

        List<MenuItem> all = repository.getAllByRestaurant(RESTAURANT1_ID);
        MENU_ITEM_TO_MATCHER.assertMatch(MenuItemsUtil.getTos(all),
                List.of(createTo(menuItem2), createTo(menuItem3),
                        createTo(yesterdayMenuItem1), createTo(yesterdayMenuItem2)));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(getUrlWithId(RESTAURANT1_ID, MENU_ITEM1_ID)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(getUrlWithId(RESTAURANT1_ID, 999)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteByDate() throws Exception {
        perform(MockMvcRequestBuilders.delete(getUrl(RESTAURANT1_ID) + "/by-date")
                .param("date", TODAY.toString()))
                .andDo(print())
                .andExpect(status().isNoContent());

        List<MenuItem> todayMenu = repository.getByRestaurantAndDate(RESTAURANT1_ID, TODAY);
        assertThat(todayMenu).isEmpty();

        List<MenuItem> yesterdayMenu = repository.getByRestaurantAndDate(RESTAURANT1_ID, YESTERDAY);
        assertThat(yesterdayMenu).hasSize(2);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteByDateForEmptyDate() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        perform(MockMvcRequestBuilders.delete(getUrl(RESTAURANT1_ID) + "/by-date")
                .param("date", futureDate.toString()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(repository.getByRestaurantAndDate(RESTAURANT1_ID, futureDate)).isEmpty();
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteByDateWithoutParam() throws Exception {
        perform(MockMvcRequestBuilders.delete(getUrl(RESTAURANT1_ID) + "/by-date"))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteByDateForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(getUrl(RESTAURANT1_ID) + "/by-date")
                .param("date", TODAY.toString()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalid() throws Exception {
        MenuItem invalid = new MenuItem(null, "", TODAY, "", -100);
        perform(MockMvcRequestBuilders.post(getUrl(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalid() throws Exception {
        MenuItem invalid = new MenuItem(MENU_ITEM1_ID, "", TODAY, "", -100);
        perform(MockMvcRequestBuilders.put(getUrlWithId(RESTAURANT1_ID, MENU_ITEM1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithNullDate() throws Exception {
        MenuItem invalid = new MenuItem(null, "Name", null, "Description", 100);
        perform(MockMvcRequestBuilders.post(getUrl(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicateForSameDateAndRestaurant() throws Exception {
        MenuItem duplicate = new MenuItem(null, "Different Name", TODAY, "Big Burger", 500);
        perform(MockMvcRequestBuilders.post(getUrl(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateHtmlUnsafe() throws Exception {
        MenuItem invalid = new MenuItem(MENU_ITEM1_ID, "<script>alert(123)</script>", TODAY, "Description", 100);
        perform(MockMvcRequestBuilders.put(getUrlWithId(RESTAURANT1_ID, MENU_ITEM1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithHtmlUnsafe() throws Exception {
        MenuItem invalid = new MenuItem(null, "Name", TODAY, "<script>alert(123)</script>", 100);
        perform(MockMvcRequestBuilders.post(getUrl(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(RESTAURANT1_ID)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getForNonExistentRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrlWithId(999, MENU_ITEM1_ID)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}