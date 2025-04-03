package app.web;

import app.exception.UsernameAlreadyExistException;
import app.model.Role;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.ListingService;
import app.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ListingService listingService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"));
    }

    @Test
    void getRequestToLoginEndpointWithErrorParameter_shouldReturnLoginViewAndErrorMessageAttribute() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attribute("errorMsg", "Incorrect username or password!"));
    }

    @Test
    void postRequestToRegisterEndpoint_happyPath() throws Exception {
        doNothing().when(userService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/register")
                        .param("username", "droger")
                        .param("password", "123123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void postRequestToRegisterEndpointWhenUsernameAlreadyExist_thenRedirectToRegisterWithFlashParameter() throws Exception {
        doThrow(new UsernameAlreadyExistException("Username already exist!"))
                .when(userService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/register")
                        .param("username", "droger")
                        .param("password", "123123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("usernameAlreadyExistMessage"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void postRequestToRegisterEndpointWithInvalidData_returnRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("password", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void getAuthenticatedRequestToHome_returnsHomeView() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("User123")
                .role(Role.USER)
                .build();
        when(userService.getById(userId)).thenReturn(user);
        when(listingService.getAll()).thenReturn(Collections.emptyList());

        AuthenticationMetadata principal = new AuthenticationMetadata(userId, user.getUsername(), "123123", Role.USER);

        mockMvc.perform(get("/home").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("allListings", org.hamcrest.Matchers.empty()));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void getUnauthenticatedRequestToHome_redirectToLogin() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection());
        verify(userService, never()).getById(any());
    }
}
