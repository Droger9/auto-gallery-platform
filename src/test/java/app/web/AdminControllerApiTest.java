package app.web;

import app.model.Role;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(AdminController.class)
public class AdminControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void testListNonAdminUsers_AsAdmin() throws Exception {
        // Prepare a dummy admin user and a couple of non-admin users.
        UUID adminId = UUID.randomUUID();
        User adminUser = User.builder()
                .id(adminId)
                .username("adminUser")
                .role(Role.ADMIN)
                .build();

        User nonAdmin1 = User.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .role(Role.USER)
                .build();
        User nonAdmin2 = User.builder()
                .id(UUID.randomUUID())
                .username("user2")
                .role(Role.USER)
                .build();
        List<User> nonAdminUsers = Arrays.asList(nonAdmin1, nonAdmin2);

        when(userService.findByUsername("adminUser")).thenReturn(adminUser);
        when(userService.findAllByRole(Role.USER)).thenReturn(nonAdminUsers);

        AuthenticationMetadata authMeta = new AuthenticationMetadata(adminId, "adminUser", "dummyPass", Role.ADMIN);

        mockMvc.perform(get("/admin/users")
                        .with(user(authMeta)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-users"))
                .andExpect(model().attribute("nonAdminUsers", hasSize(2)))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).findByUsername("adminUser");
        verify(userService, times(1)).findAllByRole(Role.USER);
    }

    @Test
    void testMakeUserAdmin() throws Exception {
        UUID adminId = UUID.randomUUID();
        User adminUser = User.builder()
                .id(adminId)
                .username("adminUser")
                .role(Role.ADMIN)
                .build();

        when(userService.findByUsername("adminUser")).thenReturn(adminUser);

        UUID targetUserId = UUID.randomUUID();

        doNothing().when(userService).promoteToAdmin(eq(targetUserId));

        AuthenticationMetadata authMeta = new AuthenticationMetadata(adminId, "adminUser", "dummyPass", Role.ADMIN);

        mockMvc.perform(post("/admin/users/{userId}/makeAdmin", targetUserId)
                        .with(user(authMeta))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).promoteToAdmin(eq(targetUserId));
    }
}
