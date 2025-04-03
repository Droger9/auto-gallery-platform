package app.service;

import app.model.Listing;
import app.model.Role;
import app.model.User;
import app.repository.ListingRepository;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListingServiceUTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private UserService userService; // for bookmark and save calls

    @InjectMocks
    private ListingService listingService;

    private Listing testListing;
    private User owner;
    private CreateNewListing createNewListing;
    private ListingCarDto listingCarDto;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .id(UUID.randomUUID())
                .username("ownerUser")
                .role(Role.USER)
                .bookmarkedListings(new ArrayList<>())
                .build();

        testListing = Listing.builder()
                .id(UUID.randomUUID())
                .title("Sample Listing")
                .phoneNumber("555-0000")
                .price("$5000")
                .color("Blue")
                .yearOfManufacture(2018)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .owner(owner)
                .build();

        listingCarDto = ListingCarDto.builder()
                .listingId(testListing.getId().toString())
                .title("Updated Listing Title")
                .phoneNumber("555-1111")
                .price("$6000")
                .color("Red")
                .yearOfManufacture(2019)
                .make("Toyota")
                .model("Corolla")
                .carType(null)
                .build();

        createNewListing = new CreateNewListing();
        createNewListing.setTitle("New Listing");
        createNewListing.setPhoneNumber("555-2222");
        createNewListing.setPrice("$7000");
        createNewListing.setColor("Green");
        createNewListing.setYearOfManufacture(2020);
    }

    @Test
    public void testCreateListing_Success() {

        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Listing created = listingService.createListing(createNewListing, owner);
        assertNotNull(created);
        assertEquals(createNewListing.getTitle(), created.getTitle());
        assertEquals(createNewListing.getPhoneNumber(), created.getPhoneNumber());
        assertEquals(createNewListing.getPrice(), created.getPrice());
        assertEquals(createNewListing.getColor(), created.getColor());
        assertEquals(createNewListing.getYearOfManufacture(), created.getYearOfManufacture());
        assertFalse(created.isDeleted());
        assertEquals(owner, created.getOwner());

        verify(listingRepository, times(1)).save(any(Listing.class));
    }

    @Test
    public void testGetAll() {
        List<Listing> listings = Arrays.asList(testListing);
        when(listingRepository.findAllByDeletedFalse()).thenReturn(listings);

        List<Listing> result = listingService.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testListing, result.get(0));
    }

    @Test
    public void testGetListingById_Success() {
        UUID id = testListing.getId();
        when(listingRepository.findById(id)).thenReturn(Optional.of(testListing));

        Listing result = listingService.getListingById(id);
        assertNotNull(result);
        assertEquals(testListing, result);
    }

    @Test
    public void testGetListingById_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(listingRepository.findById(randomId)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> listingService.getListingById(randomId));
        assertEquals("Listing with id " + randomId + " not found", ex.getMessage());
    }

    @Test
    public void testFindAllByOwner() {
        List<Listing> listings = Collections.singletonList(testListing);
        when(listingRepository.findAllByOwnerAndDeletedFalse(owner)).thenReturn(listings);

        List<Listing> result = listingService.findAllByOwner(owner);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testListing, result.get(0));
    }

    @Test
    public void testDeleteListing_AsOwner() throws Exception {
        when(listingRepository.save(testListing)).thenReturn(testListing);

        assertDoesNotThrow(() -> listingService.deleteListing(testListing.getId(), testListing, owner));
        assertTrue(testListing.isDeleted());
        verify(listingRepository, times(1)).save(testListing);
    }

    @Test
    public void testDeleteListing_AsAdmin() throws Exception {
        User admin = User.builder()
                .id(UUID.randomUUID())
                .username("adminUser")
                .role(Role.ADMIN)
                .bookmarkedListings(new ArrayList<>())
                .build();

        when(listingRepository.save(testListing)).thenReturn(testListing);

        assertDoesNotThrow(() -> listingService.deleteListing(testListing.getId(), testListing, admin));
        assertTrue(testListing.isDeleted());
        verify(listingRepository, times(1)).save(testListing);
    }

    @Test
    public void testDeleteListing_Unauthorized() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .username("otherUser")
                .role(Role.USER)
                .bookmarkedListings(new ArrayList<>())
                .build();


        Exception ex = assertThrows(AccessDeniedException.class, () -> listingService.deleteListing(testListing.getId(), testListing, otherUser));
        assertEquals("You are not authorized to delete this listing.", ex.getMessage());
        verify(listingRepository, never()).save(any(Listing.class));
    }

    @Test
    public void testUpdateListing() {
        when(listingRepository.save(testListing)).thenAnswer(invocation -> invocation.getArgument(0));

        listingService.updateListing(listingCarDto, testListing);
        assertEquals(listingCarDto.getTitle(), testListing.getTitle());
        assertEquals(listingCarDto.getPhoneNumber(), testListing.getPhoneNumber());
        assertEquals(listingCarDto.getPrice(), testListing.getPrice());
        assertEquals(listingCarDto.getColor(), testListing.getColor());
        assertEquals(listingCarDto.getYearOfManufacture(), testListing.getYearOfManufacture());
        assertNotNull(testListing.getUpdatedAt());
        verify(listingRepository, times(1)).save(testListing);
    }

    @Test
    public void testBookmarkListing() {
        owner.setBookmarkedListings(new ArrayList<>());
        doNothing().when(userService).save(owner);

        listingService.bookmarkListing(owner, testListing);
        assertTrue(owner.getBookmarkedListings().contains(testListing));
        verify(userService, times(1)).save(owner);
    }

    @Test
    public void testRemoveBookmarkedListing() {
        List<Listing> bookmarks = new ArrayList<>();
        bookmarks.add(testListing);
        owner.setBookmarkedListings(bookmarks);
        doNothing().when(userService).save(owner);

        listingService.removeBookmarkedListing(owner, testListing);
        assertFalse(owner.getBookmarkedListings().contains(testListing));
        verify(userService, times(1)).save(owner);
    }

    @Test
    public void testGetListingIfOwned_Success() throws Exception {
        when(listingRepository.findById(testListing.getId())).thenReturn(Optional.of(testListing));
        Listing result = listingService.getListingIfOwned(testListing.getId(), owner);
        assertEquals(testListing, result);
    }

    @Test
    public void testGetListingIfOwned_Unauthorized() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .username("otherUser")
                .role(Role.USER)
                .bookmarkedListings(new ArrayList<>())
                .build();

        when(listingRepository.findById(testListing.getId())).thenReturn(Optional.of(testListing));
        Exception ex = assertThrows(AccessDeniedException.class, () -> listingService.getListingIfOwned(testListing.getId(), otherUser));
        assertEquals("You do not own this listing.", ex.getMessage());
    }
}
