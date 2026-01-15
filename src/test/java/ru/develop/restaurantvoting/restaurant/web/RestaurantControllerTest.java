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
import ru.develop.restaurantvoting.restaurant.to.MenuItemTo;
import ru.develop.restaurantvoting.restaurant.to.RestaurantTo;
import ru.develop.restaurantvoting.restaurant.to.RestaurantWithMenuTo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.develop.restaurantvoting.restaurant.MenuItemTestData.TODAY;
import static ru.develop.restaurantvoting.restaurant.MenuItemTestData.YESTERDAY;
import static ru.develop.restaurantvoting.restaurant.RestaurantTestData.*;
import static ru.develop.restaurantvoting.restaurant.web.RestaurantController.REST_URL;
import static ru.develop.restaurantvoting.user.UserTestData.ADMIN_MAIL;
import static ru.develop.restaurantvoting.user.UserTestData.USER_MAIL;

class RestaurantControllerTest extends AbstractControllerTest {

    private static final String REST_URL_SLASH = REST_URL + '/';

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantTo> restaurants = JsonUtil.readValues(json, RestaurantTo.class);
                    assertThat(restaurants).hasSize(3);
                    assertThat(restaurants).extracting(RestaurantTo::getName)
                            .containsExactlyInAnyOrder("Burger King", "McDonalds", "KFC");
                });
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllAsAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantTo> restaurants = JsonUtil.readValues(json, RestaurantTo.class);
                    assertThat(restaurants).hasSize(3);
                });
    }

    @Test
    void getAllUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    RestaurantTo restaurant = JsonUtil.readValue(json, RestaurantTo.class);
                    assertThat(restaurant.getId()).isEqualTo(RESTAURANT1_ID);
                    assertThat(restaurant.getName()).isEqualTo("Burger King");
                });
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAsAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT2_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    RestaurantTo restaurant = JsonUtil.readValue(json, RestaurantTo.class);
                    assertThat(restaurant.getId()).isEqualTo(RESTAURANT2_ID);
                    assertThat(restaurant.getName()).isEqualTo("McDonalds");
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllWithMenuToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantWithMenuTo> restaurants = JsonUtil.readValues(json, RestaurantWithMenuTo.class);
                    assertThat(restaurants).hasSize(3);

                    restaurants.forEach(restaurant -> {
                        assertThat(restaurant.getMenuItems()).isNotEmpty();
                        restaurant.getMenuItems().forEach(menuItem -> {
                            assertThat(menuItem.getMenuDate()).isEqualTo(TODAY);
                        });
                    });
                });
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWithMenuTodayAsAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantWithMenuTo> restaurants = JsonUtil.readValues(json, RestaurantWithMenuTo.class);
                    assertThat(restaurants).hasSize(3);

                    RestaurantWithMenuTo burgerKing = restaurants.stream()
                            .filter(r -> r.getId().equals(RESTAURANT1_ID))
                            .findFirst()
                            .orElseThrow();
                    assertThat(burgerKing.getMenuItems()).hasSize(3);

                    RestaurantWithMenuTo mcDonalds = restaurants.stream()
                            .filter(r -> r.getId().equals(RESTAURANT2_ID))
                            .findFirst()
                            .orElseThrow();
                    assertThat(mcDonalds.getMenuItems()).hasSize(3);

                    RestaurantWithMenuTo kfc = restaurants.stream()
                            .filter(r -> r.getId().equals(RESTAURANT3_ID))
                            .findFirst()
                            .orElseThrow();
                    assertThat(kfc.getMenuItems()).hasSize(3);
                });
    }

    @Test
    void getAllWithMenuTodayUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/today"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getWithMenuToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    RestaurantWithMenuTo restaurant = JsonUtil.readValue(json, RestaurantWithMenuTo.class);
                    assertThat(restaurant.getId()).isEqualTo(RESTAURANT1_ID);
                    assertThat(restaurant.getName()).isEqualTo("Burger King");
                    assertThat(restaurant.getMenuItems()).hasSize(3);

                    List<String> menuItemNames = restaurant.getMenuItems().stream()
                            .map(item -> item.getName())
                            .collect(Collectors.toList());
                    assertThat(menuItemNames).contains("Whopper", "Cheeseburger", "Fries");
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getWithMenuTodayNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + NOT_FOUND + "/with-menu/today"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getWithMenuTodayForRestaurantWithoutTodayMenu() throws Exception {
        Restaurant restaurantWithoutMenu = new Restaurant(null, "Empty Menu Restaurant", "Address");
        Restaurant saved = restaurantRepository.save(restaurantWithoutMenu);

        try {
            perform(MockMvcRequestBuilders.get(REST_URL_SLASH + saved.getId() + "/with-menu/today"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        } finally {
            restaurantRepository.delete(saved);
        }
    }

    @Test
    void getWithMenuTodayUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu/today"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllWithMenuByDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/by-date")
                .param("date", TODAY.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantWithMenuTo> restaurants = JsonUtil.readValues(json, RestaurantWithMenuTo.class);
                    assertThat(restaurants).hasSize(3);

                    restaurants.forEach(restaurant -> {
                        assertThat(restaurant.getMenuItems()).isNotEmpty();
                        restaurant.getMenuItems().forEach(menuItem -> {
                            assertThat(menuItem.getMenuDate()).isEqualTo(TODAY);
                        });
                    });
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllWithMenuByDateYesterday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/by-date")
                .param("date", YESTERDAY.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantWithMenuTo> restaurants = JsonUtil.readValues(json, RestaurantWithMenuTo.class);
                    restaurants.forEach(restaurant -> {
                        assertThat(restaurant.getId()).isNotNull();
                        assertThat(restaurant.getName()).isNotNull();
                    });
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllWithMenuByDateFuture() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/by-date")
                .param("date", futureDate.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantWithMenuTo> restaurants = JsonUtil.readValues(json, RestaurantWithMenuTo.class);
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllWithMenuByDateWithoutParam() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/by-date"))
                .andDo(print())
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllWithMenuByDateInvalidParam() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/by-date")
                .param("date", "invalid-date"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAllWithMenuByDateUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/by-date")
                .param("date", TODAY.toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWithMenuByDateAsAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/by-date")
                .param("date", TODAY.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantWithMenuTo> restaurants = JsonUtil.readValues(json, RestaurantWithMenuTo.class);
                    assertThat(restaurants).hasSize(3);
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testCacheBehavior() throws Exception {
        ResultActions firstRequest = perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print());

        String firstResponse = firstRequest.andReturn().getResponse().getContentAsString();

        ResultActions secondRequest = perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print());

        String secondResponse = secondRequest.andReturn().getResponse().getContentAsString();

        List<RestaurantWithMenuTo> firstList = JsonUtil.readValues(firstResponse, RestaurantWithMenuTo.class);
        List<RestaurantWithMenuTo> secondList = JsonUtil.readValues(secondResponse, RestaurantWithMenuTo.class);
        assertThat(firstList).hasSameSizeAs(secondList);
        assertThat(firstList).usingRecursiveFieldByFieldElementComparator().isEqualTo(secondList);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testRestaurantOrdering() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantTo> restaurants = JsonUtil.readValues(json, RestaurantTo.class);
                    assertThat(restaurants).extracting(RestaurantTo::getName)
                            .containsExactly("Burger King", "KFC", "McDonalds");
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testRestaurantWithMenuOrdering() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<RestaurantWithMenuTo> restaurants = JsonUtil.readValues(json, RestaurantWithMenuTo.class);
                    assertThat(restaurants).extracting(RestaurantWithMenuTo::getName)
                            .containsExactly("Burger King", "KFC", "McDonalds");
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testMenuItemsOrdering() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    RestaurantWithMenuTo restaurant = JsonUtil.readValue(json, RestaurantWithMenuTo.class);
                    List<Integer> menuItemIds = restaurant.getMenuItems().stream()
                            .map(item -> item.getId())
                            .collect(Collectors.toList());
                    assertThat(menuItemIds).isSorted();
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testRestaurantWithMenuByDateForDeletedRestaurant() throws Exception {
        restaurantRepository.deleteById(RESTAURANT1_ID);

        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu/today"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testEmptyResponseForNoRestaurants() throws Exception {
        restaurantRepository.deleteAll();

        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testEmptyResponseForNoRestaurantsWithMenuToday() throws Exception {
        restaurantRepository.deleteAll();

        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testResponseStructure() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    RestaurantWithMenuTo restaurant = JsonUtil.readValue(json, RestaurantWithMenuTo.class);

                    assertThat(restaurant.getId()).isNotNull();
                    assertThat(restaurant.getName()).isNotNull();
                    assertThat(restaurant.getMenuItems()).isNotNull();

                    if (!restaurant.getMenuItems().isEmpty()) {
                        ru.develop.restaurantvoting.restaurant.to.MenuItemTo menuItem = restaurant.getMenuItems().get(0);
                        assertThat(menuItem.getId()).isNotNull();
                        assertThat(menuItem.getMenuDate()).isNotNull();
                        assertThat(menuItem.getDescription()).isNotNull();
                        assertThat(menuItem.getPrice()).isNotNull();
                    }
                });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void testRestaurantWithMenuContainsCorrectData() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT1_ID + "/with-menu/today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    RestaurantWithMenuTo restaurant = JsonUtil.readValue(json, RestaurantWithMenuTo.class);

                    assertThat(restaurant.getId()).isEqualTo(RESTAURANT1_ID);
                    assertThat(restaurant.getName()).isEqualTo("Burger King");
                    assertThat(restaurant.getMenuItems()).hasSize(3);

                    Map<String, MenuItemTo> menuItemsMap = restaurant.getMenuItems().stream()
                            .collect(Collectors.toMap(MenuItemTo::getName, Function.identity()));

                    assertThat(menuItemsMap).containsKeys("Whopper", "Cheeseburger", "Fries");

                    MenuItemTo whopper = menuItemsMap.get("Whopper");
                    assertThat(whopper.getDescription()).isEqualTo("Big Burger");
                    assertThat(whopper.getPrice()).isEqualTo(500);
                    assertThat(whopper.getMenuDate()).isEqualTo(TODAY);

                    MenuItemTo cheeseburger = menuItemsMap.get("Cheeseburger");
                    assertThat(cheeseburger.getDescription()).isEqualTo("Cheese Burger");
                    assertThat(cheeseburger.getPrice()).isEqualTo(300);
                    assertThat(cheeseburger.getMenuDate()).isEqualTo(TODAY);

                    MenuItemTo fries = menuItemsMap.get("Fries");
                    assertThat(fries.getDescription()).isEqualTo("French Fries");
                    assertThat(fries.getPrice()).isEqualTo(200);
                    assertThat(fries.getMenuDate()).isEqualTo(TODAY);
                });
    }
}