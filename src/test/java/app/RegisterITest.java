package app;

import app.model.Role;
import app.model.User;
import app.repository.UserRepository;
import app.service.UserServiceImpl;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class RegisterITest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("integrationUser");
        request.setPassword("password123");

        userServiceImpl.register(request);

        Optional<User> userOpt = userRepository.findByUsername("integrationUser");
        assertTrue(userOpt.isPresent());
        User user = userOpt.get();
        assertEquals("integrationUser", user.getUsername());
        assertNotEquals("password123", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

}
