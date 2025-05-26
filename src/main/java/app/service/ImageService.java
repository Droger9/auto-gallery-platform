package app.service;

import app.model.Image;
import app.model.Listing;
import app.model.User;
import app.web.dto.CreateNewListing;
import app.web.dto.ImageDto;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

/**
 * Service interface for managing listing images.
 */
public interface ImageService {

    /**
     * Creates and saves a new image for a listing using data from listing creation.
     */
    Image createImage(CreateNewListing createNewListing, Listing listing);

    /**
     * Saves an image to the database.
     */
    void saveImage(Image image);

    /**
     * Creates an Image instance from DTO and associates it with a listing.
     */
    Image addImage(ImageDto imageDto, Listing listing);

    /**
     * Validates that the user owns the listing associated with the image.
     */
    void validateImageOwnership(Listing listing, User user) throws AccessDeniedException;

    /**
     * Retrieves an image by ID.
     */
    Image findById(UUID imageId);

    /**
     * Deletes an image if the user owns the listing.
     */
    void deleteImage(UUID imageId, Listing listing, User user) throws AccessDeniedException;
}
