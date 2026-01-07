package ru.develop.restaurantvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import ru.develop.restaurantvoting.AbstractControllerTest;
import ru.develop.restaurantvoting.user.repository.UserRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class ProfileControllerTest extends AbstractControllerTest {

    @Autowired
    private UserRepository repository;
//
//    @Test
//    @WithUserDetails(value = USER_MAIL)
//    void get() throws Exception {
//        perform(MockMvcRequestBuilders.get(ProfileController.REST_URL))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(USER_MATCHER.contentJson(user));
//    }

//    @Test
//    void getUnAuth() throws Exception {
//        perform(MockMvcRequestBuilders.get(ProfileController.REST_URL))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithUserDetails(value = USER_MAIL)
//    void delete() throws Exception {
//        perform(MockMvcRequestBuilders.delete(ProfileController.REST_URL))
//                .andExpect(status().isNoContent());
//        USER_MATCHER.assertMatch(repository.findAll(), admin);
//    }
//
//    @Test
//    void register() throws Exception {
//        UserTo newTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword");
//        User newUser = UsersUtil.createNewFromTo(newTo);
//        ResultActions action = perform(MockMvcRequestBuilders.post(ProfileController.REST_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JsonUtil.writeValue(newTo)))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//        User created = USER_MATCHER.readFromJson(action);
//        int newId = created.id();
//        newUser.setId(newId);
//        USER_MATCHER.assertMatch(created, newUser);
//        USER_MATCHER.assertMatch(repository.getExisted(newId), newUser);
//    }
//
//    @Test
//    @WithUserDetails(value = USER_MAIL)
//    void update() throws Exception {
//        UserTo updatedTo = new UserTo(null, "newName", USER_MAIL, "newPassword");
//        perform(MockMvcRequestBuilders.put(ProfileController.REST_URL).contentType(MediaType.APPLICATION_JSON)
//                .content(JsonUtil.writeValue(updatedTo)))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//
//        USER_MATCHER.assertMatch(repository.getExisted(USER_ID), UsersUtil.updateFromTo(new User(user), updatedTo));
//    }
}