package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private UserController userController;
    private UserService userService;

    @Test
    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRegisterThenRegistered() {
        User user = new User(0, "Andrew", "wow@mail.ru", "wow");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userCaptor.capture())).thenReturn(Optional.of(user));

        Model model = new ConcurrentModel();
        String view = userController.register(model, user);
        User actualUser = userCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/index");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenRegisterExistingUserThenGetErrorPageWithMessageAndNotRegistered() {
        User user = new User(0, "Andrew", "wow@mail.ru", "password");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.save(userCaptor.capture())).thenReturn(Optional.empty());

        Model model = new ConcurrentModel();
        String view = userController.register(model, user);
        String expectedError = "Пользователь с такой почтой уже существует";

        assertThat(model.getAttribute("message")).isEqualTo(expectedError);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("wowUser")).isNotEqualTo(user);
    }

    @Test
    public void whenLoginExistingUserThenLoggedIn() {
        User user = new User(0, "Andrew", "wow@mail.ru", "wow");
        var emailCaptor = ArgumentCaptor.forClass(String.class);
        var passwordCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(emailCaptor.capture(), passwordCaptor.capture()))
                .thenReturn(Optional.of(user));
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(new MockHttpSession());
        Model model = new ConcurrentModel();
        String view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(request.getSession().getAttribute("wowUser")).isEqualTo(user);
    }

    @Test
    public void whenLoginNonExistingUserThenThenGetErrorPageWithMessage() {
        User user = new User(0, "Andrew", "wow@mail.ru", "wrongPassword");
        var emailCaptor = ArgumentCaptor.forClass(String.class);
        var passwordCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(emailCaptor.capture(), passwordCaptor.capture()))
                .thenReturn(Optional.empty());
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model model = new ConcurrentModel();
        String view = userController.loginUser(user, model, request);
        String expectedError = "Почта или пароль введены неверно";

        assertThat(expectedError).isEqualTo(model.getAttribute("error"));
        assertThat(view).isEqualTo("users/login");
    }

        @Test
        public void whenGetRegistrationPageThenRegistrationPageShown() {
            assertThat(userController.getRegistrationPage()).isEqualTo("users/register");
        }
}