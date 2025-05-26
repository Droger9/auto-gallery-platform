package app.service;

import app.model.Image;
import app.model.Listing;
import app.model.User;
import app.web.dto.CreateNewListing;
import app.web.dto.ImageDto;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

/**
 * Service interface for managing listing images
 */
public interface ImageService {

    /**
     * Creates and saves an image during listing creation
     *
     * @param createNewListing the listing creation data
     * @param listing the associated listing
     * @return the saved image
     */
    Image createImage(CreateNewListing createNewListing, Listing listing);

    /**
     * Saves an image entity.
     *
     * @param image the image to save
     */
    void saveImage(Image image);

    /**
     * Converts a DTO into an Image object and associates it with a listing
     *
     * @param imageDto the image data
     * @param listing the listing
     * @return the constructed image (not yet saved)
     */
    Image addImage(ImageDto imageDto, Listing listing);

    /**
     * Validates that the user owns the given listing
     *
     * @param listing the listing
     * @param user the user to validate
     * @throws AccessDeniedException if the user does not own the listing
     */
    void validateImageOwnership(Listing listing, User user) throws AccessDeniedException;

    /**
     * Finds an image by its ID.
     *
     * @param imageId the image ID
     * @return the image
     */
    Image findById(UUID imageId);

    /**
     * Deletes an image if the user owns the listing
     *
     * @param imageId the image ID
     * @param listing the associated listing
     * @param user the user attempting deletion
     * @throws AccessDeniedException if user does not own the listing
     */
    void deleteImage(UUID imageId, Listing listing, User user) throws AccessDeniedException;
}

