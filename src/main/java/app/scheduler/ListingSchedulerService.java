package app.scheduler;

import app.model.Listing;
import app.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.List;

@Component
public class ListingSchedulerService {

    private final ListingRepository listingRepository;

    @Autowired
    public ListingSchedulerService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void softDeleteOldListings() {

        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);

        List<Listing> activeListings = listingRepository.findAllByDeletedFalse();

        for (Listing listing : activeListings) {
            LocalDateTime lastUpdate = listing.getUpdatedAt() != null ? listing.getUpdatedAt() : listing.getCreatedAt();

            if (lastUpdate.isBefore(sixtyDaysAgo)) {
                listing.setDeleted(true);
                listingRepository.save(listing);
            }
        }
    }
}
