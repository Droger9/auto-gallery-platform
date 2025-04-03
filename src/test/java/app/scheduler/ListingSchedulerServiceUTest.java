package app.scheduler;

import app.model.Listing;
import app.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListingSchedulerServiceUTest {

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private ListingSchedulerService schedulerService;

    private Listing listingOld;
    private Listing listingRecent;

    @BeforeEach
    public void setUp() {
        listingOld = Listing.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusDays(70))
                .updatedAt(null)
                .deleted(false)
                .build();

        listingRecent = Listing.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusDays(70))
                .updatedAt(LocalDateTime.now().minusDays(10))
                .deleted(false)
                .build();
    }

    @Test
    public void testSoftDeleteOldListings() {
        List<Listing> activeListings = Arrays.asList(listingOld, listingRecent);
        when(listingRepository.findAllByDeletedFalse()).thenReturn(activeListings);

        schedulerService.softDeleteOldListings();

        assertTrue(listingOld.isDeleted());
        verify(listingRepository, times(1)).save(listingOld);

        assertFalse(listingRecent.isDeleted());
        verify(listingRepository, never()).save(listingRecent);
    }
}
