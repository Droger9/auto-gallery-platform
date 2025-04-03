package app.web;

import app.security.AuthenticationMetadata;
import app.service.UserService;
import app.model.User;
import app.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DummyController.class)
@Import(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private AuthenticationMetadata dummyAuth() {
        UUID userId = UUID.randomUUID();
        User dummyUser = User.builder()
                .id(userId)
                .username("dummyUser")
                .role(Role.USER)
                .build();
        when(userService.findByUsername("dummyUser")).thenReturn(dummyUser);
        return new AuthenticationMetadata(userId, "dummyUser", "dummyPass", Role.USER);
    }

    @Test
    public void testHandleGeneralException() throws Exception {
        AuthenticationMetadata auth = dummyAuth();
        mockMvc.perform(get("/test/general").with(user(auth)))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error-generic"))
                .andExpect(model().attribute("errorTitle", "An error occurred"))
                .andExpect(model().attribute("errorMessage", "General error occurred"))
                .andExpect(model().attributeExists("user"));
    }


    @Test
    public void testHandleResourceNotFound() throws Exception {
        AuthenticationMetadata auth = dummyAuth();
        mockMvc.perform(get("/test/notfound").with(user(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("error-404"))
                .andExpect(model().attribute("errorTitle", "Resource Not Found"))
                .andExpect(model().attribute("errorMessage", containsString("/test/notfound")))
                .andExpect(model().attributeExists("user"));
    }


    @Test
    public void testHandleAccessDenied() throws Exception {
        AuthenticationMetadata auth = dummyAuth();
        mockMvc.perform(get("/test/accessdenied").with(user(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("error-403"))
                .andExpect(model().attribute("errorTitle", "Access Denied"))
                .andExpect(model().attribute("errorMessage", "Access Denied error"))
                .andExpect(model().attributeExists("user"));
    }


}
