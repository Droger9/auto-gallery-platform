package app.web;

import app.model.Listing;
import app.model.Role;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.impl.ListingServiceImpl;
import app.service.impl.UserServiceImpl;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userServiceImpl;

    @MockitoBean
    private ListingServiceImpl listingServiceImpl;

    private AuthenticationMetadata dummyAuth(UUID userId, String username, Role role) {
        return new AuthenticationMetadata(userId, username, "dummyPass", role);
    }

    @Test
    void testShowUserProfile() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("profileUser")
                .build();
        Listing listing1 = Listing.builder().id(UUID.randomUUID()).title("Listing 1").build();
        Listing listing2 = Listing.builder().id(UUID.randomUUID()).title("Listing 2").build();
        List<Listing> userListings = Arrays.asList(listing1, listing2);
        List<Listing> bookmarkedListings = Collections.singletonList(listing1);

        when(userServiceImpl.findByUsername("profileUser")).thenReturn(user);
        when(listingServiceImpl.findAllByOwner(user)).thenReturn(userListings);
        when(userServiceImpl.findBookmarkedListings(user)).thenReturn(bookmarkedListings);

        AuthenticationMetadata auth = dummyAuth(userId, "profileUser", Role.USER);
        mockMvc.perform(get("/profile").with(user(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("userListings", hasSize(2)))
                .andExpect(model().attribute("bookmarkedListings", hasSize(1)));

        verify(userServiceImpl, times(1)).findByUsername("profileUser");
        verify(listingServiceImpl, times(1)).findAllByOwner(user);
        verify(userServiceImpl, times(1)).findBookmarkedListings(user);
    }

    @Test
    void testShowEditProfileForm() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("editUser")
                .email("oldemail@example.com")
                .build();

        when(userServiceImpl.getById(userId)).thenReturn(user);
        when(userServiceImpl.findByUsername("editUser")).thenReturn(user);

        AuthenticationMetadata auth = dummyAuth(userId, "editUser", Role.USER);
        mockMvc.perform(get("/profile/edit").with(user(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("userEditRequest", "user"));

        verify(userServiceImpl, times(1)).getById(userId);
    }

    @Test
    void testEditProfile_BindingErrors() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata auth = dummyAuth(userId, "editUser", Role.USER);

        mockMvc.perform(post("/profile/edit")
                        .param("email", "invalid-email")
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"));

        verify(userServiceImpl, never()).editUserDetails(any(), any());
    }


    @Test
    void testEditProfile_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("editUser")
                .email("oldemail@example.com")
                .build();

        UserEditRequest editRequest = UserEditRequest.builder()
                .email("newemail@example.com")
                .build();

        when(userServiceImpl.findByUsername("editUser")).thenReturn(user);

        AuthenticationMetadata auth = dummyAuth(userId, "editUser", Role.USER);
        mockMvc.perform(post("/profile/edit")
                        .param("email", "newemail@example.com")
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(userServiceImpl, times(1)).editUserDetails(any(UserEditRequest.class), eq(auth));
    }
}
