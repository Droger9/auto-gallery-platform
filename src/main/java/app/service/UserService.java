package app.service;

import app.model.Listing;
import app.model.Role;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for user-related operations
 */
public interface UserService extends UserDetailsService {

    /**
     * Registers a new user.
     *
     * @param registerRequest the registration request data
     */
    void register(RegisterRequest registerRequest);

    /**
     * Loads a user by username (used for authentication)
     *
     * @param username the username to look up
     * @return user details for authentication
     * @throws UsernameNotFoundException if the user does not exist
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Retrieves a user by ID
     *
     * @param userId the user's ID
     * @return the user entity
     */
    User getById(UUID userId);

    /**
     * Retrieves a user by username
     *
     * @param username the username
     * @return the user entity
     */
    User findByUsername(String username);

    /**
     * Gets a list of bookmarked listings for the user
     *
     * @param user the user
     * @return list of bookmarked listings
     */
    List<Listing> findBookmarkedListings(User user);

    /**
     * Saves or updates a user entity
     *
     * @param user the user to save
     */
    void save(User user);

    /**
     * Finds all users by role
     *
     * @param role the role to filter by
     * @return list of users with the given role
     */
    List<User> findAllByRole(Role role);

    /**
     * Promotes a user to admin role
     *
     * @param userId the ID of the user
     */
    void promoteToAdmin(UUID userId);

    /**
     * Revokes admin rights from a user
     *
     * @param userId the ID of the user
     */
    void revokeAdmin(UUID userId);

    /**
     * Edits the current user's email address
     *
     * @param userEditRequest the email update request
     * @param authenticationMetadata the current user's auth context
     */
    void editUserDetails(UserEditRequest userEditRequest, AuthenticationMetadata authenticationMetadata);

    /**
     * Retrieves all users
     *
     * @return list of all users
     */
    List<User> findAll();
}

