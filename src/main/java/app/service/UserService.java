package app.service;

import app.model.Listing;
import app.model.Role;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for user-related operations.
 */
public interface UserService extends UserDetailsService {

    /**
     * Registers a new user.
     */
    void register(RegisterRequest registerRequest);

    /**
     * Loads a user by username (used for authentication).
     */
    @Override
    org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username);

    /**
     * Retrieves a user by ID.
     */
    User getById(UUID userId);

    /**
     * Retrieves a user by username.
     */
    User findByUsername(String username);

    /**
     * Gets a list of bookmarked listings by a user.
     */
    List<Listing> findBookmarkedListings(User user);

    /**
     * Saves or updates a user.
     */
    void save(User user);

    /**
     * Finds all users with the specified role.
     */
    List<User> findAllByRole(Role role);

    /**
     * Promotes a user to admin.
     */
    void promoteToAdmin(UUID userId);

    /**
     * Revokes admin rights from a user.
     */
    void revokeAdmin(UUID userId);

    /**
     * Edits the email of the currently authenticated user.
     */
    void editUserDetails(UserEditRequest userEditRequest, AuthenticationMetadata authenticationMetadata);

    /**
     * Retrieves all users.
     */
    List<User> findAll();
}
