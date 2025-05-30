package app.service.impl;

import app.exception.UserDoesNotExist;
import app.exception.UsernameAlreadyExistException;
import app.model.Listing;
import app.model.Role;
import app.model.User;
import app.repository.UserRepository;
import app.security.AuthenticationMetadata;
import app.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());
        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole());
    }

    @Override
    public User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExist("User with ID " + userId + " does not exist"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    public List<Listing> findBookmarkedListings(User user) {
        return user.getBookmarkedListings();
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> findAllByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    @Override
    public void promoteToAdmin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExist("User not found"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    @Override
    public void revokeAdmin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExist("User not found"));
        if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.USER);
            userRepository.save(user);
        }
    }

    @Override
    public void editUserDetails(UserEditRequest userEditRequest, AuthenticationMetadata authenticationMetadata) {
        User user = getById(authenticationMetadata.getUserId());
        String email = userEditRequest.getEmail();
        if (email != null && email.trim().isEmpty()) {
            email = null;
        }
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
