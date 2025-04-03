package app;

import app.model.Listing;
import app.model.Role;
import app.model.User;
import app.repository.ListingRepository;
import app.repository.UserRepository;
import app.service.ListingService;
import app.web.dto.CreateNewListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListingIntegrationTest {

    @Autowired
    private ListingService listingService;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testCreateListingIntegration() {
        User owner = User.builder()
                .username("ownerUser")
                .password("encodedPass")
                .role(Role.USER)
                .build();
        owner = userRepository.save(owner);

        CreateNewListing dto = new CreateNewListing();
        dto.setTitle("Test Listing");
        dto.setPhoneNumber("555-1234");
        dto.setPrice("$10000");
        dto.setColor("Red");
        dto.setYearOfManufacture(2022);

        Listing createdListing = listingService.createListing(dto, owner);
        assertNotNull(createdListing);
        assertEquals("Test Listing", createdListing.getTitle());
        assertEquals("555-1234", createdListing.getPhoneNumber());
        assertEquals("$10000", createdListing.getPrice());
        assertEquals("Red", createdListing.getColor());
        assertEquals(2022, createdListing.getYearOfManufacture());
        assertFalse(createdListing.isDeleted());
        assertEquals(owner.getId(), createdListing.getOwner().getId());
        assertNotNull(createdListing.getCreatedAt());

        Optional<Listing> fetched = listingRepository.findById(createdListing.getId());
        assertTrue(fetched.isPresent(), "Listing should be found in the database");
        Listing fetchedListing = fetched.get();
        assertEquals("Test Listing", fetchedListing.getTitle());
    }
}
