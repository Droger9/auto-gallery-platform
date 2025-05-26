package app.service;

import app.exception.UserDoesNotExist;
import app.exception.UsernameAlreadyExistException;
import app.model.Listing;
import app.model.Role;
import app.model.User;
import app.repository.UserRepository;
import app.security.AuthenticationMetadata;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .role(Role.USER)
                .bookmarkedListings(new ArrayList<>())
                .build();
    }

    @Test
    public void testRegister_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setPassword("password");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userServiceImpl.register(req);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("newuser", savedUser.getUsername());
        assertEquals("encodedPass", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    public void testRegister_UsernameAlreadyExist() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UsernameAlreadyExistException ex = assertThrows(UsernameAlreadyExistException.class, () -> userServiceImpl.register(req));
        assertEquals("Username [testuser] already exist.", ex.getMessage());
    }

    @Test
    public void testLoadUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        AuthenticationMetadata metadata = (AuthenticationMetadata) userServiceImpl.loadUserByUsername("testuser");
        assertEquals(testUser.getId(), metadata.getUserId());
        assertEquals("testuser", metadata.getUsername());
        assertEquals(testUser.getPassword(), metadata.getPassword());
        assertEquals(testUser.getRole(), metadata.getRole());
        assertThat(metadata.getAuthorities()).hasSize(1);
        assertEquals("ROLE_USER", metadata.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.loadUserByUsername("nonexistent"));
    }

    @Test
    public void testGetById_Success() {
        UUID id = testUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));

        User user = userServiceImpl.getById(id);
        assertEquals(testUser, user);
    }

    @Test
    public void testGetById_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());
        assertThrows(UserDoesNotExist.class, () -> userServiceImpl.getById(randomId));
    }

    @Test
    public void testFindByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User user = userServiceImpl.findByUsername("testuser");
        assertEquals(testUser, user);
    }

    @Test
    public void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.findByUsername("nonexistent"));
    }

    @Test
    public void testFindBookmarkedListings() {
        Listing listing1 = new Listing();
        Listing listing2 = new Listing();
        List<Listing> bookmarked = new ArrayList<>(Arrays.asList(listing1, listing2));
        testUser.setBookmarkedListings(bookmarked);

        List<Listing> result = userServiceImpl.findBookmarkedListings(testUser);
        assertEquals(2, result.size());
        assertTrue(result.contains(listing1));
        assertTrue(result.contains(listing2));
    }

    @Test
    public void testSave() {
        userServiceImpl.save(testUser);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void testFindAllByRole() {
        List<User> userList = new ArrayList<>();
        userList.add(testUser);
        when(userRepository.findAllByRole(Role.USER)).thenReturn(userList);

        List<User> result = userServiceImpl.findAllByRole(Role.USER);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
    }

    @Test
    public void testPromoteToAdmin_Success() {
        UUID id = testUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userServiceImpl.promoteToAdmin(id);
        assertEquals(Role.ADMIN, testUser.getRole());
    }

    @Test
    public void testPromoteToAdmin_UserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserDoesNotExist.class, () -> userServiceImpl.promoteToAdmin(id));
    }

    @Test
    public void testEditUserDetails_Success() {
        UserEditRequest editRequest = new UserEditRequest();
        editRequest.setEmail("newemail@example.com");

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthenticationMetadata authMetadata = new AuthenticationMetadata(testUser.getId(), "testuser", testUser.getPassword(), testUser.getRole());
        userServiceImpl.editUserDetails(editRequest, authMetadata);
        assertEquals("newemail@example.com", testUser.getEmail());
    }
}
