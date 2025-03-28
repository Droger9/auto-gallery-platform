package app.service;

import app.model.Listing;
import app.model.Role;
import app.model.User;
import app.repository.ListingRepository;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserService userService;

    @Autowired
    public ListingService(ListingRepository listingRepository, UserService userService) {
        this.listingRepository = listingRepository;
        this.userService = userService;
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

    public List<Listing> getAll() {

        return listingRepository.findAllByDeletedFalse();
    }

    public Listing getListingById(UUID id) {
        return listingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Listing with id " + id + " not found"));
    }

    public List<Listing> findAllByOwner(User user) {
        return listingRepository.findAllByOwnerAndDeletedFalse(user);
    }

    @Transactional
    public void deleteListing(UUID id, Listing listing, User user) throws AccessDeniedException {

        boolean isOwner = (listing.getOwner() != null && listing.getOwner().getId().equals(user.getId()));
        boolean isAdmin = (user.getRole() == Role.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this listing.");
        }

        listing.setDeleted(true);
        listingRepository.save(listing);

    }

    public void updateListing(ListingCarDto dto, Listing listing) {
        listing.setTitle(dto.getTitle());
        listing.setPhoneNumber(dto.getPhoneNumber());
        listing.setPrice(dto.getPrice());
        listing.setColor(dto.getColor());
        listing.setYearOfManufacture(dto.getYearOfManufacture());
        listing.setUpdatedAt(LocalDateTime.now());
        listingRepository.save(listing);
    }

    public void bookmarkListing(User user, Listing listing) {
        user.getBookmarkedListings().add(listing);
        userService.save(user);
    }

    public void removeBookmarkedListing(User user, Listing listing) {
        user.getBookmarkedListings().remove(listing);
        userService.save(user);
    }

    public Listing getListingIfOwned(UUID listingId, User user) throws AccessDeniedException {
        Listing listing = getListingById(listingId);
        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this listing.");
        }
        return listing;
    }

}
