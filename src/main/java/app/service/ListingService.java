package app.service;

import app.model.Listing;
import app.model.User;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing car listings.
 */
public interface ListingService {

    /**
     * Creates a new listing.
     */
    Listing createListing(CreateNewListing createNewListing, User owner);

    /**
     * Retrieves all listings that are not marked as deleted.
     */
    List<Listing> getAll();

    /**
     * Finds a listing by its ID.
     */
    Listing getListingById(UUID id);

    /**
     * Finds all listings by a specific owner.
     */
    List<Listing> findAllByOwner(User user);

    /**
     * Soft-deletes a listing if the user is authorized (owner or admin).
     */
    void deleteListing(UUID id, Listing listing, User user) throws AccessDeniedException;

    /**
     * Updates an existing listing with new data.
     */
    void updateListing(ListingCarDto dto, Listing listing);

    /**
     * Adds a listing to the user's bookmarked listings.
     */
    void bookmarkListing(User user, Listing listing);

    /**
     * Removes a listing from the user's bookmarked listings.
     */
    void removeBookmarkedListing(User user, Listing listing);

    /**
     * Retrieves a listing only if the user owns it.
     */
    Listing getListingIfOwned(UUID listingId, User user) throws AccessDeniedException;
}
