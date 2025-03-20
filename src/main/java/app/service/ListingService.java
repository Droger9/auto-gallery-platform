package app.service;

import app.model.Listing;
import app.model.User;
import app.repository.ListingRepository;
import app.web.dto.CreateNewListing;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    @Autowired
    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public Listing createListing(CreateNewListing createNewListing, User owner) {

        Listing listing = Listing.builder()
                .title(createNewListing.getTitle())
                .phoneNumber(createNewListing.getPhoneNumber())
                .price(createNewListing.getPrice())
                .color(createNewListing.getColor())
                .yearOfManufacture(createNewListing.getYearOfManufacture())
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .owner(owner)
                .build();

        return listingRepository.save(listing);
    }
}
