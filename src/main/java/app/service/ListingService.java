package app.service;

import app.model.Listing;
import app.model.User;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing car listings
 */
public interface ListingService {

    /**
     * Creates a new listing.
     *
     * @param createNewListing the data for the new listing
     * @param owner the user creating the listing
     * @return the saved listing
     */
    Listing createListing(CreateNewListing createNewListing, User owner);

    /**
     * Retrieves all non-deleted listings
     *
     * @return list of active listings
     */
    List<Listing> getAll();

    /**
     * Finds a listing by its ID
     *
     * @param id the listing ID
     * @return the listing entity
     */
    Listing getListingById(UUID id);

    /**
     * Finds all listings owned by a user
     *
     * @param user the owner
     * @return list of user's listings
     */
    List<Listing> findAllByOwner(User user);

    /**
     * Soft deletes a listing if authorized
     *
     * @param id the ID of the listing
     * @param listing the listing object
     * @param user the user attempting the deletion
     * @throws AccessDeniedException if the user is not the owner or an admin
     */
    void deleteListing(UUID id, Listing listing, User user) throws AccessDeniedException;

    /**
     * Updates a listing's details
     *
     * @param dto the new listing data
     * @param listing the listing to update
     */
    void updateListing(ListingCarDto dto, Listing listing);

    /**
     * Bookmarks a listing for the user
     *
     * @param user the user
     * @param listing the listing to bookmark
     */
    void bookmarkListing(User user, Listing listing);

    /**
     * Removes a bookmarked listing from the user
     *
     * @param user the user
     * @param listing the listing to remove
     */
    void removeBookmarkedListing(User user, Listing listing);

    /**
     * Retrieves a listing only if the user is the owner
     *
     * @param listingId the listing ID
     * @param user the user attempting access
     * @return the listing
     * @throws AccessDeniedException if user is not the owner
     */
    Listing getListingIfOwned(UUID listingId, User user) throws AccessDeniedException;
}

